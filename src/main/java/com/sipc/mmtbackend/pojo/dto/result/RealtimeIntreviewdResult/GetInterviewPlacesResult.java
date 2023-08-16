package com.sipc.mmtbackend.pojo.dto.result.RealtimeIntreviewdResult;

import com.sipc.mmtbackend.pojo.dto.result.po.KVPo;
import lombok.Data;

import java.util.List;

@Data
public class GetInterviewPlacesResult {
    private Integer count;
    private List<KVPo> places;
}
