package com.huaer.resource.admin.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String encrypt(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes());
            StringBuilder sb = new StringBuilder();
            for(byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
}
