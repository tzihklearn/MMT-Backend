package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.PermissionMapper;
import com.sipc.mmtbackend.mapper.RoleMapper;
import com.sipc.mmtbackend.mapper.UserBMapper;
import com.sipc.mmtbackend.mapper.UserRoleMergeMapper;
import com.sipc.mmtbackend.pojo.domain.Permission;
import com.sipc.mmtbackend.pojo.domain.Role;
import com.sipc.mmtbackend.pojo.domain.UserB;
import com.sipc.mmtbackend.pojo.domain.UserRoleMerge;
import com.sipc.mmtbackend.pojo.domain.po.UserBMemberPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.data.MemberInfoData;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.*;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.MemberInfoResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.ValidateException;
import com.sipc.mmtbackend.service.AccountManageService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.JWTUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.PasswordUtil;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import com.sipc.mmtbackend.utils.ICodeUtil;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.util.validation.metadata.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 超级管理接口的实现类
 *
 * @author tzih
 * @version v1.0
 * @since 2023.05.20
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class AccountManageServiceImpl implements AccountManageService {

    /**
     * 社团邀请码工具类
     */
    private final ICodeUtil iCodeUtil;

    private final PermissionMapper permissionMapper;

    private final UserBMapper userBMapper;

    private final RoleMapper roleMapper;

    private final UserRoleMergeMapper userRoleMergeMapper;

    private final JWTUtil jwtUtil;

    //TODO:初步处理，以后使用Guava Cache缓存
    private final Map<Integer, String> permissionMap = new HashMap<>();

    /**
     * 生成社团邀请码，并将其放入redis中，时限10min
     *
     * @return 生成社团邀请码的返回类，包含生成的社团邀请码
     * @see ICodeResult
     */
    @Override
    public CommonResult<ICodeResult> generatedICode() {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        /*
          生成社团邀请澳门，并将其放入redis中，时限10min
         */
        String ICode = iCodeUtil.setICodeRedis(organizationId, 6);

        /*
          判断是否成功生成社团邀码
         */
        if (ICode != null) {
            ICodeResult iCodeResult = new ICodeResult();

            iCodeResult.setICode(ICode);

            return CommonResult.success(iCodeResult);
        }

        return CommonResult.fail("生成社团邀请码失败失败");
    }

    /**
     * 获取社团成员列表业务处理方法
     *
     * @param pageNum 当前页数
     * @return 返回处理的结果，包含社团成员列表
     * @see MemberInfoResult
     */
    @Override
    public CommonResult<MemberInfoResult> allMemberInfo(Integer pageNum) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        //获取权限列表，并将其存入map
        if (permissionMap.size() == 0) {
            for (Permission permission : permissionMapper.selectList(new QueryWrapper<>())) {
                permissionMap.put(permission.getId(), permission.getName());
            }
        }

        //查找社团成员列表并返回
        return CommonResult.success(getMemberInfoResult(organizationId, pageNum, 0, 0));
    }

    /**
     * 筛选社团成员列表业务处理方法
     *
     * @param pageNum    当前页数
     * @param sort       学号排序， 0为正序， 1为倒序（默认为0）
     * @param permission 成员权限筛选项
     * @return 返回处理的结果，包含社团成员列表
     * @see MemberInfoResult
     */
    @Override
    public CommonResult<MemberInfoResult> siftMemberInfo(Integer pageNum, Integer sort, String permission) {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        //获取权限列表，并将其存入map
        if (permissionMap.size() == 0) {
            for (Permission permissionPo : permissionMapper.selectList(new QueryWrapper<>())) {
                permissionMap.put(permissionPo.getId(), permissionPo.getName());
            }
        }

        //查找出permission对应的权限id
        Integer permissionId = 0;
        if (permission != null) {
            Permission permissionByValue = permissionMapper.selectOne(new QueryWrapper<Permission>().eq("name", permission).last("limit 1"));
            if (permissionByValue != null) {
                permissionId = permissionByValue.getId();
            }
        }

        //查找社团成员列表并返回
        return CommonResult.success(getMemberInfoResult(organizationId, pageNum, sort, permissionId));

    }

    /**
     * 修改社团成员信息和权限业务处理方法
     *
     * @param reviseMemberInfoParam 修改社团成员信息和权限的参数实体类
     * @return 返回处理的结果
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于事务处理回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> reviseMemberInfo(ReviseMemberInfoParam reviseMemberInfoParam) throws DateBaseException, ValidateException {

        /*
          该接口不支持创建super_admin成员
         */
        if (reviseMemberInfoParam.getPermission().equals(PermissionEnum.SUPER_ADMIN.getName())) {
            return CommonResult.userAuthError();
        }

        int permissionId = -1;
        if (reviseMemberInfoParam.getPermission().equals(PermissionEnum.MEMBER.getName())) {
            permissionId = PermissionEnum.MEMBER.getId();
        } else if (reviseMemberInfoParam.getPermission().equals(PermissionEnum.COMMITTEE.getName())){
            permissionId = PermissionEnum.COMMITTEE.getId();
        } else {
            throw new ValidateException("不正确的成员权限");
        }

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        /*
          校验参数
         */
        UserB userB = userBMapper.selectOne(
                new QueryWrapper<UserB>()
                        .eq("phone", reviseMemberInfoParam.getPhone())
                        .ne("id", reviseMemberInfoParam.getId())
                        .last("limit 1"));
        if (userB != null) {
            log.warn("超级管理修改社团成员信息和权限接口警告，修改的成员手机号重复，社团组织id：{}，超级管理id:{},重复手机号：{}",organizationId,
                    context.getUserId(), reviseMemberInfoParam.getPhone());
            return CommonResult.fail("新建成员失败，手机号重复");
        }
        userB = userBMapper.selectOne(
                new QueryWrapper<UserB>()
                        .eq("student_id", reviseMemberInfoParam.getStudentId())
                        .ne("id", reviseMemberInfoParam.getId())
                        .last("limit 1"));
        if (userB != null) {
            log.warn("超级管理修改社团成员信息和权限接口警告，修改的成员学号重复，社团组织id：{}，超级管理id:{},重复学号：{}",organizationId,
                    context.getUserId(), reviseMemberInfoParam.getStudentId());
            return CommonResult.fail("新建成员失败，学号重复");
        }

        //获取权限列表，并将其存入map
