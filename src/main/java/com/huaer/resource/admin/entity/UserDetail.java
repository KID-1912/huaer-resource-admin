package com.huaer.resource.admin.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class UserDetail implements UserDetails {
    private User user;
    // private List<RoleInfo> roleInfoList;
    // private Collection<? extends GrantedAuthority> grantedAuthorities;
    // private List<String> roles;

    public Long getUserId() {
        return this.user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 当前没有权限功能，返回空的权限列表
        return Collections.emptyList();
    }

    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    //     if (grantedAuthorities != null) return this.grantedAuthorities;
    //     List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
    //     List<String> authorities = new ArrayList<>();
    //     roleInfoList.forEach(role -> {
    //         authorities.add(role.getRoleCode());
    //         grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
    //     });
    //     this.grantedAuthorities = grantedAuthorities;
    //     this.roles = authorities;
    //     return this.grantedAuthorities;
    // }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    /**
     * 账户是否没过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否没被锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 账户凭据是否没过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否启用
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
