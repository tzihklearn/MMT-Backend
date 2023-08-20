package com.sipc.mmtbackend.pojo.dto.result.superAdmin;

import com.sipc.mmtbackend.pojo.dto.data.MessageTemplateData;
import lombok.Data;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.19
 */
@Data
public class MessageTemplateResult {

    private MessageTemplateData interviewNotice;

    private MessageTemplateData resultSuccessNotice;

    private MessageTemplateData resultFailNotice;

}
