package com.huaer.resource.admin.web;

import com.huaer.resource.admin.dto.ResultResponse;
import com.huaer.resource.admin.entity.User;
import com.huaer.resource.admin.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RestController
@Validated
public class UserController {
    @Autowired
    UserService userService;

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
}
