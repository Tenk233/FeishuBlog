package com.feishu.blog.service;

import com.feishu.blog.model.User;

import java.util.List;

/**
 * 用户业务层接口
 */
public interface UserService {

    /**
     * 获取所有用户列表
     * @return 如果没有用户，返回空列表（而非 null）
     */
    List<User> getAllUsers();

    /**
     * 根据主键 ID 查询用户
     * @param id 用户 ID
     * @return 找到则返回对应 User，否则返回 null 或抛出自定义异常
     */
    User getUserById(int id);

    /**
     * 创建新用户
     * @param user 待创建的用户对象，ID 字段可不填
     * @return 创建成功后，包含数据库生成 ID 的 User 对象
     */
    User createUser(User user);

    /**
     * 更新已有用户信息
     * @param user 必须包含 ID
     * @return 更新后最新的 User 对象
     */
    User updateUser(User user);

    /**
     * 根据 ID 删除用户
     * @param id 要删除的用户 ID
     * @return 删除成功返回 true，否则 false
     */
    boolean deleteUserById(int id);

    // ……根据业务还可以加分页查询、模糊搜索、批量操作等方法……

    /**
     * 注册用户，先查看查看用户是否已存在
     * @param user 用户
     * @return 若注册成功，返回true
     */
    boolean register(User user);
}
