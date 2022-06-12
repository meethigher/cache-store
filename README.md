参考

1. [lishuo9527/LocalCache: JAVA LocalCache -- JAVA 本地缓存工具类](https://github.com/lishuo9527/LocalCache)
2. [halo-dev/halo: ✍ 一款现代化的开源博客 / CMS 系统。](https://github.com/halo-dev/halo)


```java
package top.meethigher.cachestore.cache;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * CacheStore
 * <p>
 * 参考：https://github.com/halo-dev/halo
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2022/6/4 20:11
 */
public interface CacheStore<K, V> {
    /**
     * Gets by cache key.
     *
     * @param key must not be null
     * @return cache value
     */
    Optional<V> get(K key);

    /**
     * Puts a cache which will be expired.
     *
     * @param key      cache key must not be null
     * @param value    cache value must not be null
     * @param timeout  the key expiration must not be less than 1
     * @param timeUnit timeout unit
     */
    void put(K key, V value, long timeout, TimeUnit timeUnit);


    /**
     * Puts a non-expired cache.
     *
     * @param key   cache key must not be null
     * @param value cache value must not be null
     */
    void put(K key, V value);

    /**
     * Puts a cache which will be expired if the key is absent.
     *
     * @param key      cache key must not be null
     * @param value    cache value must not be null
     * @param timeout  the key expiration must not be less than 1
     * @param timeUnit timeout unit must not be null
     * @return true if the key is absent and the value is set, false if the key is present
     * before, or null if any other reason
     */
    Boolean putIfAbsent(K key, V value, long timeout, TimeUnit timeUnit);

    /**
     * Delete a key.
     *
     * @param key cache key must not be null
     */
    void delete(K key);

    /**
     * Returns a view of the entries stored in this cache as a none thread-safe map.
     * Modifications made to the map do not directly affect the cache.
     */
    LinkedHashMap<K, V> toMap();

    /**
     * cache store clear
     */
    void clear();
}
```

