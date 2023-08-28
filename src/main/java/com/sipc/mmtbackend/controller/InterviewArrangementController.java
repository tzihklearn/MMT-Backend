package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.interviewArrangement.*;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.AddressAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.IAAllResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.MessageCheckResult;
import com.sipc.mmtbackend.pojo.dto.result.interviewArrangement.SiftBarResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.service.InterviewArrangementService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author tzih
 * @version v1.0
 * @since 2023.08.23
 */
@RestController
@RequestMapping("/b/interview/arrangement")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@BPermission(PermissionEnum.COMMITTEE)
public class InterviewArrangementController {

    private final InterviewArrangementService interviewArrangementService;

    @PostMapping("/schedule/manual")
    public CommonResult<String> manualSchedule(@RequestBody ScheduleParam scheduleParam) {
        return interviewArrangementService.manualSchedule(scheduleParam);
    }

    @PostMapping("/schedule/automatic")
    public CommonResult<String> automaticSchedule(@RequestBody ScheduleParam scheduleParam) {
        return interviewArrangementService.automaticSchedule(scheduleParam);
    }

    @PostMapping("/address/save")
    public CommonResult<AddressAllResult> saveAddress(@RequestBody SaveAddressParam saveAddressParam) throws DateBaseException {
        return interviewArrangementService.saveAddress(saveAddressParam);
    }

    @PostMapping("/address/deleted")
    public CommonResult<AddressAllResult> deletedAddress(@RequestBody DeletedAddressParam deletedAddressParam) throws DateBaseException {
        return interviewArrangementService.deletedAddress(deletedAddressParam);
    }

    @GetMapping("/address/all")
    public CommonResult<AddressAllResult> addressAll(@RequestParam Integer round) {
        return interviewArrangementService.addressAll(round);
    }

    @GetMapping("/sift/bar")
    public CommonResult<SiftBarResult> siftBar(@RequestParam Integer round) {
        return interviewArrangementService.siftBar(round);
    }

    @GetMapping("/all")
    public CommonResult<IAAllResult> all(@RequestParam Integer page, @RequestParam Integer pageNum, @RequestParam Integer round) {
        return interviewArrangementService.all(page, pageNum, round);
    }

    @GetMapping("/sift")
    public CommonResult<IAAllResult> sift(@RequestParam Integer page, @RequestParam Integer pageNum, @RequestParam Integer round, @RequestBody SiftParam siftParam) {
        return interviewArrangementService.sift(page, pageNum, round, siftParam);
    }

    @GetMapping("/message/check")
    public CommonResult<MessageCheckResult> messageCheck(@RequestParam Integer round) {
        return interviewArrangementService.messageCheck(round);
    }

    @PostMapping("/message/send")
    public CommonResult<String> messageSend(@RequestBody MessageSendParam messageSendParam) {
        return interviewArrangementService.messageSend(messageSendParam);
    }

}
