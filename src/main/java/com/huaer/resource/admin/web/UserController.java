package com.huaer.resource.admin.web;

import com.huaer.resource.admin.bo.AccessToken;
import com.huaer.resource.admin.dto.ResultResponse;
import com.huaer.resource.admin.entity.User;
import com.huaer.resource.admin.provider.JwtProvider;
import com.huaer.resource.admin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class UserController {
    @Autowired
    private  UserService userService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/signin")
    public ResultResponse<String> doSignin(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") @Length(min = 6, max = 20, message = "密码长度不合法") String password) {
        String token = userService.signin(username, password);
        return ResultResponse.success(token);
    }

    @PostMapping("/register")
    public ResultResponse<Void> doRegister(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") @Length(min = 6, max = 20, message = "密码长度不合法") String password
    ) {
        User user = userService.register(username, password);
        return ResultResponse.success(null);
    }

    @PostMapping("/logout")
    public ResultResponse<Void> doLogout(){
        userService.logout();
        return ResultResponse.success(null);
    }

    @PostMapping("/refresh-token")
    public ResultResponse<String> doRefreshToken(HttpServletRequest request){
        String oldToken = jwtProvider.getToken(request);
        AccessToken accessToken = userService.refreshToken(oldToken);
        return ResultResponse.success(accessToken.getToken());
    }

    @GetMapping("/admin/user/list")
    public ResultResponse<List<String>> doListUsername(){
        return ResultResponse.success(userService.listUsername());
    }
}
