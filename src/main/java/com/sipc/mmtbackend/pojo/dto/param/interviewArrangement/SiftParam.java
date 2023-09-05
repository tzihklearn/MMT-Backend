package com.sipc.mmtbackend.pojo.dto.param.interviewArrangement;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class SiftParam {

    private List<Integer> departmentIdList;

    private List<Integer> admissionAddressList;

    private List<Integer> messageStatusList;

    private String search;

}
