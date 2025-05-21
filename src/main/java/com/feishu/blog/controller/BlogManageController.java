package com.feishu.blog.controller;

import com.feishu.blog.dto.BlogCreateDTO;
import com.feishu.blog.dto.GetBlogListDTO;
import com.feishu.blog.entity.Blog;
import com.feishu.blog.entity.Result;
import com.feishu.blog.service.BlogService;
import com.feishu.blog.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/21
 */
@RestController
@RequestMapping("/api/blog_m")
public class BlogManageController {
    @Resource
    private BlogService blogService;

    @PostMapping("/create")
    public Result<?> create(@RequestBody @Valid BlogCreateDTO dto,
                            HttpServletRequest req) {
        Blog blog = new Blog();
        blog.setTitle(dto.getTitle());
        blog.setContent(dto.getContent());
        blog.setAuthorId((Integer) req.getAttribute(JwtUtil.ITEM_ID));
        blog.setCoverImageUri(dto.getCoverImageUri());
        blog.setIsDraft(dto.getIsDraft());

        Blog blogCreated = blogService.createBlog(blog);
        return Result.success(blogCreated);
    }
}
