package com.sipc.mmtbackend.utils.checkRoleUtils;

import com.sipc.mmtbackend.common.Constant;
import com.sipc.mmtbackend.mapper.UserInfoMapper;
import com.sipc.mmtbackend.mapper.c.LoginStatusMapper;
import com.sipc.mmtbackend.mapper.c.UserCMapper;
import com.sipc.mmtbackend.pojo.c.domain.LoginStatus;
import com.sipc.mmtbackend.pojo.c.domain.UserC;
import com.sipc.mmtbackend.pojo.domain.UserInfo;
import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import com.sipc.mmtbackend.pojo.exceptions.ErrorException;
import com.sipc.mmtbackend.utils.RedisUtil;
import com.sipc.mmtbackend.utils.checkRoleUtils.param.CheckResultParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * 检查是否有权限的工具类
 */
@Component
@Slf4j
public class CheckRole {

    @Resource
    private UserCMapper userCMapper;
//    @Resource
//    private MemberMapper memberMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private LoginStatusMapper loginStatusMapper;
//    @Resource
//    private OrganizationUserPermissionMergeMapper organizationUserPermissionMergeMapper;
//    @Resource
//    private OrganizationPermissionMapper organizationPermissionMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    public static CheckRole checkRole;


    /**
     * 获取 B 端用户的权限
     * Superadmin: 3
     * Committee: 2
     * Member: 1
     * 出现意外会返回null
     */
//    public static Integer getPermissionId(String studentId, Integer organizationId) {
//        LoginStatus loginStatus = checkRole.loginStatusMapper.selectByValue(studentId);
//        Integer pid;
//        switch (CheckRole.getPermission(loginStatus, organizationId.toString())) {
//            case Constant.SUPER_ADMIN:
//                pid = 3;
//                break;
//            case Constant.COMMITTEE:
//                pid = 2;
//                break;
//            case Constant.MEMBER:
//                pid = 1;
//                break;
//            default:
//                pid = null;
//        }
//        return pid;
//    }

    @PostConstruct
    public void init() {
        checkRole = this;
        checkRole.userCMapper = this.userCMapper;
        checkRole.redisUtil = this.redisUtil;
        checkRole.loginStatusMapper = this.loginStatusMapper;
//        checkRole.memberMapper = this.memberMapper;
//        checkRole.organizationUserPermissionMergeMapper = this.organizationUserPermissionMergeMapper;
//        checkRole.organizationPermissionMapper = this.organizationPermissionMapper;
    }

    /**
     * B、C端验权
     * @param endType b端或者c端登录
     * @return
     */
    @Deprecated
    public static CheckResultParam check(HttpServletRequest request, HttpServletResponse response, String endType) {
        String uri = request.getRequestURI();

        //c端无需登录
        if (Constant.C_END.equals(endType)) {
            if ("/admission/getLatest".equals(uri) ||
                    "/organization/List".equals(uri) ||
                    "/majorinfo/academy".equals(uri)
            ) {
                return CheckResultParam.success(null);
            }
        }

        //打印请求头
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {//判断是否还有下一个元素
            String nextElement = headerNames.nextElement();//获取headerNames集合中的请求头
            String header2 = request.getHeader(nextElement);//通过请求头得到请求内容
            System.out.println(nextElement + ":" + header2);
        }

        //拿到ua
        String userAgent = request.getHeader("USER-AGENT");

        //登录验证*********
        String cookieName = endType.equals(Constant.B_END) ? Constant.COOKIE_B : Constant.COOKIE_C;

        //拿到cookies
        Cookie[] cookies = request.getCookies();

        //检查是否存在指定cookie
        String key = null;
        Cookie resCookie = null;

        if (cookies == null) {
            return CheckResultParam.fail("10003", "用户未登录");
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                key = cookie.getValue();
                resCookie = cookie;
            }
        }

        //永久cookie
        if ("123456".equals(key) && Constant.B_END.equals(endType)) {
            if (!checkRole.redisUtil.exists("123456")) {
                return CheckResultParam.fail("10002", "永久cookie未激活");
            }
            String value = checkRole.redisUtil.get(key).toString();
            Cookie newCookie = new Cookie(cookieName, key);
            newCookie.setMaxAge(60 * 20);
            newCookie.setPath("/");
            response.addCookie(newCookie);

            return CheckResultParam.success(value);
        }

