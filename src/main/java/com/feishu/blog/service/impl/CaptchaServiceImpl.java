package com.feishu.blog.service.impl;

import com.feishu.blog.entity.Captcha;
import com.feishu.blog.service.CaptchaService;
import com.feishu.blog.util.CaptchaUtil;
import com.feishu.blog.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/22
 */
@Slf4j
@Service             // 声明为 Bean
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    /**
     * 拼图验证码允许偏差
     **/
    private static final Integer ALLOW_DEVIATION = 3;

    @Resource
    private RedisUtil redisUtil;

    private String generateCaptchaKey(String imageKey) {
        return "imageCode:" + imageKey;
    }

    private String generateCaptchaFailKey(String imageKey) {
        return "imageCode:fail:" + imageKey;
    }
    /**
     * 校验验证码
     *
     * @param imageKey
     * @param imageCode
     * @return boolean
     **/
    @Override
    public boolean checkImageCode(String imageKey, Integer imageCode) {
        String key = generateCaptchaKey(imageKey);
        Integer codeStd = (Integer) redisUtil.get(key);
        if (codeStd == null) {
            log.warn("验证码已失效");
            return false;
        }

        // 根据移动距离判断验证是否成功
        if (Math.abs(codeStd - imageCode) > ALLOW_DEVIATION) {
            // 如果验证码存在且验证失败，查看失败次数
            Integer failCount = (Integer) redisUtil.get(generateCaptchaFailKey(imageKey));
            if (failCount == null) {
                failCount = 1;
                redisUtil.setWithExpire(generateCaptchaFailKey(imageKey), failCount, 5, TimeUnit.MINUTES);
            } else {
                failCount += 1;
                // 验证码尝试次数超过五次则失效
                if (failCount < 5) {
                    redisUtil.setWithExpire(generateCaptchaFailKey(imageKey), imageCode, 5, TimeUnit.MINUTES);
                } else {
                    redisUtil.delete(generateCaptchaFailKey(imageKey));
                    redisUtil.delete(generateCaptchaKey(imageKey));
                }
            }

            log.warn("验证失败，请控制拼图对齐缺口");
            return false;
        }
        return true;
    }

    /**
     * 缓存验证码，有效期15分钟
     *
     * @param imageKey
     * @param code
     **/
    public void saveImageCode(String imageKey, Integer code) {
        String key = generateCaptchaKey(imageKey);
        redisUtil.setWithExpire(key, code, 5, TimeUnit.MINUTES);
    }

    /**
     * 获取验证码拼图（生成的抠图和带抠图阴影的大图及抠图坐标）
     **/
    @Override
    public Captcha getCaptcha() {
        Captcha captcha = new Captcha();
        //参数校验
        CaptchaUtil.initCaptcha(captcha);
        //获取画布的宽高
        int canvasWidth = captcha.getCanvasWidth();
        int canvasHeight = captcha.getCanvasHeight();
        //获取阻塞块的宽高/半径
        int blockWidth = captcha.getBlockWidth();
        int blockHeight = captcha.getBlockHeight();
        int blockRadius = captcha.getBlockRadius();
        try {
            //获取资源图
            BufferedImage canvasImage = CaptchaUtil.getBufferedImage(1);

            //调整原图到指定大小
            canvasImage = CaptchaUtil.imageResize(canvasImage, canvasWidth, canvasHeight);
            //随机生成阻塞块坐标
            int blockX = CaptchaUtil.getNonceByRange(blockWidth, canvasWidth - blockWidth - 10);
            int blockY = CaptchaUtil.getNonceByRange(10, canvasHeight - blockHeight + 1);

            log.debug("blockX:{} blockY:{}", blockX, blockY);

            //阻塞块
            BufferedImage blockImage = new BufferedImage(blockWidth, blockHeight, BufferedImage.TYPE_4BYTE_ABGR);
            //新建的图像根据轮廓图颜色赋值，源图生成遮罩
            CaptchaUtil.cutByTemplate(canvasImage, blockImage, blockWidth, blockHeight, blockRadius, blockX, blockY);
            // 移动横坐标
            String nonceStr = UUID.randomUUID().toString().replaceAll("-", "");
            // 缓存答案到Redis中
            saveImageCode(nonceStr, blockX);
            //设置返回参数
            captcha.setNonceStr(nonceStr);
            captcha.setBlockY(blockY);
            captcha.setBlockSrc(CaptchaUtil.toBase64(blockImage, "png"));
            captcha.setCanvasSrc(CaptchaUtil.toBase64(canvasImage, "png"));
            return captcha;
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("获取滑块时出现异常");
        }
        return null;
    }
}
