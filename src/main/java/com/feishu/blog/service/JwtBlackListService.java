package com.feishu.blog.service;

/**
 * description:
 *
 * @author Tenk
 * @date 2025/5/20
 */
public interface JwtBlackListService {

    /**
     * 将用户和refreshToken关联起来，实现只能一端登录
     * @param userId 用户ID
     * @param refreshToken refreshToken
     */
    void addRefreshToken(Integer userId, String refreshToken);
    /**
     * 将refreshToken拉黑
     * @param refreshToken 用户的refreshToken
     */
    void addRefreshTokenToBlacklist(String refreshToken);

    /**
     * 查看用户是否登录
     * @param userId 用户ID
     * @return 如果已登录返回true，否则返回false
     */
    Boolean isUserLogin(Integer userId);

    /**
     * 用户登出
     */
    void userLogout(Integer userId);

    /**
     * 将用户拉黑，并将它的refreshToken拉黑
     * @param userId 用户ID
     */
    void addUserToBlacklist(Integer userId);

    Integer getLatestAccessTokenVersion(Integer userId);

    boolean checkAccessTokenVersion(Integer userId, Integer version);
}
