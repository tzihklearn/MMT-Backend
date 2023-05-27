package com.sipc.mmtbackend.config;

import com.sipc.mmtbackend.interceptor.BCheckRoleHandlerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.24
 */
@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WebMvcConfig implements WebMvcConfigurer {

    private final BCheckRoleHandlerInterceptor handlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor)
                .addPathPatterns("/b/**")
                .excludePathPatterns("/b/user/loginp")
                .excludePathPatterns("/b/user/reg")
                .excludePathPatterns("/b/user/orgs");

    }
}
