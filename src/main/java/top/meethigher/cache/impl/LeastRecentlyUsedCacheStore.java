package top.meethigher.cache.impl;

import java.util.*;

/**
 * LRU缓存服务
 * LeastRecentlyUsed-近期最少使用的数据先淘汰
 *
 * @author chenchuancheng
 * @since 2022/10/27 16:55
 */
public class LeastRecentlyUsedCacheStore<KEY, VALUE> extends AbstractCacheStore<KEY, VALUE> {

    private final LRUMap lruMap;


    public LeastRecentlyUsedCacheStore() {
        //map的长度是2的幂
        this(1 << 30, false);

    }

    public LeastRecentlyUsedCacheStore(int maxCapacity) {
        this(maxCapacity, false);
    }

    /**
     * LRU缓存
     *
     * @param maxCapacity 最大容量
     * @param accessOrder true表示访问顺序，false表示插入顺序
     */
    public LeastRecentlyUsedCacheStore(int maxCapacity, boolean accessOrder) {
        lruMap = new LRUMap(maxCapacity, accessOrder);
        getCleaner().scheduleAtFixedRate(new LRUCacheStoreCleaner(this), 0, getPERIOD());
    }

    @Override
    public synchronized VALUE remove(KEY key) {
        AbstractCacheStore<KEY, VALUE>.CacheModel<VALUE> model = lruMap.remove(key);
        return model == null ? null : model.getData();
    }

    @Override
    public synchronized void clear() {
        lruMap.clear();
    }

    @Override
    public synchronized Map<KEY, VALUE> toMap() {
        Map<KEY, VALUE> map = new LinkedHashMap<>();
        for (KEY key : new ArrayList<>(lruMap.keySet())) {
            VALUE value = get(key);
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }

    @Override
    protected synchronized void enhancedPut(KEY key, CacheModel<VALUE> model) {
        lruMap.put(key, model);
    }

    @Override
    protected synchronized boolean enhancedSet(KEY key, CacheModel<VALUE> model) {
        if (lruMap.containsKey(key)) {
            return false;
        } else {
            lruMap.put(key, model);
            return true;
        }
    }

    @Override
    protected synchronized CacheModel<VALUE> enhancedGet(KEY key) {
        return lruMap.get(key);
    }


    /**
     * 基于LRU策略的Map
     * 线程不安全
     *
     * @author chenchuancheng github.com/meethigher
     * @since 2022/10/27 20:47
     */
    private class LRUMap extends LinkedHashMap<KEY, CacheModel<VALUE>> {

        private final int maxCapacity;


        private LRUMap(int maxCapacity, boolean accessOrder) {
            //负载因子1，初始容量maxCapacity，表示实际容量超过maxCapacity*1后进行扩容
            super(maxCapacity, 1f, accessOrder);
            this.maxCapacity = maxCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<KEY, AbstractCacheStore<KEY, VALUE>.CacheModel<VALUE>> eldest) {
            return this.size() > maxCapacity;
        }
    }

    /**
     * 缓存清理器
     *
     * @author chenchuancheng
     * @since 2022/10/27 16:13
     */
    private class LRUCacheStoreCleaner extends TimerTask {

        private final LeastRecentlyUsedCacheStore<KEY, VALUE> cacheStore;

        private LRUCacheStoreCleaner(LeastRecentlyUsedCacheStore<KEY, VALUE> cacheStore) {
            this.cacheStore = cacheStore;
        }

        @Override
        public void run() {
            synchronized (cacheStore) {
                try {
                    new ArrayList<>(lruMap.keySet()).forEach(LeastRecentlyUsedCacheStore.this::get);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
