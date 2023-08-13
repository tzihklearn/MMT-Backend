package com.sipc.mmtbackend.utils.CheckinQRCodeUtil.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class QRPayloadPo implements Serializable {
    private static final long serialVersionUID = 1145141919810L;
    private Integer organizationId;
    private Integer creatorId;
}
