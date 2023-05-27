package com.sipc.mmtbackend.controller;

import com.sipc.mmtbackend.service.InterviewBoardService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/b/interview")
public class InterviewBoardController {
    @Resource
    InterviewBoardService interviewBoardService;
}
