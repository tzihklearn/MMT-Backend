package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.data.DepartmentData;
import com.sipc.mmtbackend.pojo.dto.data.TagData;
import com.sipc.mmtbackend.pojo.dto.param.superAdmin.OrganizationInfoParam;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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

    private final OrganizationDepartmentMergeMapper organizationDepartmentMergeMapper;

    private final DepartmentMapper departmentMapper;

    private final HttpServletRequest httpServletRequest;

    private final PictureUtil pictureUtil;

    private final HttpServletRequest request;


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
//        organization.setName(organizationInfoParam.getName());
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
//            //删除之前的社团标签数据。目前来说，只好删除之前的全部记录，因为不好修改具体的标签
//            organizationTagMergeMapper.delete(new QueryWrapper<OrganizationTagMerge>()
//                    .eq("organization_id", organizationId));

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

//            Iterator<OrganizationTagMerge> organizationTagMergeIterator = organizationTagMerges.listIterator();

            //遍历请求里的标签参数
            int typeI = 0;
            int typeJ = 2;
            for (TagData tagData : organizationInfoParam.getTagList()) {

//                if (tagData.getId() != null) {
//                    for (OrganizationTagMerge organizationTagMerge : organizationTagMerges) {
//                        if (tagData.getId().equals(organizationTagMerge.getTagId())) {
//                            boolean remove = organizationTagMerges.remove(organizationTagMerge);
//                            if (!remove) {
//                                log.info("删除社团已有标签出错，出错社团id：{}，出错标签id：{}",
//                                        organizationId, organizationTagMerge.getTagId());
//                                throw new RunException("ada");
//                            }
//                            break;
//                        }
//                    }
//                }

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
//                    Tag tag = new Tag();
//                    tag.setName(tagData.getTag());
//                    tag.setType(tagData.getType());
//                    //插入或更新
//                    boolean isUpdate = tagIService.saveOrUpdate(tag,
//                            new QueryWrapper<Tag>().eq("name", tagData.getTag()).eq("type", tagData.getType()));
//                    if (!isUpdate) {
//                        log.error("更新社团宣传信息接口异常，插入或更新自定义标签数出错，插入自定义标签名：{}", tagData.getTag());
//                        throw new DateBaseException("数据库插入数据异常");
//                    }

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
        //更新数据库数据
        updateNum = organizationRecruitMapper.updateById(organizationRecruit);
        if (updateNum != 1) {
            log.error("更新社团宣传信息接口异常，更新社团宣传信息数出错，更新社团宣传信息数：{}，更新社团id：{}",
                    updateNum, organizationId);
            throw new DateBaseException("数据库更新操作异常");
        }

        /*
          处理社团部门
         */
        if (organizationInfoParam.getDepartmentList() != null) {
            /*
             设置社团纳新部门信息
            */
            //获取当前社团纳新部门的数据。
            List<OrganizationDepartmentMerge> organizationDepartmentMerges = organizationDepartmentMergeMapper.selectList(
                    new QueryWrapper<OrganizationDepartmentMerge>()
                            .eq("organization_id", organizationId)
                            .orderByAsc("department_id")
            );
            //遍历请求来的社团数据
            for (DepartmentData departmentData : organizationInfoParam.getDepartmentList()) {
                //处理有id的部门
                Department department = new Department();
                if (departmentData.getId() != null) {
                    //拼装纳新部门信息实体类
                    department.setId(departmentData.getId());
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
                    for (OrganizationDepartmentMerge organizationDepartmentMerge : organizationDepartmentMerges) {
                        if (organizationDepartmentMerge.getDepartmentId().equals(departmentData.getId())) {
                            organizationDepartmentMerges.remove(organizationDepartmentMerge);
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
                    department.setName(departmentData.getName());
                    department.setBriefDescription(departmentData.getBriefIntroduction());
                    department.setDescription(departmentData.getIntroduction());
                    department.setStandard(departmentData.getStandard());

                    //插入纳新部门信息
                    int insertNum = departmentMapper.insert(department);
                    if (insertNum != 1) {
                        log.error("更新社团宣传信息接口异常，插入纳新部门信息数出错，插入纳新部门信息数：{}，插入新纳新部门名称：{}",
                                insertNum, department.getName());
                        throw new DateBaseException("数据库插入操作异常");
                    }

                    //设置社团要纳新的部门
                    //拼装社团要参加纳新的部门的实体类
                    OrganizationDepartmentMerge organizationDepartmentMerge = new OrganizationDepartmentMerge();
                    organizationDepartmentMerge.setOrganizationId(organizationId);
                    organizationDepartmentMerge.setDepartmentId(department.getId());
                    insertNum = organizationDepartmentMergeMapper.insert(organizationDepartmentMerge);
                    if (insertNum != 1) {
                        log.error("更新社团宣传信息接口异常，新增社团要参加纳新的部门数出错，新增社团要参加纳新的部门数：{}，新增社团要参加纳新的部门数的社团id：{}，新增社团要参加纳新的部门id：{}",
                                insertNum, organizationId, department.getId());
                        throw new DateBaseException("数据库插入操作异常");
                    }
                }

            }

            //删除原有社团剩下的不在请求里的纳新部门和社团的联系
            for (OrganizationDepartmentMerge organizationDepartmentMerge : organizationDepartmentMerges) {
                int deleteNum = organizationDepartmentMergeMapper.deleteById(organizationDepartmentMerge);
                if (deleteNum != 1) {
                    log.error("更新社团宣传信息接口异常，删除社团要参加纳新的部门数出错，删除社团要参加纳新的部门数：{}，删除社团要参加纳新的部门数的社团id：{}，删除社团要参加纳新的部门id：{}",
                            deleteNum, organizationDepartmentMerge.getOrganizationId(), organizationDepartmentMerge.getDepartmentId());
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
//                if (!isAdd) {
//                    log.warn("获取社团宣传信息异常，社团标签信息列表添加信息失败，出错的社团id：{}，未添加的标签id：{}",
//                            organizationId, organizationTagMerge.getTagId());
//                }
            }
        }

        /*
          获取社团的纳新部门
         */
        //获取对应的社团纳新部门id列表
        List<OrganizationDepartmentMerge> organizationDepartmentMergeList = organizationDepartmentMergeMapper.selectList(
                new QueryWrapper<OrganizationDepartmentMerge>().eq("organization_id", organizationId)
        );

        //社团纳新部门纳新宣传信息列表
        List<DepartmentData> departmentDataList = new ArrayList<>();
        //循环处理对应的社团纳新部门id，并获取相应的纳新部门的纳新宣传信息
        if (organizationDepartmentMergeList != null) {
            for (OrganizationDepartmentMerge organizationDepartmentMerge : organizationDepartmentMergeList) {
                Department department = departmentMapper.selectById(organizationDepartmentMerge.getDepartmentId());
                if (department == null) {
                    log.error("获取社团宣传信息异常，社团组织纳新部门表的纳新部门id在部门表中不存在，相应的社团id：{}，不存在的纳新部门id：{}",
                            organizationId, organizationDepartmentMerge.getDepartmentId());
                } else {
                    departmentDataList.add(new DepartmentData(
                            department.getId(), department.getName(), department.getBriefDescription(),
                            department.getDescription(), department.getStandard()
                    ));
                }
            }
        }

        //拼装要返回的社团纳新宣传信息实体类对象
        OrganizationInfoResult organizationInfoResult = new OrganizationInfoResult();
        organizationInfoResult.setName(organization.getName());
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
}
