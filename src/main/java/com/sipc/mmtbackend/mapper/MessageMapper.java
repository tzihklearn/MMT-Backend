package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.domain.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tzih
 * @since 2023-08-23
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}
