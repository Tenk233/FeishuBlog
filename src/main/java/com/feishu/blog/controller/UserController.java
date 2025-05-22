package com.feishu.blog.controller;

import com.feishu.blog.dto.GetUserListDTO;
import com.feishu.blog.dto.ModifyUserDTO;
import com.feishu.blog.entity.Result;
import com.feishu.blog.entity.User;
import com.feishu.blog.service.JwtBlackListService;
import com.feishu.blog.service.UserService;
import com.feishu.blog.util.JwtUtil;
import com.feishu.blog.vo.UserInfoVO;
import io.jsonwebtoken.Jwt;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/4/25
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private JwtBlackListService  jwtBlackListService;

    @GetMapping("/list")
    public Result<?> listUsers(
            @Valid                           // 启用 Bean Validation
            GetUserListDTO query             // Spring 自动把 ?keyWord=...&orderBy=... 映射到 DTO
    ) {
        // 此时 query.getPage()/getLimit()/getOrderBy()/getAsc() 都已经被赋值并校验过
        List<User> users = userService.getAllUsersPaged(query);

        List<UserInfoVO> userInfoVOS = new ArrayList<>();
        for (User user : users) {
            UserInfoVO vo = new UserInfoVO(user, jwtBlackListService.isUserLogin(user.getId()));
            userInfoVOS.add(vo);
        }

        return Result.success(userInfoVOS);
    }

    @DeleteMapping("/delete/{userId}")
    public Result<?> deleteUser(@PathVariable Integer userId, HttpServletRequest req)
    {
        Integer userLoginId = (Integer) req.getAttribute(JwtUtil.ITEM_ID);
        if (userLoginId == null) {
            return Result.errorClientOperation("JwtInterceptor设置用户ID失败");
        }

        // 查看当前登录用户是否为管理员
        User userLogin = userService.getUserById(userLoginId);
        if (userLogin == null || userLogin.getRole() != User.ROLE_ADMIN) {
            return Result.errorClientOperation("当前用户不属于管理员，无法删除");
        }

        // 查看要删除的用户是否存在
        User userToDelete = userService.getUserById(userId);
        if (userToDelete == null) {
            return Result.errorResourceNotFound("要删除的用户不存在");
        }

        // 检查要删除的用户身份
        if (userToDelete.getRole() == User.ROLE_ADMIN) {
            return Result.errorClientOperation("无法删除管理员用户");
        }

        // 删除用户
        userService.deleteUserById(userId);

        return Result.success();
    }

    @GetMapping("/info/{userId}")
    public Result<?> getUserInfo(@PathVariable Integer userId, HttpServletRequest req)
    {
        User userLogin = userService.getUserById((Integer) req.getAttribute(JwtUtil.ITEM_ID));
        User userSelect = userService.getUserById(userId);
        if (userLogin == null || userSelect == null) {
            return Result.errorResourceNotFound("用户不存在");
        }

        if (userLogin.getRole() == User.ROLE_ADMIN || userSelect.getId().equals(userLogin.getId())) {
            /* 设置前端能看到的用户信息 */
            userSelect.setPasswordHash(null);
            userSelect.setSalt(null);
            userSelect.setIsBlocked(null);

            UserInfoVO vo = new UserInfoVO(userSelect,  jwtBlackListService.isUserLogin(userLogin.getId()));

            return Result.success(vo);
        }

        return Result.errorClientOperation("没有权限访问该用户");
    }

    @GetMapping("/logout")
    public Result<?> logout(HttpServletRequest req) {
        jwtBlackListService.userLogout((Integer) req.getAttribute(JwtUtil.ITEM_ID));
        return Result.success();
    }

    @PostMapping("/block")
    public Result<?> block(@RequestBody @Valid ModifyUserDTO dto,
                           HttpServletRequest req) {

        if (dto.getId() == null || dto.getIsBlock() == null) {
            return Result.errorClientOperation("id和is_block不能为空");
        }

        Integer userLoginId = (Integer) req.getAttribute(JwtUtil.ITEM_ID);
        // 查看当前登录用户是否为管理员
        User userLogin = userService.getUserById(userLoginId);
        if (userLogin == null || userLogin.getRole() != User.ROLE_ADMIN) {
            return Result.errorClientOperation("当前用户不属于管理员，无法封禁/解封用户");
        }

        // 查看要设置的用户是否存在
        User userToSet = userService.getUserById(dto.getId());
        if (userToSet == null) {
            return Result.errorResourceNotFound("要设置的用户不存在");
        }

        if (userToSet.getRole() == User.ROLE_ADMIN) {
            return Result.errorClientOperation("无法设置管理员用户");
        }

        if (dto.getIsBlock() == true) {
            jwtBlackListService.addUserToBlacklist(dto.getId());
            userService.updateUserIsBlocked(dto.getId(), true);
        } else {
            userService.updateUserIsBlocked(dto.getId(), false);
        }

        return Result.success();
    }

    @GetMapping("/check_email/{email}")
    public Result<?> checkEmail(@PathVariable String email, HttpServletRequest req) {
        if (userService.getUserByEmail(email) == null) {
            return Result.success();
        }
        return Result.errorClientOperation("邮箱已占用");
    }

    @GetMapping("/check_username/{username}")
    public Result<?> checkUsername(@PathVariable String username, HttpServletRequest req) {
        if (userService.getUserByUsername(username) == null) {
            return Result.success();
        }
        return Result.errorClientOperation("用户名已占用");
    }
}
