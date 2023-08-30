package com.sipc.mmtbackend.pojo.dto.result.interviewreview;

import com.sipc.mmtbackend.pojo.dto.result.interviewreview.po.AddressPo;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.28
 */
@Data
public class AddressResult {

    private List<AddressPo> addressPoList;

    private Integer round;

}
