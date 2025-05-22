package com.feishu.blog.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feishu.blog.entity.Result;
import com.feishu.blog.util.JwtUtil;
import com.feishu.blog.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {
    @Resource
    private RedisUtil  redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rsp, Object handler)
            throws Exception {
        /* 获取用户的refreshToken */
        String refreshToken = extractRefreshToken(req);
        try {
            /* 尝试刷新accessToken */
            if (StringUtils.hasText(refreshToken)
                    && !JwtUtil.isTokenExpired(refreshToken)
                    && JwtUtil.isRefreshToken(refreshToken)) {

                if (redisUtil.get(JwtUtil.generateTokenKeyForBlackList(refreshToken)) != null) {
                    log.debug("refreshToken在黑名单中");
                    writeUnauthorized(rsp);
                    return false;
                }

                Claims claims = JwtUtil.parseToken(refreshToken);

                /* 检查Redis中的refreshToken和用户提交的是否一致 */
                Integer uid = (Integer) claims.get(JwtUtil.ITEM_ID);
                if (uid == null) {
                    throw new JwtException("Unknown token");
                }
                String key = JwtUtil.generateRefreshTokenKeyForRedis(uid);
                String refreshTokenInRedis = (String) redisUtil.get(key);

                if (refreshTokenInRedis == null || !refreshTokenInRedis.equals(refreshToken))
                {
                    throw new JwtException("Unknown token");
                }

                req.setAttribute(JwtUtil.REFRESH_TOKEN_NAME, refreshToken);

                return true;
            }

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("RefreshToken 校验异常: {}", e.getMessage());
        }

        /* 失败 → 401 */
        writeUnauthorized(rsp);
        return false;
    }

    /* ------------ 工具方法 ------------ */
    private String extractRefreshToken(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (JwtUtil.REFRESH_TOKEN_NAME.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);   // 200 + 自定义业务码更友好
        Result<?> r = Result.errorUnauthenticated("No valid refresh token or token expired");
        resp.getWriter().write(new ObjectMapper().writeValueAsString(r));
    }

}
