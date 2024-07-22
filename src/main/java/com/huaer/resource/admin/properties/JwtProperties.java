package com.huaer.resource.admin.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * 密钥
     */
    @Value("${jwt.apiSecretKey:JWT_SECRET_KEY}")
    private String apiSecretKey;

    /**
     * 过期时间-默认半个小时
     */
    @Value("${jwt.expirationTime:1800}")
    private Long expirationTime;

    /**
     * 默认存放token的请求头
     */
    @Value("${jwt.requestHeader:Authorization}")
    private String requestHeader;

    /**
     * 默认token前缀
     */
    @Value("${jwt.tokenPrefix:Bearer}")
    private String tokenPrefix;
}
