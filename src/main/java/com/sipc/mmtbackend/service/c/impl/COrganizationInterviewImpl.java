package com.sipc.mmtbackend.service.c.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipc.mmtbackend.common.Constant;
import com.sipc.mmtbackend.config.DirectRabbitConfig;
import com.sipc.mmtbackend.controller.c.UpdateUserInfoController;
import com.sipc.mmtbackend.mapper.*;
import com.sipc.mmtbackend.mapper.c.*;
import com.sipc.mmtbackend.pojo.c.domain.*;
import com.sipc.mmtbackend.pojo.c.param.*;
import com.sipc.mmtbackend.pojo.c.result.*;
import com.sipc.mmtbackend.pojo.domain.*;
import com.sipc.mmtbackend.pojo.dto.CommonResult;

import com.sipc.mmtbackend.pojo.dto.resultEnum.ResultEnum;
import com.sipc.mmtbackend.service.c.CacheService;
import com.sipc.mmtbackend.service.c.COrganizationInterviewService;
import com.sipc.mmtbackend.utils.JsonUtil;
import com.sipc.mmtbackend.utils.RedisUtil;
import com.sipc.mmtbackend.utils.checkRoleUtils.CheckRole;
import com.sipc.mmtbackend.utils.checkRoleUtils.param.CheckResultParam;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * @Author meishuhao
 * @Date 2022/8/28
 * @Version 3.2
 */
@Service
public class COrganizationInterviewImpl implements COrganizationInterviewService {

    Logger log = Logger.getLogger("OrganizationInterviewImpl");

    @Resource
    private AdmissionMapper admissionMapper;
    @Resource
    private DefaultQuestionStateMapper defaultQuestionStateMapper;
    @Resource
    private RegistrationFromMapper registrationFromMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserDepartmentRegistrationMapper userDepartmentRegistrationMapper;
    @Resource
    private UserCMapper userCMapper;
    @Resource
    private RegistrationFromDataMapper registrationFromDataMapper;
    @Resource
    private RegistrationFromJsonMapper registrationFromJsonMapper;
    @Resource
    private AcaMajorMapper acaMajorMapper;
    @Resource
    private InterviewStatusMapper interviewStatusMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private OrganizationMapper organizationMapper;

    @Resource
    private AdmissionQuestionMapper admissionQuestionMapper;

    @Resource
    private QuestionDataMapper questionDataMapper;

    @Resource
    UpdateUserInfoController updateUserInfoController;
    @Resource
    private CacheService cacheService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final AtomicInteger atomicInteger =new AtomicInteger(1000);
    @Resource
    JsonUtil jsonUtil;

    /**
     * 报名表提交
     *
     * @param registrationFormParam 社团报名信息体
     * @return 通用返回
     */
    @Override
    public CommonResult<RegistrationFormParam> setRegistrationForm(RegistrationFormParam registrationFormParam,
                                                                                HttpServletRequest request,
                                                                                HttpServletResponse response) {
        log.info("数据校验成功");
//        C 端登录检测
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END, null);
        if (!check.isResult()) {
            return CommonResult.fail(check.getErrcode(), check.getErrmsg());
        }
        String openId = check.getData();

//        String openId = "asdas";
//      用 openid 获得 userId
        Integer userId = userCMapper.selectIdByOpenId(openId);
//
//        userId = 303;
//
//        openId ="asd";
//        int userId = atomicInteger.get();


