package com.feishu.blog.controller;

import com.feishu.blog.entity.Result;
import com.feishu.blog.service.CaptchaService;
import com.feishu.blog.service.LoginAttemptService;
import com.feishu.blog.util.JwtUtil;
import com.feishu.blog.vo.CaptchaVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    @Resource
    private CaptchaService captchaService;

    @Resource
    private LoginAttemptService loginAttemptService;

    @GetMapping("/get")
    public Result<?> getCaptcha() {
        CaptchaVO vo = new CaptchaVO(captchaService.getCaptcha());
        return Result.success(vo);
    }

    @GetMapping("/check/{username}/{cid}/{x}")
    public Result<?> checkCaptcha(@PathVariable String cid, @PathVariable Integer x,
                                  HttpServletRequest req, @PathVariable String username) {
        if (captchaService.checkImageCode(cid, x)) {
            return Result.success();
        }
        loginAttemptService.loginSucceeded(username);
        return Result.errorClientOperation("滑块验证失败");
    }
}
