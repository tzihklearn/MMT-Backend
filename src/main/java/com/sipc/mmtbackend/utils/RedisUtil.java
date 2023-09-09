package com.sipc.mmtbackend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类，使用RedisTemplate操作redis
 *
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
     *
     * @param key   对应的redis的键
     * @param value 对应的redis的值
     * @return 返回是否成功设置redis缓存, 成功为true, 失败为false
     */
    public boolean setString(String key, Object value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    /**
     * 设置redis的string类型缓存，同时设置过期时间
     *
     * @param key        对应的redis的键
     * @param value      对应的redis的值
     * @param expireTime 过期时间数值
     * @param timeUnit   过期时间单位
     * @return 返回是否成功设置redis缓存, 成功为true, 失败为false
     */
    public boolean setString(String key, Object value, long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取redis的string类型对应键的缓存
     *
     * @param key    对应的redis的键
     * @param tClass 获取值的类型
     * @param <T>    对应的值的类型
     * @return 返回对应的已经强转后的对象
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

    /**
     * 删除redis的对应键的缓存
     *
     * @param key 要删除的键
     * @return 返回是否成功删除redis缓存, 成功为true, 失败为false
     */
    public boolean delete(String key) {
        boolean result = false;
        try {
            Boolean delete = redisTemplate.delete(key);
            result = Boolean.TRUE.equals(delete);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Double zSetScore(String key, Integer value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /*
      c
     */

    /**
     * 取出缓存
     * @param key 键
     * @return 缓存
     */
    public Object get(final String key){
        Object result;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);

        return result;
    }

    /**
     * 设置缓存过期时间
     * @param key 键
     * @param value 值
     * @param expireTime 过期时间
     * @param timeUnit timeUnit
     * @return boolean
     */
    public boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit){
        boolean result = false;
        try {
            ValueOperations<Serializable,Object> operations = redisTemplate.opsForValue();
            operations.set(key,value);
            redisTemplate.expire(key,expireTime,timeUnit);
            result = true;
        } catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 判断缓存是否存在
     * @param key 键
     * @return boolean
     */
    public boolean exists(final String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     * @param key 键
     */
    public void remove(final String key){
        if (exists(key)){
            redisTemplate.delete(key);
        }
    }


}