        if ("asdfghjkasdfghjc".equals(key) && Constant.C_END.equals(endType)) {
            if (!checkRole.redisUtil.exists("asdfghjkasdfghjc")) {
                return CheckResultParam.fail("10002", "永久cookie未激活");
            }
            String value = checkRole.redisUtil.get(key).toString();
            Cookie newCookie = new Cookie(cookieName, key);
            newCookie.setMaxAge(60 * 20);
            newCookie.setPath("/");
            response.addCookie(newCookie);

            return CheckResultParam.success(value);
        }

        //永久cookie结束

        //cookie不存在或者redis中无对应状态
        if (key == null || !checkRole.redisUtil.exists(key)) {
            //如果有cookie的话删除
            if (key != null) {
                Cookie tempCookie = new Cookie(cookieName, key);
                tempCookie.setPath("/");
                tempCookie.setMaxAge(0);
                response.addCookie(tempCookie);
            }

            return CheckResultParam.fail("10003", "用户未登录");
        }

        //得到value（openid或者studentId，并返回）
        String value = (String) checkRole.redisUtil.get(key);

        //找到MySQL中存储的登录状态
        LoginStatus loginStatus = checkRole.loginStatusMapper.selectByValue(value);

        if (loginStatus == null) {
            return CheckResultParam.fail(ResultEnum.USER_LOGIN_EXPIRED);
        }

        if (loginStatus.getUserAgent() == null) {
            return CheckResultParam.fail(ResultEnum.USER_LOGIN_EXPIRED);
        }


        //如果存储的ua与当前登录ua不一致，删除cookie，删除redis缓存，删除MySQL中登录状态
        if (!loginStatus.getUserAgent().equals(userAgent)) {
            //删除cookie
            resCookie.setMaxAge(0);
            Cookie tempCookie = new Cookie(cookieName, key);
            tempCookie.setPath("/");
            tempCookie.setMaxAge(0);
            response.addCookie(tempCookie);
            //删除redis缓存
            checkRole.redisUtil.remove(key);
            //删除表中登录状态
            checkRole.loginStatusMapper.deleteByValue(value);

            return CheckResultParam.fail("10003", "用户未登录");
        }

        //登录验证结束*******

        //权限验证******
        //b端
//        if (Constant.B_END.equals(endType)
//                && !"/invitation-code/club".equals(uri)
//                && !"/account/revise/phone".equals(uri)
//                && !"/account/revise/password".equals(uri)) {
//            if (loginStatus.getOrganizationId() == null) {
//                return CheckResultParam.fail("10003", "用户未加入任何组织");
//            }
//
//            //根据学号和组织id查询用户权限
//            String organizationIdTemp = request.getParameter("organizationId");
//            String permission = CheckRole.getPermission(loginStatus, organizationIdTemp);
//            if (permission == null) {
//                return CheckResultParam.fail(ResultEnum.USER_RESOURCE_EXCEPTION);
//            }
//
//            //如果用户权限是member
//
//            //需要committee的接口
//            if ("/student/info/change-student-info".equals(uri)
//                    || "/interview-reply/status".equals(uri)
//                    || "/organization/information".equals(uri)
//                    || "/organization/interview/n".equals(uri)
//                    || "/organization/interview/round".equals(uri)
//                    || "/organization/interview/sign".equals(uri)
//                    || "/organization/interview/message".equals(uri)
//                    || "/organization/department-id".equals(uri)
//
//            ) {
//                if (Constant.MEMBER.equals(permission)) {
//                    return CheckResultParam.fail(ResultEnum.AUTH_ERROR);
//                }
//            }
//
//
//            //需要super_admin的接口
//            if ("/account/manage/all".equals(uri)
//                    || "/account/manage/revise".equals(uri)
//                    || "/account/manage/delete".equals(uri)
//                    || "/organization/invitation-code".equals(uri)
//                    || "/interview-arrangement/info/like".equals(uri)
//                    || "/interview-arrangement/data".equals(uri)
//                    || "/student/info/change-student-id".equals(uri)
//                    || "/organization/information".equals(uri)
//                    || "/organization/interview/n".equals(uri)
//                    || "/organization/interview/round".equals(uri)
//                    || "/organization/interview/sign".equals(uri)
//                    || "/organization/interview/message".equals(uri)) {
//                     log.debug("进入b端superAdmin");
//                if (!Constant.SUPER_ADMIN.equals(permission)) {
//                    return CheckResultParam.fail(ResultEnum.AUTH_ERROR);
//                }
//            }
//
//        }
//
        //c端
        if (Constant.C_END.equals(endType)) {
            //检查用户是否完善信息
            boolean certification = checkRole.isCertification(loginStatus);

            if (
                    "/student/info/after".equals(uri) ||
                            "/message/getAll".equals(uri) ||
                            "/message/get".equals(uri) ||
                            "/message/read".equals(uri) ||
                            "/message/submitState".equals(uri) ||
                            "/message/userSent".equals(uri) ||
                            "/admission/getDepartment".equals(uri) ||
                            "/registration-form/get".equals(uri) ||
                            "/registration-form/post".equals(uri) ||
                            "/interview/QRCode/verify".equals(uri)
            ) {
                if (!certification) {
                    return CheckResultParam.fail("A0300", "未完善个人信息");
                }
            }


        }
        //权限验证结束******

