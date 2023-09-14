package com.sipc.mmtbackend.mapper;

import com.sipc.mmtbackend.pojo.c.domain.po.Message.MessagePo;
import com.sipc.mmtbackend.pojo.domain.InterviewStatus;
import com.sipc.mmtbackend.pojo.domain.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public interface MessageMapper extends BaseMapper<Message> {
    Boolean insertMessage(String message, LocalDateTime time, Integer state, Integer isRead, Integer organizationId,
                          Integer userId, Integer type, Integer interviewStatusId);

    ArrayList<MessagePo> selectNewestMessageByUserId(Integer userId);

    Integer selectCountForUnreadMessage(Integer organizationId, Integer id);

    List<Message> selectMessageByOrganizationIdAndUserId(Integer id, Integer userId);

    int updateIsRead(Integer messageId);

    Message selectByPrimaryId(Integer messageId);

    int updateState(Integer messageId, Integer status);

    int insertRAddress(@Param("list") List<InterviewStatus> list, @Param("organizationId") Integer organizationId, @Param("time") LocalDateTime time);
}
