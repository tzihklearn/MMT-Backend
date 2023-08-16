package com.sipc.mmtbackend.mapper.customization;

import com.sipc.mmtbackend.mapper.MajorClassMapper;
import com.sipc.mmtbackend.pojo.domain.po.MajorClassPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.17
 */
@Mapper
public interface MyMajorClassMapper extends MajorClassMapper {

    List<MajorClassPo> selectAllAndAcaMajor();

}
