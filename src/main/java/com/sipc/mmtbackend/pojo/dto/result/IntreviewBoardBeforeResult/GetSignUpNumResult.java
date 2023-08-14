package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardBeforeResult;

import lombok.Data;

/**
 * 获取报名总人数与第一志愿人数的 result
 *
 * @author DoudiNCer
 */
@Data
public class GetSignUpNumResult {
    private Integer totalNum;
    private Integer firstChoiceNum;
}
