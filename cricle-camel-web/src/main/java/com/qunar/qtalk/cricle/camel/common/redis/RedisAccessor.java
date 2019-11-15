package com.qunar.qtalk.cricle.camel.common.redis;

import com.google.common.base.Stopwatch;
import com.qunar.qtalk.cricle.camel.common.consts.OpTypeEnum;
import com.qunar.qtalk.cricle.camel.common.consts.QmoConsts;
import com.qunar.qtalk.cricle.camel.common.consts.RedisConsts;
import com.qunar.qtalk.cricle.camel.common.exception.RedisOpsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by haoling.wang on 2018/12/28.
 */
@Slf4j
@Component
public class RedisAccessor {

    public interface PrefixKeyProxy {
        String proxy(String origin, String extra);
    }

    public static String makeKey(String key) {
        return RedisConsts.REDIS_PREFIX.concat(key);
    }

    public static String getPrefixKey(OpTypeEnum opTypeEnum, PrefixKeyProxy proxy, String extra) {
        String name = opTypeEnum.getName();
        if (proxy != null) {
            name = proxy.proxy(name, extra);
        }
        return name;
    }

    private static final int MAX_RETRY = 3;

    public static <R> R execute(RedisAccess<String, R> redisAccess, String key) throws RedisOpsException {
        int retried = MAX_RETRY;
        while (retried > 0) {
            Stopwatch started = Stopwatch.createStarted();
            try {
                return redisAccess.execute(makeKey(key));
            } catch (Exception e) {
                log.error("exception when access redis", e);
                retried--;
                if (retried == 0) {
                    throw new RedisOpsException(e.getMessage());
                }
            } finally {
                log.debug("access redis cost {} ms", started.elapsed(TimeUnit.MILLISECONDS));
            }
        }
        throw new RedisOpsException("redis oprator occur exception");
    }
}
