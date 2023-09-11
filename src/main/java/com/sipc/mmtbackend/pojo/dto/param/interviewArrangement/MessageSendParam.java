package com.sipc.mmtbackend.pojo.dto.param.interviewArrangement;

import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.po.MessageSendPo;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class MessageSendParam {

    private List<MessageSendPo> messageSendPoList;



}
