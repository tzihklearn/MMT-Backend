package com.sipc.mmtbackend.pojo.dto.result.superAdmin;

import com.sipc.mmtbackend.pojo.dto.data.MemberInfoData;
import lombok.Data;

import java.util.List;

/**
 * 社团成员列表相关接口的返回类
 * @author tzih
 * @version v1.0
 * @since 2023.05.21
 * @see MemberInfoData
 */
@Data
public class MemberInfoResult {

    /**
     * 社团成员信息列表
     */
    private List<MemberInfoData> memberInfoDataList;

}
