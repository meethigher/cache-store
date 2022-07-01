package top.meethigher.cachestore.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.cachestore.model.CacheWrapper;
import top.meethigher.cachestore.utils.AssertUtil;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Least Recently Used
 * 最近最少使用缓存优先淘汰
 *
 * @author chenchuancheng
 * @since 2022/6/10 15:58
 */
public class LruInMemoryCacheStore<K, V> extends AbstractCacheStore<K, V> {

    private final Logger log = LoggerFactory.getLogger(LruInMemoryCacheStore.class);
    /**
     * 定时器
     */
    private final Timer timer;

    /**
     * 单位毫秒
     */
    private final long PERIOD = 1000;


    /**
     * 读写锁
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    /**
     * 读锁
     */
    private final Lock readLock = readWriteLock.readLock();

    /**
     * 写锁
     */
    private final Lock writeLock = readWriteLock.writeLock();


    /**
     * lruCacheMap
     */
    private final LruMap cacheMap;


    public LruInMemoryCacheStore() {
        //最大容量为int的最大值
        this(1 << 30);
    }

    @SuppressWarnings("all")
    public LruInMemoryCacheStore(int maxCapacity) {
        cacheMap = new LruMap(maxCapacity);
        this.timer = new Timer("lru-cache-expire-cleaner");
        timer.scheduleAtFixedRate(new LruInMemoryCacheStore.LruCacheExpireCleaner(), 0, PERIOD);
    }

    @Override
    public Optional<CacheWrapper<V>> getInternal(K key) {
        readLock.lock();
        try {
            AssertUtil.notEmpty(key, "[getInternal] Cache key不能为空");
            return Optional.ofNullable(cacheMap.get(key));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void putInternal(K key, CacheWrapper<V> cacheWrapper) {
        writeLock.lock();
        try {
            AssertUtil.notEmpty(key, "[putInternal] Cache key不能为空");
            cacheMap.put(key, cacheWrapper);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean putInternalIfAbsent(K key, CacheWrapper<V> cacheWrapper) {
        AssertUtil.notEmpty(key, "[putInternalIfAbsent] Cache key不能为空");
        AssertUtil.notEmpty(cacheWrapper, "[putInternalIfAbsent] Cache value不能为空");
        writeLock.lock();
        try {
            Optional<V> valueOptional = get(key);
            if (valueOptional.isPresent()) {
                log.info("[putInternalIfAbsent] 缓存中已经存在[{}], 不可重复存储", key);
                return false;
            }
            putInternal(key, cacheWrapper);
            return true;

        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(K key) {
        writeLock.lock();
        try {
            AssertUtil.notEmpty(key, "[delete] Cache key不能为空");
            cacheMap.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public LinkedHashMap<K, V> toMap() {
        readLock.lock();
        try {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            cacheMap.forEach((k, v) -> {
                map.put(k, v.getData());
            });
            return map;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            cacheMap.clear();
        } finally {
            writeLock.unlock();
        }
    }

    private class LruCacheExpireCleaner extends TimerTask {
        @Override
        public void run() {
            writeLock.lock();
            try {
                Set<K> keySet = new HashSet<>(cacheMap.keySet());
                for (K key : keySet) {
                    if (!LruInMemoryCacheStore.this.get(key).isPresent()) {
                        log.info("缓存[{}]已过期", key);
                    }
                }
            } finally {
                writeLock.unlock();
            }

        }
    }

    private class LruMap extends LinkedHashMap<K, CacheWrapper<V>> {

        private final int maxCapacity;

        public LruMap(int maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, CacheWrapper<V>> eldest) {
            log.info("队列中最不常用的数据key {}", eldest.getKey());
            return this.size() > maxCapacity;
        }
    }
}
