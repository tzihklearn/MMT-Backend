package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.AcaMajor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tzih
 * @since 2023-08-17
 */
@Mapper
public interface AcaMajorMapper extends BaseMapper<AcaMajor> {

    AcaMajor selectByAcaIdAndMajorId(Integer academyId, Integer majorId);

}
