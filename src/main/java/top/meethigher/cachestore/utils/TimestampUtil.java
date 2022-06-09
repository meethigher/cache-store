package top.meethigher.cachestore.utils;

import java.util.concurrent.TimeUnit;

public class TimestampUtil {

    public static Long add(Long start, Long timeout, TimeUnit timeUnit) {
        AssertUtil.notEmpty(start, "start 不能为空");
        AssertUtil.notEmpty(timeUnit, "timeUnit 不能为空");
        AssertUtil.notEmpty(timeout, "timeout 不能为空");

        switch (timeUnit) {
            case MICROSECONDS:
                return start + timeout / 1000;
            case MILLISECONDS:
                return start + timeout;
            case SECONDS:
                return start + timeout * 1000;
            case MINUTES:
                return start + timeout * 60 * 1000;
            case HOURS:
                return start + timeout * 60 * 60 * 1000;
            case DAYS:
                return start + timeout * 24 * 60 * 60 * 1000;
            default:
                return start + timeout;
        }
    }
}
