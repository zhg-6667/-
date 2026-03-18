package com.hk.simba.license.service.task;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.google.common.collect.Maps;
import com.hk.base.util.DateUtils;
import com.hk.base.util.modules.BeanMapper;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.enums.ViolationMessageStatusEnum;
import com.hk.simba.license.api.enums.ViolationMessageTypeEnum;
import com.hk.simba.license.api.vo.LicenseInfoVO;
import com.hk.simba.license.api.vo.SiteAndRegionVO;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.TemplateParamsEnum;
import com.hk.simba.license.service.entity.Retrain;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.entity.ViolationMessage;
import com.hk.simba.license.service.manager.MessageBoxManager;
import com.hk.simba.license.service.manager.StaffApiManager;
import com.hk.simba.license.service.service.*;
import com.hk.simba.license.service.utils.RedisUtil;
import com.hk.sisyphus.api.core.flash.MessageApi;
import com.hk.sisyphus.api.model.flash.message.SendRequest;
import com.hk.sisyphus.api.model.flash.message.SendResponse;
import com.hk.sisyphus.api.model.flash.message.SendTemplateParameter;
import com.hk.sisyphus.light.client.messagebox.param.SendResponseData;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @ClassName ViolationMessageTaskHandler
 * @Description LTS 违规信息调度处理
 * @Author chenjh1@homeking365.com
 * @Date 2020-04-17 0:56
 * @Version 1.0
 **/
@Service
@Slf4j
public class ViolationMessageTaskHandler {
    @Autowired
    ViolationMessageService violationMessageService;

    @Autowired
    ViolationService violationService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private MessageApi apiInstance;

    @Autowired
    private RetrainService retrainService;

    @Autowired
    private SiteViolationService siteViolationService;

    @Autowired
    private MailService mailService;
    @Autowired
    private MessageBoxManager messageBoxManager;

    @Resource
    private StaffApiManager staffApiManager;
    /**
     * 质质高执照违规重新提醒员工短信模板
     */
    @Value("${message.staff.repeat.sms.template}")
    private String messageStaffRepeatSmsTemplate;

    /**
     * 质质高执照违规重新提醒站长短信模板
     */
    @Value("${message.siteLeader.repeat.sms.template}")
    private String messageSiteLeaderRepeatSmsTemplate;

    /**
     * 未支付，短信重新通知时间
     */
    @Value("${message.repeat.notify.day}")
    private Integer repeatNotifyDay;

    /**
     * 违规超时关闭时间
     */
    @Value("${violation.timeout.pay}")
    private Integer timeoutPay;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 师傅扣分-师傅短信模板
     */
    @Value("${mentor.ship.mentor.sms.template}")
    private String mentorShipMentorSmsTemplate;

    /**
     * 师傅扣分-师傅app推送模板
     */
    @Value("${mentor.ship.mentor.push.template.id}")
    private Long mentorShipMentorPushTemplateId;

    /**
     * 师傅扣分-站长短信模板
     */
    @Value("${mentor.ship.site.leader.sms.template}")
    private String mentorShipSiteLeaderSmsTemplate;

    /**
     * 执照复训-员工短信模板
     */
    @Value("${retrain.staff.sms.template}")
    private String retrainStaffSmsTemplate;

    /**
     * 执照复训-员工app推送模板
     */
    @Value("${retrain.staff.push.template.id}")
    private Long retrainStaffPushTemplateId;

    /**
     * 违规复训-员工短信模板
     */
    @Value("${violation.retrain.staff.sms.template}")
    private String violationRetrainStaffSmsTemplate;

    /**
     * 违规复训-员工app推送模板
     */
    @Value("${violation.retrain.staff.push.template.id}")
    private Long violationRetrainStaffPushTemplateId;

    /**
     * 培训复训-员工短信模板
     */
    @Value("${training.retrain.staff.sms.template}")
    private String trainingRetrainStaffSmsTemplate;

    /**
     * 培训复训-员工app推送模板
     */
    @Value("${training.retrain.staff.push.template.id}")
    private Long trainingRetrainStaffPushTemplateId;

    /**
     * 质质高执照违规员工推送模板
     */
    @Value("${message.staff.push.template.id}")
    private Long licenseViolationStaffPushTemplateId;

    /**
     * 平台
     */
    @Value("${message.staff.sms.platform}")
    private String platform;

    /**
     * 用户类型为员工
     */
    @Value("${message.staff.sms.user.type}")
    private String staffUserType;

    /**
     * 用户类型为员工
     */
    @Value("${message.employee.sms.user.type}")
    private String employeeUserType;

    /**
     * 调用方代号
     */
    @Value("${message.staff.sms.business}")
    private String business;

    /**
     * 调用方业务Id
     */
    @Value("${message.staff.sms.biz.id}")
    private String bizId;

    /**
     * 质质高执照违规员工短信模板id
     */
    @Value("${staff.sms.template.id}")
    private String staffSmsTemplateId;
    @Value("${staff.feishu.template.id}")
    private String staffFeiShuTemplateId;

    /**
     * 质质高执照违规站长短信模板id
     */
    @Value("${site.leader.sms.template.id}")
    private String siteLeaderSmsTemplateId;
    @Value("${site.leader.feishu.template.id}")
    private String siteLeaderFeiShuTemplateId;

