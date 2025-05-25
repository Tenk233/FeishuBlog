package com.feishu.blog.mapper;

import com.feishu.blog.entity.UploadedImage;
import org.apache.ibatis.annotations.Mapper;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/25
 */
@Mapper
public interface ImageMapper {
    int insertImage(UploadedImage image);

    UploadedImage selectImageByName(String name);

    int updateImageStatusByName(String name, int status);
}
