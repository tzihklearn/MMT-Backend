package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.PermissionMapper;
import com.sipc.mmtbackend.mapper.UserBMapper;
import com.sipc.mmtbackend.pojo.domain.Permission;
import com.sipc.mmtbackend.pojo.domain.po.UserBMemberPo;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.data.MemberInfoData;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.ICodeResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.MemberInfoResult;
import com.sipc.mmtbackend.service.AccountManageService;
import com.sipc.mmtbackend.utils.ICodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 超级管理接口的实现类
 * @author tzih
 * @version v1.0
 * @since 2023.05.20
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AccountManageServiceImpl implements AccountManageService {

    /**
     * 社团邀请码工具类
     */
    private final ICodeUtil iCodeUtil;

    private final PermissionMapper permissionMapper;

    private final UserBMapper userBMapper;

    //TODO:初步处理，以后使用Guava Cache缓存
    private final Map<Integer, String> permissionMap = new HashMap<>();

    /**
     * 生成社团邀请码，并将其放入redis中，时限10min
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
     * @param organizationId 社团组织id
     * @param pageNum 当前页数
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
     * @param organizationId 社团组织id
     * @param pageNum 当前页数
     * @param sort 学号排序， 0为正序， 1为倒序（默认为0）
     * @param permission 成员权限筛选项
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
     * 按照一定条件社团成员列表
     * @param organizationId 社团组织id
     * @param pageNum 当前页数
     * @param sort 学号排序， 0为正序， 1为倒序（默认为0）
     * @param permissionId 成员权限筛选项(0为不筛选)
     * @return 返回查找到的社团成员列表
     */
    private MemberInfoResult getMemberInfoResult(Integer organizationId, Integer pageNum, Integer sort, Integer permissionId) {
        MemberInfoResult memberInfoResult = new MemberInfoResult();
        List<MemberInfoData> list = new ArrayList<>();

        //每页分页数量
        int pageSize = 10;

        //联表查询数据库，获取社团成员信息并拼装社团成员列表
        for (UserBMemberPo userBMemberPo : userBMapper.selectMemberInfoListByOrganizationId(
                organizationId, (pageNum-1) * pageSize, pageNum * pageSize, sort , permissionId
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

        memberInfoResult.setMemberInfoData(list);

        return memberInfoResult;
    }
}