        //刷新cookie
        Cookie newCookie = new Cookie(cookieName, key);
        newCookie.setMaxAge(60 * 60 * 12);
        newCookie.setPath("/");
        response.addCookie(newCookie);
        //刷新redis
        checkRole.redisUtil.set(key, value, 12L, TimeUnit.HOURS);

        return CheckResultParam.success(value);
    }

    /**
     * B、C端验权
     * @param endType b端或者c端登录
     * @param organizationId 部门组织
     * @return
     */
    public static CheckResultParam check(HttpServletRequest request, HttpServletResponse response, String endType,String organizationId) {
        String uri = request.getRequestURI();

        //c端无需登录
        if (Constant.C_END.equals(endType)) {
            if ("/organization-list/all".equals(uri)
                    || "/organization-list/keyWord".equals(uri)
                    || "/show-organizations/organization-introduce".equals(uri)
                    ||  "/show-organizations/department-admission".equals(uri)
                    ||"/show-organizations/department-introduce".equals(uri)

            ) {
                return CheckResultParam.success(null);
            }
        }

        //打印请求头
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {//判断是否还有下一个元素
            String nextElement = headerNames.nextElement();//获取headerNames集合中的请求头
            String header2 = request.getHeader(nextElement);//通过请求头得到请求内容
            System.out.println(nextElement + ":" + header2);
        }

        //拿到ua
        String userAgent = request.getHeader("USER-AGENT");

        //登录验证*********
        String cookieName = endType.equals(Constant.B_END) ? Constant.COOKIE_B : Constant.COOKIE_C;

        //拿到cookies
        Cookie[] cookies = request.getCookies();

        //检查是否存在指定cookie
        String key = null;
        Cookie resCookie = null;

        if (cookies == null) {
            return CheckResultParam.fail("10003", "用户未登录");
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                key = cookie.getValue();
                resCookie = cookie;
            }
        }

        //永久cookie
        if ("123456".equals(key) && Constant.B_END.equals(endType)) {
            if (!checkRole.redisUtil.exists("123456")) {
                return CheckResultParam.fail("10002", "永久cookie未激活");
            }
            String value = checkRole.redisUtil.get(key).toString();
            Cookie newCookie = new Cookie(cookieName, key);
            newCookie.setMaxAge(60 * 20);
            newCookie.setPath("/");
            response.addCookie(newCookie);

            return CheckResultParam.success(value);
        }

        if ("asdfghjkasdfghjc".equals(key) && Constant.C_END.equals(endType)) {
            if (!checkRole.redisUtil.exists("asdfghjkasdfghjc")) {
                return CheckResultParam.fail("10002", "永久cookie未激活");
            }
            String value = checkRole.redisUtil.get(key).toString();
            Cookie newCookie = new Cookie(cookieName, key);
            newCookie.setMaxAge(60 * 20);
            newCookie.setPath("/");
            response.addCookie(newCookie);

            return CheckResultParam.success(value);
        }

        //永久cookie结束

        //cookie不存在或者redis中无对应状态
        if (key == null || !checkRole.redisUtil.exists(key)) {
            //如果有cookie的话删除
            if (key != null) {
                Cookie tempCookie = new Cookie(cookieName, key);
                tempCookie.setPath("/");
                tempCookie.setMaxAge(0);
                response.addCookie(tempCookie);
            }

            return CheckResultParam.fail("10003", "用户未登录");
        }

        //得到value（openid或者studentId，并返回）
        String value = (String) checkRole.redisUtil.get(key);

        //找到MySQL中存储的登录状态
        LoginStatus loginStatus = checkRole.loginStatusMapper.selectByValue(value);

        if (loginStatus == null) {
            return CheckResultParam.fail(ResultEnum.USER_LOGIN_EXPIRED);
        }

        if (loginStatus.getUserAgent() == null) {
            return CheckResultParam.fail(ResultEnum.USER_LOGIN_EXPIRED);
        }


        //如果存储的ua与当前登录ua不一致，删除cookie，删除redis缓存，删除MySQL中登录状态
        if (!loginStatus.getUserAgent().equals(userAgent)) {
            //删除cookie
            resCookie.setMaxAge(0);
            Cookie tempCookie = new Cookie(cookieName, key);
            tempCookie.setPath("/");
            tempCookie.setMaxAge(0);
            response.addCookie(tempCookie);
            //删除redis缓存
            checkRole.redisUtil.remove(key);
            //删除表中登录状态
            checkRole.loginStatusMapper.deleteByValue(value);

            return CheckResultParam.fail("10003", "用户未登录");
        }

        //登录验证结束*******

        //权限验证******
        //b端
