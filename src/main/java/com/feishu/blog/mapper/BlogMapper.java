package com.feishu.blog.mapper;

import com.feishu.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    List<Blog> selectAllBlogPaged(@Param("offset") int offset,
                                  @Param("pageSize") int pageSize);

    int updateByPrimaryKeySelective(Blog blog);

    int deleteByPrimaryKey(@Param("blogId") Integer blogId);
}
