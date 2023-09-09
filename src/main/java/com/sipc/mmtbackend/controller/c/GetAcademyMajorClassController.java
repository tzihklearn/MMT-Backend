package com.sipc.mmtbackend.controller.c;

import com.sipc.mmtbackend.pojo.c.result.GetAcademyResult;
import com.sipc.mmtbackend.pojo.c.result.GetClassNumResult;
import com.sipc.mmtbackend.pojo.c.result.GetMajorResult;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.GetAcademyMajorClassService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/*
 * @author 肖琰
 */
@RestController
@RequestMapping("/c/major-info")
public class GetAcademyMajorClassController {
    @Resource
    private GetAcademyMajorClassService getAcademyMajorClassService;

    /**
     * @apiNote 获取学院信息 B-member
     * @return 返回学院
     */
    @GetMapping("/academy")
    public CommonResult<GetAcademyResult> getAcademy(){
        return getAcademyMajorClassService.getAcademy();
    }

    /**
     * @apiNote 获取专业信息 B-member
     * @param academyId 学院ID
     * @return 返回专业
     */
    @GetMapping("/major/{academyId}")
    public CommonResult<GetMajorResult> getMajor(@PathVariable Integer academyId){
        return getAcademyMajorClassService.getMajor(academyId);
    }

    /**
     * @apiNote 获取专业的所有班级 B-member
     * @param academyId 学院ID
     * @param majorId 专业ID
     * @return 返回班级
     */
    @GetMapping("/class-num/{academyId}/{majorId}")
    public CommonResult<GetClassNumResult> getClassNum(@PathVariable Integer academyId, @PathVariable Integer majorId) {
        return getAcademyMajorClassService.getClassNum(academyId, majorId);
    }
}