        if (openId != null) {


            //存入Registration_from_json表

//            //存入Registration_from_json表
//            RegistrationFormJson registrationFormJson = new RegistrationFormJson();
//            String jsonStr = jsonUtil.serializationJson(registrationFormParam);
//            Date date = new Date();
//
//            registrationFormJson.setAdmissionId(registrationFormParam.getAdmissionId());
//            registrationFormJson.setJson(jsonStr);
//            registrationFormJson.setTime(date.getTime() / 1000);
//            registrationFormJson.setIsReallocation(registrationFormParam.getAllowReallocation());
//            try {
//                rwLock.writeLock().lock();
//
//                registrationFormJson.setUserId(userId);
//
//                if (registrationFromJsonMapper.selectByAdmissionIdAndUserId(
//                        registrationFormParam.getAdmissionId(), userId) != 0
//                ) {
//                    registrationFromJsonMapper.updateByUserIdAndAdmissionId(registrationFormJson);
//                } else {
//
//                        registrationFromJsonMapper.insert(registrationFormJson);
//
//                }
//            } finally {
//                rwLock.writeLock().unlock();
//
//            }
//            //存入用户基本信息
//            UserInfo userInfo = new UserInfo();
//            userInfo.setId(userId);
//            userInfo.setPhone(registrationFormParam.getEssentialInformation().getPhone());
//            userInfo.setEmail(registrationFormParam.getEssentialInformation().getMail());
//            userInfo.setQq(registrationFormParam.getEssentialInformation().getQq());
//            userInfo.setBirthday(registrationFormParam.getEssentialInformation().getBirthday());
//            userInfo.setHeight(registrationFormParam.getEssentialInformation().getHeight());
//            userInfo.setWeight(registrationFormParam.getEssentialInformation().getWeight());
//            userInfoMapper.updateById(userInfo);
//            //存入用户志愿信息
//            /*
//              汤子涵改动
//             */
//            userDepartmentRegistrationMapper.deleteByUserIdAndAdmissionId(userId,
//                    registrationFormParam.getAdmissionId());
//            interviewStatusMapper.deleteByUserIdAndAdmissionId(userId, registrationFormParam.getAdmissionId());
//            List<Integer> orderId = new ArrayList<>(Arrays.asList(registrationFormParam.getUserSign().getFirstOrder(),
//                    registrationFormParam.getUserSign().getSecondOrder(),
//                    registrationFormParam.getUserSign().getThirdOrder()));
//            try {
//                rwLock.writeLock().lock();
//                for (int i = 0; i < 3; i++) {
//                    if (orderId.get(i) != null) {
//                        setOrder(userId, registrationFormParam, orderId.get(i),
//                                registrationFormParam.getOrganizationOrder(), i);
//                        interviewStatusMapper.insertUserIdAndUserIdAndRoundAndAdmissionIdAndDepartmentId(userId, 1,
//                                registrationFormParam.getAdmissionId(), orderId.get(i), registrationFormParam.getOrganizationOrder(), i+1);
//                    }
//                }
//                for (AnswerData answer : registrationFormParam.getQuestionAnswerList()) {
//                    List<CRegistrationFormData> answerData = registrationFromDataMapper.selectByUserIdAndFieldId(userId,
//                            answer.getQuestionId());
//                    if (!answerData.isEmpty()) {
//                        registrationFromDataMapper.deleteByPrimaryKey(userId, answer.getQuestionId());
//                    }
//                    RegistrationFromData registrationFormData = new RegistrationFromData();
//                    registrationFormData.setUserId(userId);
//                    registrationFormData.setAdmissionQuestionId(answer.getQuestionId());
//                    registrationFormData.setData(answer.getAnswer());
//                    registrationFromDataMapper.insert(registrationFormData);
//                }
//            } finally {
//                ++userId;
//                rwLock.writeLock().unlock();
//            }
            try {
//                rwLock.writeLock().lock();

                Boolean isR = redisUtil.hashPutIf("registration" + registrationFormParam.getAdmissionId(), String.valueOf(userId), 1);

                if (Boolean.TRUE.equals(isR)) {
                    RegistrationFormParamPo registrationFormParamPo = new RegistrationFormParamPo(registrationFormParam, userId);
                    //将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
//            rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE_NAME, DirectRabbitConfig.ROUTING_KEY, registrationFormParamPo,
//                    //配置死信队列，消息过期时间5s
//                    message -> {
//                        message.getMessageProperties().setExpiration("10000");
//                        return message;
//                    }
//            );


                    rabbitTemplate.convertAndSend(DirectRabbitConfig.EXCHANGE_NAME, DirectRabbitConfig.ROUTING_KEY, registrationFormParamPo,
                            //配置死信队列，消息过期时间5s
                    message -> {
                        message.getMessageProperties().setExpiration("1000000");
                        return message;
                    }
                    );
                } else {
                    return CommonResult.fail("您已提交,请勿重复提交");
                }

//                atomicInteger.incrementAndGet();
//                redisTemplate.opsForValue().increment("message_is_send");
            } finally {
//                rwLock.writeLock().unlock();
            }


            return CommonResult.success();
        } else {
            //用户鉴权失败
            log.warning("用户鉴权错误");
            return CommonResult.loginError();
        }
    }

    /**
     * 获取社团面试问题及回答
     *
     * @param admissionID 纳新id
     * @return 社团面试轮次返回体
     */
    @Override
    public CommonResult<OrganizationQuestionResult> getOrganizationQuestion(Integer admissionID,
                                                                            HttpServletRequest request,
                                                                            HttpServletResponse response) throws JsonProcessingException {
        log.info("数据校验成功");
        // C 端登录检测
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END, null);
        if (!check.isResult()) {
            return CommonResult.fail(check.getErrcode(), check.getErrmsg());
        }
        String openId = check.getData();
