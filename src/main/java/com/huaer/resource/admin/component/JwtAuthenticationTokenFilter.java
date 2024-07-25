package com.huaer.resource.admin.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huaer.resource.admin.entity.User;
import com.huaer.resource.admin.entity.UserDetail;
import com.huaer.resource.admin.properties.JwtProperties;
import com.huaer.resource.admin.provider.JwtProvider;
import com.huaer.resource.admin.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    UserService userService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain chain) throws ServletException, IOException {
        String authToken = jwtProvider.getToken(request);
        if(authToken != null && !authToken.isEmpty() && authToken.startsWith(jwtProperties.getTokenPrefix())){
            authToken = authToken.substring(jwtProperties.getTokenPrefix().length());
            String loginAccount = jwtProvider.getSubjectFromToken(authToken); // 解析Token，若token过期或无效，将返回null
            // loginAccount存在且无authentication验证信息
            if(loginAccount != null && !loginAccount.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 查询缓存中用户userDetail
                // UserDetail userDetail = caffeineCache.get(CacheName.USER, loginAccount, UserDetail.class);
                // 由于目前未支持缓存，改查询数据库
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("username", loginAccount);
                User user = userService.getOne(queryWrapper);
                if (user != null) {
                    UserDetail userDetails = new UserDetail();
                    userDetails.setUser(user);
                    // 创建已认证UsernamePasswordAuthenticationToken
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication); // 供后续Filter使用
                }
            }
        }
        chain.doFilter(request, response);
    }
}
