package com.feishu.blog;

import com.feishu.blog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
class FeishuBlogApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testGenJwt() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 10);
        claims.put("username", "itheima");
        SecretKey key = Keys.hmacShaKeyFor("my-very-very-strong-secret-key-for-hs256".getBytes());
        String jwt = Jwts.builder()
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 3600 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        System.out.println(jwt);
    }

    @Test
    public void parseJwt(){
        SecretKey key = Keys.hmacShaKeyFor("my-very-very-strong-secret-key-for-hs256".getBytes());
//        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MTAsInVzZXJuYW1lIjoiaXRoZWltYSIsImV4cCI6MTc0NzI1MjMwM30.i5UJhfKona5LneGm-YWayKogs4eFrnQJrjr48-6TFJs";
//
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();

//        System.out.println(claims);
    }

    @Test
    public void testJwtUtil() {
        Map<String, Object> userClaims = Map.of(
                "id", 1,
                "username", "alice"
        );

        // 生成两个 Token
        String accessToken = JwtUtil.generateAccessToken(userClaims);
        String refreshToken = JwtUtil.generateRefreshToken(userClaims);

        System.out.println("Access Token:\n" + accessToken);
        System.out.println("Refresh Token:\n" + refreshToken);

        // 验证解析
        System.out.println("✅ isAccessToken: " + JwtUtil.isAccessToken(accessToken) + " " + JwtUtil.isTokenExpired(accessToken));

        System.out.println("✅ isRefreshToken: " + JwtUtil.isRefreshToken(refreshToken) + " " + JwtUtil.isTokenExpired(accessToken));
        System.out.println("用户名: " + JwtUtil.getClaim(accessToken, "username"));
        log.info("logger");
    }
}
