package com.sipc.mmtbackend.utils.CheckroleBUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用户密码工具类
 * @author DoudiNCer
 */
@Slf4j
public class PasswordUtil {
    private static final String salt = "6s646yR0jWfefqrmNv3LjhAJsSkd2vQV07jqSIJGtjLZgSasJ7X3AC";

    /**
     * 计算密码摘要
     * @param rawPass 原始密码
     * @return 处理后的密码
     */
    public static String hashPassword(String rawPass){
        String result = null;
        try {
            MessageDigest message512Digest = MessageDigest.getInstance("SHA-512");
            String firstResult = Base64.encodeBase64String(message512Digest.digest(rawPass.getBytes()));
            String passwd1 = firstResult.concat(salt);
            MessageDigest message256Digest = MessageDigest.getInstance("SHA-256");
            result = Base64.encodeBase64String(message256Digest.digest(passwd1.getBytes()));
        }
        catch (NoSuchAlgorithmException e) {
            log.warn("Get Algorithm Error When Process Password: " + e.getMessage());
        }
        return result;
    }

    /**
     * 密码校验
     * @param rawPasswd 用户提供的密码
     * @param hashPasswd 数据库中的密码摘要
     * @return 密码是否正确
     */
    public static boolean testPasswd(String rawPasswd, String hashPasswd){
        return hashPasswd.equals(hashPassword(rawPasswd));
    }
}
