package com.sipc.mmtbackend.service.c.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sipc.mmtbackend.mapper.AcaMajorMapper;
import com.sipc.mmtbackend.mapper.MajorClassMapper;
import com.sipc.mmtbackend.pojo.c.result.GetAcademyResult;
import com.sipc.mmtbackend.pojo.c.result.GetClassNumResult;
import com.sipc.mmtbackend.pojo.c.result.GetMajorResult;
import com.sipc.mmtbackend.pojo.c.result.IdNameResult;
import com.sipc.mmtbackend.pojo.domain.AcaMajor;
import com.sipc.mmtbackend.pojo.domain.MajorClass;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.service.c.GetAcademyMajorClassService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GetAcademyMajorClassServiceImpl implements GetAcademyMajorClassService {
    @Resource
    AcaMajorMapper acaMajorMapper;
    @Resource
    MajorClassMapper majorClassMapper;

    /**
     * @return 返回学院
     * @apiNote 获取学院信息 B-member
     */
    @Override
    @Cacheable(value = "getAcademy")
    public CommonResult<GetAcademyResult> getAcademy() {
        List<AcaMajor> acaMajors = acaMajorMapper.selectAllAcademy();
        if (acaMajors == null)
            return CommonResult.fail("服务器数据有误");
        GetAcademyResult result = new GetAcademyResult();
        for (AcaMajor acaMajor : acaMajors) {
            IdNameResult aca = new IdNameResult(acaMajor.getAcaId(), acaMajor.getAcademy());
            result.getAcademy().add(aca);
        }
        return CommonResult.success(result);

    }

    /**
     * @param academyId 学院ID
     * @return 返回专业
     * @apiNote 获取专业信息 B-member
     */
    @Override
    @Cacheable(value = "getMajor", key = "#root.args")
    public CommonResult<GetMajorResult> getMajor(Integer academyId) {
        List<AcaMajor> acaMajors = acaMajorMapper.selectList(new QueryWrapper<AcaMajor>().eq("aca_id", academyId));
        if (acaMajors == null)
            return CommonResult.fail("学院不存在");
        GetMajorResult result = new GetMajorResult();
        for (AcaMajor acaMajor : acaMajors) {
            IdNameResult mjr = new IdNameResult(acaMajor.getId(), acaMajor.getMajor());
            result.getMajor().add(mjr);
        }
        return CommonResult.success(result);
    }

    /**
     * @param academyId 学院ID
     * @param majorId   专业ID
     * @return 返回班级
     * @apiNote 获取专业的所有班级 B-member
     */
    @Override
    @Cacheable(value = "getClassNum", key = "#root.args")
    public CommonResult<GetClassNumResult> getClassNum(Integer academyId, Integer majorId) {
        if (acaMajorMapper.selectByAcaIdAndMajorId(academyId, majorId) == null)
            return CommonResult.fail("学院专业有误");
        List<MajorClass> majorClasses = majorClassMapper.selectList(new QueryWrapper<MajorClass>().eq("major_id", majorId));
        if (majorClasses == null)
            return CommonResult.fail("服务器数据有误");
        GetClassNumResult result = new GetClassNumResult();
        for (MajorClass mc : majorClasses) {
            IdNameResult cs = new IdNameResult(mc.getId(), mc.getClassNum());
            result.getClassNum().add(cs);
        }
        return CommonResult.success(result);
    }
}
