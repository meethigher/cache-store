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
        this(1 << 30);

    }

    public LeastRecentlyUsedCacheStore(int maxCapacity) {
        lruMap = new LRUMap(maxCapacity);
    }

    @Override
    public synchronized VALUE remove(KEY key) {
        AbstractCacheStore<KEY, VALUE>.CacheModel<VALUE> model = lruMap.remove(key);
        return model.getData();
    }

    @Override
    public synchronized void clear() {
        lruMap.clear();
    }

    @Override
    public synchronized Map<KEY, VALUE> toMap() {
        Map<KEY, VALUE> map = new HashMap<>();
        for (KEY key : new HashSet<>(lruMap.keySet())) {
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
            System.out.println(key+"不存在，放入");
            lruMap.put(key, model);
            return true;
        }
    }

    @Override
    protected CacheModel<VALUE> enhancedGet(KEY key) {
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


        private LRUMap(int maxCapacity) {
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
        @Override
        public void run() {
            Set<KEY> keySet = new HashSet<>(lruMap.keySet());
            for (KEY key : keySet) {
                get(key);
            }
        }
    }

}
