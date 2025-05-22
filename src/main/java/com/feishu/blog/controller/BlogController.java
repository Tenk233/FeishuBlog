package com.feishu.blog.controller;

import com.feishu.blog.dto.BlogCreateDTO;
import com.feishu.blog.dto.GetBlogListDTO;
import com.feishu.blog.entity.Blog;
import com.feishu.blog.entity.BlogTag;
import com.feishu.blog.entity.Result;
import com.feishu.blog.entity.User;
import com.feishu.blog.mapper.BlogTagMapper;
import com.feishu.blog.service.BlogService;
import com.feishu.blog.service.UserService;
import com.feishu.blog.util.JwtUtil;
import com.feishu.blog.vo.BlogInfoVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    @Resource
    private UserService userService;

    @GetMapping("/info/{blogId}")
    public Result<?> getBlogInfo(@PathVariable Integer blogId) {
        Blog blogById = blogService.getBlogById(blogId);
        if (blogById == null) {
            return Result.errorResourceNotFound("文章不存在");
        }
        User user = userService.getUserById(blogById.getAuthorId());

        BlogInfoVO vo = new BlogInfoVO(blogById, user.getUsername(), blogService.getBlogTagsByBlogId(blogId));

        return Result.success(vo);
    }

    @GetMapping("/list")
    public Result<?> listBlogPaged(@Valid GetBlogListDTO query) {
        // TODO 根据查询条件过滤结果
        List<Blog> blogs = blogService.getAllBlogsPaged(query);
        List<BlogInfoVO> vos = new ArrayList<>();
        for (Blog blog : blogs) {
            User user = userService.getUserById(blog.getAuthorId());
            vos.add(new BlogInfoVO(blog, user.getUsername(), blogService.getBlogTagsByBlogId(blog.getId())));
        }
        return Result.success(vos);
    }
}
