package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.c.param.IsCertificationParam;
import com.sipc.mmtbackend.pojo.c.result.UserInfoResult;
import com.sipc.mmtbackend.pojo.domain.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tzih
 * @since 2023-08-23
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    Boolean updateSecondInfo(String phoneNum, String email, String qqNum, Integer studentId);

    Boolean updateAll(Integer userId, String name, Integer studentId, Integer gender, String academy, String major, String classNum, String phoneNum, String email, String qqNum, Integer isCertification);

    Integer selectStudentIdForInject(Integer studentId);

    UserInfoResult selectAllByStudentId(Integer studentId);

    UserInfo selectUserIdByStudentId(Integer studentId);

    UserInfo selectByStudentId(Integer studentId);

    IsCertificationParam selectCertificationByUserId(Integer userId);

    Integer selectIsCertification(Integer userId);

}
