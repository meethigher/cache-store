package top.meethigher.cache.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 常规缓存服务
 *
 * @author chenchuancheng
 * @since 2022/10/27 15:57
 */
public class DefaultCacheStore<KEY, VALUE> extends AbstractCacheStore<KEY, VALUE> {


    private final ConcurrentMap<KEY, CacheModel<VALUE>> cacheMap = new ConcurrentHashMap<>();


    public DefaultCacheStore() {
        getCleaner().scheduleAtFixedRate(new DefaultCacheStoreCleaner(), 0, getPERIOD());
    }

    @Override
    public VALUE remove(KEY key) {
        AbstractCacheStore<KEY, VALUE>.CacheModel<VALUE> model = cacheMap.remove(key);
        return model.getData();
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }

    @Override
    public Map<KEY, VALUE> toMap() {
        Map<KEY, VALUE> map = new LinkedHashMap<>();
        for (KEY key : cacheMap.keySet()) {
            VALUE value = get(key);
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }

    @Override
    protected void enhancedPut(KEY key, CacheModel<VALUE> model) {
        cacheMap.put(key, model);
    }

    @Override
    protected synchronized boolean enhancedSet(KEY key, CacheModel<VALUE> model) {
        if (cacheMap.containsKey(key)) {
            return false;
        } else {
            cacheMap.put(key, model);
            return true;
        }
    }

    @Override
    protected CacheModel<VALUE> enhancedGet(KEY key) {
        return cacheMap.get(key);
    }

    /**
     * 缓存清理器
     *
     * @author chenchuancheng
     * @since 2022/10/27 16:13
     */
    private class DefaultCacheStoreCleaner extends TimerTask {
        @Override
        public void run() {
            cacheMap.keySet().forEach(DefaultCacheStore.this::get);
        }
    }
}