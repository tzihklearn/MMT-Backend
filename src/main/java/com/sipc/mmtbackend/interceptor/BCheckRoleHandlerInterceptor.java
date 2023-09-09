package com.sipc.mmtbackend.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.utils.CheckroleBUtil.CheckPermissionUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.CheckRoleUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.JWTUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.05.24
 */
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class BCheckRoleHandlerInterceptor implements HandlerInterceptor {

    private final CheckRoleUtil checkRoleUtil;

    private final JWTUtil jwtUtil;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {


        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
/*
          获取token,并且解析token信息
         */
            String token = request.getHeader("Authorization");
            if (token != null) {
            /*
                获取请求url，如果不存在则返回false,拒绝请求
            */
                String requestURI = request.getRequestURI();
                if (requestURI != null) {
                    BTokenSwapPo bTokenSwapPo = jwtUtil.verifyToken(token);
                    if (bTokenSwapPo != null) {
                    /*
                      校验B端用户权限
                     */

                        boolean isPermission = CheckPermissionUtil.checkBPermission(handlerMethod, bTokenSwapPo.getPermissionId());
                        //权限不符合，返回false
                        if (!isPermission) {
                            setResponse(response, CommonResult.userAuthError());
                            return false;
                        }

                        //将token解析出的用户信息放入本地线程变量
                        ThreadLocalContextUtil.setTHREAD_LOCAL_Context(bTokenSwapPo);
                        return true;

                    }
                }
            }

            setResponse(response, CommonResult.userAuthError());
        }

        return false;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {

        //删除本地线程变量中的值
        ThreadLocalContextUtil.remove();

    }

    private void setResponse(HttpServletResponse response, CommonResult<Objects> result) {

        try {
            //将Java对象（result）转换为JSON格式的字符串
            String json = new ObjectMapper().writeValueAsString(result);
            response.setContentType("application/json;charset=UTF-8");
            //获取HTTP响应的输出流，并将JSON字符串（json）写入输出流，最终将其作为响应发送给客户端
            response.getWriter().println(json);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
