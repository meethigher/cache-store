package top.meethigher.cache.impl;

import org.junit.jupiter.api.Test;
import top.meethigher.cache.CacheStore;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
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

    @Test
    void concurrent() throws Exception {
        CacheStore<Integer, Integer> cache = new DefaultCacheStore<>();
        CountDownLatch countDownLatch = new CountDownLatch(4);
        new Thread(() -> {
            System.out.println(Thread.currentThread().getId());
            try {
                for (int i = 0; i < 100; i++) {
                    cache.put(i, i, 1, TimeUnit.SECONDS);
                    Thread.sleep(100);
                }
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getId());
            try {
                for (int i = 0; i < 100; i++) {
                    cache.put(i, i);
                    Thread.sleep(200);
                }
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getId());
            try {
                for (int i = 0; i < 100; i++) {
                    cache.remove(i);
                    Thread.sleep(1000);
                }
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getId());
            try {
                for (int i = 0; i < 100; i++) {
                    Map<Integer, Integer> integerIntegerMap = cache.toMap();
                    System.out.println(integerIntegerMap.size());
                    Thread.sleep(500);
                }
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        countDownLatch.await();
    }

    @Test
    void testSupply() throws Exception {
        DefaultCacheStore<String, String> cacheStore = new DefaultCacheStore<>();
        cacheStore.put("1","1",10,TimeUnit.SECONDS);
        cacheStore.supply("1","2");
        while(true) {
            System.out.println(cacheStore.get("1"));
            Thread.sleep(1000);
        }
    }
}
