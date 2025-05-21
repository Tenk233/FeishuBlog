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
 */
@RestController
@RequestMapping("/api/blog")
public class BlogController {
    @Resource
    private BlogService blogService;


    @GetMapping("/info/{blogId}")
    public Result<?> getBlogInfo(@PathVariable Integer blogId) {
        return Result.success(blogService.getBlogById(blogId));
    }

    @GetMapping("/list")
    public Result<?> listBlogPaged(@Valid GetBlogListDTO query) {
        // TODO 根据查询条件过滤结果
        return Result.success();
    }
}