//        if (Constant.B_END.equals(endType)
//                && !"/invitation-code/club".equals(uri)
//                && !"/account/revise/phone".equals(uri)
//                && !"/account/revise/password".equals(uri)) {
//            if (loginStatus.getOrganizationId() == null) {
//                return CheckResultParam.fail("10003", "用户未加入任何组织");
//            }
//
//            //根据学号和组织id查询用户权限
//            String permission = CheckRole.getPermission(loginStatus, organizationId);
//            if (permission == null) {
//                return CheckResultParam.fail(ResultEnum.USER_RESOURCE_EXCEPTION);
//            }
//
//            //如果用户权限是member
//
//            //需要committee的接口
//            if ("/student/info/change-student-info".equals(uri)
//                    || "/interview-reply/status".equals(uri)
//                    || "/organization/information".equals(uri)
//                    || "/organization/interview/n".equals(uri)
//                    || "/organization/interview/round".equals(uri)
//                    || "/organization/interview/sign".equals(uri)
//                    || "/organization/interview/message".equals(uri)
//                    || "/organization/department-id".equals(uri)
//                    || "/interview-reply/department".equals(uri)
//                    || "/interview-reply/room".equals(uri)
//                    || "/interview-reply/stu-info".equals(uri)
//                    || "/interview-reply/stu-search".equals(uri)
//
//            ) {
//                if (Constant.MEMBER.equals(permission)) {
//                    return CheckResultParam.fail(ResultEnum.AUTH_ERROR);
//                }
//            }
//
//
//            //需要super_admin的接口
//            if ("/account/manage/all".equals(uri)
//                    || "/account/manage/revise".equals(uri)
//                    || "/account/manage/delete".equals(uri)
//                    || "/organization/invitation-code".equals(uri)
//                    || "/interview-arrangement/info/like".equals(uri)
//                    || "/interview-arrangement/data".equals(uri)
//                    || "/student/info/change-student-id".equals(uri)
//                    || "/organization/information".equals(uri)
//                    || "/organization/interview/n".equals(uri)
//                    || "/organization/interview/round".equals(uri)
//                    || "/organization/interview/sign".equals(uri)
//                    || "/organization/interview/message".equals(uri)
//            )
//            {
//                log.debug("进入b端superAdmin");
//                if (!Constant.SUPER_ADMIN.equals(permission)) {
//                    return CheckResultParam.fail(ResultEnum.AUTH_ERROR);
//                }
//            }
//
//        }
//
        //c端
        if (Constant.C_END.equals(endType)) {
            //检查用户是否完善信息
            boolean certification = checkRole.isCertification(loginStatus);

            if (
                    "/message/all".equals(uri)
                            || "/message/org".equals(uri)
                            || "/organization/check".equals(uri)
                            || "/message/read".equals(uri)
                            || "/message/state".equals(uri)

            ) {
                if (!certification) {
                    return CheckResultParam.fail("A0300", "未完善个人信息");
                }
            }


        }
        //权限验证结束******

        //刷新cookie
        Cookie newCookie = new Cookie(cookieName, key);
        newCookie.setMaxAge(60 * 60 * 12);
        newCookie.setPath("/");
        response.addCookie(newCookie);
        //刷新redis
        checkRole.redisUtil.set(key, value, 12L, TimeUnit.HOURS);

        return CheckResultParam.success(value);
    }


