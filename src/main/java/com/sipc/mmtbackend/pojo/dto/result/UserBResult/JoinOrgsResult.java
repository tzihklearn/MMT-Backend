package com.sipc.mmtbackend.pojo.dto.result.UserBResult;

import com.sipc.mmtbackend.pojo.dto.result.UserBResult.po.JoinedOrgResultPo;
import lombok.Data;
import java.util.List;
@Data
public class JoinOrgsResult {
    private Integer num;
    private List<JoinedOrgResultPo> organizations;
}
