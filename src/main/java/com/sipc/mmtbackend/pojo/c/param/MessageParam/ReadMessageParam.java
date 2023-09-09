package com.sipc.mmtbackend.pojo.c.param.MessageParam;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ReadMessageParam {
    @NotNull(message = "消息ID列表不能为空")
    private @Valid List<Id> messageIds = new ArrayList<>();

    public List<Integer> getMessageIds() {
        List<Integer> ids = new ArrayList<>();
        for (Id messageId : messageIds) {
            ids.add(messageId.getMessageId());
        }
        return ids;
    }


}

@Data
class Id {
    @Min(value = 1, message = "消息ID格式错误")
    @NotNull(message = "消息ID格式错误")
    private Integer messageId;
}