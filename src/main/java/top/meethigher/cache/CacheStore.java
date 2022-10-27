package top.meethigher.cache;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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


}
