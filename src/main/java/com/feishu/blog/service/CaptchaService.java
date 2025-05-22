package com.feishu.blog.service;

import com.feishu.blog.entity.Captcha;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/22
 */
public interface CaptchaService {
    /**
     * 校验验证码
     * @param imageKey
     * @param imageCode
     * @return boolean
     **/
    boolean checkImageCode(String imageKey, Integer imageCode);

    /**
     * 获取验证码拼图（生成的抠图和带抠图阴影的大图及抠图坐标）
     **/
    Captcha getCaptcha();
}
