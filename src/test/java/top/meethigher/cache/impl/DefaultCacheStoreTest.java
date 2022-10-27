package top.meethigher.cache.impl;

import org.junit.jupiter.api.Test;
import top.meethigher.cache.CacheStore;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

class DefaultCacheStoreTest {

    @Test
    void cacheStoreTest() throws Exception {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println("NAME = " + jvmName);
        CacheStore<String, String> cacheStore = new DefaultCacheStore<>();
        cacheStore.put("111", "111");
        cacheStore.put("222", "222", 2, TimeUnit.SECONDS);
        int i = 0;
        while (true) {
            if (++i > 5) {
                break;
            }
            System.out.println(cacheStore.toMap().toString());
//            System.out.println(cacheStore.get("222"));
            Runtime runtime = Runtime.getRuntime();
            System.out.printf("%s,%s,%s\n", runtime.availableProcessors(), runtime.totalMemory(), runtime.maxMemory());
            Thread.sleep(1000L);
        }
    }
}
