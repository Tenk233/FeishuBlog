package com.feishu.blog.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/20
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    // 设置值
    public void set(Object key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 获取值
    public Object get(Object key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 删除值
    public void delete(Object key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置值并指定过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 过期单位，例如TimeUnit.SECONDS，TimeUnit.HOURS
     */
    public void setWithExpire(Object key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 操作哈希结构
    public void putHash(Object key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getHash(Object key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }
}
