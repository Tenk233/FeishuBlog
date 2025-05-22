package com.feishu.blog;

import com.feishu.blog.entity.Captcha;
import com.feishu.blog.service.CaptchaService;
import com.feishu.blog.util.CaptchaUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/22
 */

@Slf4j
@SpringBootTest
public class CaptchaUtilTest {
    @Resource
    private CaptchaService captchaService;

    @Test
    void TestUtils() {
        Captcha captcha1 = (Captcha)captchaService.getCaptcha();
        System.out.println(captcha1.display());;
    }
}