//        String openId = "asd";
        if (openId != null) {
//            List<RegistrationForm> registrationForms = registrationFromMapper.selectByAdmissionId(admissionID);

            List<AdmissionQuestion> admissionQuestionList;
            Object o = redisUtil.get("admissionQuestionList" + admissionID);
            if (o == null) {
                admissionQuestionList =
                        admissionQuestionMapper.selectList(new QueryWrapper<AdmissionQuestion>().eq("admission_id", admissionID));
                redisUtil.set("admissionQuestionList" + admissionID, admissionQuestionList, 1L, TimeUnit.HOURS);

            } else {
               admissionQuestionList = (List<AdmissionQuestion>) o;
            }

            Map<Integer, QuestionData> questionDataMap = new HashMap<>();
            Object o1 = redisUtil.get("questionDataMap");
            if (o1 == null) {
                for (QuestionData questionData : questionDataMapper.selectList(new QueryWrapper<>())) {
                    questionDataMap.put(questionData.getId(), questionData);
                }

                redisUtil.set("questionDataMap", questionDataMap, 1L, TimeUnit.HOURS);

            } else {
                questionDataMap = (Map<Integer, QuestionData>) o1;
            }


            List<OrganizationQuestionData> organizationQuestion = new ArrayList<>();
            OrganizationQuestionResult result = new OrganizationQuestionResult();
            for (AdmissionQuestion amount : admissionQuestionList) {
//                if (amount.getRemark() == -2) {
//                    continue;
//                }

//                QuestionData questionData = questionDataMapper.selectById(amount.getQuestionId());
                QuestionData questionData = questionDataMap.get(amount.getQuestionId().toString());
                if (questionData == null) {
                    continue;
                }

                //处理问题数据
                OrganizationQuestionData questionDataTemp = new OrganizationQuestionData();
                if (questionData.getSelectTypeId() == 1) {
                    //选择题
                    questionDataTemp.setSelection(true);

                    ObjectMapper objectMapper = new ObjectMapper();

                    Object[] sArray = objectMapper.readValue(questionData.getValue(), List.class).toArray();
//                    String question = sArray[0].toString();

                    questionDataTemp.setDescription(questionData.getQuestion());
                    questionDataTemp.setOption(Arrays.toString(sArray).substring(1, Arrays.toString(sArray).length() -1));
                } else if (questionData.getSelectTypeId() == 4) {
                    questionDataTemp.setDescription(questionData.getQuestion());
                    questionDataTemp.setSelection(false);
                }
                questionDataTemp.setQuestionOrder(amount.getOrder());
                questionDataTemp.setQuestionId(amount.getId());

                switch (amount.getQuestionType()) {
                    case 1:
                        questionDataTemp.setType(String.valueOf(-1));
                        break;
                    case 3:
                        questionDataTemp.setType(String.valueOf(0));
                        break;
                    case 2:
                        questionDataTemp.setType(String.valueOf(amount.getDepartmentId()));
                        break;
                }

//                questionDataTemp.setType(String.valueOf(amount.getRemark()));
                organizationQuestion.add(questionDataTemp);
            }
            result.setQuestionDataList(organizationQuestion);
            log.info("数据处理完成 向前端返回数据");
            return CommonResult.success(result);
        } else {
            return CommonResult.loginError();
        }
    }

    /**
     * 获取纳新基本问题
     *
     * @param admissionId 纳新id
     * @return 社团面试轮次返回体
     */
    @Override
    public CommonResult<OrganizationBasicQuestionResult> getAdmissionBasicSign(Integer admissionId,
                                                                               HttpServletRequest request,
                                                                               HttpServletResponse response) {
        log.info("数据校验成功");
        // C 端登录检测
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END, null);
        if (!check.isResult()) {
            return CommonResult.fail(check.getErrcode(), check.getErrmsg());
        }
        String openId = check.getData();
//        String openId = "asd";
        // 用 openid 获得 userId
        Integer userId = userCMapper.selectIdByOpenId(openId);

