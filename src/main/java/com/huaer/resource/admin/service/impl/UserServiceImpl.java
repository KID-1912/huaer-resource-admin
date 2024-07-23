package com.huaer.resource.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huaer.resource.admin.bo.AccessToken;
import com.huaer.resource.admin.entity.User;
import com.huaer.resource.admin.enums.StatusEnum;
import com.huaer.resource.admin.exception.ServiceException;
import com.huaer.resource.admin.mapper.UserMapper;
import com.huaer.resource.admin.provider.JwtProvider;
import com.huaer.resource.admin.service.UserService;
import com.huaer.resource.admin.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

private AuthenticationManager authenticationManager;

@Autowired
public void setAuthenticationManager(@Lazy AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
}

@Autowired
JwtProvider jwtProvider;

    // 注册
    @Override
    public User register(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username");
        queryWrapper.eq("username", username);
        User user = this.getOne(queryWrapper);
        // 如果用户名已存在,抛出 USER_ALREADY_EXISTS
        if (user != null) {
            throw new ServiceException(StatusEnum.USER_ALREADY_EXISTS);
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
        // 认证方法
        // 1. 创建usernameAuthenticationToken
        UsernamePasswordAuthenticationToken usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(username, password);
        // 2. 认证
        Authentication authentication;
        try {
            authentication = this.authenticationManager.authenticate(usernamePasswordAuthentication);
        }catch(BadCredentialsException e ){
            throw new ServiceException(StatusEnum.LOGIN_ERROR);
        }
        // 3. 保存认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 4. 生成自定义token
        AccessToken accessToken = jwtProvider.createToken((UserDetails) authentication.getPrincipal());
        // 5. 放入缓存
        // UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        // caffeineCache.put(CacheName.USER, userDetail.getUsername(), userDetail);
        return accessToken.getToken();
    }
}

