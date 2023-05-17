package com.sipc.mmtbackend.utils.lark;

import lombok.Data;
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
public class LarkRobot {

    private Boolean dev;

    private String url;

//    private static Entry entry;

//
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

        String urlStr = request.getRequestURL().toString();

//        @Data
//        class Request {
//            private String uri;
//            private String method;
//            private Object parameter;
//        }

        RequestData webRequestData = new RequestData();
        webRequestData.setMethod(request.getMethod());

//        webRequest.setParameter(getParameter(method, joinPoint.getArgs()));
        webRequestData.setParameter(getParameter(joinPoint));
        webRequestData.setUri(request.getRequestURI());

        Entry entry = new Entry("", urlStr, webRequestData.toString(), exception.getMessage() + "\\n" + Arrays.toString(exception.getStackTrace()));


        ResponseEntity<LarkResponse> responseResponseEntity = restTemplate.postForEntity(url, entry, LarkResponse.class);

        LarkResponse body = responseResponseEntity.getBody();

        if (body != null) {
            if (body.getCode() != 0) {
                System.out.println("no");
                System.out.println(body);
            } else {
                System.out.println("ok");
            }
        } else {
            System.out.println("no body为空");
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
