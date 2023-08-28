package com.sipc.mmtbackend.service;

import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.*;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.AddressAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.IAAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.MessageCheckResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.SiftBarResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;


public interface InterviewArrangementService {

    CommonResult<String> manualSchedule(ScheduleParam scheduleParam) throws DateBaseException;

    CommonResult<String> automaticSchedule(ScheduleParam scheduleParam) throws DateBaseException;

    CommonResult<AddressAllResult> saveAddress(SaveAddressParam saveAddressParam) throws DateBaseException;

    CommonResult<AddressAllResult> deletedAddress(DeletedAddressParam deletedAddressParam) throws DateBaseException;

    CommonResult<AddressAllResult> addressAll(Integer round);

    CommonResult<SiftBarResult> siftBar(Integer round);


    CommonResult<IAAllResult> all(Integer page, Integer pageNum, Integer round);

    CommonResult<IAAllResult> sift(Integer page, Integer pageNum, Integer round, SiftParam siftParam);

    CommonResult<MessageCheckResult> messageCheck(Integer round);

    CommonResult<String> messageSend(MessageSendParam messageSendParam);

}
