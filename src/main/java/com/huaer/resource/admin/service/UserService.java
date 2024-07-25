package com.huaer.resource.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huaer.resource.admin.bo.AccessToken;
import com.huaer.resource.admin.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    User register(String username, String password);

    String signin(String username, String password);

    AccessToken refreshToken(String token);

    void logout();

    List<String> listUsername();
}