//        if (permissionMap.size() == 0) {
//            for (Permission permissionPo : permissionMapper.selectList(new QueryWrapper<>())) {
//                permissionMap.put(permissionPo.getId(), permissionPo.getName());
//            }
//        }

        /*
          获取permission权限在社团中对应的社团权限角色role
         */
        //查找出permission对应的权限id
//        Permission permissionByValue = permissionMapper.selectOne(
//                new QueryWrapper<Permission>().eq("name", reviseMemberInfoParam.getPermission()).last("limit 1")
//        );
//        if (permissionByValue == null) {
//            log.error("修改社团成员信息接口异常，社团成员权限不存在，不存在的社团成员权限：{}，社团组织id：{}，成员用户id：{}",
//                    reviseMemberInfoParam.getPermission(), organizationId, reviseMemberInfoParam.getId());
//            return CommonResult.fail("请求失败，社团成员权限不存在");
//        }
//        Integer permissionId = permissionByValue.getId();



        Role role = roleMapper.selectOne(
                new QueryWrapper<Role>()
                        .eq("organization_id", organizationId)
                        .eq("permission_id", permissionId)
                        .last("limit 1")
        );
        if (role == null) {
            log.error("修改社团成员信息接口异常，社团成员权限角色不存在，社团组织id：{}，社团权限id：{}",
                    organizationId, reviseMemberInfoParam.getId());
            return CommonResult.fail("请求失败，社团成员权限不存在");
        }

        /*
          更新成员在社团的权限角色
         */
        int updateNum = userRoleMergeMapper.updateRoleIdByUserIdAndOrganizationId(
                reviseMemberInfoParam.getId(), organizationId, role.getId());
        if (updateNum != 1 && updateNum != 0) {
            log.error("修改社团成员信息接口异常，更新社团成员权限角色出错，更新数不正确，更新数：{}，用户id：{}，社团组织id：{}，修改权限角色id：{}",
                    updateNum, reviseMemberInfoParam.getId(), organizationId, role.getId());
            throw new DateBaseException("数据库更新数据异常");
        } else if (updateNum == 0) {
            return CommonResult.fail("请求失败，该成员不存在");
        }

        /*
          修改社团成员信息
         */
        userB = new UserB();
        userB.setId(reviseMemberInfoParam.getId());
        userB.setStudentId(reviseMemberInfoParam.getStudentId());
        userB.setUserName(reviseMemberInfoParam.getName());
        userB.setPhone(reviseMemberInfoParam.getPhone());

        updateNum = userBMapper.updateById(userB);
        if (updateNum != 1 && updateNum != 0) {
            log.error("修改社团成员信息接口异常，更新社团成员信息出错，更新数不正确，更新数：{}，社团组织id：{}，更新的成员信息：{}",
                    updateNum, organizationId, userB);
            throw new DateBaseException("数据库更新数据异常");
        } else if (updateNum == 0) {
            return CommonResult.fail("请求失败，该成员不存在");
        }

        //清楚被修改用户token
        Boolean aBoolean = jwtUtil.revokeToken(reviseMemberInfoParam.getId(), organizationId);
        if (aBoolean == null) {
            return CommonResult.fail("已完成修改，强制登出用户失败，请该用户重新登陆");
        }

        return CommonResult.success("请求成功，社团成员信息已修改");
    }

    /**
     * 修改社团成员密码业务处理方法
     *
     * @param reviseMemberPasswdParam 修改社团成员密码的参数实体类
     * @return 返回处理的结果
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于事务处理回滚
     * @see ReviseMemberPasswdParam
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> reviseMemberPasswd(ReviseMemberPasswdParam reviseMemberPasswdParam) throws DateBaseException {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        String passwd = PasswordUtil.hashPassword(reviseMemberPasswdParam.getPasswd());

        int updateNum = userRoleMergeMapper.updatePasswdByUserIdAndOrganizationId(reviseMemberPasswdParam.getUserId(), organizationId,
                passwd);
        if (updateNum != 1 && updateNum != 0) {
            log.error("修改社团成员密码接口异常，更新社团成员密码出错，更新数不正确，更新数：{}，社团组织id：{}，被更新社团成员id：{}，更新密码信息为：{}",
                    updateNum, organizationId, reviseMemberPasswdParam.getUserId(), passwd);
            throw new DateBaseException("数据库更新数据异常");
        } else if (updateNum == 0) {
            return CommonResult.fail("请求失败，该成员不存在");
        }

        //清楚被修改用户token
        Boolean aBoolean = jwtUtil.revokeToken(reviseMemberPasswdParam.getUserId(), organizationId);
        if (aBoolean == null) {
            return CommonResult.fail("已完成修改，强制登出用户失败，请该用户重新登陆");
        }

        return CommonResult.success("请求成功，社团成员密码已更新");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> reviseMember(ReviseMemberParam reviseMemberParam) throws DateBaseException, ValidateException {
        /*
          该接口不支持创建super_admin成员
         */
        if (reviseMemberParam.getPermission().equals(PermissionEnum.SUPER_ADMIN.getName())) {
            return CommonResult.userAuthError();
        }

        int permissionId = -1;
        if (reviseMemberParam.getPermission().equals(PermissionEnum.MEMBER.getName())) {
            permissionId = PermissionEnum.MEMBER.getId();
        } else if (reviseMemberParam.getPermission().equals(PermissionEnum.COMMITTEE.getName())){
            permissionId = PermissionEnum.COMMITTEE.getId();
        } else {
            throw new ValidateException("不正确的成员权限");
        }

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        /*
          校验参数
         */
        UserB userB = userBMapper.selectOne(
                new QueryWrapper<UserB>()
                        .eq("phone", reviseMemberParam.getPhone())
                        .ne("id", reviseMemberParam.getId())
                        .last("limit 1"));
        if (userB != null) {
            log.warn("超级管理修改社团成员信息接口警告，修改的成员手机号重复，社团组织id：{}，超级管理id:{},重复手机号：{}",organizationId,
                    context.getUserId(), reviseMemberParam.getPhone());
            return CommonResult.fail("新建成员失败，手机号重复");
        }
        userB = userBMapper.selectOne(
                new QueryWrapper<UserB>()
                        .eq("student_id", reviseMemberParam.getStudentId())
                        .ne("id", reviseMemberParam.getId())
                        .last("limit 1"));
        if (userB != null) {
            log.warn("超级管理修改社团成员信息接口警告，修改的成员学号重复，社团组织id：{}，超级管理id:{},重复学号：{}",organizationId,
                    context.getUserId(), reviseMemberParam.getStudentId());
            return CommonResult.fail("新建成员失败，学号重复");
        }

        //获取权限列表，并将其存入map
