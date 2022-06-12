package top.meethigher.cachestore;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.cachestore.cache.CacheStore;
import top.meethigher.cachestore.cache.impl.DefaultInMemoryCacheStore;
import top.meethigher.cachestore.cache.impl.LruInMemoryCacheStore;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

public class CacheStoreTest {

    private static final Logger log = LoggerFactory.getLogger(CacheStoreTest.class);

    @Test
    public void testCacheStore() throws Exception {
        CacheStore<String, String> cacheStore = new DefaultInMemoryCacheStore();
        cacheStore.put("one", "one");
        cacheStore.put("two", "two", 5L, TimeUnit.SECONDS);
        while (true) {
            LinkedHashMap<String, String> map = cacheStore.toMap();
            if (map.size() < 2) {
//                break;
            }
            Thread.sleep(1000L);
        }
    }

    @Test
    public void testPutAbsent() {
        CacheStore<String, String> cacheStore = new DefaultInMemoryCacheStore();
        cacheStore.put("one", "one");
        cacheStore.putIfAbsent("one", "one", 5L, TimeUnit.SECONDS);
    }

    @Test
    public void testLock() throws InterruptedException {
        CacheStore<Integer, Integer> cacheStore = new LruInMemoryCacheStore<>(4);
        cacheStore.put(1, 1, 5L, TimeUnit.SECONDS);
        cacheStore.put(12, 12, 6L, TimeUnit.SECONDS);
        while (true) {
            log.info(cacheStore.toMap().toString());
            Thread.sleep(1000L);
        }
    }

}
