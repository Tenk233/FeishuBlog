package com.feishu.blog.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    // 必须 ≥ 256-bit（32字节）
    private static final String SECRET = "my-very-secure-and-strong-jwt-secret-key-123456";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // Token 有效期
    public static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000L;   // 15分钟
    public static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L; // 7天

    // token类型标识
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    public static final String REFRESH_TOKEN_NAME = "refreshToken";
    public static final String ACCESS_TOKEN_NAME = "accessToken";
    public static final String ACCESS_TOKEN_HEAD_NAME = "Authorization";
    public static final String ACCESS_TOKEN_PREFIX = "Bearer ";

    public static final String ITEM_ID = "uid";
    public static final String ITEM_NAME = "uname";

    public static final String JWT_ACCESS_TOKEN_KEY_PREFIX = "user:access_token:";
    public static final String JWT_REFRESH_TOKEN_KEY_PREFIX = "user:refresh_token:";
    public static final String JWT_BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * 生成 Access Token
     */
    public static String generateAccessToken(Map<String, Object> claims) {
        Map<String, Object> modifiableClaims = new HashMap<>(claims);
        modifiableClaims.put(CLAIM_TOKEN_TYPE, TYPE_ACCESS);
        return buildToken(modifiableClaims, ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * 生成 Refresh Token
     */
    public static String generateRefreshToken(Map<String, Object> claims) {
        Map<String, Object> modifiableClaims = new HashMap<>(claims);
        modifiableClaims.put(CLAIM_TOKEN_TYPE, TYPE_REFRESH);
        return buildToken(modifiableClaims, REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * 通用构建 Token
     */
    private static String buildToken(Map<String, Object> claims, long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token，获取 Claims
     */
    public static Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断 Token 是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * 获取某个 Claim 值
     */
    public static String getClaim(String token, String key) {
        return parseToken(token).get(key, String.class);
    }

    /**
     * 判断是否为 Access Token
     */
    public static boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(getClaim(token, CLAIM_TOKEN_TYPE));
    }

    /**
     * 判断是否为 Refresh Token
     */
    public static boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(getClaim(token, CLAIM_TOKEN_TYPE));
    }

    public static String generateAccessTokenKeyForRedis(Integer userId) {
        return JWT_ACCESS_TOKEN_KEY_PREFIX + userId;
    }

    public static String generateRefreshTokenKeyForRedis(Integer userId) {
        return JWT_REFRESH_TOKEN_KEY_PREFIX +  userId;
    }

    public static String generateTokenKeyForBlackList(String token) {
        return JWT_BLACKLIST_PREFIX + token;
    }
}
