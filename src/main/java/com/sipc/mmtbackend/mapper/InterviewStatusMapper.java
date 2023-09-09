package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.InterviewStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tzih
 * @since 2023-08-15
 */
@Mapper
public interface InterviewStatusMapper extends BaseMapper<InterviewStatus> {

    List<InterviewStatus> selectAllArrange(Integer studentId);

    Integer deleteByUserIdAndAdmissionId(Integer userId, Integer admissionId);

    Integer insertUserIdAndUserIdAndRoundAndAdmissionIdAndDepartmentId(
            Integer userId, Integer round, Integer admissionId, Integer departmentId, Integer organizationOrder, Integer departmentOrder
    );

}
