package com.sipc.mmtbackend.mapper.customization;

import com.sipc.mmtbackend.mapper.AdmissionAddressMapper;
import com.sipc.mmtbackend.pojo.domain.AdmissionDepartmentMerge;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyAdmissionAddressMapper extends AdmissionAddressMapper {

    AdmissionDepartmentMerge selectDepartmentAndId(Integer id);

}
