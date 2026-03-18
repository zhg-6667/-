package com.hk.simba.license.service.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.google.common.collect.Maps;
import com.hk.base.exception.BusinessException;
import com.hk.base.util.DateUtils;
import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.base.distributedlock.DistributedLock;
import com.hk.simba.base.distributedlock.DistributedLockFactory;
import com.hk.simba.license.api.enums.OrderTypeEnum;
import com.hk.simba.license.api.enums.PayRecordStatusEnum;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.enums.ViolationTypeEventEnum;
import com.hk.simba.license.api.request.retrain.RetrainInvalidRequest;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.DeductTypeEnum;
import com.hk.simba.license.service.constant.enums.DepartmentTypeEnum;
import com.hk.simba.license.service.constant.enums.PositionTypeEnum;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.entity.ViolationType;
import com.hk.simba.license.service.manager.ServantManager;
import com.hk.simba.license.service.mq.dto.EventAttachment;
import com.hk.simba.license.service.mq.dto.EventResponsibility;
import com.hk.simba.license.service.mq.dto.MessageBO;
import com.hk.simba.license.service.mq.dto.MessageEntity;
import com.hk.simba.license.service.service.AppealService;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.simba.license.service.service.MailService;
import com.hk.simba.license.service.service.MqMessageService;
import com.hk.simba.license.service.service.RetrainService;
import com.hk.simba.license.service.service.SiteViolationService;
import com.hk.simba.license.service.service.ViolationMailService;
import com.hk.simba.license.service.service.ViolationMessageService;
import com.hk.simba.license.service.service.ViolationService;
import com.hk.simba.license.service.service.ViolationTypeService;
import com.hk.simba.license.service.utils.BeanCopyUtil;
import com.hk.simba.license.service.utils.RedisUtil;
import com.hk.simba.license.service.utils.RegexUtils;
import com.hk.simba.license.service.utils.YearUtil;
import com.hk.simba.staff.open.enums.YesOrNotEnum;
import com.hk.simba.staff.open.response.StaffDetailDto;
import com.hk.simba.workorder.open.SiteOpenService;
import com.hk.simba.workorder.open.request.GetByMiddleSiteIdRequest;
import com.hk.simba.workorder.open.respone.SiteDetailDto;
import com.hk.sisyphus.merope.core.staff.StaffApi;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffSensitiveInfoByStaffIdRequest;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO;
import com.hk.sisyphus.merope.model.staff.staff.GetStaffByIdRequest;
import com.hk.sisyphus.merope.model.staff.staff.GetStaffByIdStaffBasicDTO;
import com.hk.ubs.api.RegisterService;
import com.hk.ubs.api.UserService;
import com.hk.ubs.api.vo.UserVo;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description @author 羊皮
 * @since 2020-4-15 16:33:45
 */
@Component
@Slf4j
public class EventHandler implements MessageListener {

    @Autowired
    private ViolationService violationService;
    @Autowired
    private ViolationMessageService violationMessageService;
    @Autowired
    private ViolationTypeService violationTypeService;
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private LicenseService licenseService;
    @DubboReference(version = "2.0", check = false)
    private UserService userService;
    @DubboReference(version = "2.0", check = false)
    private RegisterService registerService;

    @Value("${quality.email}")
    private String qualityEmail;

    @Autowired
    private MailService mailService;

    @Autowired
    private StaffApi staffApi;

    @DubboReference(check = false)
    private SiteOpenService siteOpenService;

    @Autowired
    private SiteViolationService siteViolationService;

    @Autowired
    private RetrainService retrainService;
    @Autowired
    private ViolationMailService violationMailService;
    @Autowired
    private AppealService appealService;
    @Autowired
    private ServantManager servantManager;

    /**
     * 违规申诉期限
     */
    @Value("${violation.deadline.amount}")
    private Integer deadlineAmount;

    /**
     * 责任部门
     */
    @Value("${responsibility.dept.name}")
    private String responsibilityDeptName;

    /**
     * 服务安全(旧的保险理赔，分类:一级)
     */
    @Value("${violation.event.safe}")
    private String violationEventType;


    /**
     * 服务标准
     */
    @Value("${service.standard.config.result.ids}")
    private String serviceStandardIds;


    /**
     * 违规事件接入事件
     */
    @Value("${violation.event.import.time}")
    private String violationEventImportTime;

    /**
     * 保险理赔
     */
    @Value("${insurance.second.config.result.ids}")
    private String insuranceSecondResultIds;


    /**
     * 安全事故
     */
    @Value("${safety.second.config.result.ids}")
    private String safetySecondResultIds;

    /**
     * 保险理赔(二级事件名称)
     */
    @Value("${insurance.event.name}")
    private String insuranceName;

    /**
     * 员工违规-服务禁忌
     */
    @Value("${staff.violation.forbidden.result.ids}")
    private String staffForbiddenResultIds;

    /**
     * 站点管理违规-人员培训不到位
     */
    @Value("${site.violation.inadequate.training.ids}")
    private String siteInadequateTrainingIds;

    @Value("${noNeedFine.flexibileStaffings}")
    private String noNeedFineFlexibileStaffings;


    private final Config messageConfig = ConfigService.getConfig(MessageConstant.MESSAGE_PROPERTIES_KEY);

    private static final String TAG_PROCESSED = "PROCESSED";

    private static final String TAG_MODIFY = "MODIFY";
    //事件处理中
    private static final int EVENT_STATUS_PROCESSING = 2;
    //事件处理完成
    private static final int EVENT_STATUS_PROCESSED = 3;

