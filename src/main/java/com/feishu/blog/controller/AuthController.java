package com.feishu.blog.controller;

import com.feishu.blog.dto.UserRegisterDTO;
import com.feishu.blog.model.Result;
import com.feishu.blog.model.User;
import com.feishu.blog.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/4/25
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login() {
        return Result.success();
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody @Valid UserRegisterDTO dto) {
        log.debug("dto: {}", dto);

        User user = new User();
        user.setUsername(dto.getEmail());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPasswordHash(dto.getPasswd());

        userService.register(user);

        return Result.success();
    }
}