//        if (permissionMap.size() == 0) {
//            for (Permission permissionPo : permissionMapper.selectList(new QueryWrapper<>())) {
//                permissionMap.put(permissionPo.getId(), permissionPo.getName());
//            }
//        }

        /*
          获取permission权限在社团中对应的社团权限角色role
         */
        //查找出permission对应的权限id
//        Permission permissionByValue = permissionMapper.selectOne(
//                new QueryWrapper<Permission>().eq("name", reviseMemberParam.getPermission()).last("limit 1")
//        );


//        if (permissionByValue == null) {
//            log.error("修改社团成员信息接口异常，社团成员权限不存在，不存在的社团成员权限：{}，社团组织id：{}，成员用户id：{}",
//                    reviseMemberParam.getPermission(), organizationId, reviseMemberParam.getId());
//            return CommonResult.fail("请求失败，社团成员权限不存在");
//        }
//        Integer permissionId = permissionByValue.getId();

        Role role = roleMapper.selectOne(
                new QueryWrapper<Role>()
                        .eq("organization_id", organizationId)
                        .eq("permission_id", permissionId)
                        .last("limit 1")
        );
        if (role == null) {
            log.error("修改社团成员信息接口异常，社团成员权限角色不存在，社团组织id：{}，社团权限id：{}",
                    organizationId, reviseMemberParam.getId());
            return CommonResult.fail("请求失败，社团成员权限不存在");
        }

        /*
          更新成员在社团的角色信息（包含权限密码）
         */

        String passwd = PasswordUtil.hashPassword(reviseMemberParam.getPasswd());

        int updateNum = userRoleMergeMapper.updateRoleByUserIdAndOrganizationId(
                reviseMemberParam.getId(), organizationId, role.getId(), passwd);
        if (updateNum != 1 && updateNum != 0) {
            log.error("修改社团成员信息接口异常，更新社团成员角色信息出错，更新数不正确，更新数：{}，用户id：{}，社团组织id：{}，修改权限角色id：{}",
                    updateNum, reviseMemberParam.getId(), organizationId, role.getId());
            throw new DateBaseException("数据库更新数据异常");
        } else if (updateNum == 0) {
            return CommonResult.fail("请求失败，该成员不存在");
        }

        /*
          修改社团成员信息
         */
        userB = new UserB();
        userB.setId(reviseMemberParam.getId());
        userB.setStudentId(reviseMemberParam.getStudentId());
        userB.setUserName(reviseMemberParam.getName());
        userB.setPhone(reviseMemberParam.getPhone());

        updateNum = userBMapper.updateById(userB);
        if (updateNum != 1 && updateNum != 0) {
            log.error("修改社团成员信息接口异常，更新社团成员信息出错，更新数不正确，更新数：{}，社团组织id：{}，更新的成员信息：{}",
                    updateNum, organizationId, userB);
            throw new DateBaseException("数据库更新数据异常");
        } else if (updateNum == 0) {
            return CommonResult.fail("请求失败，该成员不存在");
        }


