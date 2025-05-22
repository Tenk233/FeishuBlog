package com.feishu.blog.service;

import com.feishu.blog.dto.BlogModifyDTO;
import com.feishu.blog.dto.GetBlogListDTO;
import com.feishu.blog.entity.Blog;

import java.util.List;

public interface BlogService {
    /**
     * 创建一个新的草稿文章
     * @param blog
     * @return
     */
    Blog createBlog(Blog blog, List<String> tags);

    /**
     * 根据博客ID获取一篇文章
     * @param id
     * @return
     */
    Blog getBlogById(Integer id);
    
    /**
     * 更新文章
     * @param blog
     * @return
     */
    Blog updateBlog(Blog blog, List<String> tags);

    List<Blog> getAllBlogsPaged(GetBlogListDTO dto);

    /**
     * 获取某博客的所有tag
     * @param blogId
     * @return
     */
    List<String> getBlogTagsByBlogId(Integer blogId);

    /**
     * 根据id删除博客
     * @param blogId
     */
    void deleteBlogByBlogId(Integer blogId);
}
