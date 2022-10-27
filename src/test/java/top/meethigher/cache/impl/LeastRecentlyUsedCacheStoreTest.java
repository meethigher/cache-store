package top.meethigher.cache.impl;

import org.junit.jupiter.api.Test;
import top.meethigher.cache.CacheStore;

import java.util.Map;
import java.util.concurrent.TimeUnit;

class LeastRecentlyUsedCacheStoreTest {

    @Test
    void name() throws Exception {
        CacheStore<String, String> cacheStore = new LeastRecentlyUsedCacheStore<>();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                String s = "" + i;
                cacheStore.set(s,s,2, TimeUnit.SECONDS);
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                String s = "" + i;
                cacheStore.put(s,s);
            }
        }).start();
        int i=0;
        while(true) {
            if(++i>20) {
                break;
            }
            Map<String, String> map = cacheStore.toMap();
            System.out.println(map.size());
            System.out.println(map);
            Thread.sleep(1000L);
        }
    }
}