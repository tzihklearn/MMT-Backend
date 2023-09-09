package com.sipc.mmtbackend.pojo.c.domain;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class CRegistrationFormData {
    private Integer userId;

    private Integer fieldId;

    private String data;

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", userId=").append(userId);
        sb.append(", fieldId=").append(fieldId);
        sb.append(", data=").append(data);
        sb.append("]");
        return sb.toString();
    }
}