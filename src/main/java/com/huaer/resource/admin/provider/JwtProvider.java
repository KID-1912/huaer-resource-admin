package com.huaer.resource.admin.provider;

import com.huaer.resource.admin.bo.AccessToken;
import com.huaer.resource.admin.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtProvider {
    final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JwtProperties jwtProperties;

    // 请求中获取token
    public String getToken(HttpServletRequest request) {
        return request.getHeader(jwtProperties.getRequestHeader());
    }

    // 根据用户信息生成token
    public AccessToken createToken(UserDetails userDetails) {
        return createToken((userDetails.getUsername()));
    }

    // 生成token
    // 参数是放入token中的字符串
    public AccessToken createToken(String subject) {
        // 当前时间
        final Date now = new Date();
        // 过期时间
        final Date expirationDate = new Date(now.getTime() + jwtProperties.getExpirationTime() * 1000);

        // jjwt
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getApiSecretKey().getBytes(StandardCharsets.UTF_8));
        String token = jwtProperties.getTokenPrefix() + Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return AccessToken.builder().loginAccount(subject).token(token).expirationTime(expirationDate).build();
    }

    // 验证token是否有效
    // 反解析token中的信息，与参数中的信息比较，再校验过期时间
    public boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject().equals(userDetails.getUsername()) && !isTokenExpired(claims);
    }

    // 从token解析出负载信息
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getApiSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("JWT反解析失败，token错误或已过期，token:{}", token);
        }
        return claims;
    }

    // 从token中获取主题
    public String getSubjectFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            return claims.getSubject();
        } else {
            return null;
        }
    }

    // 判断token是否过期
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // 刷新token
    // 过滤器会对请求进行验证，此处不用验证
    public AccessToken refreshToken(String oldToken) {
        String token = oldToken.substring(jwtProperties.getTokenPrefix().length());
        Claims claims = getClaimsFromToken(token);
        // 如果30分钟内刷新过，返回原token
        if (tokenRefreshJustBefore(claims)) {
            return AccessToken.builder().loginAccount(claims.getSubject()).token(oldToken).expirationTime(claims.getExpiration()).build();
        }else{
            return createToken(claims.getSubject());
        }
    }

    // 判断token在指定时间内是否刷新过
    private boolean tokenRefreshJustBefore(Claims claims){
        Date refreshDate = new Date();
        Calendar createCalendar = Calendar.getInstance();
        createCalendar.setTime(claims.getExpiration());
        createCalendar.add(Calendar.SECOND, (int) (-1 * jwtProperties.getExpirationTime()));
        // 刷新时间在创建时间的指定范围内
        return refreshDate.before(claims.getExpiration()) && refreshDate.after(createCalendar.getTime());
    }

}
