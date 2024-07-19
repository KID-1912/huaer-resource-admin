package com.huaer.resource.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huaer.resource.admin.entity.User;
import com.huaer.resource.admin.enums.StatusEnum;
import com.huaer.resource.admin.exception.ServiceException;
import com.huaer.resource.admin.mapper.UserMapper;
import com.huaer.resource.admin.service.UserService;
import com.huaer.resource.admin.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 注册
    @Override
    public User register(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username");
        queryWrapper.eq("username", username);
        User user = this.getOne(queryWrapper);
        // 如果用户名已存在,抛出 USER_ALREADY_EXIST
        if (user != null) {
            throw new ServiceException(StatusEnum.USER_ALREADY_EXIST);
        }
        user = new User();
        user.setUsername(username);
        user.setPassword(MD5Util.encrypt(password));
        user.setCreatedAt(System.currentTimeMillis());
        this.save(user);
        return user;
    }

    // 登录
    @Override
    public String signin(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = this.getOne(queryWrapper);
        // 如果用户不存在，抛出 Login_ERROR
        if (user == null) {
            throw new ServiceException(StatusEnum.Login_ERROR);
        }
        // 如果账号密码错误，抛出 Login_ERROR
        if (!Objects.equals(user.getPassword(), MD5Util.encrypt(password))) {
            throw new ServiceException(StatusEnum.Login_ERROR);
        }
        return "这是token";
    }
}

