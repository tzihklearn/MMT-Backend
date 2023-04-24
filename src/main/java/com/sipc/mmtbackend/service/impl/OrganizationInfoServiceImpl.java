package com.sipc.mmtbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sipc.mmtbackend.iService.OrganizationTagMergeIService;
import com.sipc.mmtbackend.iService.TagIService;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.dto.CommonResult;
import com.sipc.mmtbackend.pojo.dto.param.DepartmentData;
import com.sipc.mmtbackend.pojo.dto.param.OrganizationInfoParam;
import com.sipc.mmtbackend.pojo.dto.param.TagData;
import com.sipc.mmtbackend.pojo.exceptions.DateBaseException;
import com.sipc.mmtbackend.pojo.exceptions.RunException;
import com.sipc.mmtbackend.service.OrganizationInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author tzih
 * @version v1.0
 * @since 2023.04.23
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class OrganizationInfoServiceImpl implements OrganizationInfoService {


    private final OrganizationMapper organizationMapper;

    private final OrganizationTagMergeMapper organizationTagMergeMapper;

    private final OrganizationTagMergeIService organizationTagMergeIService;

    private final TagMapper tagMapper;

    private final TagIService tagIService;

    private final OrganizationRecruitMapper organizationRecruitMapper;

    private final OrganizationDepartmentMergeMapper organizationDepartmentMergeMapper;

    private final DepartmentMapper departmentMapper;

    /**
     * Spring AOP代理造成的，因为只有当事务方法被当前类以外的代码调用时，才会由Spring生成的代理对象来管理
     * @param organizationInfoParam 更新社团宣传信息接口的请求体参数类
     * @return 返回社团请求信息处理结果
     * @throws DateBaseException 自定义的数据库操作异常，抛出用于事务回滚
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized CommonResult<String> updateOrganizationInfo(OrganizationInfoParam organizationInfoParam) throws DateBaseException, RunException {

        /*
          更新社团基本信息表
         */
        Organization organization = new Organization();
        organization.setId(organizationInfoParam.getOrganizationId());
        organization.setName(organizationInfoParam.getName());
        organization.setDescription(organizationInfoParam.getBriefIntroduction());
        int updateNum = organizationMapper.updateById(organization);
        if (updateNum != 1) {
            log.error("更新社团宣传信息接口异常，更新社团信息数出错，更新社团信息数：{}，操作社团id：{}",
                    updateNum, organizationInfoParam.getOrganizationId());
            throw new DateBaseException("数据库更新数据异常");
        }

        /*
          处理标签
         */
        if (organizationInfoParam.getTagList() != null) {
//            //删除之前的社团标签数据。目前来说，只好删除之前的全部记录，因为不好修改具体的标签
//            organizationTagMergeMapper.delete(new QueryWrapper<OrganizationTagMerge>()
//                    .eq("organization_id", organizationInfoParam.getOrganizationId()));

            //利用stream流对TagList作排序，使得其为从系统标签到自定义标签排序
            organizationInfoParam.setTagList(
                    organizationInfoParam.getTagList().stream().sorted(Comparator.comparing(TagData::getType).reversed()).collect(Collectors.toList())
            );

            //获取当前社团标签
            List<OrganizationTagMerge> organizationTagMerges = organizationTagMergeMapper.selectList(
                    new QueryWrapper<OrganizationTagMerge>()
                            .eq("organization_id", organizationInfoParam.getOrganizationId())
                            .orderByAsc("tag_type")
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
            int i = 0;
            for (TagData tagData : organizationInfoParam.getTagList()) {
//
//                if (tagData.getId() != null) {
//                    for (OrganizationTagMerge organizationTagMerge : organizationTagMerges) {
//                        if (tagData.getId().equals(organizationTagMerge.getTagId())) {
//                            boolean remove = organizationTagMerges.remove(organizationTagMerge);
//                            if (!remove) {
//                                log.info("删除社团已有标签出错，出错社团id：{}，出错标签id：{}",
//                                        organizationInfoParam.getOrganizationId(), organizationTagMerge.getTagId());
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
                    organizationTagMerge.setOrganizationId(organizationInfoParam.getOrganizationId());
                    organizationTagMerge.setTagId(tag.getId());
                    organizationTagMerge.setTagType((byte) 1);

                    //没有社团标签数据
                    if (organizationTagMerges == null || i >= size) {
                        //插入社团标签数据
                        int insertNum = organizationTagMergeMapper.insert(organizationTagMerge);
                        if (insertNum != 1) {
                            log.error("更新社团宣传信息接口异常，插入社团的系统标签数出错，插入社团的系统标签数：{}，操作社团id：{}，操作标签id：{}",
                                    insertNum, organizationInfoParam.getOrganizationId(), tag.getId());
                            throw new DateBaseException("数据库插入数据异常");
                        }
                        ++i;
                    }
                    //还有社团标签数据，且标签为系统标签
                    else {
                        OrganizationTagMerge organizationTagMerge1 = organizationTagMerges.get(i);
                        //判断已有社团标签为系统标签
                        if (organizationTagMerge1.getTagType() == 1) {
                            //更新社团标签
                            updateNum = organizationTagMergeMapper.update(organizationTagMerge,
                                    new QueryWrapper<OrganizationTagMerge>()
                                            .eq("organization_id", organizationInfoParam.getOrganizationId())
                                            .eq("tag_id", organizationTagMerge1.getTagId())
                            );
                            if (updateNum != 1) {
                                log.error("更新社团宣传信息接口异常，更新社团系统标签数出错，更新社团系统标签数：{}，操作社团id：{}，更新系统标签id：{}，被更换系统标签id：{}",
                                        updateNum, organizationInfoParam.getOrganizationId(), tagData.getTag(), organizationTagMerge1.getTagId());
                                throw new DateBaseException("数据库更新数据异常");
                            }
                            ++i;
                        }
                    }

                }
                //处理自定义标签
                else if (tagData.getType() == 2){
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
                    organizationTagMerge.setOrganizationId(organizationInfoParam.getOrganizationId());
                    organizationTagMerge.setTagId(tag.getId());
                    organizationTagMerge.setTagType((byte) 2);

                    //如果当前社团自定义标签不同于更新自定义标签，进行更新
                    if (i < size && organizationTagMerges != null
                            && !Objects.equals(tag.getId(), organizationTagMerges.get(i).getTagId())) {
                        updateNum = organizationTagMergeMapper.update(organizationTagMerge,
                                new UpdateWrapper<OrganizationTagMerge>()
                                        .eq("organization_id", organizationInfoParam.getOrganizationId())
                                        .eq("tag_id", organizationTagMerges.get(i).getTagId()));
                        if (updateNum != 1) {
                            log.error("更新社团宣传信息接口异常，更新社团的自定义标签数出错，更新社团的系统标签数：{}，操作社团id：{}，更新标签id：{}，被更新标签id：{}",
                                    updateNum, organizationInfoParam.getOrganizationId(), tag.getId(),
                                    organizationTagMerges.get(i).getTagId());
                            throw new DateBaseException("数据库插入数据异常");
                        }
                        ++i;
                    }
                    //如果当前社团自定义标签不存在，插入自定义标签
                    else if (i >= size) {
                        //插入
                        int insertNum = organizationTagMergeMapper.insert(organizationTagMerge);
                        if (insertNum != 1) {
                            log.error("更新社团宣传信息接口异常，插入社团的自定义标签数出错，插入社团的系统标签数：{}，操作社团id：{}，操作标签id：{}",
                                    insertNum, organizationInfoParam.getOrganizationId(), tag.getId());
                            throw new DateBaseException("数据库插入数据异常");
                        }
                    }

                }
            }
        }

        /*
          设置宣传信息
         */
        //拼接organizationRecruit（社团宣传信息实体类）
        OrganizationRecruit organizationRecruit = new OrganizationRecruit();
        organizationRecruit.setOrganizationId(organizationInfoParam.getOrganizationId());
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
                    updateNum, organizationInfoParam.getOrganizationId());
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
                            .eq("organization_id", organizationInfoParam.getOrganizationId())
                            .orderByAsc("department_id")
            );
            for (DepartmentData departmentData : organizationInfoParam.getDepartmentList()) {
                //处理有id的部门
                if (departmentData.getId() != null) {
                    //拼装纳新部门信息实体类
                    Department department = new Department();
                    department.setId(departmentData.getId());
                    department.setName(department.getName());
                    department.setBriefDescription(departmentData.getBriefIntroduction());
                    department.setDescription(department.getDescription());
                    department.setStandard(department.getStandard());

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
                    Department department = new Department();
                    department.setName(department.getName());
                    department.setBriefDescription(departmentData.getBriefIntroduction());
                    department.setDescription(department.getDescription());
                    department.setStandard(department.getStandard());

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
                    organizationDepartmentMerge.setOrganizationId(organizationInfoParam.getOrganizationId());
                    organizationDepartmentMerge.setDepartmentId(department.getId());
                    insertNum = organizationDepartmentMergeMapper.insert(organizationDepartmentMerge);
                    if (insertNum != 1) {
                        log.error("更新社团宣传信息接口异常，新增社团要参加纳新的部门数出错，新增社团要参加纳新的部门数：{}，新增社团要参加纳新的部门数的社团id：{}，新增社团要参加纳新的部门id：{}",
                                insertNum, organizationInfoParam.getOrganizationId(), department.getId());
                        throw new DateBaseException("数据库插入操作异常");
                    }
                }

            }

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
}
