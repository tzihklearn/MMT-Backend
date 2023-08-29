package com.sipc.mmtbackend.pojo.dto.param.interviewreview;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.29
 */
@Data
public class SiftParam {

    private Integer page;

    private Integer departmentId;

    private Integer addressId;

    private Byte isSort;

    private List<Integer> stateList;

    private List<Integer> messageStateList;

    private String search;

}
