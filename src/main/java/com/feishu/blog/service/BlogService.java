package com.feishu.blog.service;

import com.feishu.blog.entity.Blog;

public interface BlogService {
    /**
     * 创建一个新的草稿文章
     * @param blog
     * @return
     */
    Blog createBlog(Blog blog);

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
    Blog updateBlog(Blog blog);
}
