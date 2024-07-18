package com.huaer.resource.admin.web;

import com.huaer.resource.admin.dto.ResultResponse;
import com.huaer.resource.admin.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RestController
@Validated
public class UserController {
    @PostMapping("/signin")
    public ResultResponse<Map<String, Object>> signin(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") @Size(min = 6, message = "密码长度不能少于6位") String password
    ) {
        String token = String.valueOf(System.currentTimeMillis());
        return ResultResponse.success(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResultResponse<Void> register(){
        
    }
}
