package com.feishu.blog.service.impl;

import com.feishu.blog.service.LoginAttemptService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final String LOGIN_ERROR_PREFIX = "login:error:";
    private static final int MAX_ATTEMPTS = 3;
    private static final int WINDOW_IN_SECONDS = 60;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 记录登录失败
     * @param username 用户名
     * @return 是否需要滑块验证
     */
    public boolean loginFailed(String username) {
        String key = LOGIN_ERROR_PREFIX + username;
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_IN_SECONDS * 1000L;

        // 使用ZSet存储错误时间戳
        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();

        // 移除窗口外的旧数据
        zSet.removeRangeByScore(key, 0, windowStart);

        /*
            redisTemplate.opsForZSet() 返回的是操作对象，不是数据本身。
            所有操作（增删查）都需要显式传入 key。
            获取某用户的记录用 zSet.range(key, 0, -1) 或 zSet.zCard(key)（计数）。
            存储结构：
                Key: login:error:username
                Value: 时间戳（作为 member）
                Score: 时间戳（用于排序和滑动窗口过滤）
         */
        // 添加当前错误记录
        zSet.add(key, String.valueOf(now), now);

        /* Redis 的 ZSet 不支持对单个 member 设置过期时间，必须通过 score 过滤 + 全局 expire 配合实现 */
        // 设置键的过期时间
        redisTemplate.expire(key, WINDOW_IN_SECONDS, TimeUnit.SECONDS);

        // 获取当前错误次数
        Long attempts = zSet.zCard(key);

        return attempts != null && attempts >= MAX_ATTEMPTS;
    }

    /**
     * 登录成功时清除错误记录
     * @param username 用户名
     */
    public void loginSucceeded(String username) {
        String key = LOGIN_ERROR_PREFIX + username;
        redisTemplate.delete(key);
    }

    /**
     * 检查是否需要滑块验证
     * @param username 用户名
     * @return 是否需要滑块验证
     */
    public boolean isBlocked(String username) {
        String key = LOGIN_ERROR_PREFIX + username;
        long now = System.currentTimeMillis();
        long windowStart = now - WINDOW_IN_SECONDS * 1000L;

        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();
        zSet.removeRangeByScore(key, 0, windowStart);

        Long attempts = zSet.zCard(key);
        return attempts != null && attempts >= MAX_ATTEMPTS;
    }
}