//        String passwd = PasswordUtil.hashPassword(reviseMemberPasswdParam.getPasswd());
//
//        int updateNum = userRoleMergeMapper.updatePasswdByUserIdAndOrganizationId(reviseMemberPasswdParam.getUserId(), organizationId,
//                passwd);
//        if (updateNum != 1 && updateNum != 0) {
//            log.error("修改社团成员密码接口异常，更新社团成员密码出错，更新数不正确，更新数：{}，社团组织id：{}，被更新社团成员id：{}，更新密码信息为：{}",
//                    updateNum, organizationId, reviseMemberPasswdParam.getUserId(), passwd);
//            throw new DateBaseException("数据库更新数据异常");
//        } else if (updateNum == 0) {
//            return CommonResult.fail("请求失败，该成员不存在");
//        }

        //清楚被修改用户token
        Boolean aBoolean = jwtUtil.revokeToken(reviseMemberParam.getId(), organizationId);
        if (aBoolean == null) {
            return CommonResult.fail("已完成修改，强制登出用户失败，请该用户重新登陆");
        }

        return CommonResult.success("请求成功，社团成员信息已修改");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> addMember(AddMemberParam addMemberParam) throws ValidateException, DateBaseException {

        /*
          该接口不支持创建super_admin成员
         */
        if (addMemberParam.getPermission().equals(PermissionEnum.SUPER_ADMIN.getName())) {
            return CommonResult.userAuthError();
        }

        int permissionId = -1;

        if (addMemberParam.getPermission().equals(PermissionEnum.MEMBER.getName())) {
            permissionId = PermissionEnum.MEMBER.getId();
        } else if (addMemberParam.getPermission().equals(PermissionEnum.COMMITTEE.getName())){
            permissionId = PermissionEnum.COMMITTEE.getId();
        } else {
            throw new ValidateException("不正确的成员权限");
        }

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        UserB userB = userBMapper.selectOne(new QueryWrapper<UserB>().eq("phone", addMemberParam.getPhone()));
        if (userB != null) {
            log.warn("超级管理新建社团成员接口警告，注册成员手机号重复，社团组织id：{}，超级管理id:{},重复手机号：{}",organizationId,
                    context.getUserId(), addMemberParam.getPhone());
            return CommonResult.fail("新建成员失败，手机号重复");
        }
        userB = userBMapper.selectOne(new QueryWrapper<UserB>().eq("student_id", addMemberParam.getStudentId()));
        if (userB != null) {
            log.warn("超级管理新建社团成员接口警告，注册成员学号重复，社团组织id：{}，超级管理id:{},重复学号：{}",organizationId,
                    context.getUserId(), addMemberParam.getStudentId());
            return CommonResult.fail("新建成员失败，学号重复");
        }
        // 初始化一个新组织的新角色



        Role role = roleMapper.selectOne(
                new QueryWrapper<Role>()
                        .eq("organization_id", organizationId)
                        .eq("permission_id", permissionId));
        if (role == null) {
            role = new Role();
            role.setOrganizationId(organizationId);
            role.setPermissionId(permissionId);
            int insert = roleMapper.insert(role);
            if (insert != 1) {
                log.error("超级管理新建社团成员接口异常，插入（role）社团角色数异常，受影响的行数：{}，社团组织id：{}，超级管理id：{}，角色信息：{}",
                        insert, organizationId, context.getUserId(), role);
//                log.error("初始化组织 " + organizationId + " 的角色时失败，受影响行数：" + insert);
                throw new DateBaseException("数据库插入数据异常");
            }
        }
        UserB user = new UserB();
        UserRoleMerge roleMerge = new UserRoleMerge();
        user.setPhone(addMemberParam.getPhone());
        user.setStudentId(addMemberParam.getStudentId());
        user.setUserName(addMemberParam.getName());
//        user.setEmail(param.getEmail());
        int uins = userBMapper.insert(user);
        if (uins != 1) {
            log.error("超级管理新建社团成员接口异常，插入（user_b）用户数异常，受影响的行数：{}，社团组织id：{}，超级管理id：{}，用户信息：{}",
                    uins, organizationId, context.getUserId(), user);
//            log.error("创建B端用户" + user + "失败, 受影响行数：" + uins);
            throw new DatabaseException("数据库插入数据异常");
        }
        roleMerge.setRoleId(role.getId());
        roleMerge.setUserId(user.getId());
        roleMerge.setPassword(PasswordUtil.hashPassword(addMemberParam.getPasswd()));
        int urint = userRoleMergeMapper.insert(roleMerge);
        if (urint != 1) {
            log.error("超级管理新建社团成员接口异常，插入（role_merge）角色信息数异常，受影响的行数：{}，社团组织id：{}，超级管理id：{}，角色信息：{}",
                    urint, organizationId, context.getUserId(), roleMerge);
            throw new DatabaseException("数据库插入数据异常");
        }

        return CommonResult.success("，请求成功，社团成员已创建");
    }

    /**
     * 删除社团成员业务处理方法
     *
     * @param deleteMemberParam 删除社团成员的参数实体类
     * @return 返回处理的结果
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于事务处理回滚
     * @see DeleteMemberParam
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> deleteMember(DeleteMemberParam deleteMemberParam) throws DateBaseException {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        int deleteNum = userRoleMergeMapper.logicDeleteByUserIdAndOrganizationId(deleteMemberParam.getUserId(), organizationId);
        if (deleteNum != 1 && deleteNum != 0) {
            log.error("删除社团成员接口异常，删除社团成员数出错，删除数不正确，删除数：{}，社团组织id：{}，被删除社团成员id：{}",
                    deleteNum, organizationId, deleteMemberParam.getUserId());
            throw new DateBaseException("数据库删除数据异常");
        } else if (deleteNum == 0) {
            return CommonResult.fail("请求失败，该成员不存在");
        }

        //清楚被修改用户token
        Boolean aBoolean = jwtUtil.revokeToken(deleteMemberParam.getUserId(), organizationId);
        if (aBoolean == null) {
            return CommonResult.fail("已完成修改，强制登出用户失败，请该用户重新登陆");
        }

        return CommonResult.success("请求成功，社团成员已删除");
    }

    /**
     * 按照一定条件社团成员列表
     *
     * @param organizationId 社团组织id
     * @param pageNum        当前页数
     * @param sort           学号排序， 0为正序， 1为倒序（默认为0）
     * @param permissionId   成员权限筛选项(0为不筛选)
     * @return 返回查找到的社团成员列表
     */
    private MemberInfoResult getMemberInfoResult(Integer organizationId, Integer pageNum, Integer sort, Integer permissionId) {
        MemberInfoResult memberInfoResult = new MemberInfoResult();
        List<MemberInfoData> list = new ArrayList<>();

        //每页分页数量
        int pageSize = 10;

        //联表查询数据库，获取社团成员信息并拼装社团成员列表
        for (UserBMemberPo userBMemberPo : userBMapper.selectMemberInfoListByOrganizationId(
                organizationId, (pageNum - 1) * pageSize, pageNum * pageSize, sort, permissionId
        )) {
            MemberInfoData memberInfoData = new MemberInfoData();
            memberInfoData.setId(userBMemberPo.getId());
            memberInfoData.setStudentId(userBMemberPo.getStudentId());
            memberInfoData.setName(userBMemberPo.getUserName());

            /*
              隐藏手机号的中间4位
             */
            String phone = userBMemberPo.getPhone();

            String prefix = phone.substring(0, 3);
            String suffix = phone.substring(phone.length() - 4);

            String phoneNumber = prefix + "****" + suffix;

            memberInfoData.setPhone(phoneNumber);
            memberInfoData.setPermission(permissionMap.get(userBMemberPo.getPermissionId()));

            list.add(memberInfoData);
        }

        memberInfoResult.setMemberInfoDataList(list);

        return memberInfoResult;
    }
}