//    public String getUserId(HttpServletRequest httpServletRequest, String endType) {
//        Cookie[] cookies = httpServletRequest.getCookies();
//        String data = null;
//        boolean flag = true;
//        String userId = null;
//        String studentId = null;
//        String openId = null;
//        if (cookies == null) {
//            return null;
//        }
//        for (Cookie cookie : cookies) {
//            if (Constant.B_END.equals(endType)) {
//                if (Constant.COOKIE_B.equals(cookie.getName())) {
//                    data = cookie.getValue();
//                    studentId = (String) checkRole.redisUtil.get(data);
//                    flag = false;
//                }
//            } else {
//                if (Constant.COOKIE_C.equals(cookie.getName())) {
//                    data = cookie.getValue();
//                    openId = (String) checkRole.redisUtil.get(data);
//                    flag = false;
//                }
//            }
//        }
//        if (!flag) {
//            if (Constant.B_END.equals(endType)) {
//                if (studentId == null) {
//                    return null;
//                }
//                User user = checkRole.userCMapper.selectByStudentId(Integer.valueOf(studentId));
//
//                userId = user.getId().toString();
//            } else {
//                if (openId == null) {
//                    return null;
//                }
//                User user = checkRole.userCMapper.selectByOpenid(openId);
//
//                userId = user.getId().toString();
//            }
//        }
//
//        return userId;
//    }

    /**
     * 根据loginStatus检查用户资源是否完善
     *
     * @param loginStatus
     * @return
     */
    private boolean isCertification(LoginStatus loginStatus) {
        String value = loginStatus.getValue();
        UserC user = checkRole.userCMapper.selectByOpenid(value);
        if (user == null) {
            throw new ErrorException("用户资源异常");
        }

        UserInfo userInfo = checkRole.userInfoMapper.selectById(user.getId());

        if (userInfo == null) {
            throw new ErrorException("用户资源异常");
        }

        return userInfo.getIsCertification();
    }


    /**
     * 根据用户登录信息和传入的组织id查询用户在该组织权限,如果存在对应组织，更新loginStatus表
     *
     * @param loginStatus
     * @param organizationIdTemp
     * @return
     */
//    public static String getPermission(LoginStatus loginStatus, String organizationIdTemp) {
//        if (loginStatus == null) {
//            return null;
//        }
//
//        String value = loginStatus.getValue();
//        String organizationId = loginStatus.getOrganizationId().toString();
//
//        //查询userId
//        User user = checkRole.userMapper.selectByStudentId(Integer.parseInt(value));
//
//        //用户资源异常
//        if (user == null) {
//            return null;
//        }
//
//        //如果传入操作组织id
//        if (organizationIdTemp != null) {
//            Member member = checkRole.memberMapper.selectByUserIdAndOrgId(user.getId(), Integer.parseInt(organizationIdTemp));
//
//            if (member == null) {
//                throw new ErrorException("用户资源异常");
//            }
//
//            organizationId = organizationIdTemp;
//            //更新当前登录组织
//            loginStatus.setOrganizationId(Integer.parseInt(organizationId));
//            checkRole.loginStatusMapper.updateSelective(loginStatus);
//        }
//
//        //查询用户对应的member
//        Member member = checkRole.memberMapper.selectByUserIdAndOrgId(user.getId(), Integer.parseInt(organizationId));
//
//        //用户在该组织没有身份
//        if (member == null) {
//            return null;
//        }
//
//        //已知member获取permissionId
//        OrganizationUserPermissionMerge organizationUserPermissionMerge = checkRole.organizationUserPermissionMergeMapper.selectByPrimaryKey(member.getId());
//        //组织权限表异常
//        if (organizationUserPermissionMerge == null) {
//            return null;
//        }
//
//        //已知权限id，查询权限
//        OrganizationPermission organizationPermission = checkRole.organizationPermissionMapper.selectByPrimaryKey(organizationUserPermissionMerge.getPermissionId());
//        //权限表异常
//        if (organizationPermission == null) {
//            return null;
//        }
//        return organizationPermission.getName();
//    }
//
//
//    /**
//     * 根据传入的组织id查询用户是否为超级管理员
//     *
//     * @param
//     * @return
//     */
//    public Boolean isSuperAdmin(HttpServletRequest request, HttpServletResponse response, String organizationId) {
//
//        //检查当前登录状态的权限是否为super_admin
//        CheckResultParam check = CheckRole.check(request, response, Constant.B_END, organizationId);
//        if (!check.isResult()) {
//            return false;
//        }
//        String data = check.getData();
//
//        //查询数据库是否存在登录状态
//        LoginStatus loginStatus = loginStatusMapper.selectByValue(data);
//
//        if (loginStatus == null) {
//            return false;
//        }
//
//        //根据所传数据获得所传数据的权限，并且更新logStatus表
//
//        String permission = CheckRole.getPermission(loginStatus, organizationId);
//
//        //如果根据前端所传数据获得的成员权限不是super_admin,返回权限异常
//        if (!Objects.equals(permission, Constant.SUPER_ADMIN)) {
//            return false;
//        }
//        return true;
//    }

}



