package com.sipc.mmtbackend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类，使用RedisTemplate操作redis
 * @author tzih
 * @version v1.0
 * @since 2023.05.20
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Component
public class RedisUtil {

    private final RedisTemplate<Serializable, Object> redisTemplate;

    /**
     * 设置redis的string类型缓存，过期时间为永久a
     * @param key 对应的redis的键
     * @param value 对应的redis的值
     * @return 返回是否成功设置redis缓存,成功为true,失败为false
     */
    public boolean setString(String key, Object value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    /**
     * 设置redis的string类型缓存，同时设置过期时间
     * @param key 对应的redis的键
     * @param value 对应的redis的值
     * @param expireTime 过期时间数值
     * @param timeUnit 过期时间单位
     * @return 返回是否成功设置redis缓存,成功为true,失败为false
     */
    public boolean setString(String key, Object value, long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
            result = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取redis的string类型对应键的缓存
     * @param key 对应的redis的键
     * @param tClass 获取值的类型
     * @return 返回对应的已经强转后的对象
     * @param <T> 对应的值的类型
     */
    public <T> T getString(String key, Class<T> tClass) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        } else {
            if (value.getClass() != tClass) {
                return null;
            } else {
                return (T) value;
            }
        }

    }



}
