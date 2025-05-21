package com.feishu.blog.mapper;

import com.feishu.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> selectAllUsersPaged(@Param("offset") int offset,
                                   @Param("pageSize") int pageSize);

    User selectUserByUsername(@Param("username") String username);

    User selectUserByEmail(@Param("email") String email);

    int updateUserIsBlockByPrimaryKey(Integer id, Boolean isBlocked);
}