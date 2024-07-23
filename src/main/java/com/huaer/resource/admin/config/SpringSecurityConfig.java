package com.huaer.resource.admin.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huaer.resource.admin.entity.User;
import com.huaer.resource.admin.entity.UserDetail;
import com.huaer.resource.admin.service.UserService;
import com.huaer.resource.admin.util.MD5PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

//    private final UserDetailsServiceImpl userDetailsServiceImpl;
//
//    @Autowired
//    public SpringSecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl) {
//        this.userDetailsServiceImpl = userDetailsServiceImpl;
//    }

    // securityFilterChain 自定义访问控制
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // 放行所有OPTIONS请求
                                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                // 放行静态资源请求
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/",
                                        "/*.html",
                                        "/favicon.ico",
                                        "/**/*.html",
                                        "**/*.css",
                                        "/**/*.js"
                                ).permitAll()
                                // 放行登录/注册方法
                                .requestMatchers("/signin", "/register").permitAll()
                                // 对于获取token的rest api允许匿名访问
                                // .requestMatchers("/auth/**").permitAll()
                                // 除上面的其他所有请求全部需要鉴权认证
                                .anyRequest().authenticated()
                )
                // 打开Spring Security的跨域
                .cors()
                .and()
                // 使用JWT，则禁用 csrf
                .csrf().disable()
                // 基于token，不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 禁用缓存
        // .and()
        // .headers().cacheControl().and().and();
        return http.build();
    }

    // 加密器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MD5PasswordEncoder();
    }

    // 获取用户信息处理
    private static class CustomerUserDetailsService implements UserDetailsService {
        @Autowired
        private UserService userService;

        // @AutoWired
        // private RoleInfoService roleInfoService;
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            // 根据用户名验证用户
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在");
            }

            // 构建 UserDetail 对象
            UserDetail userDetail = new UserDetail();
            userDetail.setUser(user);
            // List<RoleInfo> roleInfoList = roleInfoService.listRoleByUserId(userInfo.getUserId());
            // userDetail.setRoleInfoList(roleInfoList);
            return userDetail;
        }
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }

    // 未登录异常处理
    // @Bean public RestfulAccessDeniedHandler restfulAccessDeniedHandler

    // 权限不足异常处理
    // @Bean public RestAuthenticationEntryPoint restAuthenticationEntryPoint

    // 自定义jwt过滤器
    // @Bean public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter

    @Bean
    public AuthenticationConfiguration authenticationConfiguration() {
        return new AuthenticationConfiguration();
    }

    // 使用自带的 authenticationManager 代办认证操作
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        AuthenticationConfiguration authenticationConfiguration = authenticationConfiguration();
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 访问控制的投票器，决定是否允许访问某个资源
    // @Bean public AccessDecisionVoter<FilterInvocation> accessDecisionProcessor

    // 决策管理组件
    // @Bean
    // public AccessDecisionManager accessDecisionManager() {
    //     // 构造一个新的AccessDecisionManager 放入两个投票器
    //     List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(new WebExpressionVoter(), accessDecisionProcessor());
    //     return new UnanimousBased(decisionVoters);
    // }
}
