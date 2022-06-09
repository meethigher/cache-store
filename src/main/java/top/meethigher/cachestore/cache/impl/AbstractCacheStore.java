package top.meethigher.cachestore.cache.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.cachestore.cache.CacheStore;
import top.meethigher.cachestore.model.CacheWrapper;
import top.meethigher.cachestore.utils.AssertUtil;
import top.meethigher.cachestore.utils.TimestampUtil;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 抽象类
 * 抽象类如果要使用必须要有实现
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2022/6/4 20:31
 */
public abstract class AbstractCacheStore<K, V> implements CacheStore<K, V> {

    private final Logger log = LoggerFactory.getLogger(AbstractCacheStore.class);


    public abstract Optional<CacheWrapper<V>> getInternal(K key);

    public abstract void putInternal(K key, CacheWrapper<V> cacheWrapper);

    public abstract boolean putInternalIfAbsent(K key, CacheWrapper<V> cacheWrapper);

    @Override
    public Optional<V> get(K key) {
        AssertUtil.notEmpty(key, "[get] Cache key不能为空");
        return getInternal(key).map(cacheWrapper -> {
            long now = System.currentTimeMillis();
            if (cacheWrapper.getExpireTs() != null &&
                    cacheWrapper.getExpireTs() <= now) {
                log.warn("Cache key:[{}]已经过期", key);
                delete(key);
                return null;
            } else {
                return cacheWrapper.getData();
            }
        });
    }

    @Override
    public void put(K key, V value, long timeout, TimeUnit timeUnit) {
        putInternal(key, buildCacheWrapper(value, timeout, timeUnit));
    }

    @Override
    public void put(K key, V value) {
        putInternal(key, buildCacheWrapper(value, 0L, null));
    }

    @Override
    public Boolean putIfAbsent(K key, V value, long timeout, TimeUnit timeUnit) {
        return putInternalIfAbsent(key, buildCacheWrapper(value, timeout, timeUnit));
    }

    /**
     * 封装Cache value
     *
     * @param value
     * @param timeout
     * @param timeUnit 时间单位为空时，表示缓存永久生效
     * @return
     */
    private CacheWrapper<V> buildCacheWrapper(V value, long timeout, TimeUnit timeUnit) {
        AssertUtil.notEmpty(value, "[buildCacheWrapper] Cache value不能为空");
        AssertUtil.isTrue(timeout >= 0L, "[buildCacheWrapper] Cache timeout不能为负数");
        Long expireTs = null;
        long now = System.currentTimeMillis();
        if (timeUnit != null) {
            expireTs = TimestampUtil.add(now, timeout, timeUnit);
        }
        CacheWrapper<V> wrapper = new CacheWrapper<>();
        wrapper.setData(value);
        wrapper.setCreateTs(now);
        wrapper.setExpireTs(expireTs);
        return wrapper;
    }


}
