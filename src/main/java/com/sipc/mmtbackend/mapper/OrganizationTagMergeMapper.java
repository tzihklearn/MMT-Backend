package com.sipc.mmtbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sipc.mmtbackend.pojo.domain.OrganizationTagMerge;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author tzih
 * @since 2023-05-03
 */
@Mapper
public interface OrganizationTagMergeMapper extends BaseMapper<OrganizationTagMerge> {

    List<Integer> selectTagIdByOrganizationI(Integer organizationId);

}