//        Integer userId = 303;
        if (openId != null) {

            //置入面试基本信息
            OrganizationBasicQuestionResult result = new OrganizationBasicQuestionResult();
            Admission admission = cacheService.getAdmissionCache(admissionId);
            if (admission == null) {
                cacheService.deleteAdmissionCache(admissionId);
                admission = cacheService.getAdmissionCache(admissionId);
            }

            if (admission == null) {
                log.warning("社团基本纳新问题接口异常，数据库出错，admission表异常，admissionId:" + admissionId);
                return CommonResult.serverError();
            }

            result.setAdmissionId(admissionId);
//            result.setAllowReallocation(admission.getAllowReallocation());
            result.setAllowReallocation(admission.getRounds());
            result.setAllowDepartmentAmount(admission.getAllowDepartmentAmount());
            List<DepartmentResult> departmentResults = cacheService.organizationDepartmentMergeCache(admissionId);
            result.setOrganizationAllowDepartment(departmentResults);
            UserBasicInformation userBasicInformation = new UserBasicInformation();
            //置入用户基本信息
            UserInfo userInfo = userInfoMapper.selectById(userId);
            userBasicInformation.setName(userInfo.getName());
            userBasicInformation.setUserId(Integer.valueOf(userInfo.getStudentId()));
            userBasicInformation.setPhone(userInfo.getPhone());
            if (userInfo.getGander() == 1) {
                userBasicInformation.setGender("男");
            } else {
                userBasicInformation.setGender("女");
            }

            AcaMajor acaMajor;
            Object o1 = redisUtil.get("acaMajor" + userInfo.getAcaMajorId());
            if (o1 == null) {
                acaMajor = acaMajorMapper.selectById(userInfo.getAcaMajorId());
                redisUtil.set("acaMajor" + userInfo.getAcaMajorId(), acaMajor, 1L, TimeUnit.HOURS);
            } else {
                acaMajor = (AcaMajor) o1;
            }

//            AcaMajor acaMajor = acaMajorMapper.selectById(userInfo.getAcaMajorId());
            userBasicInformation.setAcademy(acaMajor.getAcademy());
            userBasicInformation.setMajor(acaMajor.getMajor());
            userBasicInformation.setClassNum(userInfo.getClassNum());
            result.setUserBasicInformation(userBasicInformation);
            //处理预设问题
            List<String> generalQuestion = new ArrayList<>();

            DefaultQuestionState defaultQuestionState;
            Object o = redisUtil.get("defaultQuestionState" + admissionId);
            if (o == null) {
                defaultQuestionState = defaultQuestionStateMapper.selectByAdmissionIdOrderById(admissionId);
                redisUtil.set("defaultQuestionState" + admissionId, defaultQuestionState, 1L, TimeUnit.HOURS);
            } else {
                defaultQuestionState = (DefaultQuestionState) o;
            }

//            DefaultQuestionState defaultQuestionState =
//                    defaultQuestionStateMapper.selectByAdmissionIdOrderById(admissionId);
            if (defaultQuestionState != null) {
                if (defaultQuestionState.getQq() != null) {
                    generalQuestion.add("QQ");
                }
                if (defaultQuestionState.getBirthday() != null) {
                    generalQuestion.add("生日");
                }
                if (defaultQuestionState.getHeight() != null) {
                    generalQuestion.add("身高");
                }
                if (defaultQuestionState.getWeight() != null) {
                    generalQuestion.add("体重");
                }
            }
            result.setGeneralQuestions(generalQuestion);
            return CommonResult.success(result);
        } else {
            return CommonResult.loginError();
        }
    }

    /**
     * 获取社团面试基本问题及答案
     *
     * @param admissionId 纳新id
     * @return 社团面试轮次返回体
     */
    @Override
    public CommonResult<OrganizationQuestionAnswerResult> getOrganizationQuestionAnswer(Integer admissionId,
                                                                                        HttpServletRequest request,
                                                                                        HttpServletResponse response) {
        log.info("数据校验成功");
        // C 端登录检测
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END, null);
        if (!check.isResult()) {
            return CommonResult.fail(check.getErrcode(), check.getErrmsg());
        }
        String openId = check.getData();
//        String openId = "asd";
        // 用 openid 获得 userId
        Integer userId = userCMapper.selectIdByOpenId(openId);
