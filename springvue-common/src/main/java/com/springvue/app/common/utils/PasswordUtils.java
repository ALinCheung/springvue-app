package com.springvue.app.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * 校验密码复杂度
 */
@Slf4j
public class PasswordUtils {

    /**
     * 数字
     */
    public static final String REG_NUMBER = ".*\\d+.*";

    /**
     * 小写字母
     */
    public static final String REG_UPPERCASE = ".*[A-Z]+.*";

    /**
     * 大写字母
     */
    public static final String REG_LOWERCASE = ".*[a-z]+.*";

    /**
     * 特殊符号
     */
    public static final String REG_SYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*";

    /**
     * 加密盐与密码分隔符
     */
    public static final String split = ":";

    /**
     * 数字、大小写字母、特殊字符至少包含3种的8位以上密码
     */
    public static boolean checkRule(String password) {
        //密码为空或者长度小于8位则返回false
        if (password == null || password.length() < 8) return false;
        int i = 0;
        if (password.matches(REG_NUMBER)) i++;
        if (password.matches(REG_LOWERCASE)) i++;
        if (password.matches(REG_UPPERCASE)) i++;
        if (password.matches(REG_SYMBOL)) i++;

        if (i < 3) return false;

        return true;
    }

    /**
     * sha1加密
     */
    public String encode(CharSequence rawPassword) {
        byte[] salt = DigestsUtils.generateSalt(8);
        byte[] hashPassword = DigestsUtils.sha1(rawPassword.toString().getBytes(), salt, 1024);
        String encodedPassword = Hex.encodeHexString(salt) + split + Hex.encodeHexString(hashPassword);
        return encodedPassword;
    }

    public static String encode(String rawPassword, String salt) throws DecoderException {
        byte[] hashPassword = DigestsUtils.sha1(rawPassword.toString().getBytes(), Hex.decodeHex(salt.toCharArray()), 1024);
        String encodedPassword = Hex.encodeHexString(hashPassword);
        return encodedPassword;
    }

    /**
     * 加盐密码匹配
     */
    public boolean matches(CharSequence rawPassword, String salt, String encodedPassword) {
        try {
            String rawEncodePassword = encode(rawPassword.toString(), salt);
            return rawEncodePassword.equals(encodedPassword);
        } catch (Exception e) {
            log.error("encode error[{}]", e, e);
        }
        return false;
    }

}
