package com.feishu.blog.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feishu.blog.model.Result;
import com.feishu.blog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
import java.util.Set;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String TOKEN_PREFIX   = "Bearer ";
    private static final String ACCESS_HEADER  = HttpHeaders.AUTHORIZATION;
    private static final String REFRESH_COOKIE = "refreshToken";
    private static final String NEW_TOKEN_HEADER = "X-New-Access-Token";

    // 白名单路径
    private static final Set<String> WHITE =
            Set.of("/auth/login", "/auth/register", "/swagger", "/error");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String uri = request.getRequestURI();
        if (WHITE.stream().anyMatch(uri::startsWith)) {
            return true;                                          // ① 白名单直接放行
        }

        String accessToken  = extractAccessToken(request);
        String refreshToken = extractRefreshToken(request);

        try {
            /* ② 先验 Access Token */
            if (StringUtils.hasText(accessToken)
                    && JwtUtil.isAccessToken(accessToken)
                    && !JwtUtil.isTokenExpired(accessToken)) {

                bindUser(request, accessToken);                    // 绑定用户信息
                return true;
            }

            /* ③ Access 失效 → 刷新 */
            if (StringUtils.hasText(refreshToken)
                    && JwtUtil.isRefreshToken(refreshToken)
                    && !JwtUtil.isTokenExpired(refreshToken)) {

                // TODO: 如有黑名单 / 单端登录要求，可在此检查 refreshToken 是否被吊销
                Claims claims = JwtUtil.parseToken(refreshToken);

                Map<String, Object> newClaims = new HashMap<>(claims); // 拷贝全部业务字段
                String newAccess = JwtUtil.generateAccessToken(newClaims);

                response.setHeader(NEW_TOKEN_HEADER, newAccess);   // 告知前端替换
                bindUser(request, newAccess);                      // 绑定用户后放行
                return true;
            }

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token 校验异常: {}", e.getMessage());
        }

        /* ④ 全部失败 → 401 */
        writeUnauthorized(response);
        return false;
    }

    /* ------------ 工具方法 ------------ */

    private String extractAccessToken(HttpServletRequest req) {
        String h = req.getHeader(ACCESS_HEADER);
        return (StringUtils.hasText(h) && h.startsWith(TOKEN_PREFIX)) ? h.substring(TOKEN_PREFIX.length()) : null;
    }

    private String extractRefreshToken(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (REFRESH_COOKIE.equals(c.getName())) return c.getValue();
        }
        return null;
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
