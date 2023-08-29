package com.sipc.mmtbackend.pojo.dto.param.interviewreview;

import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.po.MessageSendPo;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.29
 */
@Data
public class SendParam {

    private List<MessageSendPo> messageSendPoList;

    private String message;

}
