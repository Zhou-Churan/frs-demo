package com.example.frs.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class RateLimiter {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LUA_SCRIPT =
            "local key = KEYS[1]\n" +
            "local now = tonumber(ARGV[1])\n" +
            "local window = tonumber(ARGV[2])\n" +
            "local maxCount = tonumber(ARGV[3])\n" +
            "redis.call('zremrangebyscore', key, 0, now - window * 1000)\n" +
            "local count = redis.call('zcard', key)\n" +
            "if count < maxCount then\n" +
            "    redis.call('zadd', key, now, now)\n" +
            "    redis.call('expire', key, window)\n" +
            "    return 1\n" +
            "else\n" +
            "    return 0\n" +
            "end";

    /**
     * 滑动窗口限流
     * @param key           限流key
     * @param maxCount      最大请求数
     * @param windowSeconds 窗口时间（秒）
     * @return 是否允许通过
     */
    public boolean isAllowed(String key, int maxCount, int windowSeconds) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
        Long result = redisTemplate.execute(redisScript,
                Collections.singletonList(key),
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(windowSeconds),
                String.valueOf(maxCount));

        return result != null && result == 1L;
    }
}