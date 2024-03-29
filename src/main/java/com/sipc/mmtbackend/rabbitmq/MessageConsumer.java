package com.sipc.mmtbackend.rabbitmq;

import com.sipc.mmtbackend.config.DirectRabbitConfig;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.c.*;
import com.sipc.mmtbackend.pojo.c.domain.CRegistrationFormData;
import com.sipc.mmtbackend.pojo.c.domain.RegistrationFormJson;
import com.sipc.mmtbackend.pojo.c.domain.UserDepartmentRegistration;
import com.sipc.mmtbackend.pojo.c.param.AnswerData;
import com.sipc.mmtbackend.pojo.c.param.RegistrationFormParam;
import com.sipc.mmtbackend.pojo.c.param.RegistrationFormParamPo;
import com.sipc.mmtbackend.pojo.domain.RegistrationFromData;
import com.sipc.mmtbackend.pojo.domain.UserInfo;
import com.sipc.mmtbackend.utils.JsonUtil;
import com.sipc.mmtbackend.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author tzih
 * @version 1.0
 * @since 2023/4/10 11:26
 */
@Slf4j
@Component
public class MessageConsumer {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserDepartmentRegistrationMapper userDepartmentRegistrationMapper;

    @Resource
    private RegistrationFromDataMapper registrationFromDataMapper;
    @Resource
    private RegistrationFromJsonMapper registrationFromJsonMapper;
    @Resource
    private InterviewStatusMapper interviewStatusMapper;

    @Resource
    private RedisTemplate redisTemplate;

    private final ReentrantReadWriteLock RFJrwLock = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    @Resource
    JsonUtil jsonUtil;

//    @RabbitListener(queues = DirectRabbitConfig.QUEUE_NAME, concurrency = "1")
    @RabbitListener(queues = DirectRabbitConfig.QUEUE_NAME, containerFactory = "batchQueueRabbitListenerContainerFactory")
    @Transactional(rollbackFor = Exception.class)
    public void setRegistrationForm(RegistrationFormParamPo registrationFormParamPo) {

        Integer userId = registrationFormParamPo.getUserId();
        RegistrationFormParam registrationFormParam = registrationFormParamPo.getRegistrationFormParam();

        log.info("开始消费，userId:{}", userId);
        log.info("开始消费，registrationFormParamPo:{}", registrationFormParamPo);

//        rwLock.readLock().lock();

        //存入Registration_from_json表
        RegistrationFormJson registrationFormJson = new RegistrationFormJson();
        String jsonStr = jsonUtil.serializationJson(registrationFormParam);
        Date date = new Date();

        registrationFormJson.setAdmissionId(registrationFormParam.getAdmissionId());
        registrationFormJson.setJson(jsonStr);
        registrationFormJson.setTime(date.getTime() / 1000);
        registrationFormJson.setIsReallocation(registrationFormParam.getAllowReallocation());
        try {
            RFJrwLock.writeLock().lock();

            registrationFormJson.setUserId(userId);

            if (registrationFromJsonMapper.selectByAdmissionIdAndUserId(
                    registrationFormParam.getAdmissionId(), userId) != 0
            ) {
                registrationFromJsonMapper.updateByUserIdAndAdmissionId(registrationFormJson);
            } else {

                registrationFromJsonMapper.insert(registrationFormJson);

            }
        } finally {
            RFJrwLock.writeLock().unlock();
        }

        //存入用户基本信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setPhone(registrationFormParam.getEssentialInformation().getPhone());
        userInfo.setEmail(registrationFormParam.getEssentialInformation().getMail());
        userInfo.setQq(registrationFormParam.getEssentialInformation().getQq());
        userInfo.setBirthday(registrationFormParam.getEssentialInformation().getBirthday());
        userInfo.setHeight(registrationFormParam.getEssentialInformation().getHeight());
        userInfo.setWeight(registrationFormParam.getEssentialInformation().getWeight());
        userInfo.setIsCertification(true);

        userInfoMapper.updateById(userInfo);


        //存入用户志愿信息
            /*
              汤子涵改动
             */
        userDepartmentRegistrationMapper.deleteByUserIdAndAdmissionId(userId,
                registrationFormParam.getAdmissionId());
        interviewStatusMapper.deleteByUserIdAndAdmissionId(userId, registrationFormParam.getAdmissionId());
        List<Integer> orderId = new ArrayList<>(Arrays.asList(registrationFormParam.getUserSign().getFirstOrder(),
                registrationFormParam.getUserSign().getSecondOrder(),
                registrationFormParam.getUserSign().getThirdOrder()));
        try {
            rwLock.writeLock().lock();
            for (int i = 0; i < 3; i++) {
                if (orderId.get(i) != null) {
                    setOrder(userId, registrationFormParam, orderId.get(i),
                            registrationFormParam.getOrganizationOrder(), i + 1);
                    interviewStatusMapper.insertUserIdAndUserIdAndRoundAndAdmissionIdAndDepartmentId(userId, 1,
                            registrationFormParam.getAdmissionId(), orderId.get(i), registrationFormParam.getOrganizationOrder(), i+1);
                }
            }
            for (AnswerData answer : registrationFormParam.getQuestionAnswerList()) {
                List<CRegistrationFormData> answerData = registrationFromDataMapper.selectByUserIdAndFieldId(userId,
                        answer.getQuestionId());
                if (!answerData.isEmpty()) {
                    registrationFromDataMapper.deleteByPrimaryKey(userId, answer.getQuestionId());
                }
                RegistrationFromData registrationFormData = new RegistrationFromData();
                registrationFormData.setUserId(userId);
                registrationFormData.setAdmissionQuestionId(answer.getQuestionId());
                registrationFormData.setData(answer.getAnswer());
                registrationFromDataMapper.insert(registrationFormData);
            }
        } finally {
            rwLock.writeLock().unlock();
        }

        log.info("消费成功，userId:{}", userId);

        redisTemplate.opsForValue().increment("message_is_end");

    }

    private void setOrder(Integer userId, RegistrationFormParam registrationFormParam, Integer departmentId,
                          Integer organizationOrder, Integer departmentOrder) {
        UserDepartmentRegistration order = new UserDepartmentRegistration();
        order.setUserId(userId);
        order.setAdmissionId(registrationFormParam.getAdmissionId());
        order.setDepartmentId(departmentId);
        order.setOrganizationOrder(organizationOrder);
        order.setDepartmentOrder(departmentOrder);
        userDepartmentRegistrationMapper.insertByExample(order);
    }

}
