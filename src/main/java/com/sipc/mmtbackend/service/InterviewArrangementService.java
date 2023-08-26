package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.MessageSendParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.ScheduleParam;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.SiftParam;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.AddressAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.IAAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.MessageCheckResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.SiftBarResult;


public interface InterviewArrangementService {



    CommonResult<String> manualSchedule(ScheduleParam scheduleParam);

    CommonResult<String> automaticSchedule(ScheduleParam scheduleParam);

    CommonResult<AddressAllResult> addressAll(Integer round);

    CommonResult<SiftBarResult> siftBar(Integer round);


    CommonResult<IAAllResult> all(Integer page, Integer pageNum, Integer round);

    CommonResult<IAAllResult> sift(Integer page, Integer pageNum, Integer round, SiftParam siftParam);

    CommonResult<MessageCheckResult> messageCheck(Integer round);

    CommonResult<String> messageSend(MessageSendParam messageSendParam);

}
