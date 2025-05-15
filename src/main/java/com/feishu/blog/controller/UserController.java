package com.feishu.blog.controller;

import com.feishu.blog.model.Result;
import com.feishu.blog.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/4/25
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @GetMapping("/get_all")
    public Result<?> listUsers() {
        return Result.success(userService.getAllUsers());
    }
}
