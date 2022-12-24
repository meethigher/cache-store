package top.meethigher.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存监控管理器
 * 如果不用监控，可以直接实例对象
 *
 * @author chenchuancheng
 * @since 2023/1/10 9:53
 */
public final class CacheStoreManager {

    private static final Map<String, CacheStore> cacheNameMap = new ConcurrentHashMap<>();


    /**
     * 构建缓存，使用该方法构建的目的，是为了方便监控
     *
     * @param name       缓存名称
     * @param cacheStore 缓存实例
     * @param <KEY>      缓存KEY的类型
     * @param <VALUE>    缓存VALUE的类型
     * @return 缓存示例
     */
    public static <KEY, VALUE> CacheStore<KEY, VALUE> build(String name, CacheStore<KEY, VALUE> cacheStore) {
        cacheNameMap.put(name, cacheStore);
        return cacheStore;
    }

    /**
     * @return 查询所有使用的缓存。修改该map不会影响原值
     */
    public static Map<String, CacheStore> queryAll() {
        Map<String, CacheStore> tempMap = new ConcurrentHashMap<>();
        for (String key : cacheNameMap.keySet()) {
            tempMap.put(key, cacheNameMap.get(key));
        }
        return tempMap;
    }

}
