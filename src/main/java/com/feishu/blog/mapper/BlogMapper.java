package com.feishu.blog.mapper;

import com.feishu.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/21
 */
@Mapper
public interface BlogMapper {
    /**
     * 创建
     * @param blog
     * @return
     */
    int createBlog(Blog blog);

    Blog selectBlogByPrimaryKey(Integer blogId);

    int updateBlog(Blog blog);
}
