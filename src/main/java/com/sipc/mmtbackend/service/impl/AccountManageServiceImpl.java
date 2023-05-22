package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.PermissionMapper;
import com.sipc.mmtbackend.mapper.RoleMapper;
import com.sipc.mmtbackend.mapper.UserBMapper;
import com.sipc.mmtbackend.mapper.UserRoleMergeMapper;
import com.sipc.mmtbackend.pojo.domain.Permission;
import com.sipc.mmtbackend.pojo.domain.Role;
import com.sipc.mmtbackend.pojo.domain.UserB;
import com.sipc.mmtbackend.pojo.domain.po.UserBMemberPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.data.MemberInfoData;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.ReviseMemberInfoParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.MemberInfoResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.AccountManageService;
import com.sipc.mmtbackend.utils.ICodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    //TODO:初步处理，以后使用Guava Cache缓存
    private final Map<Integer, String> permissionMap = new HashMap<>();

    /**
     * 生成社团邀请码，并将其放入redis中，时限10min
     *
     * @param organizationId 社团组织id
     * @return 生成社团邀请码的返回类，包含生成的社团邀请码
     * @see ICodeResult
     */
    @Override
    public CommonResult<ICodeResult> generatedICode(Integer organizationId) {

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
     * @param organizationId 社团组织id
     * @param pageNum        当前页数
     * @return 返回处理的结果，包含社团成员列表
     * @see MemberInfoResult
     */
    @Override
    public CommonResult<MemberInfoResult> allMemberInfo(Integer organizationId, Integer pageNum) {

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
     * @param organizationId 社团组织id
     * @param pageNum        当前页数
     * @param sort           学号排序， 0为正序， 1为倒序（默认为0）
     * @param permission     成员权限筛选项
     * @return 返回处理的结果，包含社团成员列表
     * @see MemberInfoResult
     */
    @Override
    public CommonResult<MemberInfoResult> siftMemberInfo(Integer organizationId, Integer pageNum, Integer sort, String permission) {

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
     * @param reviseMemberInfoParam 修改社团成员信息和权限的参数实体类
     * @return 返回处理的结果
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于事务处理回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<String> reviseMemberInfo(ReviseMemberInfoParam reviseMemberInfoParam) throws DateBaseException {

        Integer organizationId = 1;

        //获取权限列表，并将其存入map
        if (permissionMap.size() == 0) {
            for (Permission permissionPo : permissionMapper.selectList(new QueryWrapper<>())) {
                permissionMap.put(permissionPo.getId(), permissionPo.getName());
            }
        }

        /*
          获取permission权限在社团中对应的社团权限角色role
         */
        //查找出permission对应的权限id
        Permission permissionByValue = permissionMapper.selectOne(
                new QueryWrapper<Permission>().eq("name", reviseMemberInfoParam.getPermission()).last("limit 1")
        );
        if (permissionByValue == null) {
            log.error("修改社团成员信息接口异常，社团成员权限不存在，不存在的社团成员权限：{}，社团组织id：{}，成员用户id：{}",
                    reviseMemberInfoParam.getPermission(), organizationId, reviseMemberInfoParam.getId());
            return CommonResult.fail("请求失败，社团成员权限不存在");
        }
        Integer permissionId = permissionByValue.getId();

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
        if (updateNum != 1) {
            log.error("修改社团成员信息接口异常，更新社团成员权限角色出错，更新数不正确，更新数：{}，用户id：{}，社团组织id：{}，修改权限角色id：{}",
                    updateNum, reviseMemberInfoParam.getId(), organizationId, role.getId());
            throw new DateBaseException("数据库更新数据异常");
        }

        /*
          修改社团成员信息
         */
        UserB userB = new UserB();
        userB.setId(reviseMemberInfoParam.getId());
        userB.setStudentId(reviseMemberInfoParam.getStudentId());
        userB.setUserName(reviseMemberInfoParam.getName());

        updateNum = userBMapper.updateById(userB);
        if (updateNum != 0) {
            log.error("修改社团成员信息接口异常，更新社团成员信息出错，更新数不正确，更新数：{}，社团组织id：{}，更新的成员信息：{}",
                    updateNum, organizationId, userB);
            throw new DateBaseException("数据库更新数据异常");
        }

        return CommonResult.success("请求成功，社团成员信息以修改");
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
