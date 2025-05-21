package com.feishu.blog.service.impl;

import com.feishu.blog.service.JwtBlackListService;
import com.feishu.blog.util.JwtUtil;
import com.feishu.blog.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/20
 */
@Slf4j
@Service             // 声明为 Bean
@RequiredArgsConstructor
public class JwtBlackListServiceImpl implements JwtBlackListService {

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void addRefreshToken(Integer userId, String refreshToken) {
        String key = JwtUtil.generateRefreshTokenKeyForRedis(userId);
        log.debug("加入refreshToken {}:{}", key, refreshToken);
        redisUtil.setWithExpire(key, refreshToken, JwtUtil.REFRESH_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
    }

    @Override
    public void addAccessToken(Integer userId, String accessToken) {
        String key = JwtUtil.generateAccessTokenKeyForRedis(userId);
        log.debug("加入accessToken {}:{}", key, accessToken);
        redisUtil.setWithExpire(key, accessToken, JwtUtil.ACCESS_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
    }

    public void addRefreshTokenToBlacklist(String refreshToken) {
        String key = JwtUtil.generateTokenKeyForBlackList(refreshToken);
        redisUtil.setWithExpire(key, "", JwtUtil.REFRESH_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
    }

    public void addAccessTokenToBlacklist(String accessToken) {
        String key = JwtUtil.generateTokenKeyForBlackList(accessToken);
        redisUtil.setWithExpire(key, "", JwtUtil.ACCESS_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean isUserLogin(Integer userId) {
        String key = JwtUtil.generateRefreshTokenKeyForRedis(userId);
        String refreshToken = (String) redisUtil.get(key);

        return (StringUtils.hasText(refreshToken) &&
                !JwtUtil.isTokenExpired(refreshToken) &&
                JwtUtil.isRefreshToken(refreshToken));
    }

    @Override
    public void userLogout(Integer userId) {
        /* 拉黑accessToken */
        String accessTokenKey = JwtUtil.generateAccessTokenKeyForRedis(userId);
        String accessToken = (String) redisUtil.get(accessTokenKey);
        if (accessToken != null) {
            log.debug("拉黑accessToken {}:{}", accessTokenKey, accessToken);
            addAccessTokenToBlacklist(accessToken);
        }
        /* 拉黑refreshToken */
        String refreshTokenKey = JwtUtil.generateRefreshTokenKeyForRedis(userId);
        String refreshToken = (String) redisUtil.get(refreshTokenKey);
        if (refreshToken != null) {
            addRefreshTokenToBlacklist(refreshToken);
            log.debug("refreshToken {}:{}", refreshTokenKey, refreshToken);
        }
    }

    @Override
    public void addUserToBlacklist(Integer userId) {
        userLogout(userId);
    }

}
