package com.sipc.mmtbackend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * ICode（社团邀请码）操作工具类
 *
 * @author tzih
 * @version v1.0
 * @since 2023.05.20
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Component
public class ICodeUtil {

    /**
     * ICode的在redis的key的后缀
     */
    private final static String ICodeKeyPrefix = "ICode:";
    private final RedisUtil redisUtil;

    /**
     * 私有方法，生成对应社团的邀请码
     *
     * @param length 生成的社团邀请码长度
     * @return 社团邀请码
     */
    private String generateRandomString(int length) {
        /*
          原始字符序列，要生成的字符串从中随机选出
         */
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        /*
          随机选出字符串
         */
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    /**
     * 调用generateRandomString方法生成对应社团的邀请码，同时将其缓存到redis中，时间限制10min
     *
     * @param organizationId 社团组织id
     * @param length         要生成的社团邀请码的长度
     * @return 返回生成好的社团邀请码
     */
    public String setICodeRedis(Integer organizationId, int length) {

        boolean flag = false;

        String ICode = null;

        /*
          生成社团邀请码，并确保其唯一
         */
        while (!flag) {
            ICode = generateRandomString(length);

            Integer value = redisUtil.getString(ICodeKeyPrefix + ICode, Integer.class);

            if (value == null) {
                flag = true;
            }

        }

        //将生成的社团邀请码放入redis缓存
        boolean isSet = redisUtil.setString(ICodeKeyPrefix + ICode, organizationId,  10, TimeUnit.MINUTES);

        if (isSet) {
            return ICode;
        } else {
            return null;
        }

    }

    /**
     * 验证社团邀请码
     *
     * @param ICode 待验证的社团邀请码
     * @return 返回验证成功后邀请码对应的社团组织id, 若失败返回null
     */
    public Integer verifyICode(String ICode) {
        return redisUtil.getString(ICodeKeyPrefix + ICode, Integer.class);
    }

}
