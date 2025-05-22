package com.feishu.blog.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.feishu.blog.entity.Captcha;
import lombok.Data;

@Data
public class CaptchaVO {
    /**
     * 随机字符串
     **/
    private String id;

    /**
     * 生成的画布的base64
     **/
    @JsonProperty("background")
    private String canvasSrc;
    /**
     * 画布宽度
     **/
    @JsonProperty("bg_width")
    private Integer canvasWidth;
    /**
     * 画布高度
     **/
    @JsonProperty("bg_height")
    private Integer canvasHeight;
    /**
     * 生成的阻塞块的base64
     **/
    @JsonProperty("block")
    private String blockSrc;
    /**
     * 阻塞块宽度
     **/
    @JsonProperty("bl_width")
    private Integer blockWidth;
    /**
     * 阻塞块高度
     **/
    @JsonProperty("bl_height")
    private Integer blockHeight;

    /**
     * 阻塞块的纵轴坐标
     **/
    @JsonProperty("bl_y")
    private Integer blockY;

    public CaptchaVO(Captcha captcha) {
        this.id = captcha.getNonceStr();
        this.canvasSrc = captcha.getCanvasSrc();
        this.canvasWidth = captcha.getCanvasWidth();
        this.canvasHeight = captcha.getCanvasHeight();
        this.blockSrc = captcha.getBlockSrc();
        this.blockWidth = captcha.getBlockWidth();
        this.blockHeight = captcha.getBlockHeight();
        this.blockY = captcha.getBlockY();
    }
}
