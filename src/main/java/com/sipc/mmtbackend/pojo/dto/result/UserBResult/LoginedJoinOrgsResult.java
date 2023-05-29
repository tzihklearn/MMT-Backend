package com.sipc.mmtbackend.pojo.dto.result.UserBResult;

import com.sipc.mmtbackend.pojo.dto.result.UserBResult.po.LoginedJoinedOrgResultPo;
import lombok.Data;

import java.util.List;

@Data
public class LoginedJoinOrgsResult {
    private Integer num;
    private List<LoginedJoinedOrgResultPo> organizations;
}
