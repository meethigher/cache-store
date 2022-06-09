package top.meethigher.cachestore;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.cachestore.cache.CacheStore;
import top.meethigher.cachestore.cache.impl.DefaultInMemoryCacheStore;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

public class CacheStoreTest {

    private final Logger log = LoggerFactory.getLogger(CacheStoreTest.class);

    @Test
    public void testCacheStore() throws Exception {
        CacheStore cacheStore = new DefaultInMemoryCacheStore<String>();
        cacheStore.put("one", "one");
        cacheStore.put("two", "two", 5L, TimeUnit.SECONDS);
        while (true) {
            LinkedHashMap<String, String> map = cacheStore.toMap();
            if (map.size() < 2) {
                break;
            }
            Thread.sleep(1000L);
        }
    }

    @Test
    public void testPutAbsent() {
        CacheStore cacheStore = new DefaultInMemoryCacheStore<String>();
        cacheStore.put("one", "one");
        cacheStore.putIfAbsent("one", "one", 5L, TimeUnit.SECONDS);
    }
}
