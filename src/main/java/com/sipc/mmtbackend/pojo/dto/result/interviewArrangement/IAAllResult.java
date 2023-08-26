package com.sipc.mmtbackend.pojo.dto.result.interviewArrangement;

import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.IAInfoPo;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class IAAllResult {

    private List<IAInfoPo> iaInfoPos;

    private Integer page;

    private Integer pageNum;

    private Integer allNum;

}
