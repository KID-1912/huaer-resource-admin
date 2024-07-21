package com.huaer.resource.admin.provider;

import com.huaer.resource.admin.entity.UserDetail;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Signature;
import java.util.Date;

@Component
public class JwtProvider {
    @Autowired
    private JwtProperties jwtProperties;

    // 请求中获取token
    public String getToken(HttpServletRequest request) {
        return request.getHeader(jwtProperties.getRequestHeader());
    }

    // 根据用户信息生成token
    public AccessToken createToken(UserDetails userDetails){
        return createToken((userDetails.getUsername));
    }

    // 生成token
    // 参数是放入token中的字符串
    public AccessToken createToken(String subject) {
        // 当前时间
        final Date now = new Date();
        // 过期时间
        final Date expirationDate = new Date(now.getTime() + jwtProperties.getExpirationTime() * 1000)
        String token = jwtProperties.getTokenPrefix() + Jwt.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getApiSecretKey())
                .compact();
        return AccessToken.builder().loginAccount(subject).token(token).expirationTime(expirationDate).build();

    }
}
