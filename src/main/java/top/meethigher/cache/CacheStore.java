package top.meethigher.cache;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 缓存服务
 * <p>
 * 参考：https://github.com/halo-dev/halo
 *
 * @author chenchuancheng
 * @since 2022/10/27 14:54
 */
public interface CacheStore<KEY, VALUE> {


    /**
     * 获取缓存内容
     *
     * @param key 键
     * @return 值
     */
    VALUE get(KEY key);

    /**
     * 获取缓存内容，通过function进行缓存防透穿，内部实现基于put方法
     * 参考自[ben-manes/caffeine: A high performance caching library for Java](https://github.com/ben-manes/caffeine)
     *
     * @param key      键
     * @param function 函数式接口
     * @return 值
     */
    VALUE demand(KEY key, Function<KEY, VALUE> function);

    /**
     * 获取缓存内容，通过function进行缓存防透穿，内部实现基于put方法
     *
     * @param key      键
     * @param function 函数式接口
     * @param timeout  过期时间
     * @param timeUnit 过期时间单位
     * @return 值
     */
    VALUE demand(KEY key, Function<KEY, VALUE> function, long timeout, TimeUnit timeUnit);

    /**
     * 设置缓存。如果已存在key，会将其替换掉
     *
     * @param key   键
     * @param value 值
     */
    void put(KEY key, VALUE value);

    /**
     * 设置缓存，并在指定timeout时间后被清理掉。如果已存在key，会将其覆盖掉
     *
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 过期时间的单位
     */
    void put(KEY key, VALUE value, long timeout, TimeUnit timeUnit);

    /**
     * 设置缓存。如果已存在key，则放入失败
     *
     * @param key   键
     * @param value 值
     * @return 执行是否成功，true表示成功
     */
    boolean set(KEY key, VALUE value);

    /**
     * 设置缓存，并在指定timeout时间后被清理掉。如果已存在key，则放入失败
     *
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 过期时间的单位
     * @return 执行是否成功，true表示成功
     */
    boolean set(KEY key, VALUE value, long timeout, TimeUnit timeUnit);

    /**
     * 如果未设置过期时间，此处作用相当于put方法
     * 如果设置了过期时间，此处只是将内容修改，过期时间不进行变动
     *
     * @param key   键
     * @param value 值
     */
    void supply(KEY key, VALUE value);

    /**
     * 是否包含key
     *
     * @param key 键
     * @return true表示包含
     */
    boolean contains(KEY key);

    /**
     * 移除缓存
     *
     * @param key 键
     * @return 移除的值
     */
    VALUE remove(KEY key);

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 返回此缓存中存储的项的视图，作为非线程安全的映射。对映射所做的修改不会直接影响缓存。
     *
     * @return 缓存map
     */
    Map<KEY, VALUE> toMap();


    /**
     * @return 缓存容量大小
     */
    int size();


}
