package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.c.domain.CRegistrationFormData;
import com.sipc.mmtbackend.pojo.domain.RegistrationFromData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tzih
 * @since 2023-08-23
 */
@Mapper
public interface RegistrationFromDataMapper extends BaseMapper<RegistrationFromData> {

    List<CRegistrationFormData> selectByUserIdAndFieldId(Integer userId, Integer fieldId);

    int deleteByPrimaryKey(Integer userId, Integer fieldId);

}
