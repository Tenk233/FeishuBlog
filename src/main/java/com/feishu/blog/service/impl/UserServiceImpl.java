package com.feishu.blog.service.impl;

import com.feishu.blog.exception.BizException;
import com.feishu.blog.mapper.UserMapper;
import com.feishu.blog.model.User;
import com.feishu.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service (value = "userServiceImpl")             // 声明为 Bean，默认类名 userServiceImpl
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    @Override
    public User getUserById(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional  // 涉及写操作时加事务
    public User createUser(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        userMapper.updateByPrimaryKeySelective(user);
        return userMapper.selectByPrimaryKey(user.getId());
    }

    @Transactional
    @Override
    public boolean deleteUserById(int id) {
        return userMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean register(User user) {
        /* 查看用户名是否存在 */
        if (userMapper.selectUserByUsername(user.getUsername()) != null) {
            log.info("User `{}` existed!", user.getUsername());
            throw new BizException(BizException.REGISTER_EXISTED_USER, "User existed!");
        }
        /* 查看邮箱是否存在 */
        if (userMapper.selectUserByEmail(user.getEmail()) != null) {
            log.info("User with email `{}` existed!", user.getEmail());
            throw new BizException(BizException.REGISTER_EXISTED_USER, "User existed!");
        }

        return false;
    }
}
