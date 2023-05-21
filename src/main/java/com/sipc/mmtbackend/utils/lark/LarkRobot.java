package com.sipc.mmtbackend.utils.lark;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * @author tzih
 * @version 1.0
 * @since 2023/4/10 14:57
 */
@ConfigurationProperties("lark-robot")
@Component
@Data
@Slf4j
public class LarkRobot {

    private Boolean dev;

    private String url;

    @Resource
    private RestTemplate restTemplate;

    public void send(JoinPoint joinPoint, Exception exception) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        //获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        //获取请求的url
        String urlStr = request.getRequestURL().toString();

        /*
          设置请求信息
         */
        RequestData webRequestData = new RequestData();
        webRequestData.setMethod(request.getMethod());

        webRequestData.setParameter(getParameter(joinPoint));
        webRequestData.setUri(request.getRequestURI());

        /*
          组装消息实体类
         */
        Entry entry = new Entry("", urlStr, webRequestData.toString(), exception.getMessage() + "\\n" + Arrays.toString(exception.getStackTrace()));

        //发送消息
        ResponseEntity<LarkResponse> responseResponseEntity = restTemplate.postForEntity(url, entry, LarkResponse.class);

        LarkResponse body = responseResponseEntity.getBody();

        if (body != null) {
            if (body.getCode() != 0) {
                log.warn("消息发送失败，返回结果：{}，消息体：{}", body, entry);
            }
        } else {
            log.warn("消息发送失败，返回结果为空，消息体：{}", entry);
        }

    }

    public static List<Object> getParameter(JoinPoint joinPoint) {
        //获取请求参数
        Object[] args = joinPoint.getArgs();
        //设置请求参数类
        List<Object> argList = new ArrayList<>();
        //获取请求参数
        Class declaringType = joinPoint.getSignature().getDeclaringType();
        int i = 0;
        for (TypeVariable typeParameter : declaringType.getTypeParameters()) {
            //获取@RequestBody注解的参数
            RequestBody annotation = typeParameter.getAnnotation(RequestBody.class);
            if (annotation != null) {
                argList.add(args[i]);
                ++i;
                continue;
            }

            //获取@RequestParam注解的参数
            RequestParam annotation1 = typeParameter.getAnnotation(RequestParam.class);
            if (annotation1 != null) {
                Map<String, Object> map = new HashMap<>();
                map.put(typeParameter.getName(), args[i]);
                argList.add(map);
            }
            ++i;
        }

        if (args.length != 0) {
            argList.add(args);
        }

        return argList;
    }

}
