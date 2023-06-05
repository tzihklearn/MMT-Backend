package com.sipc.mmtbackend.utils.CheckroleBUtil;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import org.springframework.web.method.HandlerMethod;

/**
 * 校验权限的工具类
 * @author tzih
 * @version v1.0
 * @since 2023.06.05
 */
public class CheckPermissionUtil {

    /**
     * 校验B端用户的权限的方法
     * @param handlerMethod 选择要执行的处理程序，用于类型和/或实例计算
     * @param userPermissionId 用户自身权限id
     * @return 返回用户是否具有权限操作，false为不具有，true为具有
     */
    public static boolean checkBPermission(HandlerMethod handlerMethod, int userPermissionId) {

        /*
          获取请求方法上的@BPermission注解，判断用户是否具有其权限，如果没有该注解，去请求类型上寻找
         */
        BPermission bPermission = handlerMethod.getMethodAnnotation(BPermission.class);

        if (bPermission != null) {
            PermissionEnum permissionEnum = bPermission.value();
            return userPermissionId <= permissionEnum.getId();
        }

        /*
           获取请求类型上的@BPermission注解，判断用户是否具有其权限，如果没有该注解，返回false
         */
        Class<?> beanType = handlerMethod.getBeanType();

        BPermission bPermissionType = beanType.getAnnotation(BPermission.class);

        if (bPermissionType != null) {
            PermissionEnum permissionEnum = bPermissionType.value();

            return userPermissionId <= permissionEnum.getId();
        }

        return false;
    }

}
