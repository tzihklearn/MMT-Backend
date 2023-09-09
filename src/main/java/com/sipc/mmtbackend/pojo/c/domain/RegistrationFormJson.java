package com.sipc.mmtbackend.pojo.c.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class RegistrationFormJson {

    @TableField("user_id")
    private Integer userId;

    @TableField("admission_id")
    private Integer admissionId;

    @TableField("json")
    private String json;

    @TableField("time")
    private Long time;

    @TableField("is_reallocation")
    private Integer isReallocation;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", organizationId=").append(admissionId);
        sb.append(", json=").append(json);
        sb.append(", time=").append(time);
        sb.append("]");
        return sb.toString();
    }
}