    private static final long expireTime = 3600;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Override
    @Transactional
    public Action consume(Message msg, ConsumeContext context) {
        String body = new String(msg.getBody());
        log.info("收到消息,msgid:{},topic:{},tag:{},body:{}", msg.getMsgID(), msg.getTopic(), msg.getTag(), body);

        mqMessageService.saveMessage(msg);
        if (StringUtils.isBlank(violationEventImportTime)) {
            log.info("事件导入时间不能为空");
            return Action.CommitMessage;
        }

        if (!msg.getTag().equals(TAG_PROCESSED) && !msg.getTag().equals(TAG_MODIFY)) {
            log.info("事件消息的tag不符合，不接入执照系统tag={},msgId={}", msg.getTag(), msg.getMsgID());
            return Action.CommitMessage;
        }

        MessageEntity entity = JSONObject.parseObject(body, MessageEntity.class);
        Date startTime = DateUtils.convertToDate(violationEventImportTime, "yyyy-MM-dd HH:mm:ss");
        if (entity.getCreateTime() == null || entity.getCreateTime().before(startTime)) {
            log.info("时间不符合,数据不接入createTime={}, msgId={}", entity.getCreateTime(), msg.getMsgID());
            return Action.CommitMessage;
        }

        // 处理方式整合
        List<String> processModeNames = new ArrayList<>();
        Optional.ofNullable(entity.getProcessMode1()).filter(StringUtils::isNotBlank).ifPresent(processModeNames::add);
        Optional.ofNullable(entity.getProcessMode2()).filter(StringUtils::isNotBlank).ifPresent(processModeNames::add);
        Optional.ofNullable(entity.getProcessMode3()).filter(StringUtils::isNotBlank).ifPresent(processModeNames::add);
        entity.setProcessList(processModeNames);

        String listKey = String.format("LIST:CODE:%s", entity.getId());
        List<String> list = redisUtil.getList(listKey, 0, -1);
        MessageBO bo = new MessageBO();
        bo.setMessageEntity(entity);
        bo.setMsgId(msg.getMsgID());
        bo.setTag(msg.getTag());
        if (CollectionUtils.isEmpty(list)) {
            // 消息加入队列,处理数据
            redisUtil.rPush(listKey, bo, expireTime);
            return doProcess(bo, listKey);
        } else {
            // 已有数据正在处理，加入队列后续处理
            redisUtil.rPush(listKey, bo, expireTime);
            log.warn("消息加入队列,按顺序消费，稍后处理，msgId:{},list:{}", msg.getMsgID(), list);
        }

        return Action.CommitMessage;
    }

    private Action doProcess(MessageBO bo, String listKey) {
        MessageEntity entity = bo.getMessageEntity();
        String key = String.format("CODE:%s", entity.getId());
        DistributedLock lock = distributedLockFactory.newLock(key);
        boolean isLock = lock.tryLock();
        if (isLock) {
            try {
                List<EventResponsibility> responsibilityList = entity.getEventResponsibilities();
                // 责任部门变更，旧数据处理
                this.serviceChangeToNoService(entity.getId(), responsibilityList);

                // 判断是否存在服务人员责任
                Boolean isStaffDeduct = checkIsStaffDeduct(responsibilityList);
                // 事件处理完成
                if (bo.getTag().equals(TAG_PROCESSED) || entity.getStatus() == EVENT_STATUS_PROCESSED) {
                    this.createOrChangeViolation(responsibilityList, entity, bo.getMsgId(), isStaffDeduct);
                    log.info("消息处理完成.processed,msgid:{}", bo.getMsgId());
                    return Action.CommitMessage;
                }

                // 事件处理中
                if (entity.getStatus() == EVENT_STATUS_PROCESSING) {
                    //部门为空，只创建站点违规
                    if (CollectionUtils.isEmpty(responsibilityList)) {
                        this.createOrChangeSiteViolationWhenEmptyDept(entity);
                        log.info("消息处理完成.responsibilityList.isEmpty,msgid:{}", bo.getMsgId());
                        return Action.CommitMessage;
                    }
                    //新事件不判断事件是否结束，通过nonInsuranceChange字段控制是否已失效变更过
                    boolean nonInsuranceChange = false;
                    for (EventResponsibility res : responsibilityList) {
                        Integer departmentType = responsibilityDeptName.equals(res.getResponsibilityDeptName()) ?
                                DepartmentTypeEnum.SERVICE_DEPT.getValue() : DepartmentTypeEnum.NO_SERVICE_DEPT.getValue();
                        // 判断匹配的违规配置类型是否判断事件结束
                        if (this.checkEventOver(entity, departmentType)) {
                            // 新事件判断事件结束才生成违规信息，判断是否是失效过或变更过
                            if (!nonInsuranceChange) {
                                this.nonInsuranceChangeToNoViolation(entity);
                                nonInsuranceChange = true;
                            }
                        } else {
                            List<EventResponsibility> tempResList = new ArrayList<>();
                            tempResList.add(res);
                            this.createOrChangeViolation(tempResList, entity, bo.getMsgId(), isStaffDeduct);
                        }
                    }
                }

                log.info("消息处理完成.processing,msgid:{}", bo.getMsgId());
            } catch (Exception e) {
                log.error("消息处理异常，msgId:{}", bo.getMsgId(), e);
                throw e;
            } finally {
                lock.unlock();
                // 当前消息处理完成，移除队列
                Object o = redisUtil.lPop(listKey);
                log.info("当前消息处理完成，移除队列,msgId:{},o:{}", bo.getMsgId(), o);

                // 判断队列中是否还有未处理数据
                MessageBO newBo = redisUtil.lGetIndex(listKey, 0);
                if (newBo != null) {
                    doProcess(newBo, listKey);
                }
            }
        } else {
            log.warn("消息处理异常,数据并发，msgId:{}", bo.getMsgId());
        }
        return Action.CommitMessage;
    }

    /**
     * 此方法大前提是【新事件是非保险，且事件为处理中status=2】
     * 因此，不管新事件符不符合违规规则,均要失效掉旧的保险事件
     */
    private void nonInsuranceChangeToNoViolation(MessageEntity entity) {
        if (entity.getStatus() == EVENT_STATUS_PROCESSING) {
            List<Violation> violationList = this.violationService.findListByCode(entity.getId());
            if (!CollectionUtils.isEmpty(violationList)) {
                for (Violation vo : violationList) {
                    if (vo.getEventType() != null && vo.getEventType().equals(ViolationTypeEventEnum.INSURANCE.getValue())) {
                        log.info("旧违规是保险类型的，则失效掉,oldVioId={}", vo.getId());
                        this.changeInvalidType(vo);
                        this.invalidViolation(vo, false, null, false, true);
                    }
                }
            }

        }

    }


