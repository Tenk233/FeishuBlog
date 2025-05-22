package com.feishu.blog.service.impl;

import com.feishu.blog.dto.GetUserListDTO;
import com.feishu.blog.exception.BizException;
import com.feishu.blog.mapper.UserMapper;
import com.feishu.blog.entity.User;
import com.feishu.blog.service.UserService;
import com.feishu.blog.util.RedisUtil;
import com.feishu.blog.util.SecUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service (value = "userServiceImpl")             // 声明为 Bean，默认类名 userServiceImpl
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RedisUtil redisUtil;

    @Override
    public User getUserById(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
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
        /* 要删除用户，先删除那些与用户相关的文件和数据库条目 */
        deleteUserByIdInternal(id);
        /* 再从user表中删除该用户 */
        if (userMapper.deleteByPrimaryKey(id) != 1)
        {
            throw new BizException(BizException.INTERNAL_ERROR, "删除用户失败");
        }
        return true;
    }

    /**
     * 删除用户的博客和文件
     * */
    private void deleteUserByIdInternal(int id) {
        // TODO
    }

    @Transactional
    @Override
    public void register(User user) {
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

        /* 生成盐值和密码哈希 */
        user.setSalt(SecUtil.generateSalt());
        user.setPasswordHash(SecUtil.hashPassword(user.getPasswordHash(), user.getSalt()));

        /* 插入数据库 */
        if (userMapper.insertSelective(user) != 1)
        {
            throw new BizException(BizException.INTERNAL_ERROR, "用户注册失败");
        }
    }

    @Override
    public User authenticate(String username, String password) {
        User user = userMapper.selectUserByUsername(username);
        if (user == null) {
            return null;
        }

        if (!user.getPasswordHash().equals(SecUtil.hashPassword(password, user.getSalt()))) {
            return null;
        }

        return user;
    }

    @Override
    public List<User> getAllUsersPaged(GetUserListDTO dto) {
        /* 默认从第一页开始 */
        int page = 0;
        /* 默认每页显示20条记录 */
        int limit = 20;

        if (dto.getPage() != null) {
            /* 参数中的page是从1开始，所以要减一 */
            page = dto.getPage() - 1;
        }
        if (dto.getLimit() != null) {
            limit = dto.getLimit();
        }

        return userMapper.selectAllUsersPaged(page * limit, limit);
    }

    @Override
    @Transactional
    public Boolean updateUserIsBlocked(Integer id, Boolean isBlocked) {
        return userMapper.updateUserIsBlockByPrimaryKey(id, isBlocked) == 1;
    }

    @Override
    public Boolean checkUserLoginTimes(Integer id) {
        String key = SecUtil.generateUserLoginTimesKeyForRedis(id);
        List<Date> times = (List<Date>) redisUtil.get(key);
        if (times == null) {
            times = new ArrayList<>();
            times.add(new Date());
            return true;
        } else {
//            if ()
        }
        return null;
    }
}
