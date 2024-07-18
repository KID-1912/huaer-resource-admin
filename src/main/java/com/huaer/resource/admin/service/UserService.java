package com.huaer.resource.admin.service;

import com.huaer.resource.admin.entity.User;
import com.huaer.resource.admin.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
    @Autowired
    UserMapper userMapper;

    // 注册
    public User register(String username, String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        // mybatis-plus mapper向数据库插入用户
        userMapper.insert(user);
        return user;
    }
}