//        Integer userId = 303;
        int answerAmount = 0;
        if (openId != null) {
            List<OrganizationQuestionAnswerData> organizationQuestionAnswerDataList = new ArrayList<>();
//            List<RegistrationForm> registrationForms = registrationFromMapper.selectByAdmissionId(admissionId);


            List<AdmissionQuestion> admissionQuestionList;
            Object o = redisUtil.get("admissionQuestionList" + admissionId);
            if (o == null) {
                admissionQuestionList =
                        admissionQuestionMapper.selectList(new QueryWrapper<AdmissionQuestion>().eq("admission_id", admissionId));
                redisUtil.set("admissionQuestionList" + admissionId, admissionQuestionList, 1L, TimeUnit.HOURS);

            } else {
                admissionQuestionList = (List<AdmissionQuestion>) o;
            }

            Map<Integer, QuestionData> questionDataMap = new HashMap<>();
            Object o1 = redisUtil.get("questionDataMap");
            if (o1 == null) {
                for (QuestionData questionData : questionDataMapper.selectList(new QueryWrapper<>())) {
                    questionDataMap.put(questionData.getId(), questionData);
                }

                redisUtil.set("questionDataMap", questionDataMap, 1L, TimeUnit.HOURS);

            } else {
                questionDataMap = (Map<Integer, QuestionData>) o1;
            }

//            List<AdmissionQuestion> admissionQuestionList = admissionQuestionMapper.selectList(new QueryWrapper<AdmissionQuestion>().eq("admission_id", admissionId));

            for (AdmissionQuestion admissionQuestion : admissionQuestionList) {

                List<RegistrationFromData> registrationFromData = registrationFromDataMapper.selectList(
                        new QueryWrapper<RegistrationFromData>()
                                .eq("user_id", userId)
                                .eq("admission_question_id", admissionQuestion.getQuestionId())
                );

//                QuestionData questionData = questionDataMapper.selectById(admissionQuestion.getQuestionId());

                QuestionData questionData = questionDataMap.get(admissionQuestion.getQuestionId().toString());

                if (registrationFromData.isEmpty()) continue;
                if (registrationFromData.size() != 1) return CommonResult.serverError();
                OrganizationQuestionAnswerData temp = new OrganizationQuestionAnswerData();
                temp.setQuestionId(admissionQuestion.getQuestionId());

                switch (admissionQuestion.getQuestionType()) {
                    case 1:
                        temp.setType(String.valueOf(-1));
                        break;
                    case 3:
                        temp.setType(String.valueOf(0));
                        break;
                    case 2:
                        temp.setType(String.valueOf(admissionQuestion.getDepartmentId()));
                        break;
                }

//                temp.setType(String.valueOf(form.getRemark()));
                temp.setQuestionOrder(admissionQuestion.getOrder());
                temp.setDescription(questionData.getQuestion());

                temp.setSelection(questionData.getType() == 1 || questionData.getType() == 2 || questionData.getType() == 3);

                temp.setSelection(questionData.getType().equals("1"));

                temp.setAnswer(registrationFromData.get(0).getData());
                organizationQuestionAnswerDataList.add(temp);
                answerAmount++;
            }

//            for (RegistrationForm form : registrationForms) {
//                List<CRegistrationFormData> registrationFormData =
//                        registrationFromDataMapper.selectByUserIdAndFieldId(userId, form.getId());
//                if (registrationFormData.size() == 0) continue;
//                if (registrationFormData.size() != 1) return CommonResult.serverError();
//                OrganizationQuestionAnswerData temp = new OrganizationQuestionAnswerData();
//                temp.setQuestionId(form.getId());
//                temp.setType(String.valueOf(form.getRemark()));
//                temp.setQuestionOrder(form.getQuestionOrder());
//                temp.setDescription(form.getQuestion());
//                temp.setSelection(form.getType().equals("1"));
//                temp.setAnswer(registrationFormData.get(0).getData());
//                organizationQuestionAnswerDataList.add(temp);
//                answerAmount++;
//            }
            OrganizationQuestionAnswerResult result = new OrganizationQuestionAnswerResult();
            if (answerAmount != admissionQuestionList.size()) {
                result.setStatus("该用户并未完成所有回答");
            } else {
                result.setStatus("一切正常");
            }
            result.setOrganizationQuestionAnswerList(organizationQuestionAnswerDataList);
            return CommonResult.success(result);
        } else {
            return CommonResult.loginError();
        }
    }

    /**
     * 设置社团面试通知消息
     *
     * @return 通用返回
     */
    @Override
    public CommonResult<NoData> interviewMessageC(Integer admissionId, HttpServletRequest request,
                                                  HttpServletResponse response) {
        log.info("数据校验成功");
        // C 端登录检测
        CheckResultParam check = CheckRole.check(request, response, Constant.C_END, null);
        if (!check.isResult()) {
            return CommonResult.fail(check.getErrcode(), check.getErrmsg());
        }
        String openId = check.getData();
        // 用 openid 获得 userId
        Integer userId = userCMapper.selectIdByOpenId(openId);
        if (openId != null) {
            Admission admission = admissionMapper.selectById(admissionId);
            Organization organization = organizationMapper.selectById(admission.getOrganizationId());
//            messageMapper.insertMessage("已将报名表信息发送给" + organization.getName(), System.currentTimeMillis() / 1000, 0,
//                    -1, admission.getOrganizationId(), userId, 3, 0);

            messageMapper.insertMessage("已将报名表信息发送给" + organization.getName(), LocalDateTime.now(), 0,
                    -1, admission.getOrganizationId(), userId, 3, 0);
            return CommonResult.success();
        } else {
            return CommonResult.loginError();
        }
    }

    /**
     * 判断是否可报名该组织
     */
    @Override
    public CommonResult<Boolean> check(HttpServletRequest request, HttpServletResponse response
            , Integer admissionId) {

        // 验证登录状态，获取 userID
        CommonResult<IsCertificationParam> isCertificationParamCommonResult =
                updateUserInfoController.updateUserInfo(request, response);
        if (!Objects.equals(isCertificationParamCommonResult.getCode(), ResultEnum.SUCCESS.getCode())) {
            return CommonResult.fail(isCertificationParamCommonResult.getCode(),
                    isCertificationParamCommonResult.getMessage());
        }
        if (!isCertificationParamCommonResult.getData().getIs_certification())
            return CommonResult.fail("A0140", "用户未认证");
        Integer userId = isCertificationParamCommonResult.getData().getUserId();

        boolean result = true;

        Object hash = redisUtil.getHash("registration" + admissionId, userId.toString());

        if (hash != null) {
            result = false;
            return CommonResult.success(result);
        }

        for (Integer u : userDepartmentRegistrationMapper.selectUserIdsByAdmissionId(admissionId)) {
            if (Objects.equals(u, userId)) {
                result = false;
                break;
            }
        }
        return CommonResult.success(result);
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

    public Integer TrueFalseToInteger(boolean b) {
        return b ? 1 : 0;
    }

    public boolean IntegerToTrueFalse(Integer b) {
        return b != 0;
    }

    private void commonQuestionInsert(Integer admissionId, Integer remark, RegistrationForm registrationForm,
                                      boolean selection, String description, OptionData option, Integer questionOrder) {
        //type:1-选择题 2-填空题
        if (selection) {
            registrationForm.setType("1");
        } else {
            registrationForm.setType("2");
        }
        if (selection) {
            registrationForm.setQuestion(description + '=' + option.toString());
        } else {
            registrationForm.setQuestion(description);
        }
        registrationForm.setAdmissionId(admissionId);
        registrationForm.setRemark(remark);
        registrationForm.setQuestionOrder(questionOrder);
        registrationFromMapper.insert(registrationForm);
    }

//    private void insertQuestionForm(Integer admissionId, Integer remark, List<QuestionData> organizationQuestions) {
//        //remark:-2为内置问题，-1为基本问题，0为综合问题，标注部门id为部门问题
//        for (QuestionData q : organizationQuestions) {
//            RegistrationForm registrationForm = new RegistrationForm();
//            commonQuestionInsert(admissionId, remark, registrationForm, q.isSelection(), q.getDescription(),
//                    q.getOption(), q.getQuestionOrder());
//        }
//    }
//
//    private void insertDepartmentQuestionForm(Integer admissionId,
//                                              List<DepartmentQuestionData> departmentOrganizationQuestions) {
//        for (DepartmentQuestionData q : departmentOrganizationQuestions) {
//            RegistrationForm registrationForm = new RegistrationForm();
//            commonQuestionInsert(admissionId, q.getDepartmentId(), registrationForm, q.isSelection(),
//                    q.getDescription(), q.getOption(), q.getQuestionOrder());
//        }
//    }


}