    /**
     * 质质高执照违规重新提醒员工短信模板id
     */
    @Value("${staff.repeat.sms.template.id}")
    private String staffRepeatSmsTemplateId;
    @Value("${staff.repeat.feishu.template.id}")
    private String staffRepeatFeiShuTemplateId;

    /**
     * 质质高执照违规重新提醒站长短信模板id
     */
    @Value("${site.leader.repeat.sms.template.id}")
    private String siteLeaderRepeatSmsTemplateId;
    @Value("${site.leader.repeat.feishu.template.id}")
    private String siteLeaderRepeatFeiShuTemplateId;

    /**
     * 师傅扣分-师傅短信模板id
     */
    @Value("${mentor.ship.mentor.sms.template.id}")
    private String mentorShipMentorSmsTemplateId;

    /**
     * 师傅扣分-站长短信模板id
     */
    @Value("${mentor.ship.site.leader.sms.template.id}")
    private String mentorShipSiteLeaderSmsTemplateId;

    /**
     * 执照复训-员工短信模板id
     */
    @Value("${retrain.staff.sms.template.id}")
    private String retrainStaffSmsTemplateId;
    @Value("${retrain.staff.feishu.template.id}")
    private String retrainStaffFeiShuTemplateId;

    /**
     * 违规复训-员工短信模板id
     */
    @Value("${violation.retrain.staff.sms.template.id}")
    private String violationRetrainStaffSmsTemplateId;
    @Value("${violation.retrain.staff.feishu.template.id}")
    private String violationRetrainStaffFeiShuTemplateId;

    /**
     * 培训复训-员工短信模板id
     */
    @Value("${training.retrain.staff.sms.template.id}")
    private String trainingRetrainStaffSmsTemplateId;
    @Value("${training.retrain.staff.feishu.template.id}")
    private String trainingRetrainStaffFeiShuTemplateId;

    /**
     * 复训超时时间
     **/
    @Value("${notify.limit.day}")
    private Integer limitDay;

    private Config messageConfig = ConfigService.getConfig(MessageConstant.MESSAGE_PROPERTIES_KEY);

