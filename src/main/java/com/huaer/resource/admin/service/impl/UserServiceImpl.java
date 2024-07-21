package com.huaer.resource.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huaer.resource.admin.dto.ResultResponse;
import com.huaer.resource.admin.entity.User;
import com.huaer.resource.admin.enums.StatusEnum;
import com.huaer.resource.admin.exception.ServiceException;
import com.huaer.resource.admin.mapper.UserMapper;
import com.huaer.resource.admin.service.UserService;
import com.huaer.resource.admin.util.MD5Util;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
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
        // 如果用户名已存在,抛出 USER_NOT_FOUND
        if (user != null) {
            throw new ServiceException(StatusEnum.USER_NOT_FOUND);
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
        // 如果用户不存在，抛出 LOGIN_ERROR
        if (user == null) {
            throw new ServiceException(StatusEnum.LOGIN_ERROR);
        }
        // 如果账号密码错误，抛出 LOGIN_ERROR
        if (!Objects.equals(user.getPassword(), MD5Util.encrypt(password))) {
            throw new ServiceException(StatusEnum.LOGIN_ERROR);
        }
        // 认证方法
//        // 1. 创建usernameAuthenticationToken
//        UsrernamePasswordAuthenticationToken usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(username, password);
//        // 2. 认证
//        Authentication authentication = this.authenticationManager.authenticate(usernamePasswordAuthentication);
//        // 3. 保存认证信息
//        SecurityConextHolder.getContext().setAuthentication(anthentication);
//        // 4. 生成自定义token
//        UserDetail userDetail = (Userdetail) authentication.getPrincipal();
//        AccessToken accessToken = jwtProvider.createToken((UserDetails) authentication.getPrincipal());
//        // 5. 放入缓存
//        caffeineCache.put(CacheName.USER, userDetail.getUsername(), userDetail);
//        return ResultResponse.success(accessToken);
        return "这是token";
    }
}

