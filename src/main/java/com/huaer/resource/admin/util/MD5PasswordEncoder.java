package com.huaer.resource.admin.util;

import org.springframework.security.crypto.password.PasswordEncoder;

public class MD5PasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return MD5Util.encrypt((String) rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return MD5Util.encrypt((String) rawPassword).equals(encodedPassword);
    }
}