    /***
     * 按违规类型创建对应的违规信息
     *
     * @param responsibilityList
     * @param entity
     * @param msgId
     * @param isStaffDeduct 是否存在服务人员责任
     */
    private void createOrChangeViolation(List<EventResponsibility> responsibilityList, MessageEntity entity, String msgId, Boolean isStaffDeduct) {
        //新事件违规类型不存在,则失效原来事件，并更改原先事件失效的类型
        if (CollectionUtils.isEmpty(responsibilityList)) {
            this.createOrChangeSiteViolationWhenEmptyDept(entity);
            return;
        }
        //保存违规人员员工Id
        List<Long> idList = new ArrayList<>();
        for (EventResponsibility res : responsibilityList) {
            if (responsibilityDeptName.equals(res.getResponsibilityDeptName())) {
                idList.add(res.getResponsibilityUser());
            }
        }
        //是否已执行过服务人员变更的事件失效流程
        ViolationType vt = null;
        Boolean executeInValidByStaffChange = false;
        Boolean existSiteViolation = false;
        for (EventResponsibility res : responsibilityList) {
            if (responsibilityDeptName.equals(res.getResponsibilityDeptName())) {
                vt = this.violationTypeService.getViolationTypeByEventCodeAndDept(entity, DepartmentTypeEnum.SERVICE_DEPT.getValue());
            } else {
                vt = this.violationTypeService.getViolationTypeByEventCodeAndDept(entity, DepartmentTypeEnum.NO_SERVICE_DEPT.getValue());
            }
            if (vt == null) {
                // 没有匹配的违规类型
                if (!Boolean.TRUE.equals(isStaffDeduct)) {
                    log.info("未找到匹配的违规类型且不存在服务人员责任，失效原违规记录，eventResponsibilities={}，entity={}", JSON.toJSONString(res), JSON.toJSONString(entity));
                    this.invalidAndChangeInvalidType(entity.getId());
                }
                continue;
            }
            if (vt.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                //查询是否存在站点违规,存在失效有效的，并更改原先的失效类型
                this.siteViolationService.invalidAndChangeInvalidType(entity.getId(), vt);
                //判断该事件原先是否存在有效的违规记录,员工是否发生改变
                if (!executeInValidByStaffChange) {
                    this.inValidByStaffChange(idList, entity.getId());
                    executeInValidByStaffChange = true;
                }
                //创建员工违规
                this.createOrChangeStaffViolation(res, entity, msgId);
                continue;
            }
            if (vt.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue()) && !existSiteViolation) {
                existSiteViolation = true;
                //获取站点id
                if (StringUtils.isNotBlank(entity.getSite())) {
                    String[] str = entity.getSite().split("_");
                    String siteId = str[0];
                    Long id = Long.valueOf(siteId);
                    this.createOrChangeSiteViolation(entity, id, vt);
                }
            }
        }
    }

    /***
     * 当部门为空时,只能创建站点违规
     */
    private void createOrChangeSiteViolationWhenEmptyDept(MessageEntity entity) {
        if (CollectionUtils.isEmpty(entity.getEventResponsibilities())) {
            ViolationType vt =
                    this.violationTypeService.getViolationTypeByEventCodeAndDept(entity, DepartmentTypeEnum.ALL.getValue());
            // 责任部门为空时，有匹配的违规类型，扣分类型为站点，不判断事件是否结束或者事件已结束
            if (vt != null && vt.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue())
                    && (YesOrNotEnum.NOT.getValue().equals(vt.getCheckEventOver()) || entity.getStatus() != EVENT_STATUS_PROCESSING)) {
                if (StringUtils.isNotBlank(entity.getSite())) {
                    String[] str = entity.getSite().split("_");
                    String siteId = str[0];
                    Long id = Long.valueOf(siteId);
                    this.createOrChangeSiteViolation(entity, id, vt);
                }
            } else {
                //此处只对部门为空时，原先是站点违规的做失效和类型变更处理;(原先是员工违规的,在serviceChangeToNoService()方法已处理)
                List<Violation> violationList = violationService.findListByCode(entity.getId());
                if (CollectionUtils.isNotEmpty(violationList)) {
                    for (Violation vo : violationList) {
                        if (vo.getStatus().equals(StatusEnum.WAIT_VALID.getValue()) || vo.getStatus()
                                .equals(StatusEnum.VALID.getValue())) {
                            this.invalidViolation(vo, false, null, false, true);
                        }
                        if (vo.getStatus().equals(StatusEnum.HAND_INVALID.getValue()) || vo.getStatus()
                                .equals(StatusEnum.APPEAL_INVALID.getValue())) {
                            this.changeInvalidType(vo);
                        }
                    }
                }
            }
        }
    }

    /**
     * 创建或者变更违规事件
     * 1、先判断服务人员是否发生变更
     * 2、在判断违规类型是否变更
     * idList中存储的是同一个消息进来的员工Id,确保循环时，不会失效掉本次新的服务人员对应的违规记录
     */
    private void createOrChangeStaffViolation(EventResponsibility res, MessageEntity entity, String msgId) {
        if (res.getResponsibilityUser() == null || (!responsibilityDeptName.equals(res.getResponsibilityDeptName()))) {
            return;
        }
        Long staffId = res.getResponsibilityUser();
        String staffName = res.getResponsibilityUserName();
        String responsibilityDeptName = res.getResponsibilityDeptName();
        // 构建违规信息
        Violation violationInfo = this.createViolationInfo(entity, msgId, staffId, staffName, responsibilityDeptName);

        if (log.isInfoEnabled()) {
            log.info("EventHandler.createOrChangeStaffViolation,msgId:{}", msgId);
        }
        // 查询旧违规信息
        List<Violation> violationList = violationService.findListByCodeAndStaffId(entity.getId(), staffId);
        if (CollectionUtils.isEmpty(violationList)) {
            //进入新增事件流程,构建违规信息
            if (null == violationInfo) {
                //流入的事件，不满足构建违规信息条件，进入下一次循环
                return;
            }
            //生成违规记录，分数扣减，生成短信
            this.createNewViolation(violationInfo, violationList, responsibilityDeptName);
        } else {
            //进入事件变更流程，先将消息放到生成事件的方法中
            if (null == violationInfo) {
                //变更后，不满足创建违规事件的，则旧的违规失效
                log.info("事件变更后，不符合违规信息,msgId:{}", msgId);
                this.changeInvalidType(null, violationList);
                for (Violation violation : violationList) {
                    if (violation.getStatus().equals(StatusEnum.VALID.getValue()) || violation.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
                        this.invalidViolation(violation, false, null, false, true);
                        log.info("事件变更后，不符合条件的，旧违规失效，violationId={},msgId:{}", violation.getId(), msgId);
                    }
                }
            } else {
                log.info("开始进入事件变更流程,新事件信息violation={},msgId:{}", JSON.toJSONString(violationInfo), msgId);
                this.judgeViolationChange(violationInfo, entity, violationList, res.getResponsibilityDeptName());
            }
        }
    }


    /**
     * 服务人员变更[A-B]，失效掉旧的有效记录
     */
    private void inValidByStaffChange(List<Long> staffIds, String code) {

        List<Violation> violationList = violationService.findListByCode(code);
        List<Violation> inValidList = new ArrayList<>();
        List<Violation> validList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(violationList)) {
            for (Violation vo : violationList) {
                if (vo.getStatus().equals(StatusEnum.HAND_INVALID.getValue()) || vo.getStatus().equals(StatusEnum.APPEAL_INVALID.getValue())) {
                    inValidList.add(vo);
                } else if (vo.getStatus().equals(StatusEnum.VALID.getValue()) || vo.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
                    validList.add(vo);
                }
            }
        }

        //判断该事件原先是否存在有效的违规记录,员工是否发生改变
        if (!CollectionUtils.isEmpty(validList) && !CollectionUtils.isEmpty(staffIds)) {
            for (Violation vo : validList) {
                if (vo.getStaffId() != null && !staffIds.contains(vo.getStaffId())) {
                    //因服务人员变更，失效掉旧的违规
                    this.invalidViolation(vo, false, null, false, false);
                }
            }
        }

        //失效的事件，失效状态变更成系统失效
        if (!CollectionUtils.isEmpty(inValidList) && !CollectionUtils.isEmpty(staffIds)) {
            for (Violation vo : inValidList) {
                if (vo.getStaffId() != null && !staffIds.contains(vo.getStaffId())) {
                    this.changeInvalidType(vo);
                }
            }
        }
    }


    /**
     * 责任部门变更(服务人员变更变成非服务人员或部门变为空)
     * 1、对于员工违规类型的,部门为空时失效掉原先有效的违规类型，并更改原先失效类型状态
     * 2、对于员工违规类型的,部门不为空时,更改原先失效类型状态
     * 3、对于站点违规类型的，部门门变更时，无需改变
     */
    private void serviceChangeToNoService(String code, List<EventResponsibility> responsibilityList) {
        List<Violation> violationList = violationService.findListByCode(code);
        if (CollectionUtils.isEmpty(violationList)) {
            return;
        }

        List<Violation> validList = violationList.stream().filter(
                vo -> vo.getStatus().equals(StatusEnum.VALID.getValue()) || vo.getStatus()
                        .equals(StatusEnum.WAIT_VALID.getValue())).collect(Collectors.toList());
        List<Violation> inValidList = violationList.stream().filter(
                vo -> vo.getStatus().equals(StatusEnum.HAND_INVALID.getValue()) || vo.getStatus()
                        .equals(StatusEnum.APPEAL_INVALID.getValue())).collect(Collectors.toList());

        //责任部门为空，失效掉原先记录
        if (CollectionUtils.isEmpty(responsibilityList)) {
            if (CollectionUtils.isNotEmpty(validList)) {
                for (Violation vo : validList) {
                    //判断是否是员工违规。(备注:站点的违规与部门无关,其生效或失效均判断均在createOrChangeViolation()方法中判断)
                    if (vo.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                        this.invalidViolation(vo, false, null, false, false);
                    }
                }
            }
            //修改旧事件的失效的违规状态类型
            if (CollectionUtils.isNotEmpty(inValidList)) {
                for (Violation vo : inValidList) {
                    if (vo.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                        this.changeInvalidType(vo);
                    }
                }
            }
        } else {
            //旧事件是服务人员违规-新事件是非服务人员违规, 即使执照里的违规配置不变，仍然需要失效掉员工违规
            List<EventResponsibility> noServiceList = new ArrayList<>();
            // 判断责任部门是否存在服务人员的违规记录
            boolean isStaffDeduct = false;
            for (EventResponsibility event : responsibilityList) {
                if (event.getResponsibilityDeptName().equals(responsibilityDeptName)) {
                    isStaffDeduct = true;
                }
            }
            if (!isStaffDeduct && CollectionUtils.isNotEmpty(validList)) {
                for (Violation v : validList) {
                    if (v.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                        this.invalidViolation(v, false, null, false, false);
                    }
                }
            }
        }
    }


    /**
     * 判断是否是保险
     */
    private Boolean isInsurance(MessageEntity entity, Integer departmentType) {
        ViolationType violationType = violationTypeService.getViolationTypeByEventCodeAndDept(entity, departmentType);
        if (violationType != null && ViolationTypeEventEnum.INSURANCE.getValue() == violationType.getEventType()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否事件结束
     *
     * @param entity
     * @param departmentType
     * @return
     */
    private Boolean checkEventOver(MessageEntity entity, Integer departmentType) {
        ViolationType violationType = violationTypeService.getViolationTypeByEventCodeAndDept(entity, departmentType);
        if (violationType != null && YesOrNotEnum.YES.getValue().equals(violationType.getCheckEventOver())) {
            return true;
        }
        return false;
    }

    private void setCleanerInfo(Violation v, Long staffId, String staffName, Boolean isRetry) {
        FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO dto = this.findStaffInfoById(staffId);
        if (dto == null) {
            log.warn("查无此员工信息,staffId = {} ", staffId);
            return;
        }
        if (dto.getName().equals(staffName)) {
            if (StringUtils.isNotBlank(dto.getCareerName()) && dto.getCareerName().equals(PositionTypeEnum.COOKER.getValue())) {
                v.setCooker(Boolean.TRUE);
            } else {
                v.setCooker(Boolean.FALSE);
            }
            v.setPosition(dto.getCareerName());
            v.setIdCard(dto.getIdCard());
            v.setCityCode(dto.getCityCode());
            v.setCityName(dto.getCityName());
            v.setPhone(dto.getPhone());
            v.setGender(dto.getSex());
            setSiteInfo(v, dto.getSiteId());
        } else if (isRetry) {
            setCleanerInfo(v, staffId, staffName, Boolean.FALSE);
        }
    }

    private void setUserInfo(Violation v) {
        String phone = StringUtils.trimToEmpty(v.getPhone());
        if (StringUtils.length(phone) != 11 || !StringUtils.isNumeric(phone)) {
            log.warn("手机号不正确,v:{}", v);
            throw new BusinessException(0, "手机号不正确");
        }
        UserVo uv = userService.findByPhone(phone);
        if (null == uv) {
            uv = registerService.registerByPhone(phone, Constants.USER_SOURCE);
        }
        v.setUserId(uv.getId());
    }

    private void setExtInfo(Violation v, MessageEntity entity) {
        v.setDescription(entity.getContent());
        v.setHappenTime(entity.getCreateTime());
        v.setOrderId(entity.getWorkOrderId());
        v.setServiceTime(entity.getServiceTime());
        v.setAnnex(getAnnex(entity.getEventAttachmentDtos()));
        v.setCode(entity.getId());
    }

    private String getAnnex(List<EventAttachment> eas) {
        if (!CollectionUtils.isEmpty(eas)) {
            return JSONArray.toJSONString(eas);
        }
        return null;
    }

    private void setSiteInfo(Violation v, Long siteId) {
        GetByMiddleSiteIdRequest request = new GetByMiddleSiteIdRequest();
        request.setMiddleSiteId(siteId);
        BaseResponse<SiteDetailDto> response = this.siteOpenService.getSiteDetailByMiddleSiteId(request);
        if (!response.isSuccess()) {
            log.info("【调用工单查询站点详情出错】，siteId={}, eMsg={}", siteId, response.getErrorCode().getMessage());
            return;
        }
        SiteDetailDto dto = response.getData();
        v.setSiteId(siteId);
        v.setSiteLeaderId(dto.getLeaderId());
        v.setSiteLeaderName(dto.getLeaderName());
        String leaderPhone = this.licenseService.getSiteLeaderPhone(dto.getLeaderId());
        if (StringUtils.isNotBlank(leaderPhone)) {
            v.setSiteLeaderPhone(leaderPhone);
        }
        v.setSiteName(dto.getName());
    }

    /***
     * 根据员工id查询员工基础信息(含敏感信息)
     *
     */
    private FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO findStaffInfoById(Long staffId) {
        FindStaffSensitiveInfoByStaffIdRequest f = new FindStaffSensitiveInfoByStaffIdRequest();
        f.setStaffId(staffId);
        BaseResponse<FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO> baseResponse = staffApi.findStaffSensitiveInfoByStaffId(f);
        if (!baseResponse.isSuccess()) {
            baseResponse.getErrorCode().getMessage();
            log.info("【调用开发平台查询员工信息出错】，staffId={}, eMsg={}", staffId, baseResponse.getErrorCode().getMessage());
            return null;
        }
        return baseResponse.getData();
    }

    private GetStaffByIdStaffBasicDTO getStaffBasicInfoById(Long staffId) {
        GetStaffByIdRequest getStaffRequest = new GetStaffByIdRequest();
        getStaffRequest.setId(staffId);
        BaseResponse<GetStaffByIdStaffBasicDTO> response = staffApi.getStaffById(getStaffRequest);
        if (!response.isSuccess()) {
            log.info("【调用开发平台查询员工信息出错】，staffId={}, eMsg={}", staffId, response.getErrorCode().getMessage());
            throw new com.hk.simba.base.common.exception.BusinessException(response.getErrorCode());
        }
        return response.getData();
    }

    /**
     * 构建违规信息
     *
     * @param entity
     * @param msgId
     * @param staffId
     * @param staffName
     */

    private Violation createViolationInfo(MessageEntity entity, String msgId, Long staffId, String staffName, String responsibilityDept) {
        Integer departmentType = DepartmentTypeEnum.NO_SERVICE_DEPT.getValue();
        if (responsibilityDeptName.equals(responsibilityDept)) {
            departmentType = DepartmentTypeEnum.SERVICE_DEPT.getValue();
        }
        if (StringUtils.isBlank(staffName) || staffId == null) {
            log.info("员工信息为空，不处理。msgId:{}", msgId);
            return null;
        }
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        Date deadLine = YearUtil.getNextDayZeroTime(now, deadlineAmount);

        ViolationType vt = violationTypeService.getViolationTypeByEventCodeAndDept(entity, departmentType);
        if (null == vt) {
            log.info("非服务事故相关事件，不处理。msgId:{}", msgId);
            return null;
        }
        StaffDetailDto staffDetail = servantManager.getStaffDetail(staffId);
        //进来的事件满足上述条件后，则均可生成对应的违规信息
        Violation v = new Violation();
        v.setStaffId(staffId);
        v.setName(staffName);
        v.setCreateTime(now);
        v.setDeadlineTime(deadLine);
        v.setCreateBy(Constants.SYS);
        v.setDepartmentType(vt.getDepartmentType());
        v.setEventType(vt.getEventType());
        v.setViolationTypeId(vt.getId());
        v.setWorkType(staffDetail.getWorkType());
        List<Integer> noNeedFineFlexibileStaffingList = new ArrayList<>();
        if (StringUtils.isNotBlank(noNeedFineFlexibileStaffings)) {
            noNeedFineFlexibileStaffingList = JSON.parseArray(noNeedFineFlexibileStaffings, Integer.class);
        }
        if (vt.getFee().compareTo(new BigDecimal(0)) == 0) {
            v.setPayStatus(PayRecordStatusEnum.PAY.getValue());
        } else {
            v.setPayStatus(PayRecordStatusEnum.NO_PAY.getValue());
        }
        v.setTotalAmount(vt.getFee());
        String resultIds = Constants.COMMA + staffForbiddenResultIds + Constants.COMMA;
        String code = Constants.COMMA + vt.getCode() + Constants.COMMA;
        if (resultIds.contains(code)) {
            v.setStatus(StatusEnum.WAIT_VALID.getValue());
        } else {
            v.setStatus(StatusEnum.VALID.getValue());
        }
        v.setOrderType(OrderTypeEnum.TYPE_1.getValue());

        v.setScore(vt.getScore());

        v.setType(vt.getType());
        v.setDetail(vt.getDetail());
        v.setViolationType(vt.getCode());
        v.setDeductType(DeductTypeEnum.STAFF_DEDUCT.getValue());
        //设置员工信息
        this.setCleanerInfo(v, staffId, staffName, Boolean.TRUE);
        setUserInfo(v);
        setExtInfo(v, entity);
        return v;
    }

    /**
     * 判断事件变更流程(对比新旧违规记录)
     * 1、先判断是否存在有效的旧违规记录，
     * 1-1、存在则对比新旧违规是否发生改变;
     * 1-2、不存在，则新增
     *
     * @param vo
     * @param entity
     */
    private void judgeViolationChange(Violation vo, MessageEntity entity, List<Violation> violationList, String responsibilityDeptName) {
        if (null == vo || CollectionUtils.isEmpty(violationList)) {
            return;
        }
        //查看旧记录中是否有有效的违规记录(同一事件、同一员工对应的有效违规记录，最多只有一条)
        Violation oldVio = null;
        for (Violation violation : violationList) {
            if (violation.getStatus().equals(StatusEnum.VALID.getValue()) || violation.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
                oldVio = violation;
                break;
            }
        }

        List<Violation> tempViolationList = new ArrayList<>();
        for (Violation violation : violationList) {
            Violation vio = new Violation();
            BeanCopyUtil.copyPropertiesIgnoreNull(violation, vio);
            tempViolationList.add(vio);
        }
        if (null == oldVio) {
            this.changeInvalidType(vo, tempViolationList);
            this.createNewViolation(vo, violationList, responsibilityDeptName);
        } else {
            //新旧违规对比,若事件类型发生改变，执行以下操作
            if (!this.compareViolation(oldVio, vo)) {
                log.info("=====开始进入新旧事件变更=======");
                //判断新违规是否是保险,再判断成本
                if (vo.getEventType().equals(ViolationTypeEventEnum.INSURANCE.getValue())) {
                    Boolean greaterThanZero = false;
                    if (entity.getProcessCost() != null && entity.getProcessCost().compareTo(BigDecimal.ZERO) > 0) {
                        greaterThanZero = true;
                    }
                    //旧违规失效,生成新违规、扣分、生成短信
                    this.changeInvalidType(vo, tempViolationList);
                    this.invalidViolation(oldVio, true, vo, greaterThanZero, true);
                    this.createNewViolation(vo, violationList, responsibilityDeptName);
                    log.info("保险新旧事件对比，旧事件失效，同时生成新事件,oldViolationId={},newViolationId={}", oldVio.getId(), vo.getId());
                } else {
                    //非保险，则旧违规失效，生成新违规、扣分、生成短信
                    this.changeInvalidType(vo, tempViolationList);
                    this.invalidViolation(oldVio, false, vo, false, true);
                    this.createNewViolation(vo, violationList, responsibilityDeptName);
                    log.info("非保险新旧事件对比，旧事件失效，同时生成新事件,oldViolationId={},newViolationId={}", oldVio.getId(), vo.getId());
                }
            }
        }
    }

    /**
     * 新旧事件对比(返回true则未改变)
     *
     * @param oldViolation
     * @param newViolation
     */

    private Boolean compareViolation(Violation oldViolation, Violation newViolation) {
        if (null == oldViolation || newViolation == null) {
            return false;
        }
        if (StringUtils.isEmpty(oldViolation.getViolationType())) {
            if (StringUtils.isNotBlank(oldViolation.getType()) && StringUtils.isNotBlank(newViolation.getType())) {
                if (!oldViolation.getType().equals(newViolation.getType())) {
                    return false;
                }
                if (StringUtils.isNotBlank(oldViolation.getDetail()) && StringUtils.isNotBlank(newViolation.getDetail())) {
                    if (oldViolation.getDetail().equals(newViolation.getDetail()) && oldViolation.getDeductType().equals(newViolation.getDeductType())) {
                        return true;
                    }
                }
            }
        } else if (oldViolation.getViolationType().equals(newViolation.getViolationType())) {
            if (oldViolation.getDeductType().equals(newViolation.getDeductType()) && oldViolation.getDepartmentType().equals(newViolation.getDepartmentType())
                    && oldViolation.getEventType().equals(newViolation.getEventType())) {
                if (oldViolation.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue()) && !oldViolation.getSiteId().equals(newViolation.getSiteId())) {
                    return false;
                }

                // 判断是否匹配同一个违规类型
                Long oldViolationTypeId = Optional.ofNullable(oldViolation.getViolationTypeId()).orElse(-1L);
                Long newViolationTypeId = Optional.ofNullable(newViolation.getViolationTypeId()).orElse(-1L);
                return oldViolationTypeId.equals(newViolationTypeId);
            }

        }
        return false;
    }

    /**
     * 违规失效，分数回滚，失效短信
     * 原先待生效的违规也要变成失效
     *
     * @param oldViolation
     * @param newInsurance    (新事件是否是保险事件)
     * @param newViolation
     * @param greaterThanZero (若新事件是保险，成本是否大于0)
     */
    private void invalidViolation(Violation oldViolation, Boolean newInsurance, Violation newViolation, Boolean greaterThanZero, Boolean sendEmail) {
        if (null == oldViolation) {
            return;
        }
        if (oldViolation.getStatus().equals(StatusEnum.VALID.getValue()) || oldViolation.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
            Integer oldStatus = oldViolation.getStatus();
            oldViolation.setStatus(StatusEnum.INVALID.getValue());
            oldViolation.setModifyTime(new Date());
            oldViolation.setModifyBy(Constants.SYS);
            oldViolation.setReason(Constants.INVALID_BY_SYSTEM);
            this.violationService.updateById(oldViolation);
            if (oldViolation.getDeductType() == null || oldViolation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                if (oldStatus.equals(StatusEnum.VALID.getValue())) {
                    //如果已发起申述，撤回飞书审批流程
                    appealService.refuseAppealForViolationInvalid(oldViolation.getId());
                    //分数回滚
                    this.licenseService.rollbackScore(oldViolation, Constants.INVALID_BY_SYSTEM);
                    //短信失效
                    this.violationMessageService.invalidViolationMessage(oldViolation);
                }
                //发送违规失效邮件
//                if (sendEmail) {
//                    this.sendViolationInvalidEmailToQuality(oldViolation, newInsurance, newViolation, greaterThanZero);
//                }
            }
//            if (sendEmail && oldViolation.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue())) {
//                this.sendViolationInvalidEmailToQuality(oldViolation, newInsurance, newViolation, greaterThanZero);
//            }

            //失效站点复训记录
            if (oldViolation.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue())) {
                RetrainInvalidRequest invalidRequest = new RetrainInvalidRequest();
                invalidRequest.setOperator(Constants.SYS);
                invalidRequest.setReason(Constants.VIOLATION_INVALID);
                invalidRequest.setViolationId(oldViolation.getId());
                this.retrainService.invalidRetrainByCondition(invalidRequest);
            }
        }
    }

    /**
     * 创建新违规,扣除分数生成短信
     *
     * @param newVio
     */
    private void createNewViolation(Violation newVio, List<Violation> violationList, String responsibilityDeptName) {
        if (null == newVio) {
            return;
        }
        //若新事件与最近的一条失效事件，违规类型一样，且失效类型为后台和申诉失效，则不生成新记录
        if (!CollectionUtils.isEmpty(violationList)) {
            Violation violation = violationList.get(0);
            if (StringUtils.isNotBlank(violation.getViolationType()) && newVio.getViolationType().equals(violation.getViolationType())) {
                if (violation.getStatus().equals(StatusEnum.HAND_INVALID.getValue()) ||
                        violation.getStatus().equals(StatusEnum.APPEAL_INVALID.getValue())) {
                    log.info("[手工或者审核失效的]事件类型未发生变更，不生成新纪录,旧的violationId={}", violation.getId());
                    return;
                }
            }
        }

        this.violationService.insert(newVio);
        //用状态,判断是否是服务禁忌,若是,待质质高审核通过才生效
        if (newVio.getStatus().equals(StatusEnum.VALID.getValue())) {
            this.licenseService.doViolation(newVio);
            this.violationMessageService.asyncSaveViolationMessage(newVio);
            //给培训师发送违规邮件通知
            if (newVio.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                violationMailService.asyncSendViolationNotifyEmailToTrainTeacher(newVio);
            }
        }
        if (newVio.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
            this.violationService.sendStaffForbiddenEmail(newVio, responsibilityDeptName);
        }
    }

    /**
     * 更改失效类型
     */
    private void changeInvalidType(Violation newVio, List<Violation> violationList) {
        if (!CollectionUtils.isEmpty(violationList)) {
            List<Violation> tempList = new ArrayList<>();
            Date date = new Date();
            for (Violation vo : violationList) {
                //违规类型未改变，则状态不变更
                if (newVio != null) {
                    if ((StringUtils.isNotBlank(vo.getViolationType()) && newVio.getViolationType().equals(vo.getViolationType()))) {
                        continue;
                    }

                }
                if (vo.getStatus().equals(StatusEnum.HAND_INVALID.getValue()) || vo.getStatus().equals(StatusEnum.APPEAL_INVALID.getValue())) {
                    vo.setStatus(StatusEnum.INVALID.getValue());
                    vo.setModifyBy(Constants.SYS);
                    vo.setModifyTime(date);
                    vo.setReason(Constants.INVALID_BY_SYSTEM);
                    tempList.add(vo);
                }
            }
            if (!CollectionUtils.isEmpty(tempList)) {
                this.violationService.updateBatchById(tempList);
            }
        }
    }


    /**
     * 更改失效类型
     */
    private void changeInvalidType(Violation vo) {
        if (vo != null) {
            if (vo.getStatus().equals(StatusEnum.HAND_INVALID.getValue()) ||
                    (StringUtils.isNotBlank(vo.getReason()) && !vo.getReason().equals(Constants.QUALITY_APPROVE) && !vo.getReason().equals(Constants.INVALID_BY_SYSTEM))) {
                vo.setStatus(StatusEnum.INVALID.getValue());
                vo.setModifyBy(Constants.SYS);
                vo.setModifyTime(new Date());
                vo.setReason(Constants.INVALID_BY_SYSTEM);
                this.violationService.updateById(vo);
            } else if (vo.getStatus().equals(StatusEnum.APPEAL_INVALID.getValue()) ||
                    (StringUtils.isNotBlank(vo.getReason()) && vo.getReason().equals(Constants.QUALITY_APPROVE))) {
                vo.setStatus(StatusEnum.INVALID.getValue());
                vo.setReason(Constants.INVALID_BY_SYSTEM);
                vo.setModifyBy(Constants.SYS);
                vo.setModifyTime(new Date());
                this.violationService.updateById(vo);
            }
        }
    }


    /**
     * 发送[违规失效提醒]邮件至质质高
     *
     * @param oldVio
     * @param newVio
     * @param greaterThanZero
     * @param newInsurance
     */
    private void sendViolationInvalidEmailToQuality(Violation oldVio, Boolean newInsurance, Violation newVio, Boolean greaterThanZero) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("violationCode", oldVio.getCode());
        params.put("responsibleDept", responsibilityDeptName);
        ViolationType type = this.violationTypeService.getViolationTypeByCodeAndDeptType(oldVio.getViolationType(), oldVio.getDepartmentType());
        if (type != null) {
            //保险事件总成本大于0。
            if (ViolationTypeEventEnum.INSURANCE.getValue() == type.getEventType()) {
                params.put("levelOne", type.getType());
                params.put("levelTwo", type.getDetail());
                params.put("levelthree", type.getName());
                params.put("totalCost", "大于0");
            } else {
                params.put("levelOne", type.getType());
                params.put("levelTwo", type.getDetail());
                params.put("levelthree", "无");
                params.put("totalCost", "无");
            }
        } else {
            //旧的保险类型处理
            if (violationEventType.equals(oldVio.getType()) || insuranceName.contains(oldVio.getType())) {
                params.put("levelOne", violationEventType);
                params.put("levelTwo", oldVio.getType());
                params.put("levelthree", oldVio.getDetail());
                params.put("totalCost", "大于0");
            } else {
                params.put("levelOne", oldVio.getType());
                params.put("levelTwo", oldVio.getDetail());
                params.put("levelthree", "无");
                params.put("totalCost", "无");
            }
        }
        //不是保险
        if (!newInsurance) {
            params.put("newTotalCost", "无");
            if (newVio == null) {
                params.put("newLevelOne", "无");
                params.put("newLevelTwo", "无");
                params.put("newLevelthree", "无");
                params.put("newResponsibleDept", "无");

            } else {
                params.put("newLevelOne", newVio.getType());
                params.put("newLevelTwo", newVio.getDetail());
                params.put("newLevelthree", "无");
                params.put("newResponsibleDept", responsibilityDeptName);
            }
        } else {
            ViolationType tempType = this.violationTypeService.getViolationTypeByCodeAndDeptType(newVio.getViolationType(), newVio.getDepartmentType());
            params.put("newLevelOne", tempType.getType());
            params.put("newLevelTwo", tempType.getDetail());
            params.put("newLevelthree", tempType.getName());
            params.put("newResponsibleDept", responsibilityDeptName);
            if (greaterThanZero) {
                params.put("newTotalCost", "大于0");
            } else {
                params.put("newTotalCost", "不大于0");
            }
        }
        try {
            mailService.sendTemplateMail(qualityEmail, MessageConstant.VIOLATION_INVALID_SUBJECT, MessageConstant.VIOLATION_INVALID_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[违规失效提醒]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[违规失效提醒]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[违规失效提醒]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[违规失效提醒]邮件发送失败:{}", e.getMessage(), e);
        }

    }

    /***
     * 新事件违规类型不存在时，失效原先有效的和待生效的违规，并更改原先失效的违规的类型
     */
    private void invalidAndChangeInvalidType(String code) {
        List<Violation> violations = violationService.findListByCode(code);
        if (CollectionUtils.isEmpty(violations)) {
            return;
        }
        List<Violation> validList = new ArrayList<>();
        List<Violation> inValidList = new ArrayList<>();
        for (Violation vo : violations) {
            if (vo.getStatus().equals(StatusEnum.VALID.getValue()) || vo.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
                validList.add(vo);
            }
            if (vo.getStatus().equals(StatusEnum.APPEAL_INVALID.getValue()) || vo.getStatus().equals(StatusEnum.HAND_INVALID.getValue())) {
                inValidList.add(vo);
            }
        }
        if (!CollectionUtils.isEmpty(validList)) {
            for (Violation v : validList) {
                this.invalidViolation(v, false, null, false, true);
            }
        }
        if (!CollectionUtils.isEmpty(inValidList)) {
            this.changeInvalidType(null, inValidList);
        }
    }

    /***
     * 创建更改站点违规事件
     * 新事件违规类型不存在时，失效原先有效的违规，并更改原先失效的违规的类型
     */
    private void createOrChangeSiteViolation(MessageEntity entity, Long siteId, ViolationType vt) {
        List<Violation> oldViolations = violationService.findListByCode(entity.getId());
        Violation newVo = this.siteViolationService.createSiteViolationInfo(entity, siteId, vt);
        if (CollectionUtils.isEmpty(oldViolations)) {
            if (newVo != null) {
                this.saveSiteViolation(newVo, entity);
            }
        } else {
            //收集旧的失效和生效事件
            List<Violation> inValidList = new ArrayList<>();
            List<Violation> validList = new ArrayList<>();
            for (Violation vo : oldViolations) {
                if (vo.getStatus().equals(StatusEnum.APPEAL_INVALID.getValue()) || vo.getStatus().equals(StatusEnum.HAND_INVALID.getValue())) {
                    inValidList.add(vo);
                }
                if (vo.getStatus().equals(StatusEnum.VALID.getValue()) || vo.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
                    validList.add(vo);
                }
            }
            //更改旧违规的失效类型
            if (!CollectionUtils.isEmpty(inValidList)) {
                this.changeInvalidType(newVo, inValidList);
            }
            if (!CollectionUtils.isEmpty(validList)) {
                //失效旧的,新增新的，原先是员工违规(含有多个员工违规情况),现在变成站点违规，只需新增一条站点违规
                Boolean create = false;
                for (Violation oldVo : validList) {
                    if (!this.compareViolation(oldVo, newVo)) {
                        this.changeInvalidType(oldVo);
                        this.invalidViolation(oldVo, false, newVo, false, true);
                        if (!create) {
                            create = true;
                        }
                    }
                }
                if (create && newVo != null) {
                    this.saveSiteViolation(newVo, entity);
                }
            } else {
                //若新事件与最近的一条失效事件，违规类型一样，且失效类型为后台和申诉失效，则不生成新记录
                Violation violation = oldViolations.get(0);
                if (violation.getSiteId().equals(newVo.getSiteId())) {
                    if (StringUtils.isNotBlank(violation.getViolationType()) && newVo.getViolationType().equals(violation.getViolationType())) {
                        if (violation.getStatus().equals(StatusEnum.HAND_INVALID.getValue()) ||
                                violation.getStatus().equals(StatusEnum.APPEAL_INVALID.getValue())) {
                            log.info("[站点违规,手工或者审核失效的]事件类型未发生变更，不生成新纪录,旧的violationId={}", violation.getId());
                            return;
                        }
                    }
                }
                this.saveSiteViolation(newVo, entity);
            }
        }
    }

    private void saveSiteViolation(Violation violation, MessageEntity entity) {
        this.violationService.insert(violation);
        List<Long> staffIds = new ArrayList<>();
        if (StringUtils.isNotBlank(entity.getStaff())) {
            String[] staffs = entity.getStaff().split(",");
            for (String s : staffs) {
                if (StringUtils.isBlank(s)) {
                    continue;
                }
                String[] tmp = s.split("_");
                if (!RegexUtils.isNumber(tmp[0])) {
                    continue;
                }
                Long staffId = Long.parseLong(tmp[0]);
                staffIds.add(staffId);
            }
        }
        if (violation.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
            this.siteViolationService.sendSiteForbiddenEmail(violation);
            //保存员工id,供站点违规审核时生成复训使用
            redisUtil.set(Constants.SITE_VIOLATION_ID + violation.getId(), staffIds);
        }
        //违规为[站点管理违规-人员培训不到位],则创建站点违规对应的复训(站点违规不一定有员工id)
        if (StringUtils.isBlank(siteInadequateTrainingIds)) {
            return;
        }
        String violationCodes = Constants.COMMA + siteInadequateTrainingIds + Constants.COMMA;
        if (violation.getStatus().equals(StatusEnum.VALID.getValue()) && !CollectionUtils.isEmpty(staffIds) &&
                violationCodes.contains(violation.getViolationType())) {
            for (Long staffId : staffIds) {
                violation.setStaffId(staffId);
                this.siteViolationService.createSiteRetrain(violation);
            }
        }
    }

    /**
     * 判断责任部门列表中是否存在服务人员责任
     *
     * @param responsibilityList
     * @return
     */
    private Boolean checkIsStaffDeduct(List<EventResponsibility> responsibilityList) {
        if (CollectionUtils.isEmpty(responsibilityList)) {
            return Boolean.FALSE;
        }
        Boolean isStaffDeduct = Boolean.FALSE;
        for (EventResponsibility event : responsibilityList) {
            if (event.getResponsibilityDeptName().equals(responsibilityDeptName)) {
                isStaffDeduct = Boolean.TRUE;
            }
        }
        return isStaffDeduct;
    }
}
