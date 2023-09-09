package com.sipc.mmtbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sipc.mmtbackend.pojo.domain.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author DoudiNCer
 * @since 2023-05-06
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    List<String> selectNameByTagIds(@Param("tagIds") List<Integer> tagIds);

}
