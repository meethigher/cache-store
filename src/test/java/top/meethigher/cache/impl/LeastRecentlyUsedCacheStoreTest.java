package top.meethigher.cache.impl;

import org.junit.jupiter.api.Test;
import top.meethigher.cache.CacheStore;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class LeastRecentlyUsedCacheStoreTest {

    @Test
    void concurrent() throws Exception {
        CacheStore<Integer, Integer> cache = new LeastRecentlyUsedCacheStore<>(4);
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
    void order() throws Exception {
        LeastRecentlyUsedCacheStore<Integer, Integer> cacheStore = new LeastRecentlyUsedCacheStore<>(2,true);
        cacheStore.put(1,1);
        cacheStore.put(2,2);
        System.out.println(cacheStore.toMap());
        cacheStore.put(1,1,1,TimeUnit.SECONDS);
        cacheStore.put(3,3);
        System.out.println(cacheStore.toMap());
        Thread.sleep(1000);
        System.out.println(cacheStore.toMap());

    }

    public static void main(String[] args) {
        LeastRecentlyUsedCacheStore<Integer, Integer> cacheStore = new LeastRecentlyUsedCacheStore<>(4);
        for (int i = 0; i < 10; i++) {
            cacheStore.put(i,i);
        }
        //用于测试并发是否存在问题。线程断点参考[IDEA调试技巧](https://meethigher.top/blog/2022/idea-debug/)
        new Thread(new Runnable() {
            @Override
            public void run() {
                cacheStore.remove(1);
            }
        },"测试线程").start();
        cacheStore.toMap();
    }
}