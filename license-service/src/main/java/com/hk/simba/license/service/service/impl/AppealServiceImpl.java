package com.hk.simba.license.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hk.base.util.DateUtils;
import com.hk.simba.license.api.enums.AppealStatusEnum;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.request.appeal.AppealQueryRequest;
import com.hk.simba.license.api.request.retrain.RetrainInvalidRequest;
import com.hk.simba.license.api.vo.AppealVO;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.ApproveStatusEnum;
import com.hk.simba.license.service.constant.enums.ApproveTypeEnum;
import com.hk.simba.license.service.constant.enums.DeductTypeEnum;
import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.simba.license.service.constant.enums.TaskNodeEnum;
import com.hk.simba.license.service.entity.Appeal;
import com.hk.simba.license.service.entity.AppealApproveLog;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.exception.CommonException;
import com.hk.simba.license.service.manager.LarkInstanceManager;
import com.hk.simba.license.service.manager.UserApiManager;
import com.hk.simba.license.service.manager.WorkflowManager;
import com.hk.simba.license.service.mapper.AppealMapper;
import com.hk.simba.license.service.service.AppealApproveLogService;
import com.hk.simba.license.service.service.AppealService;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.simba.license.service.service.MailService;
import com.hk.simba.license.service.service.RetrainService;
import com.hk.simba.license.service.service.ViolationService;
import com.hk.simba.workflow.event.WorkFlowEvent;
import com.hk.simba.workflow.open.constant.WorkFlowStatus;
import com.hk.simba.x.lark.open.response.InstanceResponse;
import com.hk.simba.x.lark.open.response.InstanceResponse.Task;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 申诉信息表 服务实现类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Slf4j
@Service
public class AppealServiceImpl extends ServiceImpl<AppealMapper, Appeal> implements AppealService {

    @Autowired
    private ViolationService violationService;
    @Autowired
    private AppealApproveLogService appealApproveLogService;
    @Autowired
    private UserApiManager userApiManager;
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private LarkInstanceManager larkInstanceManager;
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private MailService mailService;
    @Autowired
    private RetrainService retrainService;

    @Override
    public List<AppealVO> selectPageList(Page<Appeal> page, AppealQueryRequest request) {
        return baseMapper.selectPageList(page, request);
    }

    @Override
    public List<Appeal> waitRegionApprovePageList(Page<Appeal> page, int timeoutAppeal) {
        return baseMapper.waitRegionApprovePageList(page, timeoutAppeal);
    }

