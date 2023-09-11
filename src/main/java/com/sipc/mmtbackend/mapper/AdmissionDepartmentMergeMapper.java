package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.c.result.DepartmentResult;
import com.sipc.mmtbackend.pojo.domain.AdmissionDepartmentMerge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tzih
 * @since 2023-08-13
 */
@Mapper
public interface AdmissionDepartmentMergeMapper extends BaseMapper<AdmissionDepartmentMerge> {

    List<Integer> selectDepartmentIdByAdmissionId(Integer admissionId);

    List<DepartmentResult> selectRelationByAdmissionId(Integer admissionId);

}
