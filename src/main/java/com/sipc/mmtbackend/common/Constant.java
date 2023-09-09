package com.sipc.mmtbackend.common;

import org.springframework.stereotype.Component;

/**
 * @author valentine
 * 存常量
 */
@Component
public class Constant {
    /**
     * 密码加密
     */
    public static final String SALT = "*fdsvkljd&SHDFEKfEW03";

    /**
     * 小程序相关
     */
//    public static final String APPID = "wxd63ea02596dbe989";
//    public static final String APPID = "wx4748ccdd0d3b3537";
//    public static final String APPID = "wxb41b6920f3414c92";
//    public static final String SECRET = "00c297896eb66a7511daf429607e9cec";
    public static final String APPID = "wxab15065dd80f9c52";
    public static final String SECRET = "82b4e3160824045c37ea745ea39aabef";


//    public static final String SECRET = "588f985e1fba8717ce619843627bd8de";
//    public static final String SECRET = "659657466a3d405f557026e86dbf321e";
//    659657466a3d405f557026e86dbf321e
    /**
     * b端cookieName
     */
    public static final String COOKIE_B = "studentId";

    /**
     * c端cookieName
     */
    public static final String COOKIE_C = "openid";

    /**
     * b端标识
     */
    public static final String B_END = "b";

    /**
     * c端标识
     */
    public static final String C_END = "c";

    /**
     * 用户组织内权限super_admin
     */
    public static final String SUPER_ADMIN = "super_admin";

    /**
     * 用户组织内权限member
     */
    public static final String MEMBER = "member";

    /**
     * 用户组织内权限committee
     */
    public static final String COMMITTEE = "committee";

    /**
     * ip次数验证中，redisKey的前缀
     */
    public static final String REDIS_KEY_SECOND_ACCESS = "redisredis";
}


