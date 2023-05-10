package com.sipc.mmtbackend.pojo.dto.result.UserBResult;

import com.sipc.mmtbackend.pojo.dto.result.UserBResult.po.UserPermissionPo;
import lombok.Data;
import java.util.List;

@Data
public class LoginResult {
    private Integer userId;
    private String token;
    private List<UserPermissionPo> permissions;
}
