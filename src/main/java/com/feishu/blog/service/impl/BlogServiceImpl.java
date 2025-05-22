package com.feishu.blog.service.impl;

import com.feishu.blog.dto.BlogCreateDTO;
import com.feishu.blog.dto.BlogModifyDTO;
import com.feishu.blog.dto.GetBlogListDTO;
import com.feishu.blog.entity.Blog;
import com.feishu.blog.entity.BlogTag;
import com.feishu.blog.exception.BizException;
import com.feishu.blog.mapper.BlogMapper;
import com.feishu.blog.mapper.BlogTagMapper;
import com.feishu.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service             // 声明为 Bean
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogMapper blogMapper;
    private final BlogTagMapper blogTagMapper;

    @Override
    @Transactional
    public Blog createBlog(Blog blog, List<String> tags) {
        if (blogMapper.createBlog(blog) != 1) {
            throw new BizException(BizException.INTERNAL_ERROR, "插入博客失败");
        }
        if (tags != null && !tags.isEmpty()) {
            updateBlogTags(blog.getId(), tags);
        }
        return blogMapper.selectBlogByPrimaryKey(blog.getId());
    }

    @Override
    public Blog getBlogById(Integer id) {
        return blogMapper.selectBlogByPrimaryKey(id);
    }

    /**
     * 更新文章的标签
     * @param blogId
     * @param tags
     */
    public void updateBlogTags(Integer blogId, List<String> tags) {
        /* 简单做法，把原来的标签删掉再将新的标签插入，后续换为更更规范的做法 */
        blogTagMapper.deleteByBlogId(blogId);

        BlogTag blogTag = new BlogTag();
        for (String tag : tags) {
            blogTag.setBlogId(blogId);
            blogTag.setTag(tag);
            blogTagMapper.createBlogTag(blogTag);
        }
    }

    @Override
    @Transactional
    public Blog updateBlog(Blog blog, List<String> tags) {
        if (blogMapper.updateByPrimaryKeySelective(blog) != 1) {
            throw new BizException(BizException.INTERNAL_ERROR, "文章更新异常");
        }
        if (tags != null && !tags.isEmpty()) {
            updateBlogTags(blog.getId(), tags);
        }

        return blogMapper.selectBlogByPrimaryKey(blog.getId());
    }

    @Override
    public List<Blog> getAllBlogsPaged(GetBlogListDTO dto) {
        int page = 0;
        int limit = 20;
        if (dto.getPage() != null) {
            page = dto.getPage() - 1;
        }
        if (dto.getLimit() != null) {
            limit = dto.getLimit();
        }

        return blogMapper.selectAllBlogPaged(page * limit, limit);
    }

    @Override
    public List<String> getBlogTagsByBlogId(Integer blogId) {
        List<BlogTag> blogTags = blogTagMapper.selectBlogTagsByBid(blogId);
        List<String> tags = new ArrayList<>();
        for (BlogTag blogTag : blogTags) {
            tags.add(blogTag.getTag());
        }
        return tags;
    }

    @Override
    @Transactional
    public void deleteBlogByBlogId(Integer blogId) {
        blogTagMapper.deleteByBlogId(blogId);
        if (blogMapper.deleteByPrimaryKey(blogId) != 1) {
            throw new BizException(BizException.INTERNAL_ERROR, "数据库删除操作失败");
        }
    }
}
