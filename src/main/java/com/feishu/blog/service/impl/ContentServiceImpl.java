package com.feishu.blog.service.impl;

import com.feishu.blog.dto.ClassificationDTO;
import com.feishu.blog.entity.AbnormalEvent;
import com.feishu.blog.entity.Blog;
import com.feishu.blog.entity.UploadedImage;
import com.feishu.blog.mapper.BlogMapper;
import com.feishu.blog.mapper.EventMapper;
import com.feishu.blog.mapper.ImageMapper;
import com.feishu.blog.service.*;
import com.feishu.blog.util.FileUtil;
import com.feishu.blog.util.ImgUrlExtractorUtil;
import com.feishu.blog.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service             // 声明为 Bean
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private static final String CONTENT_LEVEL_PREFIX = "content:level:";
    private static final String IMAGE_LEVEL_PREFIX = "image:level:";
    public static final int LEVEL_LOCAL_CHECK = 0;
    public static final int LEVEL_COZE_CHECK = 1;
    public static final int LEVEL_BOTH_CHECK = 3;

    private final RedisUtil redisUtil;

    private final LocalTextService localTextService;
    private final CozeTextService cozeTextService;

    private final LocalImageService localImageService;
    private final CozeImageService cozeImageService;

    private final BlogMapper blogMapper;
    private final EventMapper eventMapper;
    private final ImageMapper imageMapper;

    /**
     * 异步执行：内容和图片合规校验 + 更新数据库
     */
    @Async("imageTaskExecutor")                 // ★ 指定线程池
    @Transactional
    @Override
    public void checkContent(String content, Integer blogId, Integer userId) {
        boolean contentValid = true;
        String msg = null;
        log.debug("开始异步审查用户{}的博客{}", userId, blogId);
        // 检查文本内容
        if (getContentCheckLevel() == LEVEL_LOCAL_CHECK || getContentCheckLevel() == LEVEL_BOTH_CHECK) {
            ClassificationDTO classificationDTO = localTextService.processContentCompliance(content);
            contentValid = classificationDTO != null && classificationDTO.getData().isValid();
            if (!contentValid) {
                msg = "本地模型审查结果，消极的博客内容: " + content;
            }
        }
        if (contentValid && getContentCheckLevel() == LEVEL_COZE_CHECK || getContentCheckLevel() == LEVEL_BOTH_CHECK) {
            ClassificationDTO classificationDTO = cozeTextService.processContentCompliance(content);
            contentValid = classificationDTO != null && classificationDTO.getData().isValid();
            if (!contentValid) {
                msg = "COZE审查结果，消极的博客内容: " + content;
            }
        }

        // 检查图片内容
        if (contentValid) {
            List<String> names = ImgUrlExtractorUtil.extractImageNames(content);
            for (String name : names) {
                /* 先看数据库有没有*/
                UploadedImage uploadedImage = imageMapper.selectImageByName(name);
                if (uploadedImage != null && uploadedImage.getStatus() == UploadedImage.STATUS_PASSED) {
                    continue;
                }

                if (getImageCheckLevel() == LEVEL_LOCAL_CHECK || getImageCheckLevel() == LEVEL_BOTH_CHECK) {
                    try {
                        ClassificationDTO classificationDTO = localImageService.classifyImageWithPath(FileUtil.IMAGE_SAVE_DIR + "/" + name);
                        contentValid = classificationDTO != null && classificationDTO.getData().isValid();
                        if (!contentValid) {
                            msg = "本地模型审查结果，消极的图片: " + FileUtil.IMAGE_URI_PREFIX + "/" + name;
                            break;
                        }
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                }

                if (getImageCheckLevel() == LEVEL_COZE_CHECK || getImageCheckLevel() == LEVEL_BOTH_CHECK) {
                    try {
                        ClassificationDTO classificationDTO = cozeImageService.processImageComplianceWithPath(FileUtil.IMAGE_SAVE_DIR + "/" + name);
                        contentValid = classificationDTO != null && classificationDTO.getData().isValid();
                        if (!contentValid) {
                            msg = "COZE审查结果，消极的图片: " + FileUtil.IMAGE_URI_PREFIX + "/" + name;
                            break;
                        }
                    } catch (Exception e) {
                        log.info(e.getMessage());
                    }
                }

                // 图片审查没问题，数据库保存结果
                imageMapper.updateImageStatusByName(name,  UploadedImage.STATUS_PASSED);
            }
        }

        if (!contentValid) {
            /* 审查失败 */
            blogMapper.updateBlogStatusByPrimaryKey(blogId, Blog.STATUS_FORBIDDEN);
            // 数据库记录
            eventMapper.insertAbnormalEvent(
                    AbnormalEvent.generateBlogEvent(userId, blogId, msg)
            );
        } else {
            /* 审查成功 */
            blogMapper.updateBlogStatusByPrimaryKey(blogId, Blog.STATUS_PUBLISHED);
        }
        log.debug("用户{}的博客{}的审查结果为{}", userId, blogId, contentValid);
    }

    @Override
    public void updateConfig(Integer contentLevel, Integer imageLevel) {
        if (contentLevel != null && contentLevel >= LEVEL_LOCAL_CHECK && contentLevel <= LEVEL_BOTH_CHECK) {
            redisUtil.set(CONTENT_LEVEL_PREFIX, contentLevel);
        }

        if (imageLevel != null && imageLevel >= LEVEL_LOCAL_CHECK && imageLevel <= LEVEL_BOTH_CHECK) {
            redisUtil.set(IMAGE_LEVEL_PREFIX, imageLevel);
        }
    }

    private Integer getContentCheckLevel() {
        Integer level = (Integer) redisUtil.get(CONTENT_LEVEL_PREFIX);
        if (level == null) {
            level = 0;
        }
        redisUtil.set(CONTENT_LEVEL_PREFIX, level);
        return level;
    }

    private Integer getImageCheckLevel() {
        Integer level = (Integer) redisUtil.get(IMAGE_LEVEL_PREFIX);
        if (level == null) {
            level = 0;
        }
        redisUtil.set(IMAGE_LEVEL_PREFIX, level);
        return level;
    }
}
