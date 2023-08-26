package com.sipc.mmtbackend.pojo.dto.param.interviewArrangement;

import lombok.Data;

import java.util.List;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@Data
public class ScheduleParam {

    private List<Integer> interviewIdList;

    private List<Integer> addressIdList;

    private Long startTime;

    private Long endTime;

    private Integer time;

}
