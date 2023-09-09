package com.sipc.mmtbackend.pojo.c.param.MessageParam;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

@Data
public class SubmitStateParam {

    @Min(value = 1, message = "消息ID格式错误")
    private Integer messageId;

    /**
     * 1同意，2拒绝，3待定（时间冲突）
     */
    @Range(min = 1, max = 3, message = "状态代码格式错误")
    private Integer state;
}
