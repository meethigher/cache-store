package top.meethigher.cachestore.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * 工具类
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2022/6/4 20:43
 */
public abstract class AssertUtil {

    public static void notNull(Object obj, String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void notEmpty(Object obj, String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg);
        }

        if (obj instanceof Optional) {
            if (!((Optional<?>) obj).isPresent()) {
                throw new IllegalArgumentException(msg);
            }
        }
        if (obj instanceof CharSequence) {
            if (((CharSequence) obj).length() == 0) {
                throw new IllegalArgumentException(msg);
            }
        }
        if (obj.getClass().isArray()) {
            if (Array.getLength(obj) == 0) {
                throw new IllegalArgumentException(msg);
            }
        }
        if (obj instanceof Collection) {
            if (((Collection<?>) obj).isEmpty()) {
                throw new IllegalArgumentException(msg);
            }
        }
        if (obj instanceof Map) {
            if (((Map<?, ?>) obj).isEmpty()) {
                throw new IllegalArgumentException(msg);
            }
        }
    }

    public static void isTrue(boolean exp, String msg) {
        if (!exp) {
            throw new IllegalArgumentException(msg);
        }
    }
}
