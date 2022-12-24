package top.meethigher.cache.impl;

import top.meethigher.cache.CacheStore;

import java.io.Serializable;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 封装共性的缓存实现规范
 *
 * @author chenchuancheng
 * @since 2022/10/27 15:33
 */
public abstract class AbstractCacheStore<KEY, VALUE> implements CacheStore<KEY, VALUE> {


    /**
     * 缓存清理器
     */
    private final Timer cleaner;

    //单位毫秒
    private final long PERIOD = 60 * 1000;

    public AbstractCacheStore() {
        this.cleaner = new Timer("cleaner@" + Integer.toHexString(hashCode()));
    }

    public Timer getCleaner() {
        return cleaner;
    }

    public long getPERIOD() {
        return PERIOD;
    }

    /**
     * 增强put。如果已存在key，会将其进行覆盖
     *
     * @param key   键
     * @param model 模型
     */
    protected abstract void enhancedPut(KEY key, CacheModel<VALUE> model);

    /**
     * 增强set。如果已存在key，会将其进行覆盖
     *
     * @param key   键
     * @param model 模型
     * @return 是否set成功
     */
    protected abstract boolean enhancedSet(KEY key, CacheModel<VALUE> model);

    /**
     * 增强get。
     *
     * @param key 键
     * @return 模型
     */
    protected abstract CacheModel<VALUE> enhancedGet(KEY key);

    /**
     * 通用消费方法
     *
     * @param key      键
     * @param function function接口
     * @param consumer consumer接口
     * @return 值
     */
    private VALUE getConsumer(KEY key, Function<KEY, VALUE> function, Consumer<VALUE> consumer) {
        CacheModel<VALUE> model = enhancedGet(key);
        if (model == null) {
            VALUE value = null;
            if (function != null && consumer != null) {
                value = function.apply(key);
                if (value != null) {
                    consumer.accept(value);
                }
            }
            return value;
        }
        Long expireTime = model.getExpireTime();
        //判断时间是否已经过期，过期即移除掉
        long now = System.currentTimeMillis();
        if (expireTime != null && expireTime < now) {
            remove(key);
            return null;
        }
        return model.getData();
    }

    @Override
    public VALUE get(KEY key) {
        checkNull(key, "参数 'key' 不可为空");
        return getConsumer(key, null, null);
    }

    @Override
    public VALUE demand(KEY key, Function<KEY, VALUE> function) {
        checkNull(key, "参数 'key' 不可为空");
        checkNull(function, "参数 'function' 不可为空");
        return getConsumer(key, function, value -> put(key, value));
    }


    @Override
    public VALUE demand(KEY key, Function<KEY, VALUE> function, long timeout, TimeUnit timeUnit) {
        checkNull(key, "参数 'key' 不可为空");
        checkNull(function, "参数 'function' 不可为空");
        checkNull(timeUnit, "参数 'timeUnit' 不可为空");
        if (timeout <= 0) {
            throw new IllegalArgumentException("参数 'timeout' 必须为大于0的数");
        }
        return getConsumer(key, function, value -> put(key, value, timeout, timeUnit));
    }

    @Override
    public void put(KEY key, VALUE value) {
        checkNull(key, "参数 'key' 不可为空");
        checkNull(value, "参数 'value' 不可为空");
        enhancedPut(key, buildCacheModel(value, 0, null));
    }

    @Override
    public void put(KEY key, VALUE value, long timeout, TimeUnit timeUnit) {
        checkNull(key, "参数 'key' 不可为空");
        checkNull(value, "参数 'value' 不可为空");
        checkNull(timeUnit, "参数 'timeUnit' 不可为空");
        if (timeout <= 0) {
            throw new IllegalArgumentException("参数 'timeout' 必须为大于0的数");
        }
        enhancedPut(key, buildCacheModel(value, timeout, timeUnit));
    }

    @Override
    public boolean set(KEY key, VALUE value) {
        checkNull(key, "参数 'key' 不可为空");
        checkNull(value, "参数 'value' 不可为空");
        return enhancedSet(key, buildCacheModel(value, 0, null));
    }

    @Override
    public boolean set(KEY key, VALUE value, long timeout, TimeUnit timeUnit) {
        checkNull(key, "参数 'key' 不可为空");
        checkNull(value, "参数 'value' 不可为空");
        checkNull(timeUnit, "参数 'timeUnit' 不可为空");
        if (timeout <= 0) {
            throw new IllegalArgumentException("参数 'timeout' 必须为大于0的数");
        }
        return enhancedSet(key, buildCacheModel(value, timeout, timeUnit));
    }

    @Override
    public void supply(KEY key, VALUE value) {
        checkNull(key, "参数 'key' 不可为空");
        checkNull(value, "参数 'value' 不可为空");
        CacheModel<VALUE> cacheModel = enhancedGet(key);
        if (cacheModel == null || cacheModel.getData() == null) {
            put(key, value);
        } else {
            cacheModel.setData(value);
            enhancedPut(key, cacheModel);
        }
    }

    @Override
    public boolean contains(KEY key) {
        VALUE value = get(key);
        return value != null;
    }

    /**
     * 校验，Null时丢出异常
     *
     * @param o   需校验的对象
     * @param msg 丢出的异常msg
     */
    private void checkNull(Object o, String msg) {
        if (o == null) {
            throw new IllegalArgumentException(msg);
        }
    }


    /**
     * 封装缓存model
     *
     * @param value    值
     * @param timeout  过期时长
     * @param timeUnit 时间单位。可传null，时效性为永久
     * @return 缓存model
     */
    private CacheModel<VALUE> buildCacheModel(VALUE value, long timeout, TimeUnit timeUnit) {
        CacheModel<VALUE> model = new CacheModel<>();
        model.setCreateTime(System.currentTimeMillis());
        if (timeUnit != null) {
            model.setExpireTime(add(model.getCreateTime(), timeout, timeUnit));
        }
        model.setData(value);
        return model;
    }

    /**
     * 获取时间累积后的结果
     *
     * @param start    开始时间
     * @param timeout  过期时长
     * @param timeUnit 过期时长的单位
     * @return start+timeout
     */
    private static long add(long start, long timeout, TimeUnit timeUnit) {
        switch (timeUnit) {
            case MICROSECONDS:
                return start + timeout / 1000;
            case SECONDS:
                return start + timeout * 1000;
            case MINUTES:
                return start + timeout * 60 * 1000;
            case HOURS:
                return start + timeout * 60 * 60 * 1000;
            case DAYS:
                return start + timeout * 24 * 60 * 60 * 1000;
            case MILLISECONDS:
            default:
                return start + timeout;
        }
    }


    /**
     * 缓存Model
     *
     * @author chenchuancheng github.com/meethigher
     * @since 2022/10/27 22:00
     */
    protected class CacheModel<VALUE> implements Serializable {

        /**
         * 缓存数据
         */
        private VALUE data;

        /**
         * 过期时间
         */
        private Long expireTime;

        /**
         * 创建时间
         */
        private Long createTime;


        public VALUE getData() {
            return data;
        }

        public void setData(VALUE data) {
            this.data = data;
        }

        public Long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Long expireTime) {
            this.expireTime = expireTime;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }
    }
}
