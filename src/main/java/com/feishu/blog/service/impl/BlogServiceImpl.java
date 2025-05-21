package com.feishu.blog.service.impl;

import com.feishu.blog.entity.Blog;
import com.feishu.blog.exception.BizException;
import com.feishu.blog.mapper.BlogMapper;
import com.feishu.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service             // 声明为 Bean
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogMapper blogMapper;

    @Override
    @Transactional
    public Blog createBlog(Blog blog) {
        if (blogMapper.createBlog(blog) != 1) {
            throw new BizException(BizException.INTERNAL_ERROR, "插入博客失败");
        }
        return blogMapper.selectBlogByPrimaryKey(blog.getId());
    }

    @Override
    public Blog getBlogById(Integer id) {
        return blogMapper.selectBlogByPrimaryKey(id);
    }

    @Override
    public Blog updateBlog(Blog blog) {
        return null;
    }
}
