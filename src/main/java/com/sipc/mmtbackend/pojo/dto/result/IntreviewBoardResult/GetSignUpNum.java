package com.sipc.mmtbackend.pojo.dto.result.IntreviewBoardResult;

import lombok.Data;

/**
 * 获取报名总人数与第一志愿人数的 result
 *
 * @author DoudiNCer
 */
@Data
public class GetSignUpNum {
    private Integer totalNum;
    private Integer firstChoiceNum;
}
