package com.sipc.mmtbackend.pojo.dto.enums;

import lombok.Getter;
import lombok.ToString;

/**
 * 面试轮次与其中文名对应
 *
 * @author DoudiNCer
 */
@Getter
@ToString
public enum InterviewRoundEnum {
    FIRST_ROUND(1, "第一轮面试"),
    SECOND_ROUND(2, "第二轮面试"),
    THIRD_ROUND(3, "第三轮面试");
    private final Integer id;
    private final String name;

    InterviewRoundEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 获取面试轮次对应的 InterviewRoundEnum
     *
     * @param id 面试轮次
     * @return 对应的 InterviewRoundEnum，无对应值返回 null
     */
    public static InterviewRoundEnum checkRound(Integer id){
        switch (id){
            case 1:
                return InterviewRoundEnum.FIRST_ROUND;
            case 2:
                return InterviewRoundEnum.SECOND_ROUND;
            case 3:
                return InterviewRoundEnum.THIRD_ROUND;
            default:
                return null;
        }
    }
}
