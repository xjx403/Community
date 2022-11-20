package com.coder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUD(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密
    // hello  -> 113rsadsadd1
    // hello + 3e4a8 ->aabcdscasad
    public static String md5(String key){
        if(StringUtils.isBlank(key)) return null;

        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
