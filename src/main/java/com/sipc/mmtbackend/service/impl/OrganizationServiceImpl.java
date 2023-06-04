package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.data.DepartmentData;
import com.sipc.mmtbackend.pojo.dto.data.QuestionPoData;
import com.sipc.mmtbackend.pojo.dto.data.QuestionValueData;
import com.sipc.mmtbackend.pojo.dto.data.TagData;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.OrganizationInfoParam;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.OrganizationPublishParam;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.OrganizationInfoResult;
import com.sipc.mmtbackend.pojo.dto.result.superAdmin.UploadAvatarResult;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;
import com.sipc.mmtbackend.service.OrganizationService;
import com.sipc.mmtbackend.utils.CheckroleBUtil.pojo.BTokenSwapPo;
import com.sipc.mmtbackend.utils.PictureUtil.PictureUtil;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.DefaultPictureIdEnum;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.PictureUsage;
import com.sipc.mmtbackend.utils.PictureUtil.pojo.UsageEnum;
import com.sipc.mmtbackend.utils.ThreadLocalContextUtil;
import com.sipc.mmtbackend.utils.TimeTransUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 有关超级管理的社团宣传与面试的功能的业务处理
 *
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {


    private final OrganizationMapper organizationMapper;

    private final OrganizationTagMergeMapper organizationTagMergeMapper;

    private final TagMapper tagMapper;

    private final OrganizationRecruitMapper organizationRecruitMapper;

    private final AdmissionMapper admissionMapper;

    private final DepartmentMapper departmentMapper;

    private final AdmissionQuestionMapper admissionQuestionMapper;

    private final QuestionDataMapper questionDataMapper;

    private final HttpServletRequest httpServletRequest;

    private final PictureUtil pictureUtil;


    /**
     * 设置社团宣传信息的业务处理方法，处理设置社团宣传信息
     *
     * @param organizationInfoParam 更新社团宣传信息接口的请求体参数类
     * @return 返回社团请求信息处理结果
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于事务回滚
     * @throws RunException      自定义的运行异常，抛出用于事务回滚
     *                           Spring AOP代理造成的，因为只有当事务方法被当前类以外的代码调用时，才会由Spring生成的代理对象来管理
     * @see OrganizationInfoParam
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized CommonResult<String> updateOrganizationInfo(OrganizationInfoParam organizationInfoParam) throws DateBaseException, RunException {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        /*
          更新社团基本信息表
         */
        Organization organization = new Organization();
        organization.setId(organizationId);

        organization.setDescription(organizationInfoParam.getBriefIntroduction());
        int updateNum = organizationMapper.updateById(organization);
        if (updateNum != 1) {
            log.error("更新社团宣传信息接口异常，更新社团信息数出错，更新社团信息数：{}，操作社团id：{}",
                    updateNum, organizationId);
            throw new DateBaseException("数据库更新数据异常");
        }

        /*
          处理标签
         */
        if (organizationInfoParam.getTagList() != null) {
            //删除之前的社团标签数据。目前来说，只好删除之前的全部记录，因为不好修改具体的标签


            //利用stream流对TagList作排序，使得其为从系统标签到自定义标签排序,Comparator.comparing(TagData::getType).reversed()从大到小输出
            organizationInfoParam.setTagList(
                    organizationInfoParam.getTagList().stream().sorted(Comparator.comparing(TagData::getType)).collect(Collectors.toList())
            );

            //获取当前社团标签
            List<OrganizationTagMerge> organizationTagMerges = organizationTagMergeMapper.selectList(
                    new QueryWrapper<OrganizationTagMerge>()
                            .eq("organization_id", organizationId)
                            .orderByAsc("tag_type")
                            .orderByAsc("tag_id")
            );

            //获取当前社团标签数
            int size = 0;
            if (organizationTagMerges != null) {
                size = organizationTagMerges.size();
                if (size != 0 && (size > 3 || size < 2)) {
                    log.warn("社团标签数异常，当前社团标签数：{}", organizationTagMerges.size());
                    organizationTagMerges = null;
                }
            }

            //遍历请求里的标签参数
            int typeI = 0;
            int typeJ = 2;
            for (TagData tagData : organizationInfoParam.getTagList()) {

                //处理系统标签
                if (tagData.getType() == 1) {

                    //获取tag的Id
                    Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>()
                            .select("id")
                            .eq("name", tagData.getTag())
                            .eq("type", 1)
                            .last("limit 1"));

                    OrganizationTagMerge organizationTagMerge = new OrganizationTagMerge();
                    organizationTagMerge.setOrganizationId(organizationId);
                    organizationTagMerge.setTagId(tag.getId());
                    organizationTagMerge.setTagType((byte) 1);

                    //没有社团标签数据
                    if (organizationTagMerge.getTagId() == null || typeI >= size) {
                        //插入社团标签数据
                        int insertNum = organizationTagMergeMapper.insert(organizationTagMerge);
                        if (insertNum != 1) {
                            log.error("更新社团宣传信息接口异常，插入社团的系统标签数出错，插入社团的系统标签数：{}，操作社团id：{}，操作标签id：{}",
                                    insertNum, organizationId, tag.getId());
                            throw new DateBaseException("数据库插入数据异常");
                        }
                    }
                    //还有社团标签数据，且标签为系统标签
                    else {
                        OrganizationTagMerge organizationTagMerge1 = organizationTagMerges.get(typeI);
                        //判断已有社团标签为系统标签
                        if (organizationTagMerge1.getTagType() == 1 &&
                                !organizationTagMerge.getTagId().equals(organizationTagMerge1.getTagId())) {
                            //更新社团标签
                            updateNum = organizationTagMergeMapper.update(organizationTagMerge,
                                    new QueryWrapper<OrganizationTagMerge>()
                                            .eq("id", organizationTagMerge1.getId())
                            );
                            if (updateNum != 1) {
                                log.error("更新社团宣传信息接口异常，更新社团系统标签数出错，更新社团系统标签数：{}，操作社团id：{}，更新系统标签id：{}，被更换系统标签id：{}",
                                        updateNum, organizationId, tagData.getTag(), organizationTagMerge1.getTagId());
                                throw new DateBaseException("数据库更新数据异常");
                            }
                        }
                    }
                    ++typeI;

                }
                //处理自定义标签
                else if (tagData.getType() == 2) {

                    //查找标签信息
                    Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>()
                            .select("id")
                            .eq("name", tagData.getTag())
                            .eq("type", 2)
                            .last("limit 1")
                    );
                    //如果在数据库中查找不到标签信息，插入标签信息
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagData.getTag());
                        tag.setType((byte) 2);
                        int insertNum = tagMapper.insert(tag);
                        if (insertNum != 1) {
                            log.error("更新社团宣传信息接口异常，插入或更新自定义标签数出错，插入自定义标签名：{}", tagData.getTag());
                            throw new DateBaseException("数据库插入数据异常");
                        }
                    }

                    //拼装
                    OrganizationTagMerge organizationTagMerge = new OrganizationTagMerge();
                    organizationTagMerge.setOrganizationId(organizationId);
                    organizationTagMerge.setTagId(tag.getId());
                    organizationTagMerge.setTagType((byte) 2);

                    //如果当前社团自定义标签不同于更新自定义标签，进行更新
                    if (typeJ < size && organizationTagMerges != null) {
                        if (!Objects.equals(tag.getId(), organizationTagMerges.get(typeJ).getTagId())) {
                            updateNum = organizationTagMergeMapper.update(organizationTagMerge,
                                    new UpdateWrapper<OrganizationTagMerge>()
                                            .eq("id", organizationTagMerges.get(typeJ).getId()));
                            if (updateNum != 1) {
                                log.error("更新社团宣传信息接口异常，更新社团的自定义标签数出错，更新社团的系统标签数：{}，操作社团id：{}，更新标签id：{}，被更新标签id：{}",
                                        updateNum, organizationId, tag.getId(),
                                        organizationTagMerges.get(typeJ).getTagId());
                                throw new DateBaseException("数据库插入数据异常");
                            }

                        }
                        ++typeJ;
                    }
                    //如果当前社团自定义标签不存在，插入自定义标签
                    else if (typeJ >= size) {
                        //插入
                        int insertNum = organizationTagMergeMapper.insert(organizationTagMerge);
                        if (insertNum != 1) {
                            log.error("更新社团宣传信息接口异常，插入社团的自定义标签数出错，插入社团的系统标签数：{}，操作社团id：{}，操作标签id：{}",
                                    insertNum, organizationId, tag.getId());
                            throw new DateBaseException("数据库插入数据异常");
                        }

                    }

                }
            }

            //处理多于的自定义标签
            if (typeJ < size) {
                for (; typeJ < size; ++typeJ) {
                    int deleteNum = organizationTagMergeMapper.deleteById(organizationTagMerges.get(typeJ).getId());
                    if (deleteNum != 1) {
                        log.error("更新社团宣传信息接口异常，删除社团多余的自定义标签数出错，删除社团多余的自定义标签数：{}，删除社团和标签的对应id：{}",
                                deleteNum, organizationTagMerges.get(typeJ).getId());
                        throw new RunException("删除社团多余的自定义标签异常");
                    }
                }
            }
        }

        /*
          设置宣传信息
         */
        //拼接organizationRecruit（社团宣传信息实体类）
        OrganizationRecruit organizationRecruit = new OrganizationRecruit();
        organizationRecruit.setOrganizationId(organizationId);
        organizationRecruit.setDescription(organizationInfoParam.getBriefIntroduction());
        organizationRecruit.setFeature(organizationInfoParam.getFeature());
        organizationRecruit.setDaily(organizationInfoParam.getDaily());
        organizationRecruit.setSlogan(organizationInfoParam.getSlogan());
        organizationRecruit.setContactInfo(organizationInfoParam.getContactInfo());
        organizationRecruit.setMore(organizationInfoParam.getMore());

        //更新数据库数据，如果数据库内没有数据，则更新数据
        OrganizationRecruit organizationRecruitNow = organizationRecruitMapper.selectOne(
                new QueryWrapper<OrganizationRecruit>()
                        .eq("organization_id", organizationId)
        );

        if (organizationRecruitNow != null) {
            updateNum = organizationRecruitMapper.updateById(organizationRecruit);
            if (updateNum != 1) {
                log.error("更新社团宣传信息接口异常，更新社团宣传信息数出错，更新社团宣传信息数：{}，更新社团id：{}",
                        updateNum, organizationId);
                throw new DateBaseException("数据库更新操作异常");
            }
        } else {
            int insertNum = organizationRecruitMapper.insert(organizationRecruit);
            if (insertNum != 1) {
                log.error("更新社团宣传信息接口异常，插入社团宣传信息数出错，插入社团宣传信息数：{}，插入社团id：{}",
                        insertNum, organizationId);
                throw new DateBaseException("数据库插入操作异常");
            }
        }

        /*
          处理社团部门
         */
        if (organizationInfoParam.getDepartmentList() != null) {
            /*
             设置社团纳新部门信息
            */
            //获取当前社团纳新部门的数据。
            List<Department> departments = departmentMapper.selectList(new QueryWrapper<Department>().eq("organization_id", organizationId));

            //遍历请求来的社团部门数据
            for (DepartmentData departmentData : organizationInfoParam.getDepartmentList()) {
                //处理有id的部门
                Department department = new Department();
                if (departmentData.getId() != null) {
                    //拼装纳新部门信息实体类
                    department.setId(departmentData.getId());
                    department.setOrganizationId(organizationId);
                    department.setName(departmentData.getName());
                    department.setBriefDescription(departmentData.getBriefIntroduction());
                    department.setDescription(departmentData.getIntroduction());
                    department.setStandard(departmentData.getStandard());

                    //更新纳新部门信息
                    updateNum = departmentMapper.updateById(department);
                    if (updateNum != 1) {
                        log.error("更新社团宣传信息接口异常，更新纳新部门信息数出错，更新纳新部门信息数：{}，更新纳新部门id：{}",
                                updateNum, departmentData.getId());
                        throw new DateBaseException("数据库更新操作异常");
                    }
                    int flag = -1;
                    for (Department departmentPo : departments) {
                        if (departmentPo.getId().equals(departmentData.getId())) {
                            departments.remove(departmentPo);
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == -1) {
                        log.error("更新社团宣传信息接口异常，传入的已经有id的部门不是当前社团纳新部门");
                        throw new RunException("传入的已经有id的部门不是当前社团纳新部门");
                    }
                }
                //处理没有id的部门，即新增的纳新部门
                else {
                    //拼装纳新部门信息实体类
                    department.setOrganizationId(organizationId);
                    department.setName(departmentData.getName());
                    department.setBriefDescription(departmentData.getBriefIntroduction());
                    department.setDescription(departmentData.getIntroduction());
                    department.setStandard(departmentData.getStandard());

                    //插入纳新部门信息和与纳新社团的对应关系
                    int insertNum = departmentMapper.insert(department);
                    if (insertNum != 1) {
                        log.error("更新社团宣传信息接口异常，插入纳新部门信息数出错，插入纳新部门信息数：{}，插入新纳新部门名称：{}",
                                insertNum, department.getName());
                        throw new DateBaseException("数据库插入操作异常");
                    }
                }
            }

            //删除原有社团剩下的不在请求里的纳新部门和社团的联系
            for (Department departmentPo : departments) {
                int deleteNum = departmentMapper.deleteById(departmentPo);
                if (deleteNum != 1) {
                    log.error("更新社团宣传信息接口异常，删除社团要参加纳新的部门数出错，删除社团要参加纳新的部门数：{}，删除社团要参加纳新的部门数的社团id：{}，删除社团要参加纳新的部门id：{}",
                            deleteNum, organizationId, departmentPo.getId());
                    throw new DateBaseException("数据库删除操作异常");
                }
            }

        }

        return CommonResult.success("操作成功");
    }

    /**
     * 获取社团宣传信息的业务处理方法
     *
     * @return CommonResult<< OrganizationInfoResult>> 返回处理的结果，包括社团纳新宣传信息
     */
    @Override
    public CommonResult<OrganizationInfoResult> getOrganizationInfo() {

         /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        /*
          获取社团基本信息表中对应的实体类对象
         */
        Organization organization = organizationMapper.selectById(organizationId);
        if (organization == null) {
            return CommonResult.fail("社团组织id不存在");
        }

        /*
          获取社团宣传信息表中对应的实体类对象
         */
        OrganizationRecruit organizationRecruit = organizationRecruitMapper.selectById(organizationId);
        if (organizationRecruit == null) {
            return CommonResult.fail("社团组织id不存在");
        }

        /*
          获取对应的社团标签
         */
        //获取对应的社团标签id列表
        List<OrganizationTagMerge> organizationTagMergeList = organizationTagMergeMapper.selectList(
                new QueryWrapper<OrganizationTagMerge>()
                        .eq("organization_id", organizationId)
                        .orderByAsc("tag_type")
                        .orderByAsc("tag_id")
        );
        //社团标签数异常的话就打印日志
        int size = 0;
        if (organizationTagMergeList == null || organizationTagMergeList.size() < 2 || organizationTagMergeList.size() > 4) {
            if (organizationTagMergeList != null) {
                size = organizationTagMergeList.size();
            }
            log.warn("获取社团宣传信息异常，社团组织标签信息数出错，社团组织标签数：{}，社团组织id：{}", size, organizationId);
        }

        //对社团标签进行排序
        if (organizationTagMergeList != null) {
            organizationTagMergeList = organizationTagMergeList.stream().sorted(Comparator.comparing(OrganizationTagMerge::getTagId)).collect(Collectors.toList());
        }

        //社团标签信息列表
        List<TagData> tagDataList = new ArrayList<>();
        //循环处理设社团标签id,并获取相应的标签名称
        if (organizationTagMergeList != null) {
            for (OrganizationTagMerge organizationTagMerge : organizationTagMergeList) {
                Tag tag = tagMapper.selectById(organizationTagMerge.getTagId());
                if (tag == null) {
                    log.error("获取社团宣传信息异常，社团组织标签表的标签id在标签表中不存在，相应的社团id：{}，不存在的标签id：{}",
                            organizationId, organizationTagMerge.getTagId());
                }

                if (tag != null) {
                    tagDataList.add(new TagData(tag.getName(), Integer.valueOf(tag.getType())));
                }
            }
        }

        /*
          获取社团的纳新部门
         */
        //社团纳新部门纳新宣传信息列表
        List<DepartmentData> departmentDataList = new ArrayList<>();
        for (Department department : departmentMapper.selectList(new QueryWrapper<Department>().eq("organization_id", organizationId))) {
            departmentDataList.add(new DepartmentData(
                        department.getId(), department.getName(), department.getBriefDescription(),
                        department.getDescription(), department.getStandard()
            ));

        }

        //拼装要返回的社团纳新宣传信息实体类对象
        OrganizationInfoResult organizationInfoResult = new OrganizationInfoResult();
        organizationInfoResult.setName(organization.getName());
        //设置社团头像信息，如果社团头像为空，则使用默认头像
        if (organization.getAvatarId() == null || organization.getAvatarId().length() == 0) {
            organizationInfoResult.setAvatarUrl(
                    pictureUtil.getPictureURL(DefaultPictureIdEnum.ORG_AVATAR.getPictureId(), true));
        } else {
            organizationInfoResult.setAvatarUrl(pictureUtil.getPictureURL(organization.getAvatarId(), false));
        }
        organizationInfoResult.setBriefIntroduction(organization.getDescription());
        organizationInfoResult.setTagList(tagDataList);
        organizationInfoResult.setIntroduction(organizationRecruit.getDescription());
        organizationInfoResult.setFeature(organizationRecruit.getFeature());
        organizationInfoResult.setDaily(organizationInfoResult.getDaily());
        organizationInfoResult.setSlogan(organizationInfoResult.getSlogan());
        organizationInfoResult.setContactInfo(organizationRecruit.getContactInfo());
        organizationInfoResult.setMore(organizationInfoResult.getMore());
        organizationInfoResult.setDepartmentList(departmentDataList);

        return CommonResult.success(organizationInfoResult);
    }


    /**
     * 上传社团头像接口
     *
     * @return CommonResult<UploadAvatarResult> 返回上传社团头像接口处理果，包含图像的url
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于事务回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<UploadAvatarResult> uploadAvatar() throws DateBaseException {

        /*
          鉴权并且获取用户所属社团组织id
         */
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        //获取token
        String token = context.getToken();

        /*
          获取相应的form-data参数
         */
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest multipartHttpServletRequest = multipartResolver.resolveMultipart(httpServletRequest);

        //获取社团头像
        MultipartFile avatar = multipartHttpServletRequest.getFile("avatar");

        //上传图像
        String pictureId = pictureUtil.uploadPicture(avatar, new PictureUsage(UsageEnum.ORG_AVATAR, token));

        /*
          更新数据库中社团头像信息
         */
        Organization organization = new Organization();

        organization.setId(organizationId);
        organization.setAvatarId(pictureId);

        int updateNum = organizationMapper.updateById(organization);
        if (updateNum != 0) {
            log.error("上传头像接口异常，更新社团信息数出错，更新数：{}，更新社团id：{}，更新头像id：{}",
                    updateNum, organizationId, pictureId);
            throw new DateBaseException("数据库更新更新操作异常");
        }

        return CommonResult.success("上传社团头像成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized CommonResult<String> publishAdmission(OrganizationPublishParam organizationPublishParam) throws DateBaseException, RunException {

        //获取操作用户信息，并获取其操作社团id
        BTokenSwapPo context = ThreadLocalContextUtil.getContext();
        Integer organizationId = context.getOrganizationId();

        //从数据库中查找是否有已经保存的信息
        Admission admissionNow = admissionMapper.selectOne(new QueryWrapper<Admission>()
                .select("id")
                .eq("organization_id", organizationId)
                .isNull("start_time")
                .orderByDesc("id")
                .last("limit 1"));

        /*
          判断是否有已经保存的纳新信息
         */

        Integer admissionId;

        if (admissionNow != null) {

            admissionId = admissionNow.getId();

            //删除已经保存的社团报名表问题信息
            admissionQuestionMapper.delete(new QueryWrapper<AdmissionQuestion>().eq("admission_id", admissionId));

        } else {
            //没有已经保存的数据，插入一个新的纳新信息
            Admission admission = new Admission();
            admission.setInitiator(context.getUserId());
            admission.setOrganizationId(organizationId);
            admission.setStartTime(LocalDateTime.now());
            admission.setEndTime(TimeTransUtil.transStringToTime(organizationPublishParam.getEndTime()));
            admission.setDepartmentNum(organizationPublishParam.getDepartmentNum());
            admission.setAllowDepartmentAmount(organizationPublishParam.getMaxDepartmentNum());
            admission.setRounds(0);
            admission.setIsDeleted((byte) 0);

            //插入新的纳新记录
            int insertNum = admissionMapper.insert(admission);
            if (insertNum != 1) {
                log.error("发布纳新接口异常，插入admission表数据数错误，插入admission表数据数：{}，插入社团id：{}，插入信息：{}",
                        insertNum, organizationId, organizationPublishParam);
                throw new DateBaseException("插入数据库操作异常");
            }

            admissionId = admission.getId();
        }

        //设置纳新报名表基本问题
        if (organizationPublishParam.getEssentialQuestionList() != null) {
            setQuestion(organizationPublishParam.getEssentialQuestionList(), organizationId, admissionId, 1);
        }

        //设置纳新报名表部门问题
        setQuestion(organizationPublishParam.getDepartmentQuestionList(), organizationId, admissionId, 2);

        //设置纳新报名表综合问题
        setQuestion(organizationPublishParam.getComprehensiveQuestionList(), organizationId, admissionId, 3);

        return CommonResult.success("发起纳新成功");
    }

    /**
     * 获取questionValueDataList的深度的方法
     * @param questionValueDataList questionValueDataList对象
     * @return 返回questionValueDataList的深度，几级级联选择器
     */
    private int getDepth(List<QuestionValueData> questionValueDataList) {
        //调用递归QuestionValueDataList获取其深度的方法，并设当前深度为0
        return recursionQuestionValueDataList(questionValueDataList, 0);
    }

    /**
     * 递归QuestionValueDataList获取其深度的方法
     * @param questionValueDataList QuestionValueDataList对象
     * @param depth 当前深度
     * @return 返回当前新的深度
     */
    private int recursionQuestionValueDataList(List<QuestionValueData> questionValueDataList, int depth) {

        //如果questionValueDataList为null，代表异常传参，直接返回当前深度
        //不需要使深度+1,使其加上这一层的深度，因为这一层是空的
        if (questionValueDataList == null) {
            return depth;
        }

//        questionDataMapper.selectMaps()

        //临时变量，用于记录新的深度
        int t;

        for (QuestionValueData questionValueData : questionValueDataList) {

            //ChildValueList不为null,代表不是最深，继续递归
            if (questionValueData.getChildValueList() != null) {
                t = recursionQuestionValueDataList(questionValueData.getChildValueList(), ++depth);
            }
            //ChildValueList为null，代表已经是最深的了，停止递归，并使深度+1,加上这一层的深度
            else {
                return ++depth;
            }

            //如果新的深度大于当前深度，更新当前深度为最新深度
            if (depth < t) {
                depth = t;
            }
        }
        return depth;
    }

    private void setQuestion(List<QuestionPoData> questionPoDataList, int organizationId, int admissionId, int questionType) throws RunException, DateBaseException {

        //记录问题次序
        int order = 0;

        Map<Integer, Boolean> departmentMap = new HashMap<>();

        //如果是部门问题，则去数据库中找出社团所有的纳新部门，并且将其放入map中
        if (questionType == 2) {
            for (Department department : departmentMapper.selectList(
                    new QueryWrapper<Department>()
                            .select("id")
                            .eq("organization_id", organizationId)
            )) {
                departmentMap.put(department.getId(), true);
            }

        }

        //迭代处理报名表基本问题列表
        for (QuestionPoData questionPoData : questionPoDataList) {

            //如果是部门问题，判断该部门在社团中是否存在，如果不存在，则跳过该问题
            if (questionType == 2 && departmentMap.get(questionPoData.getDepartmentId()) == null) {
                continue;
            }

            //如果问题选项列表为空，则跳过该问题
            if (questionPoData.getSelectValue() == null) {
                continue;
            }

            ++order;
            QuestionData questionData = new QuestionData();
            //如果是系统内置问题
            if (questionPoData.getType()) {
                questionData = questionDataMapper.selectOne(
                        new QueryWrapper<QuestionData>()
                                .eq("type", true)
                                .eq("select_type_id", questionPoData.getSelectType())
                                .eq("question", questionPoData.getName())
                );
                if (questionData == null) {
                    continue;
                }
            }
            //如果是自定义问题，则新增问题信息记录
            else {
                /*
              在question_date表中插入问题信息
             */
                questionData.setType((byte) 1);
                questionData.setSelectTypeId(questionPoData.getSelectType());
                questionData.setQuestion(questionPoData.getName());

                //将问题的选项信息转换为json字符串存储
                ObjectMapper objectMapper = new ObjectMapper();
                String json;
                try {
                    json = objectMapper.writeValueAsString(questionPoData.getSelectValue());
                } catch (JsonProcessingException e) {
                    throw new RunException("对象转换json字符串异常，异常为JsonProcessingException");
                }
                questionData.setValue(json);

                //设置非级联选择器问题的选项数
                if (questionPoData.getSelectType() != 5) {
                    //判断QuestionValueDataList是否为空，防止传入错误数据
                    if (questionPoData.getSelectValue().getQuestionValueDataList() == null) {
                        questionData.setNum(1);
                    } else {
                        questionData.setNum(questionPoData.getSelectValue().getQuestionValueDataList().size());
                    }
                }
                //设置级联选择器问题的选项数
                else {
                    //调用获取questionValueDataList的深度的方法获取深度设置为问题选项的数量
                    questionData.setNum(getDepth(questionPoData.getSelectValue().getQuestionValueDataList()));
                }
                questionData.setIsDeleted((byte) 0);

                //在question_data表中插入问题信息
                int insertNum = questionDataMapper.insert(questionData);
                if (insertNum != 1) {
                    log.error("发布纳新接口异常，插入question_date表数据数错误，插入question_date表数据数：{}，插入社团id：{}，插入问题信息：{}",
                            insertNum, organizationId, questionPoData);
                    throw new DateBaseException("插入数据库操作异常");
                }
            }

            /*
              在admission_question表中插入社团纳新报名表相关的问题信息
             */
            AdmissionQuestion admissionQuestion = new AdmissionQuestion();
            admissionQuestion.setAdmissionId(admissionId);
            //如果是部门问题，设置对于的部门id
            if (questionType == 2) {
                admissionQuestion.setDepartmentId(questionPoData.getDepartmentId());
            }
            admissionQuestion.setQuestionId(questionData.getId());
            admissionQuestion.setQuestionType(questionType);
            admissionQuestion.setOrder(order);
            admissionQuestion.setIsDeleted((byte) 0);

            int insertNum = admissionQuestionMapper.insert(admissionQuestion);
            if (insertNum != 1) {
                log.error("发布纳新接口异常，插入admission_question表数据数错误，插入admission_question表数据数：{}，插入社团id：{}，插入社团报名表问题信息：{}",
                        insertNum, organizationId, admissionQuestion);
                throw new DateBaseException("插入数据库操作异常");
            }
        }
    }


}
