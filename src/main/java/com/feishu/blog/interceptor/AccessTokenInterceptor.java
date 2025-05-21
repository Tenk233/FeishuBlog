package com.feishu.blog.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feishu.blog.entity.Result;
import com.feishu.blog.util.JwtUtil;
import com.feishu.blog.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class AccessTokenInterceptor implements HandlerInterceptor {

    private static final String ACCESS_HEADER  = HttpHeaders.AUTHORIZATION;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rsp, Object handler)
            throws Exception {

        String accessToken  = extractAccessToken(req);

        try {
            /* ② 先验 Access Token */
            if (StringUtils.hasText(accessToken)
                    && !JwtUtil.isTokenExpired(accessToken)
                    && JwtUtil.isAccessToken(accessToken)) {
                /* 绑定用户信息 */
                bindUser(req, accessToken);

                if (redisUtil.get(JwtUtil.generateTokenKeyForBlackList(accessToken)) == null) {
                    log.debug("accessToken不在黑名单中，放行");
                    return true;
                }
                log.debug("accessToken在黑名单中");
            }


        } catch (JwtException | IllegalArgumentException e) {
            log.warn("accessToken 校验异常: {}", e.getMessage());
        }

        /* 失败 → 401 */
        writeUnauthorized(rsp);
        return false;
    }

    /* ------------ 工具方法 ------------ */

    private String extractAccessToken(HttpServletRequest req) {
        String h = req.getHeader(ACCESS_HEADER);
        return (StringUtils.hasText(h) && h.startsWith(JwtUtil.ACCESS_TOKEN_PREFIX)) ? h.substring(JwtUtil.ACCESS_TOKEN_PREFIX.length()) : null;
    }

    private void bindUser(HttpServletRequest req, String token) {
        Claims c = JwtUtil.parseToken(token);
        req.setAttribute(JwtUtil.ITEM_ID,   c.get(JwtUtil.ITEM_ID));
        req.setAttribute(JwtUtil.ITEM_NAME, c.get(JwtUtil.ITEM_NAME));
        // 若想全局可取，可放到 ThreadLocal / SecurityContext
    }

    private void writeUnauthorized(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);   // 200 + 自定义业务码更友好
        Result<?> r = Result.error(Result.UNAUTHENTICATED, "Unauthenticated");
        resp.getWriter().write(new ObjectMapper().writeValueAsString(r));
    }

}
