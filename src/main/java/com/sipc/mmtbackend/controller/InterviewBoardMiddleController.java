package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.annotation.BPermission;
import com.sipc.mmtbackend.service.InterviewBoardMiddleService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.PermissionEnum;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/b/interview/middle")
@BPermission(PermissionEnum.MEMBER)
public class InterviewBoardMiddleController {
    @Resource
    InterviewBoardMiddleService interviewBoardMiddleService;


}