    public void sendStaffSMS() {
        log.info("sendStaffSMS");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(ViolationMessageTypeEnum.STAFF_SMS.getValue());
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        Violation violation = new Violation();
                        violation.setCode(message.getViolationCode());
                        violation.setStaffId(message.getStaffId());
                        Wrapper<Violation> violationWrapper = new EntityWrapper<>(violation).orderBy("id", false);
                        violation = violationService.selectOne(violationWrapper);
                        if (violation != null) {
                            String templateName = messageConfig.getProperty(MessageConstant.STAFF_SMS_TEMPLATE_KEY, MessageConstant.STAFF_SMS_TEMPLATE);
                            // ums短信发送接口替换成flash消息发送接口
                            SendRequest request = new SendRequest();
                            request.setTemplateId(Long.parseLong(staffSmsTemplateId));
                            request.setPlatform(platform); //申请模板时获得
                            request.setUserType(staffUserType);
                            request.setAccount(message.getStaffPhone());
                            request.setBusiness(business);
                            request.setBizId(bizId);
                            List<SendTemplateParameter> parameters = new ArrayList<>(8);
                            // 配置参数
                            SendTemplateParameter param1 = this.setSendTemplateParam("name", violation.getName());
                            parameters.add(param1);
                            SendTemplateParameter param2 = this.setSendTemplateParam("serviceTime",
                                    Optional.ofNullable(violation.getServiceTime())
                                            .map(e -> DateUtils.formatDate(e, "yyyy年MM月dd日")).orElse(""));
                            parameters.add(param2);
                            SendTemplateParameter param3 = this.setSendTemplateParam("type", violation.getType());
                            parameters.add(param3);
                            SendTemplateParameter param4 = this.setSendTemplateParam("detail", violation.getDetail());
                            parameters.add(param4);
                            SendTemplateParameter param5 = this.setSendTemplateParam("score", "" + violation.getScore());
                            parameters.add(param5);
                            SendTemplateParameter param6 = this.setSendTemplateParam("amount", violation.getTotalAmount().toString());
                            parameters.add(param6);
                            SendTemplateParameter param7 = this.setSendTemplateParam("timeoutPay", timeoutPay.toString());
                            parameters.add(param7);
                            SendTemplateParameter param8 = this.setSendTemplateParam("limitDay", "" + messageConfig.getIntProperty(MessageConstant.APPEAL_LIMIT_DAY_KEY, MessageConstant.APPEAL_LIMIT_DAY));
                            parameters.add(param8);
                            request.setParameters(parameters);
                            //调用发送接口
                            SendResponse result = apiInstance.send(request);
                            if (result.isSuccess()) {
                                message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            }
                            message.setMessageKey(result.getData().getMessageId().toString());
                            message.setTemplateName(templateName);
                            // 发送飞书消息
                            this.sendFeiShuMessage(request, Long.parseLong(staffFeiShuTemplateId), message.getStaffId());
                        }
                    } catch (Exception e) {
                        log.error("sendStaffSMS-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setTryTimes(tryTimes);
                        message.setModifyBy(Constants.SYS);
                        message.setModifyTime(new Date());
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                //TODO 补充消息发送失败流程
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }

    public void sendStaffPush() {
        log.info("sendStaffPush");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(ViolationMessageTypeEnum.STAFF_PUSH.getValue());
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        Violation violation = new Violation();
                        violation.setCode(message.getViolationCode());
                        violation.setStaffId(message.getStaffId());
                        Wrapper<Violation> violationWrapper = new EntityWrapper<>(violation).orderBy("id", false);
                        violation = violationService.selectOne(violationWrapper);
                        if (violation != null) {
                            Map<String, String> params = new HashMap<>();
                            String name = violation.getName();
                            String serviceTime = DateUtils.formatDate(violation.getServiceTime(), "yyyy年MM月dd日");
                            String violationType = violation.getType();
                            String violationDetail = violation.getDetail();
                            String score = "" + violation.getScore();
                            String amount = violation.getTotalAmount().toString();
                            String limitDay = "" + messageConfig.getIntProperty(MessageConstant.APPEAL_LIMIT_DAY_KEY, MessageConstant.APPEAL_LIMIT_DAY);
                            params.put(TemplateParamsEnum.NAME.getValue(), name);
                            params.put(TemplateParamsEnum.SERVICE_TIME.getValue(), serviceTime);
                            params.put(TemplateParamsEnum.VIOLATION_TYPE.getValue(), violationType);
                            params.put(TemplateParamsEnum.VIOLATION_DETAIL.getValue(), violationDetail);
                            params.put(TemplateParamsEnum.SCORE.getValue(), score);
                            params.put(TemplateParamsEnum.AMOUNT.getValue(), amount);
                            params.put(TemplateParamsEnum.VIOLATION_ID.getValue(), violation.getId().toString());
                            params.put(TemplateParamsEnum.LIMIT_DAY.getValue(), limitDay);
                            params.put(TemplateParamsEnum.TIMEOUT_PAY.getValue(), timeoutPay.toString());
                            SendResponseData sendResponseData =
                                    messageBoxManager.singleSend(licenseViolationStaffPushTemplateId, message.getStaffId(),
                                            params);
                            message.setMessageKey(sendResponseData.getMessageBoxRecordId().toString());
                            message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            message.setTemplateName(licenseViolationStaffPushTemplateId.toString());
                        }
                    } catch (Exception e) {
                        log.error("sendStaffPush-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setTryTimes(tryTimes);
                        message.setModifyBy(Constants.SYS);
                        message.setModifyTime(new Date());
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                //TODO 补充消息发送失败流程
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }

    public void sendSiteLeaderSMS() {
        log.info("sendSiteLeaderSMS");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(ViolationMessageTypeEnum.SITE_LEADER_SMS.getValue());
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        Violation violation = new Violation();
                        violation.setCode(message.getViolationCode());
                        violation.setSiteLeaderId(message.getStaffId());
                        violation.setStatus(StatusEnum.VALID.getValue());
                        Wrapper<Violation> violationWrapper = new EntityWrapper<>(violation).orderBy("id", false);
                        List<Violation> violationList = this.violationService.selectList(violationWrapper);
                        if (!CollectionUtils.isEmpty(violationList)) {
                            StringBuffer buffer = new StringBuffer();
                            for (Violation vo : violationList) {
                                buffer.append(vo.getName() + "、");
                            }
                            violation = violationList.get(0);
                            String name = buffer.substring(0, buffer.length() - 1);
                            String templateName = messageConfig.getProperty(MessageConstant.SITELEADER_SMS_TEMPLATE_KEY, MessageConstant.SITELEADER_SMS_TEMPLATE);
                            // ums短信发送接口替换成flash消息发送接口
                            SendRequest request = new SendRequest();
                            request.setTemplateId(Long.parseLong(siteLeaderSmsTemplateId));
                            request.setPlatform(platform); //申请模板时获得
                            request.setUserType(employeeUserType);
                            request.setAccount(message.getStaffPhone());
                            request.setBusiness(business);
                            request.setBizId(bizId);
                            List<SendTemplateParameter> parameters = new ArrayList<>(8);
                            // 配置参数
                            SendTemplateParameter param1 = this.setSendTemplateParam("name", name);
                            parameters.add(param1);
                            SendTemplateParameter param2 = this.setSendTemplateParam("serviceTime",
                                    Optional.ofNullable(violation.getServiceTime())
                                            .map(e -> DateUtils.formatDate(e, "yyyy年MM月dd日")).orElse(""));
                            parameters.add(param2);
                            SendTemplateParameter param3 = this.setSendTemplateParam("order", violation.getOrderId());
                            parameters.add(param3);
                            SendTemplateParameter param4 = this.setSendTemplateParam("type", violation.getType());
                            parameters.add(param4);
                            SendTemplateParameter param5 = this.setSendTemplateParam("detail", violation.getDetail());
                            parameters.add(param5);
                            SendTemplateParameter param6 = this.setSendTemplateParam("score", "" + violation.getScore());
                            parameters.add(param6);
                            SendTemplateParameter param7 = this.setSendTemplateParam("amount", violation.getTotalAmount().toString());
                            parameters.add(param7);
                            request.setParameters(parameters);
                            //调用发送接口
                            SendResponse result = apiInstance.send(request);
                            if (result.isSuccess()) {
                                message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            }
                            message.setMessageKey(result.getData().getMessageId().toString());
                            message.setTemplateName(templateName);

                            // 发送飞书消息
                            this.sendFeiShuMessage(request, Long.parseLong(siteLeaderFeiShuTemplateId), message.getStaffId());
                        }
                    } catch (Exception e) {
                        log.error("sendSiteLeaderSMS-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setTryTimes(tryTimes);
                        message.setModifyBy(Constants.SYS);
                        message.setModifyTime(new Date());
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                //TODO 补充消息发送失败流程
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }


    /***
     * 80天未缴纳罚款，短信再次提醒员工和站长；
     * 1.已离职员工不进行提醒.
     */
    public void sendNoPayMsg() {
        Page<Violation> page = violationService.findValidAndNoPayViolation(0, 100);
        int totalPage = page.getPages();
        try {
            Date now = new Date();
            for (int pageNo = 1; pageNo <= totalPage; pageNo++) {
                page = violationService.findValidAndNoPayViolation(pageNo, 100);
                if (CollectionUtils.isEmpty(page.getRecords())) {
                    continue;
                }
                for (Violation violation : page.getRecords()) {
                    if (staffApiManager.isResigned(violation.getStaffId())) {
                        continue;
                    }
                    Date expireDate = DateUtils.addDays(violation.getCreateTime(), repeatNotifyDay);
                    if (now.after(expireDate)) {
                        //构建并发送未支付提醒短信
                        this.sendNoPayMsgToStaff(violation);
                        this.sendNoPayMsgToSiteLeader(violation);
                    }
                }
            }
        } catch (Exception e) {
            log.error("发送未支付重新提醒短信失败 , e=", e);
        }
    }

    private void sendNoPayMsgToStaff(Violation violation) {
        ViolationMessage message = this.violationMessageService.saveStaffRepeatNotifyMessage(violation);
        if (message != null) {
            if (message.getStatus().equals(ViolationMessageStatusEnum.WAITTING.getValue())) {
                try {
                    if (violation != null) {
                        String templateName = messageStaffRepeatSmsTemplate;
                        if (StringUtils.isBlank(templateName)) {
                            return;
                        }
                        // ums短信发送接口替换成flash消息发送接口
                        SendRequest request = new SendRequest();
                        request.setTemplateId(Long.parseLong(staffRepeatSmsTemplateId));
                        request.setPlatform(platform); //申请模板时获得
                        request.setUserType(staffUserType);
                        request.setAccount(message.getStaffPhone());
                        request.setBusiness(business);
                        request.setBizId(bizId);
                        List<SendTemplateParameter> parameters = new ArrayList<>(8);
                        // 配置参数
                        SendTemplateParameter param1 = this.setSendTemplateParam("name", violation.getName());
                        parameters.add(param1);
                        SendTemplateParameter param2 = this.setSendTemplateParam("serviceTime",
                                Optional.ofNullable(violation.getServiceTime())
                                        .map(e -> DateUtils.formatDate(e, "yyyy年MM月dd日")).orElse(""));
                        parameters.add(param2);
                        SendTemplateParameter param3 = this.setSendTemplateParam("type", violation.getType());
                        parameters.add(param3);
                        SendTemplateParameter param4 = this.setSendTemplateParam("detail", violation.getDetail());
                        parameters.add(param4);
                        SendTemplateParameter param5 = this.setSendTemplateParam("amount", violation.getTotalAmount().toString());
                        parameters.add(param5);
                        Date closeDate = DateUtils.addDays(violation.getCreateTime(), timeoutPay);
                        SendTemplateParameter param6 = this.setSendTemplateParam("closeTime", DateUtils.formatDate(closeDate, "yyyy年MM月dd日"));
                        parameters.add(param6);
                        BigDecimal totalAmount = violation.getTotalAmount().multiply(new BigDecimal(1.5));
                        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
                        SendTemplateParameter param7 = this.setSendTemplateParam("totalAmount", totalAmount.toString());
                        parameters.add(param7);
                        request.setParameters(parameters);
                        //调用发送接口
                        SendResponse result = apiInstance.send(request);
                        if (result.isSuccess()) {
                            message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                        }
                        message.setMessageKey(result.getData().getMessageId().toString());
                        message.setTemplateName(templateName);
                        // 发送飞书消息
                        this.sendFeiShuMessage(request, Long.parseLong(staffRepeatFeiShuTemplateId), message.getStaffId());
                    }
                } catch (Exception e) {
                    log.error("sendNoPayMsgToStaff-" + message.getViolationCode() + "-error", e);
                } finally {
                    message.setModifyBy(Constants.SYS);
                    message.setModifyTime(new Date());
                    int tryTimes = message.getTryTimes() + 1;
                    message.setTryTimes(tryTimes);
                    if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                        int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                        if (tryTimes > tryTimesLimit) {
                            message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                        }
                    }
                }
                violationMessageService.updateById(message);
            }
        }
    }


    private void sendNoPayMsgToSiteLeader(Violation violation) {
        ViolationMessage message = this.violationMessageService.saveLeaderRepeatNotifyMessage(violation);
        if (message != null) {
            if (message.getStatus().equals(ViolationMessageStatusEnum.WAITTING.getValue())) {
                try {
                    if (violation != null) {
                        String templateName = messageSiteLeaderRepeatSmsTemplate;
                        if (StringUtils.isBlank(templateName)) {
                            return;
                        }
                        // ums短信发送接口替换成flash消息发送接口
                        SendRequest request = new SendRequest();
                        request.setTemplateId(Long.parseLong(siteLeaderRepeatSmsTemplateId));
                        request.setPlatform(platform); //申请模板时获得
                        request.setUserType(employeeUserType);
                        request.setAccount(message.getStaffPhone());
                        request.setBusiness(business);
                        request.setBizId(bizId);
                        List<SendTemplateParameter> parameters = new ArrayList<>(8);
                        // 配置参数
                        SendTemplateParameter param1 = this.setSendTemplateParam("staffName", violation.getName());
                        parameters.add(param1);
                        SendTemplateParameter param2 = this.setSendTemplateParam("serviceTime",
                                Optional.ofNullable(violation.getServiceTime())
                                        .map(e -> DateUtils.formatDate(e, "yyyy年MM月dd日")).orElse(""));
                        parameters.add(param2);
                        SendTemplateParameter param3 = this.setSendTemplateParam("type", violation.getType());
                        parameters.add(param3);
                        SendTemplateParameter param4 = this.setSendTemplateParam("detail", violation.getDetail());
                        parameters.add(param4);
                        SendTemplateParameter param5 = this.setSendTemplateParam("amount", violation.getTotalAmount().toString());
                        parameters.add(param5);
                        Date closeDate = DateUtils.addDays(violation.getCreateTime(), timeoutPay);
                        SendTemplateParameter param6 = this.setSendTemplateParam("closeTime", DateUtils.formatDate(closeDate, "yyyy年MM月dd日"));
                        parameters.add(param6);
                        BigDecimal totalAmount = violation.getTotalAmount().multiply(new BigDecimal(1.5));
                        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
                        SendTemplateParameter param7 = this.setSendTemplateParam("totalAmount", totalAmount.toString());
                        parameters.add(param7);
                        request.setParameters(parameters);
                        //调用发送接口
                        SendResponse result = apiInstance.send(request);
                        if (result.isSuccess()) {
                            message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                        }
                        message.setMessageKey(result.getData().getMessageId().toString());
                        message.setTemplateName(templateName);
                        // 发送飞书消息
                        this.sendFeiShuMessage(request, Long.parseLong(siteLeaderRepeatFeiShuTemplateId), message.getStaffId());
                    }
                } catch (Exception e) {
                    log.error("sendNoPayMsgToSiteLeader-" + message.getViolationCode() + "-error", e);
                } finally {
                    int tryTimes = message.getTryTimes() + 1;
                    message.setTryTimes(tryTimes);
                    message.setModifyTime(new Date());
                    message.setModifyBy(Constants.SYS);
                    if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                        int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                        if (tryTimes > tryTimesLimit) {
                            message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                        }
                    }
                }
                violationMessageService.updateById(message);
            }
        }
    }


    /***
     * 师徒制-师傅扣分短信
     */
    public void sendMentorShipSms() {
        log.info("sendMentorShipSms()");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(ViolationMessageTypeEnum.MENTOR_SHIP_SMS.getValue());
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        Violation violation = this.getViolationInfo(message.getViolationCode(), message.getStaffId());
                        if (violation != null) {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", violation.getName());
                            params.put("type", violation.getType());
                            params.put("score", "" + violation.getScore());
                            params.put("amount", violation.getTotalAmount().toString());
                            String templateName = mentorShipMentorSmsTemplate;
                            // ums短信发送接口替换成flash消息发送接口
                            SendRequest request = new SendRequest();
                            request.setTemplateId(Long.parseLong(mentorShipMentorSmsTemplateId));
                            request.setPlatform(platform); //申请模板时获得
                            request.setUserType(staffUserType);
                            request.setAccount(message.getStaffPhone());
                            request.setBusiness(business);
                            request.setBizId(bizId);
                            List<SendTemplateParameter> parameters = new ArrayList<>(8);
                            // 配置参数
                            SendTemplateParameter param1 = this.setSendTemplateParam("name", violation.getName());
                            parameters.add(param1);
                            SendTemplateParameter param2 = this.setSendTemplateParam("type", violation.getType());
                            parameters.add(param2);
                            SendTemplateParameter param3 = this.setSendTemplateParam("score", "" + violation.getScore());
                            parameters.add(param3);
                            SendTemplateParameter param4 = this.setSendTemplateParam("amount", violation.getTotalAmount().toString());
                            parameters.add(param4);
                            request.setParameters(parameters);
                            //调用发送接口
                            SendResponse result = apiInstance.send(request);
                            if (result.isSuccess()) {
                                message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            }
                            message.setMessageKey(result.getData().getMessageId().toString());
                            message.setTemplateName(templateName);
                        }
                    } catch (Exception e) {
                        log.error("sendMentorShipSms-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setModifyBy(Constants.SYS);
                        message.setTryTimes(tryTimes);
                        message.setModifyTime(new Date());
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }

    /***
     * 师徒制-师傅扣分app推送
     */
    public void sendMentorShipAppPush() {
        log.info("sendMentorShipAppPush()");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(ViolationMessageTypeEnum.MENTOR_SHIP_PUSH.getValue());
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        Violation violation = this.getViolationInfo(message.getViolationCode(), message.getStaffId());
                        if (violation != null) {
                            Map<String, String> params = new HashMap<>();
                            String name = violation.getName();
                            String violationType = violation.getType();
                            String score = Optional.ofNullable(violation.getScore()).map(Objects::toString).orElse("");
                            String amount = violation.getTotalAmount().toString();
                            params.put(TemplateParamsEnum.NAME.getValue(), name);
                            params.put(TemplateParamsEnum.VIOLATION_TYPE.getValue(), violationType);
                            params.put(TemplateParamsEnum.SCORE.getValue(), score);
                            params.put(TemplateParamsEnum.AMOUNT.getValue(), amount);
                            params.put(TemplateParamsEnum.VIOLATION_ID.getValue(), violation.getId().toString());
                            SendResponseData sendResponseData =
                                    messageBoxManager.singleSend(mentorShipMentorPushTemplateId, message.getStaffId(),
                                            params);
                            message.setMessageKey(sendResponseData.getMessageBoxRecordId().toString());
                            message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            message.setTemplateName(mentorShipMentorPushTemplateId.toString());

                        }
                    } catch (Exception e) {
                        log.error("sendMentorShipAppPush-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setTryTimes(tryTimes);
                        message.setModifyBy(Constants.SYS);
                        message.setModifyTime(new Date());
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }

    /***
     * 师徒制-师傅扣分站长短信
     */
    public void sendMentorShipLeaderSms() {
        log.info("sendMentorShipLeaderSms");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(ViolationMessageTypeEnum.MENTOR_SHIP_LEADER_SMS.getValue());
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        Violation violation = new Violation();
                        violation.setCode(message.getViolationCode());
                        violation.setSiteLeaderId(message.getStaffId());
                        violation.setStatus(StatusEnum.VALID.getValue());
                        Wrapper<Violation> violationWrapper = new EntityWrapper<>(violation).orderBy("id", false);
                        List<Violation> violationList = this.violationService.selectList(violationWrapper);
                        if (!CollectionUtils.isEmpty(violationList)) {
                            StringBuffer buffer = new StringBuffer();
                            for (Violation vo : violationList) {
                                buffer.append(vo.getName() + "、");
                            }
                            violation = violationList.get(0);
                            String name = buffer.substring(0, buffer.length() - 1);
                            String templateName = mentorShipSiteLeaderSmsTemplate;
                            // ums短信发送接口替换成flash消息发送接口
                            SendRequest request = new SendRequest();
                            request.setTemplateId(Long.parseLong(mentorShipSiteLeaderSmsTemplateId));
                            request.setPlatform(platform); //申请模板时获得
                            request.setUserType(employeeUserType);
                            request.setAccount(message.getStaffPhone());
                            request.setBusiness(business);
                            request.setBizId(bizId);
                            List<SendTemplateParameter> parameters = new ArrayList<>(8);
                            // 配置参数
                            SendTemplateParameter param1 = this.setSendTemplateParam("name", name);
                            parameters.add(param1);
                            SendTemplateParameter param2 = this.setSendTemplateParam("type", violation.getType());
                            parameters.add(param2);
                            SendTemplateParameter param3 = this.setSendTemplateParam("score", "" + violation.getScore());
                            parameters.add(param3);
                            SendTemplateParameter param4 = this.setSendTemplateParam("amount", violation.getTotalAmount().toString());
                            parameters.add(param4);
                            request.setParameters(parameters);
                            //调用发送接口
                            SendResponse result = apiInstance.send(request);
                            if (result.isSuccess()) {
                                message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            }
                            message.setMessageKey(result.getData().getMessageId().toString());
                            message.setTemplateName(templateName);
                        }
                    } catch (Exception e) {
                        log.error("sendMentorShipLeaderSms-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setTryTimes(tryTimes);
                        message.setModifyBy(Constants.SYS);
                        message.setModifyTime(new Date());
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }


    private Violation getViolationInfo(String violationCode, Long staffId) {
        Violation violation = new Violation();
        violation.setCode(violationCode);
        violation.setStaffId(staffId);
        Wrapper<Violation> violationWrapper = new EntityWrapper<>(violation).orderBy("id", false);
        violation = violationService.selectOne(violationWrapper);
        return violation;
    }


    /***
     * 复训-员工短息
     */
    public void sendRetrainStaffSms() {
        log.info("sendRetrainStaffSms()");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(ViolationMessageTypeEnum.RETRAIN_STAFF_SMS.getValue());
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        Violation violation = this.getViolationInfo(message.getViolationCode(), message.getStaffId());
                        if (violation != null) {
                            String templateName = retrainStaffSmsTemplate;
                            // ums短信发送接口替换成flash消息发送接口
                            SendRequest request = new SendRequest();
                            request.setTemplateId(Long.parseLong(retrainStaffSmsTemplateId));
                            request.setPlatform(platform); //申请模板时获得
                            request.setUserType(staffUserType);
                            request.setAccount(message.getStaffPhone());
                            request.setBusiness(business);
                            request.setBizId(bizId);
                            List<SendTemplateParameter> parameters = new ArrayList<>(8);
                            // 配置参数
                            SendTemplateParameter param1 = this.setSendTemplateParam("name", violation.getName());
                            parameters.add(param1);
                            request.setParameters(parameters);
                            //调用发送接口
                            SendResponse result = apiInstance.send(request);
                            if (result.isSuccess()) {
                                message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            }
                            message.setMessageKey(result.getData().getMessageId().toString());
                            message.setTemplateName(templateName);
                            // 发送飞书消息
                            this.sendFeiShuMessage(request, Long.parseLong(retrainStaffFeiShuTemplateId), message.getStaffId());
                        }
                    } catch (Exception e) {
                        log.error("sendRetrainStaffSms-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setModifyBy(Constants.SYS);
                        message.setTryTimes(tryTimes);
                        message.setModifyTime(new Date());
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }


    /***
     * 复训-员工app推送
     */
    public void sendRetrainStaffAppPush() {
        log.info("sendRetrainStaffAppPush()");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(ViolationMessageTypeEnum.RETRAIN_STAFF_PUSH.getValue());
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        Violation violation = this.getViolationInfo(message.getViolationCode(), message.getStaffId());
                        if (violation != null) {
                            Map<String, String> params = new HashMap<>();
                            String name = violation.getName();
                            params.put(TemplateParamsEnum.NAME.getValue(), name);
                            SendResponseData sendResponseData =
                                    messageBoxManager.singleSend(retrainStaffPushTemplateId, message.getStaffId(), params);
                            message.setMessageKey(sendResponseData.getMessageBoxRecordId().toString());
                            message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            message.setTemplateName(retrainStaffPushTemplateId.toString());
                        }
                    } catch (Exception e) {
                        log.error("sendRetrainStaffAppPush-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setTryTimes(tryTimes);
                        message.setModifyBy(Constants.SYS);
                        message.setModifyTime(new Date());
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }

    /**
     * 给员工发送违规复训短信
     */
    public void sendViolationRetrainStaffSms() {
        this.sendViolationOrTrainingRetrainStaffSms(ViolationMessageTypeEnum.VIOLATION_RETRAIN_STAFF_SMS.getValue(), violationRetrainStaffSmsTemplate);
    }

    /**
     * 给员工发送培训复训短信
     */
    public void sendTrainingRetrainStaffSms() {
        this.sendViolationOrTrainingRetrainStaffSms(ViolationMessageTypeEnum.TRAINING_RETRAIN_STAFF_SMS.getValue(), trainingRetrainStaffSmsTemplate);
    }

    /**
     * 给员工发送违规复训或培训复训短信
     */
    public void sendViolationOrTrainingRetrainStaffSms(Integer messageType, String templateName) {
        log.info("sendViolationOrTrainingRetrainStaffSms(), messageType = {}, templateName ={}", messageType, templateName);
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        //每次都取第一页
        int pageNo = 0;
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(messageType);
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            Date modifyTime = new Date();
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        LicenseInfoVO vo = this.licenseService.findLicenseByStaffId(message.getStaffId());
                        if (vo != null) {
                            // ums短信发送接口替换成flash消息发送接口
                            SendRequest request = new SendRequest();
                            if (violationRetrainStaffSmsTemplate.equals(templateName)) {
                                request.setTemplateId(Long.parseLong(violationRetrainStaffSmsTemplateId));
                            } else if (trainingRetrainStaffSmsTemplate.equals(templateName)) {
                                request.setTemplateId(Long.parseLong(trainingRetrainStaffSmsTemplateId));
                            }
                            request.setPlatform(platform); //申请模板时获得
                            request.setUserType(staffUserType);
                            request.setAccount(message.getStaffPhone());
                            request.setBusiness(business);
                            request.setBizId(bizId);
                            List<SendTemplateParameter> parameters = new ArrayList<>(8);
                            // 配置参数
                            SendTemplateParameter param1 = this.setSendTemplateParam("name", vo.getName());
                            parameters.add(param1);
                            request.setParameters(parameters);
                            //调用发送接口
                            SendResponse result = apiInstance.send(request);
                            if (result.isSuccess()) {
                                message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            }
                            message.setMessageKey(result.getData().getMessageId().toString());
                            message.setTemplateName(templateName);
                            // 发送飞书消息
                            if (violationRetrainStaffSmsTemplate.equals(templateName)) {
                                this.sendFeiShuMessage(request, Long.parseLong(violationRetrainStaffFeiShuTemplateId), message.getStaffId());
                            } else if (trainingRetrainStaffSmsTemplate.equals(templateName)) {
                                this.sendFeiShuMessage(request, Long.parseLong(trainingRetrainStaffFeiShuTemplateId), message.getStaffId());
                            }
                        }
                    } catch (Exception e) {
                        log.error("sendViolationOrTrainingRetrainStaffSms-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setModifyBy(Constants.SYS);
                        message.setTryTimes(tryTimes);
                        message.setModifyTime(modifyTime);
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }

    /**
     * 给员工发送违规复训——app推送
     */
    public void sendViolationRetrainStaffAppPush() {
        this.sendViolationOrTrainingRetrainStaffAppPush(
                ViolationMessageTypeEnum.VIOLATION_RETRAIN_STAFF_PUSH.getValue(), violationRetrainStaffPushTemplateId);
    }

    /**
     * 给员工发送培训复训——app推送
     */
    public void sendTrainingRetrainStaffAppPush() {
        this.sendViolationOrTrainingRetrainStaffAppPush(ViolationMessageTypeEnum.TRAINING_RETRAIN_STAFF_PUSH.getValue(),
                trainingRetrainStaffPushTemplateId);
    }

    /***
     * 给员工发送违规复训或培训复训——app推送
     */
    public void sendViolationOrTrainingRetrainStaffAppPush(Integer messageType, Long templateId) {
        log.info("sendViolationOrTrainingRetrainStaffAppPush()");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<ViolationMessage> page = new Page<>(pageNo, pageSize);
            ViolationMessage violationMessage = new ViolationMessage();
            violationMessage.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            violationMessage.setType(messageType);
            Wrapper<ViolationMessage> wrapper = new EntityWrapper<>(violationMessage);
            page = violationMessageService.selectPage(page, wrapper);
            Date modifyTime = new Date();
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                currentSize = page.getRecords().size();
                for (ViolationMessage message : page.getRecords()) {
                    try {
                        LicenseInfoVO vo = this.licenseService.findLicenseByStaffId(message.getStaffId());
                        if (vo != null) {
                            Map<String, String> params = new HashMap<>();
                            String name = vo.getName();
                            params.put("name", name);
                            SendResponseData sendResponseData =
                                    messageBoxManager.singleSend(templateId, message.getStaffId(), params);
                            message.setMessageKey(sendResponseData.getMessageBoxRecordId().toString());
                            message.setStatus(ViolationMessageStatusEnum.SENDED.getValue());
                            message.setTemplateName(templateId.toString());
                        }
                    } catch (Exception e) {
                        log.error("sendViolationOrTrainingRetrainStaffAppPush-" + message.getViolationCode() + "-error", e);
                    } finally {
                        int tryTimes = message.getTryTimes() + 1;
                        message.setTryTimes(tryTimes);
                        message.setModifyBy(Constants.SYS);
                        message.setModifyTime(modifyTime);
                        if (ViolationMessageStatusEnum.SENDED.getValue() != message.getStatus()) {
                            int tryTimesLimit = messageConfig.getIntProperty(MessageConstant.TRYTIMES_LIMIT_KEY, MessageConstant.TRYTIMES_LIMIT);
                            if (tryTimes > tryTimesLimit) {
                                message.setStatus(ViolationMessageStatusEnum.FAILD.getValue());
                            }
                        }
                    }
                    violationMessageService.updateById(message);
                }
            }
        } while (currentSize == pageSize);
    }

    private SendTemplateParameter setSendTemplateParam(String key, String value) {
        SendTemplateParameter parameter = new SendTemplateParameter();
        parameter.setKey(key);
        parameter.setValue(value);
        return parameter;
    }


    /***
     * 超时未复训-发邮件提醒培训师
     */
    public void sendTimeOutRetrainEmailToTeacher() {
        log.info("sendTimeOutRetrainEmailToTeacher()");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        Page<Retrain> page = new Page<>(1, pageSize);
        this.retrainService.pageTimeoutRetrainList(page, limitDay);
        int totalPage = page.getPages();
        for (int pageNo = 1; pageNo <= totalPage; pageNo++) {
            page = new Page<>(pageNo, pageSize);
            List<Retrain> list = retrainService.pageTimeoutRetrainList(page, limitDay);
            if (!CollectionUtils.isEmpty(list)) {
                for (Retrain retrain : list) {
                    this.sendRetrainTimeoutNotifyEmailToTrainTeacher(retrain);
                }
            }
        }
    }

    private void sendRetrainTimeoutNotifyEmailToTrainTeacher(Retrain retrain) {
        //给培训师发邮件
        String key = Constants.RETRAIN_TIMEOUT_REMIND_ID + retrain.getId();
        if (redisUtil.get(key) != null) {
            return;
        }
        SiteAndRegionVO vo = siteViolationService.getTrainTeacherInfo(retrain.getSiteId());
        if (StringUtils.isBlank(vo.getTrainingTeacherEmail())) {
            return;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("teacherName", vo.getTrainingTeacherName() + "");
        params.put("siteName", retrain.getSiteName());
        params.put("staffName", retrain.getName());
        String createTime = DateUtils.formatDate(retrain.getCreateTime(), "yyyy年MM月dd日");
        params.put("createTime", createTime);
        params.put("limitDay", limitDay);
        try {
            mailService.sendTemplateMail(vo.getTrainingTeacherEmail(), MessageConstant.RETRAIN_TIMEOUT_TEACHER_NOTIFY_SUBJECT, MessageConstant.RETRAIN_TIMEOUT_TEACHER_NOTIFY_TEMPLATE, params);
            redisUtil.set(key, retrain.getId(), 10 * 3600 * 24L);
        } catch (TemplateException e) {
            log.error("[给培训师发送复训超时邮件通知]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[给培训师发送复训超时邮件通知]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[给培训师发送复训超时邮件通知]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[给培训师发送复训超时邮件通知]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    private void sendFeiShuMessage(SendRequest request, Long templateId, Long staffId) {
        log.info("sendFeiShuMessage,request:{}, templateId:{},staffId:{}", request, templateId, staffId);
        SendRequest sendRequest = BeanMapper.map(request, SendRequest.class);
        sendRequest.setPlatform(null);
        sendRequest.setAccount(null);
        sendRequest.setTemplateId(templateId);
        sendRequest.setUserId(staffId);
        apiInstance.send(sendRequest);
    }
}
