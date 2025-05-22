package com.feishu.blog.service;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/22
 */
public interface LoginAttemptService {

    /**
     * 记录登录失败
     * @param username 用户名
     * @return 是否需要滑块验证
     */
    public boolean loginFailed(String username);

    /**
     * 登录成功时清除错误记录
     * @param username 用户名
     */
    public void loginSucceeded(String username);

    /**
     * 检查是否需要滑块验证
     * @param username 用户名
     * @return 是否需要滑块验证
     */
    public boolean isBlocked(String username);
}
