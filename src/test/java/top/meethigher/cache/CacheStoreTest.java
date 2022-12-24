package top.meethigher.cache;

import org.junit.jupiter.api.Test;
import top.meethigher.cache.impl.DefaultCacheStore;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存测试
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/1/2 19:04
 */
class CacheStoreTest {

    private static final CacheStore<Object, Object> cacheStore = new DefaultCacheStore<>();

    @Test
    void get() throws Exception {
        Object o = cacheStore.demand(1, key -> 111, 5, TimeUnit.SECONDS);
        Object o1 = cacheStore.demand(2, key -> 222);
        cacheStore.demand(3, key -> null);
        int i = 0;
        while (i < 6) {
            System.out.println(cacheStore.get(1));
            System.out.println(cacheStore.get(2));
            System.out.println(cacheStore.get(3));
            ++i;
            Thread.sleep(1000);
        }
    }

    @Test
    void name() {
        CacheStoreManager cacheStoreManager = new CacheStoreManager();
        CacheStore<String, String> test = cacheStoreManager.build("test", new DefaultCacheStore<>());

        test.put("1","1");
        Map<String, CacheStore> stringCacheStoreMap = cacheStoreManager.queryAll();
        System.out.println();
    }
}