    @Override
    public List<Appeal> timeoutAppealApprove(int approveTimeoutDay) {
        return baseMapper.timeoutAppealApprove(approveTimeoutDay);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recallAppeal(WorkFlowEvent event) {
        Appeal appeal = this.selectById(Long.parseLong(event.getBizId()));
        if (null == appeal) {
            throw new CommonException(ResponseCodeEnum.ERROR_NONE_RECORD.getCode(), ResponseCodeEnum.ERROR_NONE_RECORD.getMessage());
        }
        if (appeal.getStatus().equals(ApproveStatusEnum.APPEAL_RECALL.getValue())) {
            return;
        } else if (!appeal.getStatus().equals(ApproveStatusEnum.WAIT_REGION_APPROVE.getValue())) {
            throw new CommonException(ResponseCodeEnum.CAN_NOT_APPEAL_RECALL.getCode(), ResponseCodeEnum.CAN_NOT_APPEAL_RECALL.getMessage());
        }
        Violation violation = this.violationService.selectById(appeal.getViolationId());
        if (!violation.getStatus().equals(StatusEnum.VALID.getValue())) {
            log.info("recallAppeal:{}", ResponseCodeEnum.VIOLATION_INVALID.getMessage());
            return;
        }
        if (!violation.getAppealStatus().equals(AppealStatusEnum.APPEALING.getValue())) {
            log.info("recallAppeal:violation.getAppealStatus={},message={}", AppealStatusEnum.getEnumByValue(violation.getAppealStatus()).getValue(), ResponseCodeEnum.ERROR_APPEAL_STATUS.getMessage());
            throw new CommonException(ResponseCodeEnum.ERROR_APPEAL_STATUS.getCode(), ResponseCodeEnum.ERROR_APPEAL_STATUS.getMessage());
        }
        String operator = Constants.SYS;
        if (!ObjectUtils.isEmpty(event.getTask())) {
            operator = userApiManager.findOperatorThirdUserId(event.getTask().getUserId());
        }
        //申诉变成撤销
        Date date = new Date();
        appeal.setModifyTime(date);
        appeal.setModifyBy(operator);
        appeal.setStatus(ApproveStatusEnum.APPEAL_RECALL.getValue());
        appeal.setRecallTime(date);
        this.updateById(appeal);

        //违规记录变成撤回
        violation.setModifyTime(date);
        violation.setModifyBy(operator);
        violation.setAppealStatus(AppealStatusEnum.APPEAL_REVOKE.getValue());
        this.violationService.updateById(violation);

        //生成审批日志,且状态变成已撤销
        AppealApproveLog log = new AppealApproveLog();
        log.setAppealId(appeal.getId());
        log.setCreateTime(date);
        log.setViolationId(violation.getId());
        log.setCreateBy(operator);
        log.setStatus(ApproveStatusEnum.APPEAL_RECALL.getValue());
        log.setApproveType(ApproveTypeEnum.SITE_LEADER.getValue());
        log.setRemark(ApproveTypeEnum.SITE_LEADER.getText() + ":" + operator + ";撤回申诉");
        this.appealApproveLogService.insert(log);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passAppeal(WorkFlowEvent event) {
        log.info("passAppeal, appealId={}", event.getBizId());
        Appeal appeal = this.selectById(Long.parseLong(event.getBizId()));
        if (null == appeal) {
            throw new CommonException(ResponseCodeEnum.ERROR_NONE_RECORD.getCode(), ResponseCodeEnum.ERROR_NONE_RECORD.getMessage());
        }
        String instanceCode = workflowManager.getEngineInstanceIdByBizIdAndBizType(event.getBizId(), event.getBizType());
        InstanceResponse instance = larkInstanceManager.getInstanceDetail(instanceCode);
        //自动审批通过-飞书任务状态DONE，判断实例状态是否为：审批中PENDING，已通过APPROVED
        if (WorkFlowStatus.CANCEL.equals(event.getStatus()) && !ObjectUtils.isEmpty(instance)) {
            if (!Constants.WORKFLOW_INSTANCE_PENDING.equals(instance.getStatus())
                    && !Constants.WORKFLOW_INSTANCE_APPROVED.equals(instance.getStatus())) {
                log.info("passAppeal:task=CANCEL,instance.status={}", instance.getStatus());
                return;
            }
        }
        TaskNodeEnum taskNodeEnum = this.getTaskNodeByStatus(appeal.getStatus());
        if (ObjectUtils.isEmpty(taskNodeEnum)) {
            log.info("passAppeal:taskNode not found");
            return;
        }
        // 审批节点未通过.
        if (!larkInstanceManager.isTaskPass(instance, taskNodeEnum.getText())) {
            log.info("passAppeal:taskNode not pass");
            return;
        }
        ApproveStatusEnum nextStatusEnum = ApproveStatusEnum.getPassAppealStatus(appeal.getStatus());
        if (ObjectUtils.isEmpty(nextStatusEnum)) {
            log.info("passAppeal:status={}, nextStatusEnum is null", appeal.getStatus());
            return;
        }
        Violation violation = violationService.selectById(appeal.getViolationId());
        if (!violation.getStatus().equals(StatusEnum.VALID.getValue())) {
            log.info("passAppeal:{}", ResponseCodeEnum.VIOLATION_INVALID.getMessage());
            return;
        }
        if (!violation.getAppealStatus().equals(AppealStatusEnum.APPEALING.getValue())) {
            log.info("passAppeal:violation.getAppealStatus={},message={}", AppealStatusEnum.getEnumByValue(violation.getAppealStatus()).getValue(),
                    ResponseCodeEnum.ERROR_APPEAL_STATUS.getMessage());
            return;
        }
        String operator = Constants.SYS;
        String approver = this.getApprover(instance.getTaskList(), taskNodeEnum.getText());
        if (StringUtils.isNotBlank(approver)) {
            operator = approver;
        } else {
            if (!ObjectUtils.isEmpty(event.getTask())) {
                operator = userApiManager.findOperatorThirdUserId(event.getTask().getUserId());
            }
        }
        //更新申诉信息
        Date date = new Date();
        appeal.setStatus(nextStatusEnum.getValue());
        appeal.setDealTime(date);
        appeal.setModifyTime(date);
        appeal.setModifyBy(operator);
        this.updateById(appeal);
        if (ApproveStatusEnum.APPROVE_SUCCESS.equals(nextStatusEnum)) {
            //更新违规申诉状态
            violation.setAppealStatus(AppealStatusEnum.APPEAL_PASS.getValue());
            violation.setStatus(StatusEnum.APPEAL_INVALID.getValue());
            violation.setReason(Constants.QUALITY_APPROVE);
            violation.setModifyTime(date);
            violation.setModifyBy(operator);
            //员工违规类型,分数回滚
            if (DeductTypeEnum.STAFF_DEDUCT.getValue().equals(violation.getDeductType())) {
                licenseService.appealPass(violation);
                //申诉结果提醒邮件
                this.sendAppealResultEmail(violation, appeal.getAppealTime(), ApproveStatusEnum.APPROVE_SUCCESS.getText());
            }
            if (DeductTypeEnum.SITE_DEDUCT.getValue().equals(violation.getDeductType())) {
                // 由于站点违规不扣员工分，所以审批通过后便失效掉复训记录.
                this.invalidRetrain(violation);
                this.sendSiteAppealResultEmail(violation, appeal.getAppealTime(), ApproveStatusEnum.APPROVE_SUCCESS.getText());
            }
            violationService.updateById(violation);
        }
        //生成审批日志,状态与申诉状态一致
        AppealApproveLog approveLog = new AppealApproveLog();
        approveLog.setAppealId(appeal.getId());
        approveLog.setCreateTime(date);
        approveLog.setCreateBy(operator);
        approveLog.setViolationId(violation.getId());
        approveLog.setStatus(appeal.getStatus());
        ApproveTypeEnum typeEnum = ApproveTypeEnum.getEnumByValue(taskNodeEnum.getValue());
        if (typeEnum == null) {
            log.warn("找不到审批节点类型：{}", JSON.toJSONString(appeal));
            throw new CommonException(ResponseCodeEnum.ERROR_NONE_RECORD.getCode(), "找不到审批类型");
        }
        approveLog.setApproveType(typeEnum.getValue());
        String improveAdvice = larkInstanceManager.getTaskComment(instance, taskNodeEnum);
        approveLog.setImproveAdvice(improveAdvice);
        appealApproveLogService.insert(approveLog);
    }

    private String getApprover(List<Task> taskList, String nodeName) {
        for (Task task : taskList) {
            if (nodeName.equals(task.getNodeName())
                    && Constants.WORKFLOW_TASK_APPROVED.equals(task.getStatus())) {
                return userApiManager.findOperatorThirdUserId(task.getUserId());
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuseAppeal(WorkFlowEvent event, Boolean isTimeout) {
        log.info("refuseAppeal, appealId={},isTimeout={}", event.getBizId(), isTimeout);
        Appeal appeal = this.selectById(Long.parseLong(event.getBizId()));
        if (null == appeal) {
            throw new CommonException(ResponseCodeEnum.ERROR_NONE_RECORD.getCode(), ResponseCodeEnum.ERROR_NONE_RECORD.getMessage());
        }
        String instanceCode = workflowManager.getEngineInstanceIdByBizIdAndBizType(event.getBizId(), event.getBizType());
        InstanceResponse instance = larkInstanceManager.getInstanceDetail(instanceCode);
        if (ObjectUtils.isEmpty(instance) || Constants.WORKFLOW_INSTANCE_APPROVED.equals(instance.getStatus())) {
            log.info("refuseAppeal,fail instance.status={}", instance.getStatus());
            return;
        }
        String taskName = larkInstanceManager.getLastTaskName(instance);
        TaskNodeEnum taskNodeEnum = TaskNodeEnum.getEnumByText(taskName);
        if (ObjectUtils.isEmpty(taskNodeEnum)) {
            log.info("refuseAppeal:taskNode not found");
            return;
        }
        ApproveStatusEnum nextStatusEnum = ApproveStatusEnum.getRefuseAppealStatus(appeal.getStatus());
        ApproveTypeEnum typeEnum = ApproveTypeEnum.getEnumByValue(taskNodeEnum.getValue());
        if (ObjectUtils.isEmpty(nextStatusEnum)) {
            log.info("refuseAppeal:status={}, nextStatusEnum is null", appeal.getStatus());
            return;
        }
        Violation violation = this.violationService.selectById(appeal.getViolationId());
        if (!violation.getStatus().equals(StatusEnum.VALID.getValue())) {
            log.info("refuseAppeal:{}", ResponseCodeEnum.VIOLATION_INVALID.getMessage());
            return;
        }
        if (!violation.getAppealStatus().equals(AppealStatusEnum.APPEALING.getValue())) {
            log.info("refuseAppeal:violation.getAppealStatus={},message={}", AppealStatusEnum.getEnumByValue(violation.getAppealStatus()).getValue(), ResponseCodeEnum.ERROR_APPEAL_STATUS.getMessage());
            return;
        }
        String operator = Constants.SYS;
        if (!ObjectUtils.isEmpty(event.getTask())) {
            operator = userApiManager.findOperatorThirdUserId(event.getTask().getUserId());
        }
        String improveAdvice = Constants.TIME_OUT_REJECT;
        if (!isTimeout) {
            improveAdvice = larkInstanceManager.getTaskComment(instance, taskNodeEnum);
        }

        //更新申诉信息
        Date date = new Date();
        appeal.setStatus(nextStatusEnum.getValue());
        appeal.setDealTime(date);
        appeal.setModifyTime(date);
        appeal.setModifyBy(operator);
        this.updateById(appeal);

        //更新违规申诉状态
        violation.setModifyTime(date);
        violation.setModifyBy(operator);
        //更新违规申诉状态
        violation.setModifyTime(date);
        violation.setModifyBy(operator);
        violation.setAppealStatus(AppealStatusEnum.APPEAL_REJECT.getValue());
        this.violationService.updateById(violation);

        //申诉结果提醒邮件
        if (violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
            this.sendAppealResultEmail(violation, appeal.getAppealTime(), ApproveStatusEnum.getEnumByValue(appeal.getStatus()).getText());
        }
        if (violation.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue())) {
            this.sendSiteAppealResultEmail(violation, appeal.getAppealTime(), ApproveStatusEnum.getEnumByValue(appeal.getStatus()).getText());
        }
        //生成审批日志,状态与申诉状态一致
        AppealApproveLog approveLog = new AppealApproveLog();
        approveLog.setAppealId(appeal.getId());
        approveLog.setCreateTime(date);
        approveLog.setCreateBy(operator);
        approveLog.setViolationId(violation.getId());
        approveLog.setStatus(appeal.getStatus());
        approveLog.setApproveType(typeEnum.getValue());
        approveLog.setImproveAdvice(improveAdvice);
        this.appealApproveLogService.insert(approveLog);
        if (isTimeout) {
            try {
                larkInstanceManager.reject(instanceCode, instance, Constants.TIME_OUT_REJECT);
            } catch (Exception e) {
                log.error("refuseAppeal, isTimeout");
            }
        }
    }

    @Override
    public void refuseAppealForViolationInvalid(Long violationId) {
        List<Appeal> appealList = this.selectList(new EntityWrapper<Appeal>().eq("violation_id", violationId).isNotNull("instance_id").in("status",
                Lists.newArrayList(ApproveStatusEnum.WAIT_REGION_APPROVE.getValue(), ApproveStatusEnum.WAIT_SERVICE_APPROVE.getValue(), ApproveStatusEnum.WAIT_BUSINESS_APPROVE.getValue(), ApproveStatusEnum.WAIT_QUALITY_APPROVE.getValue())));
        if (CollectionUtils.isEmpty(appealList)) {
            return;
        }
        appealList.forEach(appeal -> {
            String instanceCode = workflowManager.getEngineInstanceIdByBizIdAndBizType(appeal.getId().toString(), Constants.LICENSE_APPEAL);
            InstanceResponse instance = larkInstanceManager.getInstanceDetail(instanceCode);
            larkInstanceManager.reject(instanceCode, instance, Constants.VIOLATION_INVAILD_REJECT);
        });
    }

    /**
     * 申诉结果提醒邮件
     */
    @Override
    public void sendAppealResultEmail(Violation violation, Date appealTime, String result) {
        Map<String, Object> params = Maps.newHashMap();
        String email = licenseService.getSiteLeaderEmail(violation.getSiteLeaderId());
        if (StringUtils.isEmpty(email)) {
            log.error("[申诉处理]站点:{}-{}的站长邮箱为空", violation.getSiteName(), violation.getSiteId());
            return;
        }
        String appealTimeStr = DateUtils.formatDate(appealTime, "yyyy年MM月dd日");
        params.put("appealTime", appealTimeStr);
        params.put("staffName", violation.getName());
        params.put("code", violation.getCode());
        params.put("result", result);
        try {
            mailService.sendTemplateMail(email, MessageConstant.APPEAL_RESULT_SUBJECT, MessageConstant.APPEAL_RESULT_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[申诉结果]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[申诉结果]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[申诉结果]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[申诉结果]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    /**
     * 站点申诉结果提醒邮件
     */
    @Override
    public void sendSiteAppealResultEmail(Violation violation, Date appealTime, String result) {
        Map<String, Object> params = Maps.newHashMap();
        String email = licenseService.getSiteLeaderEmail(violation.getSiteLeaderId());
        if (StringUtils.isEmpty(email)) {
            log.error("[站点申诉结果提醒]站点:{}-{}的站长邮箱为空", violation.getSiteName(), violation.getSiteId());
            return;
        }
        String appealTimeStr = DateUtils.formatDate(appealTime, "yyyy年MM月dd日");
        params.put("appealTime", appealTimeStr);
        params.put("code", violation.getCode());
        params.put("result", result);
        try {
            mailService.sendTemplateMail(email, MessageConstant.SITE_APPEAL_RESULT_SUBJECT, MessageConstant.SITE_APPEAL_RESULT_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[站点申诉结果提醒]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[站点申诉结果提醒]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[站点申诉结果提醒]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[站点申诉结果提醒]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    private TaskNodeEnum getTaskNodeByStatus(Integer status) {
        log.info("getTaskNodeByStatus, status={}", status);
        ApproveStatusEnum curStatus = ApproveStatusEnum.getEnumByValue(status);
        TaskNodeEnum taskNodeEnum = null;
        switch (curStatus) {
            case WAIT_REGION_APPROVE:
                taskNodeEnum = TaskNodeEnum.REGION;
                break;
            case WAIT_SERVICE_APPROVE:
                taskNodeEnum = TaskNodeEnum.SERVICE;
                break;
            case WAIT_BUSINESS_APPROVE:
                taskNodeEnum = TaskNodeEnum.BUSINESS;
                break;
            case WAIT_QUALITY_APPROVE:
                taskNodeEnum = TaskNodeEnum.QUALITY;
                break;
            default:
                break;
        }
        return taskNodeEnum;
    }

    private void invalidRetrain(Violation violation) {
        RetrainInvalidRequest invalidRequest = new RetrainInvalidRequest();
        invalidRequest.setOperator(violation.getModifyBy());
        invalidRequest.setReason(Constants.VIOLATION_INVALID);
        invalidRequest.setViolationId(violation.getId());
        this.retrainService.invalidRetrainByCondition(invalidRequest);
    }
}
