package com.sipc.mmtbackend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JSON 序列化/反序列化相关工具类
 *
 * @author DoudiNCer
 */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Component
@Slf4j
public class JsonUtil {
    private final ObjectMapper mapper;
    /**
     * 反序列化 JSON 为 Java 对象
     *
     * @param json 要反序列化的 JSON 字符串
     * @param tClass 对象类型
     * @return 反序列化的对象，出现任何异会返回 null 并在日志中输出 warning 信息
     */
    public <T> T deserializationJson(String json, Class<T> tClass){
        T t;
        try {
            t = mapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            log.warn("JsonUtil: 反序列化 " + json + " 时抛出异常 JsonProcessingException ：" + e.getMessage());
            return null;
        }
        return t;
    }
}
