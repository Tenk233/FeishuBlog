package com.feishu.blog.controller;

import com.feishu.blog.dto.BlogCreateDTO;
import com.feishu.blog.dto.BlogModifyDTO;
import com.feishu.blog.dto.GetBlogListDTO;
import com.feishu.blog.entity.Blog;
import com.feishu.blog.entity.Result;
import com.feishu.blog.entity.User;
import com.feishu.blog.service.BlogService;
import com.feishu.blog.service.UserService;
import com.feishu.blog.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Result<?> create(@RequestBody @Valid BlogCreateDTO dto,
                            HttpServletRequest req) {
        Blog blog = new Blog();
        blog.setTitle(dto.getTitle());
        blog.setContent(dto.getContent());
        blog.setAuthorId((Integer) req.getAttribute(JwtUtil.ITEM_ID));
        blog.setCoverImageUri(dto.getCoverImageUri());
        blog.setStatus(dto.getStatus());

        Blog blogCreated = blogService.createBlog(blog, dto.getTags());

        return Result.success(blogCreated);
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody @Valid BlogModifyDTO dto,
                            HttpServletRequest req) {
        if (dto.getId() == null) {
            return Result.errorClientOperation("文章ID不能省略");
        }

        Blog blogToUpdate = blogService.getBlogById(dto.getId());
        if (blogToUpdate == null ||
            !blogToUpdate.getAuthorId().equals(req.getAttribute(JwtUtil.ITEM_ID))) {
            return Result.errorClientOperation("无法修改其他人的文章内容");
        }

        Blog blog = new Blog();
        blog.setId(dto.getId());
        blog.setTitle(dto.getTitle());
        blog.setContent(dto.getContent());
        blog.setCoverImageUri(dto.getCoverImageUri());
        blog.setStatus(dto.getStatus());

        return Result.success(blogService.updateBlog(blog, dto.getTags()));
    }

    @DeleteMapping("/delete/{blogId}")
    public Result<?> deleteBlog(@PathVariable Integer blogId,
                                HttpServletRequest req) {
        Blog blog = blogService.getBlogById(blogId);
        if (blog == null) {
            return Result.errorResourceNotFound("要删除的博客不存在");
        }
        User author = userService.getUserById(blog.getAuthorId());
        User userLogin = userService.getUserById((Integer)req.getAttribute(JwtUtil.ITEM_ID));

        /* 管理员能删除普通用户的文章，用户也能删除自己的文章 */
        if (author.getId().equals(userLogin.getId()) ||
            userLogin.getRole() == User.ROLE_ADMIN && author.getRole() == User.ROLE_USER) {
            blogService.deleteBlogByBlogId(blogId);
            return Result.success();
        }
        return Result.errorClientOperation("无法删除该文章");
    }

}
