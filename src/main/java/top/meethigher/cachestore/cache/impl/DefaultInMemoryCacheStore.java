package top.meethigher.cachestore.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.cachestore.model.CacheWrapper;
import top.meethigher.cachestore.utils.AssertUtil;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 默认内存中缓存存储实现
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2022/6/4 21:59
 */
public class DefaultInMemoryCacheStore<V> extends AbstractCacheStore<String, V> {

    private final Timer timer;

    private final long PERIOD = 1000;


    private final Logger log = LoggerFactory.getLogger(DefaultInMemoryCacheStore.class);

    private final ConcurrentHashMap<String, CacheWrapper<V>> cacheMap = new ConcurrentHashMap<>();


    private final ReentrantLock lock = new ReentrantLock();

    public DefaultInMemoryCacheStore() {
        this.timer = new Timer();
        timer.scheduleAtFixedRate(new CacheExpireCleaner(), 0, PERIOD);
    }

    @Override
    public void delete(String key) {
        AssertUtil.notEmpty(key, "[delete] Cache key不能为空");
        cacheMap.remove(key);
    }

    @Override
    public LinkedHashMap<String, V> toMap() {
        LinkedHashMap<String, V> map = new LinkedHashMap<>();
        cacheMap.forEach((k, v) -> {
            map.put(k, v.getData());
        });
        return map;
    }

    @Override
    public Optional<CacheWrapper<V>> getInternal(String key) {
        AssertUtil.notEmpty(key, "[getInternal] Cache key不能为空");
        return Optional.ofNullable(cacheMap.get(key));
    }

    @Override
    public void putInternal(String key, CacheWrapper<V> cacheWrapper) {
        AssertUtil.notEmpty(key, "[putInternal] Cache key不能为空");
        cacheMap.put(key, cacheWrapper);
    }

    @Override
    public boolean putInternalIfAbsent(String key, CacheWrapper<V> cacheWrapper) {
        AssertUtil.notEmpty(key, "[putInternalIfAbsent] Cache key不能为空");
        AssertUtil.notEmpty(cacheWrapper, "[putInternalIfAbsent] Cache value不能为空");
        lock.lock();
        try {
            Optional<V> valueOptional = get(key);
            if (valueOptional.isPresent()) {
                log.info("[putInternalIfAbsent] 缓存中已经存在[{}], 不可重复存储", key);
                return false;
            }
            putInternal(key, cacheWrapper);
            return true;

        } finally {
            lock.unlock();
        }
    }


    private class CacheExpireCleaner extends TimerTask {
        @Override
        public void run() {
            cacheMap.keySet().forEach(x -> {
                if (!DefaultInMemoryCacheStore.this.get(x).isPresent()) {
                    log.info("缓存[{}]已过期", x);
                }
            });
        }
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }
}
