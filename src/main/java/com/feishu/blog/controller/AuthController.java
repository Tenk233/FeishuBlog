package com.feishu.blog.controller;

import com.feishu.blog.dto.UserLoginDTO;
import com.feishu.blog.dto.UserRegisterDTO;
import com.feishu.blog.entity.AbnormalEvent;
import com.feishu.blog.entity.Result;
import com.feishu.blog.entity.User;
import com.feishu.blog.service.*;
import com.feishu.blog.util.JwtUtil;
import com.feishu.blog.util.RedisUtil;
import com.feishu.blog.util.SecUtil;
import com.feishu.blog.vo.UserInfoVO;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/4/25
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private JwtBlackListService jwtBlackListService;

    @Resource
    private LoginAttemptService loginAttemptService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private CaptchaService captchaService;

    @Resource
    private AbnormalEventService abnormalEventService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody @Valid UserLoginDTO dto,
                           HttpServletResponse rsp) {
        if (loginAttemptService.isBlocked(dto.getUsername()) && dto.getCode() == null) {
            return Result.errorToMuchLoginAttempts(null);
        }

        // 1. 认证（校验用户名/密码）——业务逻辑放 Service
        User user = userService.authenticate(dto.getUsername(), dto.getPassword());

        if (user == null) {
            String msg = "用户名或密码错误";
            log.warn("{} : {}", dto.getUsername(), msg);
            if (loginAttemptService.loginFailed(dto.getUsername())) {

                User userTry = userService.getUserByUsername(dto.getUsername());
                if (userTry != null) {
                    abnormalEventService.addAbnormalEvent(
                            AbnormalEvent.generateBlogEvent(userTry.getId(), "连续错误登录次数太多")
                    );
                }

                return Result.errorToMuchLoginAttempts(null);
            }
            return Result.errorClientOperation(msg);
        }

        if (user.getIsBlocked() == null || user.getIsBlocked()) {
            return Result.errorClientOperation("用户已被封禁，请联系管理员");
        }

        // 2. 生成双 JWT（纯技术性操作，可放工具类）
        Map<String, Object> claims_r = Map.of(
                JwtUtil.ITEM_ID, user.getId(),
                JwtUtil.ITEM_NAME, user.getUsername()
        );
        String refresh = JwtUtil.generateRefreshToken(new HashMap<>(claims_r));

        Map<String, Object> claims_a = Map.of(
                JwtUtil.ITEM_ID, user.getId(),
                JwtUtil.ITEM_NAME, user.getUsername(),
                JwtUtil.ITEM_VERSION, jwtBlackListService.getLatestAccessTokenVersion(user.getId())
        );
        String access  = JwtUtil.generateAccessToken (new HashMap<>(claims_a));

        // 3. 把 refreshToken 写到 HttpOnly Cookie
        ResponseCookie cookie = ResponseCookie.from(JwtUtil.REFRESH_TOKEN_NAME, refresh)
                .httpOnly(true)
                .path("/")                  // 整站有效
                .sameSite("Lax")           // ★跨域必需
                .maxAge(Duration.ofDays(7))
                .build();
        rsp.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        // 4. 把accessToken写入到header
        rsp.setHeader(HttpHeaders.AUTHORIZATION, JwtUtil.ACCESS_TOKEN_PREFIX + access);

        // 将refreshToken写到Redis
        jwtBlackListService.addRefreshToken(user.getId(), refresh);

        /* 返回用户信息供前端使用 */
        UserInfoVO vo = new UserInfoVO(user, true);
        vo.setPasswordHash(null);
        return Result.success(vo);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody @Valid UserRegisterDTO dto) {
        if (dto.getCaptchaId() == null || dto.getCaptchaCode() == null) {
            return Result.errorClientOperation("滑块验证码不能为空");
        }

        if (!captchaService.checkImageCode(dto.getCaptchaId(), dto.getCaptchaCode())) {
            return Result.errorClientOperation("滑块验证失败");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(dto.getPassword());

        if (dto.getInviteCode() != null && SecUtil.checkInviteCode(dto.getInviteCode())) {
            /* 有邀请码可以注册为管理员 */
            user.setRole(User.ROLE_ADMIN);
        }

        userService.register(user);

        return Result.success();
    }

    @GetMapping("/fresh")
    public Result<?> fresh(HttpServletRequest req, HttpServletResponse rsp) {
        String refreshToken = (String) req.getAttribute(JwtUtil.REFRESH_TOKEN_NAME);
        Claims claims = JwtUtil.parseToken(refreshToken);

        /* refreshToken有效，更新accessToken */
        Map<String, Object> newClaims = new HashMap<>();
        // 拷贝业务字段
        newClaims.put(JwtUtil.ITEM_ID, claims.get(JwtUtil.ITEM_ID));
        newClaims.put(JwtUtil.ITEM_NAME, claims.get(JwtUtil.ITEM_NAME));
        // 添加版本号
        newClaims.put(JwtUtil.ITEM_VERSION, jwtBlackListService.getLatestAccessTokenVersion((Integer) claims.get(JwtUtil.ITEM_ID)));

        String newAccess = JwtUtil.generateAccessToken(newClaims);

        rsp.setHeader(HttpHeaders.AUTHORIZATION, JwtUtil.ACCESS_TOKEN_PREFIX + newAccess);   // 告知前端替换

        return Result.success();
    }
}
