package com.sipc.mmtbackend.pojo.dto.result.interviewArrangement;

import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.po.AddressPo;
import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class AddressAllResult {

    private List<AddressPo> addressPoList;

}
