package com.sipc.mmtbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sipc.mmtbackend.pojo.domain.Admission;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author tzih
 * @since 2023-05-03
 */
@Mapper
public interface AdmissionMapper extends BaseMapper<Admission> {

}
