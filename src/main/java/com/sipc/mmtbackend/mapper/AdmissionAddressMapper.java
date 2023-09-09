package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.AdmissionAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tzih
 * @since 2023-08-13
 */
@Mapper
public interface AdmissionAddressMapper extends BaseMapper<AdmissionAddress> {

    String selectAddressById(Integer id);

}
