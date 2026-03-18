package com.hk.simba.license.service.open;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hk.base.util.DateUtils;
import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.quark.base.util.ReplaceUtils;
import com.hk.simba.archive.open.response.department.DepartmentData;
import com.hk.simba.archive.open.response.department.SiteDepartmentData;
import com.hk.simba.base.common.dto.response.PageData;
import com.hk.simba.base.json.JsonUtils;
import com.hk.simba.license.api.AppealOpenService;
import com.hk.simba.license.api.enums.AppealStatusEnum;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.appeal.AppealDetailRequest;
import com.hk.simba.license.api.request.appeal.AppealQueryRequest;
import com.hk.simba.license.api.request.appeal.AppealRequest;
import com.hk.simba.license.api.request.retrain.RetrainInvalidRequest;
import com.hk.simba.license.api.vo.AppealApproveLogVO;
import com.hk.simba.license.api.vo.AppealDetailVO;
import com.hk.simba.license.api.vo.AppealVO;
import com.hk.simba.license.api.vo.EventAttachment;
import com.hk.simba.license.api.vo.SiteAndRegionVO;
import com.hk.simba.license.api.vo.UserInfoVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.ApproveStatusEnum;
import com.hk.simba.license.service.constant.enums.ApproveTypeEnum;
import com.hk.simba.license.service.constant.enums.ButtonEnum;
import com.hk.simba.license.service.constant.enums.DeductTypeEnum;
import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.simba.license.service.constant.enums.UserType;
import com.hk.simba.license.service.entity.Appeal;
import com.hk.simba.license.service.entity.AppealApproveLog;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.manager.ArchiveManager;
import com.hk.simba.license.service.manager.WorkflowManager;
import com.hk.simba.license.service.service.AppealApproveLogService;
import com.hk.simba.license.service.service.AppealService;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.simba.license.service.service.MailService;
import com.hk.simba.license.service.service.RetrainService;
import com.hk.simba.license.service.service.SiteAndRegionInfoService;
import com.hk.simba.license.service.service.ViolationService;
import com.hk.simba.license.service.utils.AttachmentUtil;
import com.hk.simba.license.service.utils.R;
import com.hk.simba.license.service.utils.YearUtil;
import com.hk.simba.workflow.open.request.ProcessApproverRequest;
import com.hk.simba.workflow.open.request.SubmitInstanceRequest;
import com.hk.simba.workflow.open.request.field.AttachmentField;
import com.hk.simba.workflow.open.request.field.Field;
import com.hk.simba.workflow.open.request.field.PictureField;
import com.hk.simba.workflow.open.request.field.TextAreaField;
import com.hk.simba.workflow.open.request.field.TextField;
import com.hk.sisyphus.merope.core.contract.ContractItemApi;
import com.hk.sisyphus.merope.core.mpworkorder.ServiceWorkOrderApi;
import com.hk.sisyphus.merope.core.sas.UserApi;
import com.hk.sisyphus.merope.model.contract.contractitem.QueryContractItemBaseInfoPageContractItemBaseInfoDto;
import com.hk.sisyphus.merope.model.contract.contractitem.QueryContractItemBaseInfoPageRequest;
import com.hk.sisyphus.merope.model.contract.contractitem.QueryContractItemBaseInfoPageUserInfoParam;
import com.hk.sisyphus.merope.model.mpworkorder.serviceworkorder.QueryByServiceOrderIdRequest;
import com.hk.sisyphus.merope.model.mpworkorder.serviceworkorder.QueryByServiceOrderIdServiceWorkOrderData;
import com.hk.sisyphus.merope.model.sas.user.GetUserTypeRequest;
import com.hk.sisyphus.merope.model.sas.user.GetUserTypeUserTypeDto;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 羊皮
 * @description
 * @since 2020-4-10 9:23:37
 * 备注：申诉审核通过或者驳回的原因都是记录在AppealApproveLog日志里，Appeal中的remark是站长发起申诉填写的原因
 */
@Slf4j
@DubboService
public class AppealOpenServiceImpl implements AppealOpenService {

    /**
     * 违规服务类
     */
    @Autowired
    private ViolationService violationService;

    /**
     * 申诉服务类
     */
    @Autowired
    private AppealService appealService;

    /**
     * 申诉日志
     */
    @Autowired
    private AppealApproveLogService appealApproveLogService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RetrainService retrainService;

    @Autowired
    private ServiceWorkOrderApi serviceWorkOrderApi;

    @Autowired
    private UserApi userApi;

    @Autowired
    private ContractItemApi contractItemApi;

    @Autowired
    private SiteAndRegionInfoService siteAndRegionInfoService;

    @Autowired
    private WorkflowManager workflowManager;

    @Autowired
    private ArchiveManager archiveManager;
    /**
     * 质质高邮箱
     */
    @Value("${quality.email}")
    private String qualityEmail;

    @Value("${appeal.limit.day}")
    private String appealLimitDay;

    @Value("${appeal.approvalCode}")
    private String approvalCode;

    @Value("${appeal.qualityApprover}")
    private Long qualityApprover;

    @Value("${appeal.approveOpen}")
    private Boolean approveOpen;

    private final List<String> taskNodeNameList = Lists.newArrayList("城市或大区审批", "服务部审批", "事业部审批");

    /***
     * 申诉判断逻辑
     * 1、是否处于申诉中、申诉成功
     * 2、(未申诉、申诉驳回、撤回的状态)是否超过3天有效期，是——直接返回过期，否——判断是否当天撤回,当天撤回的可以再次发起申诉
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse startAppeal(AppealRequest request) {
        if (null == request.getViolationId() || StringUtils.isBlank(request.getRemark())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        Violation violation = this.violationService.selectById(request.getViolationId());
        if (null == violation) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        if (violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue()) && StringUtils.isBlank(request.getSiteLeaderRemark())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        if (!violation.getStatus().equals(StatusEnum.VALID.getValue())) {
            return R.result(ResponseCodeEnum.VIOLATION_INVALID);
        }
        if (violation.getAppealStatus().equals(AppealStatusEnum.APPEAL_PASS.getValue())) {
            return R.result(ResponseCodeEnum.SUCCESS);
        } else if (violation.getAppealStatus().equals(AppealStatusEnum.APPEALING.getValue())) {
            return R.result(ResponseCodeEnum.HAS_APPEALING);
        } else {
            //判断有效期(5天内可以发起申诉)
            if (violation.getDeadlineTime().after(new Date())) {
                //可以发起申诉
                this.createAppeal(request, violation);
                return R.result(ResponseCodeEnum.SUCCESS);
            } else {
                //5天外,如果是当天撤回,可以发起申诉
                if (violation.getAppealStatus().equals(AppealStatusEnum.APPEAL_REVOKE.getValue())) {
                    List<Appeal> appealList = this.appealService.selectList(new EntityWrapper<Appeal>().
                            eq("violation_id", violation.getId()).
                            eq("status", ApproveStatusEnum.APPEAL_RECALL.getValue()).orderBy("create_time", false));
                    if (!CollectionUtils.isEmpty(appealList)) {
                        Date recallTime = appealList.get(0).getRecallTime();
                        if (recallTime != null && YearUtil.isToday(recallTime)) {
                            //可以发起申诉
                            this.createAppeal(request, violation);
                            return R.result(ResponseCodeEnum.SUCCESS);
                        }

                    }

                }
            }

            return R.result(ResponseCodeEnum.DEADLINE_APPEAL);
        }

    }

    /**
     * 创建申诉记录
     * 更新违规记录为申诉中，同时构建申诉内容并生成对应审批日志
     */
    private void createAppeal(AppealRequest request, Violation violation) {
        Date date = new Date();
        violation.setAppealStatus(AppealStatusEnum.APPEALING.getValue());
        violation.setModifyBy(request.getOperator());
        violation.setModifyTime(date);
        this.violationService.updateById(violation);

        //构建申诉记录
        Appeal appeal = new Appeal();
        appeal.setViolationId(request.getViolationId());
        appeal.setRemark(request.getRemark());
        if (StringUtils.isNotBlank(request.getSiteLeaderRemark())) {
            appeal.setSiteLeaderRemark(request.getSiteLeaderRemark());
        }
        appeal.setAppealTime(date);
        appeal.setModifyTime(date);
        appeal.setModifyBy(request.getOperator());
        appeal.setCreateTime(date);
        appeal.setCreateBy(request.getOperator());
        appeal.setStatus(ApproveStatusEnum.WAIT_REGION_APPROVE.getValue());
        appeal.setAnnex(this.saveAnnex(request.getImageAnnex(), request.getOtherAnnex()));
        this.appealService.insert(appeal);

        if (violation.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue())) {
            this.sendSiteAppealDealEmail(violation);
        }
        if (violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
            if (this.approveOpen) {
                SubmitInstanceRequest submitInstanceRequest = this.getSubmitInstanceRequest(appeal, violation, request);
                Long instanceId = workflowManager.submit(submitInstanceRequest);
                appeal.setInstanceId(instanceId);
                this.appealService.updateById(appeal);
            } else {
                this.sendAppealDealEmail(violation);
            }
        }
        //生成审批日志
        AppealApproveLog log = new AppealApproveLog();
        log.setAppealId(appeal.getId());
        log.setStatus(ApproveStatusEnum.WAIT_REGION_APPROVE.getValue());
        log.setViolationId(violation.getId());
        log.setCreateBy(request.getOperator());
        log.setCreateTime(date);
        log.setApproveType(ApproveTypeEnum.SITE_LEADER.getValue());
        log.setRemark(ApproveTypeEnum.SITE_LEADER.getText() + ":" + request.getOperator() + "发起申诉");
        this.appealApproveLogService.insert(log);
    }

    /**
     * 存储附件信息
     * 图片附件
     *
     * @param imageAnnex 其他附件
     * @param otherAnnex
     */
    private String saveAnnex(List<EventAttachment> imageAnnex, List<EventAttachment> otherAnnex) {

        List<EventAttachment> attachmentList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(imageAnnex)) {
            for (EventAttachment other : imageAnnex) {
                attachmentList.add(other);
            }
        }
        if (!CollectionUtils.isEmpty(otherAnnex)) {
            for (EventAttachment other : otherAnnex) {
                attachmentList.add(other);
            }
        }
        if (!CollectionUtils.isEmpty(attachmentList)) {
            return JSON.toJSONString(attachmentList);
        }
        return null;

    }

    @Override
    public BaseResponse<PageResult<AppealVO>> page(PageRequest page, AppealQueryRequest request) {
        Page<Appeal> appealPage = new Page<>(page.getPageNo(), page.getPageSize());
        List<AppealVO> voList = appealService.selectPageList(appealPage, request);
        if (CollectionUtils.isEmpty(voList)) {
            return R.result(ResponseCodeEnum.SUCCESS, new PageResult<>());
        }
        for (AppealVO vo : voList) {
            if (StringUtils.isNotBlank(vo.getPhone())) {
                vo.setPhone(ReplaceUtils.replacePhone(vo.getPhone()));
            }
            ApproveStatusEnum statusEnum = ApproveStatusEnum.getEnumByValue(vo.getStatus());
            switch (statusEnum) {
                case WAIT_REGION_APPROVE:
                    vo.setRegionAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                    vo.setQualityAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                    break;
                case APPROVE_SUCCESS:
                    vo.setRegionAppeal(ButtonEnum.PASS.getValue());
                    vo.setQualityAppeal(ButtonEnum.PASS.getValue());
                    break;
                case APPROVE_REJECT:
                    //大区通过，质质高驳回
                    //大区驳回，质质未审核
                    List<AppealApproveLogVO> logList = this.appealApproveLogService.getAppealApproveLogsByCondition(ApproveStatusEnum.WAIT_QUALITY_APPROVE.getValue(), vo.getId(), ApproveTypeEnum.REGION.getValue());
                    if (!CollectionUtils.isEmpty(logList)) {
                        vo.setRegionAppeal(ButtonEnum.PASS.getValue());
                        vo.setQualityAppeal(ButtonEnum.REJECT.getValue());
                    } else {
                        vo.setRegionAppeal(ButtonEnum.REJECT.getValue());
                        vo.setQualityAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                    }
                    break;
                case APPEAL_RECALL:
                    vo.setRegionAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                    vo.setQualityAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                    break;
                case WAIT_QUALITY_APPROVE:
                    vo.setRegionAppeal(ButtonEnum.PASS.getValue());
                    vo.setQualityAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                    break;
                case QUALITY_REJECT:
                    vo.setRegionAppeal(ButtonEnum.PASS.getValue());
                    vo.setQualityAppeal(ButtonEnum.REJECT.getValue());
                default:
            }

        }
        PageResult<AppealVO> pageResult = new PageResult<>();
        pageResult.setResults(voList);
        pageResult.setCount(appealPage.getTotal());
        pageResult.setPageNo(appealPage.getCurrent());
        pageResult.setPageSize(appealPage.getSize());
        pageResult.setTotalPage(appealPage.getPages());
        return R.result(ResponseCodeEnum.SUCCESS, pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<AppealDetailVO> appealDetail(AppealDetailRequest request) {
        if (null == request.getViolationId()) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        Violation violation = this.violationService.selectById(request.getViolationId());
        if (null == violation) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }

        AppealDetailVO vo = new AppealDetailVO();
        //存储违规信息
        vo.setViolationId(violation.getId());
        vo.setCode(violation.getCode());
        if (StringUtils.isNotBlank(violation.getOrderId())) {
            vo.setOrderId(violation.getOrderId());
        }
        if (violation.getServiceTime() != null) {
            vo.setServiceTime(violation.getServiceTime());
        }
        vo.setHappenTime(violation.getHappenTime());
        vo.setType(violation.getType());
        vo.setDetail(violation.getDetail());
        vo.setViolationStatus(violation.getStatus());

        List<AppealDetailVO.AppealInfo> appealInfoList = new ArrayList<>();
        List<Appeal> appealList = new ArrayList<>();
        if (request.getAppealId() != null) {
            Appeal appeal = this.appealService.selectById(request.getAppealId());
            if (appeal != null) {
                appealList.add(appeal);
            }
        } else {
            appealList = this.appealService.selectList(new EntityWrapper<Appeal>().
                    eq("violation_id", violation.getId()).ne("status", ApproveStatusEnum.APPEAL_RECALL.getValue()).orderBy("create_time", false));
        }

        if (!CollectionUtils.isEmpty(appealList)) {
            for (Appeal appeal : appealList) {
                AppealDetailVO.AppealInfo appealInfo = new AppealDetailVO.AppealInfo();
                appealInfo.setViolationId(appeal.getViolationId());
                appealInfo.setId(appeal.getId());
                appealInfo.setInstanceId(appeal.getInstanceId());
                appealInfo.setStatus(appeal.getStatus());

                appealInfo.setAppealBy(appeal.getCreateBy());
                appealInfo.setAppealReason(appeal.getRemark());
                appealInfo.setAppealTime(appeal.getAppealTime());

                if (StringUtils.isNotBlank(appeal.getAnnex())) {
                    appealInfo.setImageAnnex(AttachmentUtil.getImageAnnex(appeal.getAnnex()));
                    appealInfo.setOtherAnnex(AttachmentUtil.getOtherAnnex(appeal.getAnnex()));
                }
                if (StringUtils.isNotBlank(appeal.getSiteLeaderRemark())) {
                    appealInfo.setSiteLeaderAppealReason(appeal.getSiteLeaderRemark());
                }

                //查找大区审批日志
                List<AppealApproveLogVO> regionLogList = appealApproveLogService.getAppealApproveLogList(appeal.getId(), ApproveTypeEnum.REGION.getValue());
                if (!CollectionUtils.isEmpty(regionLogList)) {
                    AppealApproveLogVO regionLog = regionLogList.get(0);
                    appealInfo.setRegionApproveBy(regionLog.getCreateBy());
                    appealInfo.setRegionApproveReason(regionLog.getRemark());
                    appealInfo.setRegionApproveTime(regionLog.getCreateTime());
                    if (StringUtils.isNotBlank(regionLog.getUserComplaintAnalysis())) {
                        appealInfo.setUserComplaintAnalysis(regionLog.getUserComplaintAnalysis());
                    }
                    if (StringUtils.isNotBlank(regionLog.getSiteLeaderAppealAnalysis())) {
                        appealInfo.setSiteLeaderAppealAnalysis(regionLog.getSiteLeaderAppealAnalysis());
                    }
                    ApproveStatusEnum statusEnum = ApproveStatusEnum.getEnumByValue(regionLog.getStatus());
                    switch (statusEnum) {
                        case WAIT_QUALITY_APPROVE:
                            appealInfo.setRegionAppeal(ButtonEnum.PASS.getValue());
                            break;
                        case APPROVE_REJECT:
                            appealInfo.setRegionAppeal(ButtonEnum.REJECT.getValue());
                            break;
                        default:
                            appealInfo.setRegionAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                            break;
                    }
                } else {
                    //查询大区审批人员
                    SiteAndRegionVO siteAndRegionVO = siteAndRegionInfoService.getSiteAndRegionInfo(violation.getSiteId(), violation.getSiteLeaderId());
                    if (siteAndRegionVO != null && StringUtils.isNotBlank(siteAndRegionVO.getManager())) {
                        appealInfo.setRegionApproveBy(siteAndRegionVO.getManager());
                    }
                    appealInfo.setRegionAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                }

                //查找质质高审批日志
                List<AppealApproveLogVO> qualityLogList = appealApproveLogService.getAppealApproveLogList(appeal.getId(), ApproveTypeEnum.QUALITY.getValue());
                if (!CollectionUtils.isEmpty(qualityLogList)) {
                    AppealApproveLogVO qualityLog = qualityLogList.get(0);
                    appealInfo.setQualityApproveBy(qualityLog.getCreateBy());
                    appealInfo.setQualityApproveReason(qualityLog.getRemark());
                    appealInfo.setQualityApproveTime(qualityLog.getCreateTime());
                    if (StringUtils.isNotBlank(qualityLog.getImproveAdvice())) {
                        appealInfo.setImproveAdvice(qualityLog.getImproveAdvice());
                    }
                    ApproveStatusEnum statusEnum = ApproveStatusEnum.getEnumByValue(qualityLog.getStatus());
                    switch (statusEnum) {
                        case APPROVE_SUCCESS:
                            appealInfo.setQualityAppeal(ButtonEnum.PASS.getValue());
                            break;
                        //旧的数据判断(未区分大区和质质驳回)
                        case APPROVE_REJECT:
                            appealInfo.setQualityAppeal(ButtonEnum.REJECT.getValue());
                            break;
                        case QUALITY_REJECT:
                            appealInfo.setQualityAppeal(ButtonEnum.REJECT.getValue());
                            break;
                        default:
                            appealInfo.setQualityAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                            break;

                    }
                } else {
                    appealInfo.setQualityAppeal(ButtonEnum.WAIT_APPROVE.getValue());
                }

                appealInfoList.add(appealInfo);
            }
        }
        vo.setAppealInfoList(appealInfoList);
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<AppealDetailVO> appealApprovelDetail(AppealDetailRequest request) {
        if (null == request.getAppealId()) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        Appeal appeal = this.appealService.selectById(request.getAppealId());
        if (null == appeal) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        Violation violation = this.violationService.selectById(appeal.getViolationId());
        if (null == violation) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }

        AppealDetailVO vo = new AppealDetailVO();
        //存储违规信息
        vo.setViolationId(violation.getId());
        vo.setCode(violation.getCode());
        if (StringUtils.isNotBlank(violation.getOrderId())) {
            vo.setOrderId(violation.getOrderId());
        }
        if (violation.getServiceTime() != null) {
            vo.setServiceTime(violation.getServiceTime());
        }
        vo.setHappenTime(violation.getHappenTime());
        vo.setType(violation.getType());
        vo.setDetail(violation.getDetail());
        vo.setViolationStatus(violation.getStatus());

        List<AppealDetailVO.AppealInfo> appealInfoList = new ArrayList<>();
        List<Appeal> appealList = this.appealService.selectList(new EntityWrapper<Appeal>().eq("violation_id", violation.getId()).ne("status", ApproveStatusEnum.APPEAL_RECALL.getValue()).orderBy("create_time", false));
        if (CollectionUtils.isEmpty(appealList)) {
            appealList = Lists.newArrayList(appeal);
        }
        for (Appeal item : appealList) {
            AppealDetailVO.AppealInfo appealInfo = new AppealDetailVO.AppealInfo();
            appealInfo.setViolationId(item.getViolationId());
            appealInfo.setId(item.getId());
            appealInfo.setInstanceId(item.getInstanceId());
            appealInfo.setStatus(item.getStatus());

            appealInfo.setAppealBy(item.getCreateBy());
            appealInfo.setAppealReason(item.getRemark());
            appealInfo.setAppealTime(item.getAppealTime());

            if (StringUtils.isNotBlank(item.getAnnex())) {
                appealInfo.setImageAnnex(AttachmentUtil.getImageAnnex(item.getAnnex()));
                appealInfo.setOtherAnnex(AttachmentUtil.getOtherAnnex(item.getAnnex()));
            }
            if (StringUtils.isNotBlank(item.getSiteLeaderRemark())) {
                appealInfo.setSiteLeaderAppealReason(item.getSiteLeaderRemark());
            }
            appealInfoList.add(appealInfo);
        }
        List<AppealApproveLogVO> logList = appealApproveLogService.getAppealApproveLogList(appeal.getId(), null);
        vo.setLogList(logList);
        vo.setAppealInfoList(appealInfoList);
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse recallAppeal(AppealDetailRequest request) {
        if (null == request.getAppealId() || StringUtils.isBlank(request.getOperator())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }

        Appeal appeal = this.appealService.selectById(request.getAppealId());
        if (null == appeal) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }

        if (appeal.getStatus().equals(ApproveStatusEnum.APPEAL_RECALL.getValue())) {
            return R.result(ResponseCodeEnum.SUCCESS);
        } else if (!appeal.getStatus().equals(ApproveStatusEnum.WAIT_REGION_APPROVE.getValue())) {
            return R.result(ResponseCodeEnum.CAN_NOT_APPEAL_RECALL);

        }

        Violation violation = this.violationService.selectById(appeal.getViolationId());
        if (!violation.getStatus().equals(StatusEnum.VALID.getValue())) {
            return R.result(ResponseCodeEnum.VIOLATION_INVALID);
        }
        if (violation.getAppealStatus().equals(AppealStatusEnum.APPEALING.getValue())) {

            //申诉变成撤销
            Date date = new Date();
            appeal.setModifyTime(date);
            appeal.setModifyBy(request.getOperator());
            appeal.setStatus(ApproveStatusEnum.APPEAL_RECALL.getValue());
            appeal.setRecallTime(date);
            this.appealService.updateById(appeal);

            //违规记录变成撤回
            violation.setModifyTime(date);
            violation.setModifyBy(request.getOperator());
            violation.setAppealStatus(AppealStatusEnum.APPEAL_REVOKE.getValue());
            this.violationService.updateById(violation);

            //生成审批日志,且状态变成已撤销
            AppealApproveLog log = new AppealApproveLog();
            log.setAppealId(appeal.getId());
            log.setCreateTime(date);
            log.setViolationId(violation.getId());
            log.setCreateBy(request.getOperator());
            log.setStatus(ApproveStatusEnum.APPEAL_RECALL.getValue());
            log.setApproveType(ApproveTypeEnum.SITE_LEADER.getValue());
            log.setRemark(ApproveTypeEnum.SITE_LEADER.getText() + ":" + request.getOperator() + ";撤回申诉");
            this.appealApproveLogService.insert(log);
            return R.result(ResponseCodeEnum.SUCCESS);
        }

        return R.result(ResponseCodeEnum.ERROR_APPEAL_STATUS);

    }

    /***
     * 大区和质量审核通关过都用该接口
     * 列表中处于申诉的才审核
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse passAppeal(AppealDetailRequest request) {
        if (null == request.getAppealId() || StringUtils.isBlank(request.getOperator()) || StringUtils.isBlank(request.getRemark()) || null == request.getApproveType()) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }

        ApproveTypeEnum typeEnum = ApproveTypeEnum.getEnumByValue(request.getApproveType());
        if (null == typeEnum) {
            return R.result(ResponseCodeEnum.ERROR_APPROVE_TYPE);
        }

        Appeal appeal = this.appealService.selectById(request.getAppealId());
        if (null == appeal) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }

        //待大区或者质质高审批
        if (appeal.getStatus().equals(ApproveStatusEnum.WAIT_REGION_APPROVE.getValue())
                || appeal.getStatus().equals(ApproveStatusEnum.WAIT_QUALITY_APPROVE.getValue())) {

            if (appeal.getStatus().equals(ApproveStatusEnum.WAIT_REGION_APPROVE.getValue()) && ApproveTypeEnum.REGION.equals(typeEnum)) {
                appeal.setStatus(ApproveStatusEnum.WAIT_QUALITY_APPROVE.getValue());
            } else if (appeal.getStatus().equals(ApproveStatusEnum.WAIT_QUALITY_APPROVE.getValue()) && ApproveTypeEnum.QUALITY.equals(typeEnum)) {
                appeal.setStatus(ApproveStatusEnum.APPROVE_SUCCESS.getValue());
            }

            Violation violation = this.violationService.selectById(appeal.getViolationId());
            if (violation == null) {
                log.info("passAppeal()申诉对应的违规记录不存在,appealId={},violationId={}", appeal.getId(), appeal.getViolationId());
                return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
            }
            if (ApproveTypeEnum.REGION.equals(typeEnum) && violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                if (StringUtils.isBlank(request.getUserComplaintAnalysis()) || StringUtils.isBlank(request.getSiteLeaderAppealAnalysis())) {
                    return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
                }
            }
            if (ApproveTypeEnum.QUALITY.equals(typeEnum) && violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue()) && StringUtils.isBlank(request.getImproveAdvice())) {
                return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
            }

            if (!violation.getStatus().equals(StatusEnum.VALID.getValue())) {
                return R.result(ResponseCodeEnum.VIOLATION_INVALID);
            }
            if (violation.getAppealStatus().equals(AppealStatusEnum.APPEALING.getValue())) {

                //更新申诉信息
                Date date = new Date();
                appeal.setDealTime(date);
                appeal.setModifyTime(date);
                appeal.setModifyBy(request.getOperator());
                this.appealService.updateById(appeal);

                //更新违规申诉状态
                violation.setModifyTime(date);
                violation.setModifyBy(request.getOperator());
                //违规列表只有质质高审核通过了才变成通过状态
                if (appeal.getStatus().equals(ApproveStatusEnum.APPROVE_SUCCESS.getValue())
                        && violation.getStatus().equals(StatusEnum.VALID.getValue())) {
                    violation.setAppealStatus(AppealStatusEnum.APPEAL_PASS.getValue());
                    violation.setStatus(StatusEnum.APPEAL_INVALID.getValue());
                    violation.setReason(Constants.QUALITY_APPROVE);
                    //员工违规类型,分数回滚
                    if (violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                        this.licenseService.appealPass(violation);
                        //申诉结果提醒邮件
                        appealService.sendAppealResultEmail(violation, appeal.getAppealTime(), ApproveStatusEnum.APPROVE_SUCCESS.getText());
                    }
                    if (violation.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue())) {
                        //由于站点违规不扣员工分，所以审批通过后便失效掉复训记录
                        this.invalidRetrain(violation);
                        appealService.sendSiteAppealResultEmail(violation, appeal.getAppealTime(), ApproveStatusEnum.APPROVE_SUCCESS.getText());
                    }

                }
                this.violationService.updateById(violation);

                //生成审批日志,状态与申诉状态一致
                AppealApproveLog log = new AppealApproveLog();
                log.setAppealId(appeal.getId());
                log.setCreateTime(date);
                log.setCreateBy(request.getOperator());
                log.setViolationId(violation.getId());
                log.setStatus(appeal.getStatus());
                log.setApproveType(request.getApproveType());
                log.setRemark(request.getRemark());
                if (violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                    if (StringUtils.isNotBlank(request.getSiteLeaderAppealAnalysis()) && ApproveTypeEnum.REGION.equals(typeEnum)) {
                        log.setSiteLeaderAppealAnalysis(request.getSiteLeaderAppealAnalysis());
                    }
                    if (StringUtils.isNotBlank(request.getUserComplaintAnalysis()) && ApproveTypeEnum.REGION.equals(typeEnum)) {
                        log.setUserComplaintAnalysis(request.getUserComplaintAnalysis());
                    }
                    if (StringUtils.isNotBlank(request.getImproveAdvice()) && ApproveTypeEnum.QUALITY.equals(typeEnum)) {
                        log.setImproveAdvice(request.getImproveAdvice());
                    }
                }
                this.appealApproveLogService.insert(log);

                return R.result(ResponseCodeEnum.SUCCESS);

            }
        }

        return R.result(ResponseCodeEnum.ERROR_APPEAL_STATUS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse refuseAppeal(AppealDetailRequest request) {
        if (null == request.getAppealId() || StringUtils.isBlank(request.getOperator()) || StringUtils.isBlank(request.getRemark()) || null == request.getApproveType()) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }

        ApproveTypeEnum typeEnum = ApproveTypeEnum.getEnumByValue(request.getApproveType());
        if (null == typeEnum) {
            return R.result(ResponseCodeEnum.ERROR_APPROVE_TYPE);
        }

        Appeal appeal = this.appealService.selectById(request.getAppealId());
        if (null == appeal) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        //待大区或者质质高审批
        if (appeal.getStatus().equals(ApproveStatusEnum.WAIT_REGION_APPROVE.getValue())
                || appeal.getStatus().equals(ApproveStatusEnum.WAIT_QUALITY_APPROVE.getValue())) {

            Violation violation = this.violationService.selectById(appeal.getViolationId());
            if (violation == null) {
                log.error("申诉对应的违规记录不存在,appealId={},violationId={}", appeal.getId(), appeal.getViolationId());
                return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
            }
            if (!violation.getStatus().equals(StatusEnum.VALID.getValue())) {
                return R.result(ResponseCodeEnum.VIOLATION_INVALID);
            }

            if (ApproveTypeEnum.REGION.equals(typeEnum) && violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                if (StringUtils.isBlank(request.getUserComplaintAnalysis()) || StringUtils.isBlank(request.getSiteLeaderAppealAnalysis())) {
                    return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
                }
            }
            if (ApproveTypeEnum.QUALITY.equals(typeEnum) && violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue()) && StringUtils.isBlank(request.getImproveAdvice())) {
                return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
            }

            if (violation.getAppealStatus().equals(AppealStatusEnum.APPEALING.getValue())) {

                //更新申诉信息
                Date date = new Date();
                appeal.setDealTime(date);
                appeal.setModifyTime(date);
                appeal.setModifyBy(request.getOperator());
                if (appeal.getStatus().equals(ApproveStatusEnum.WAIT_REGION_APPROVE.getValue())) {
                    appeal.setStatus(ApproveStatusEnum.APPROVE_REJECT.getValue());
                } else if (appeal.getStatus().equals(ApproveStatusEnum.WAIT_QUALITY_APPROVE.getValue())) {
                    appeal.setStatus(ApproveStatusEnum.QUALITY_REJECT.getValue());
                }

                this.appealService.updateById(appeal);

                //更新违规申诉状态
                violation.setModifyTime(date);
                violation.setModifyBy(request.getOperator());
                violation.setAppealStatus(AppealStatusEnum.APPEAL_REJECT.getValue());
                this.violationService.updateById(violation);

                //申诉结果提醒邮件
                if (violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                    appealService.sendAppealResultEmail(violation, appeal.getAppealTime(), ApproveStatusEnum.getEnumByValue(appeal.getStatus()).getText());
                }
                if (violation.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue())) {
                    appealService.sendSiteAppealResultEmail(violation, appeal.getAppealTime(), ApproveStatusEnum.getEnumByValue(appeal.getStatus()).getText());
                }

                //生成审批日志,且状态变成驳回
                AppealApproveLog log = new AppealApproveLog();
                log.setAppealId(appeal.getId());
                log.setCreateTime(date);
                log.setCreateBy(request.getOperator());
                log.setViolationId(violation.getId());
                log.setStatus(appeal.getStatus());
                log.setApproveType(request.getApproveType());
                log.setRemark(request.getRemark());
                if (violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                    if (ApproveTypeEnum.REGION.equals(typeEnum) && StringUtils.isNotBlank(request.getSiteLeaderAppealAnalysis())) {
                        log.setSiteLeaderAppealAnalysis(request.getSiteLeaderAppealAnalysis());
                    }
                    if (ApproveTypeEnum.REGION.equals(typeEnum) && StringUtils.isNotBlank(request.getUserComplaintAnalysis())) {
                        log.setUserComplaintAnalysis(request.getUserComplaintAnalysis());
                    }
                    if (StringUtils.isNotBlank(request.getImproveAdvice()) && ApproveTypeEnum.QUALITY.equals(typeEnum)) {
                        log.setImproveAdvice(request.getImproveAdvice());
                    }
                }
                this.appealApproveLogService.insert(log);
                return R.result(ResponseCodeEnum.SUCCESS);
            }
        }

        return R.result(ResponseCodeEnum.ERROR_APPEAL_STATUS);
    }

    @Override
    public BaseResponse userInfo(Long orderId) {
        UserInfoVO infoVO = new UserInfoVO();
        QueryByServiceOrderIdRequest request = new QueryByServiceOrderIdRequest();
        request.setServiceOrderId(orderId);
        com.hk.simba.base.common.dto.response.BaseResponse<List<QueryByServiceOrderIdServiceWorkOrderData>> workOrderResponse = serviceWorkOrderApi.queryByServiceOrderId(request);
        if (workOrderResponse.isSuccess() && !CollectionUtils.isEmpty(workOrderResponse.getData())) {
            List<QueryByServiceOrderIdServiceWorkOrderData> dataList = workOrderResponse.getData();
            QueryByServiceOrderIdServiceWorkOrderData data = dataList.get(0);
            GetUserTypeRequest userTypeRequest = new GetUserTypeRequest();
            userTypeRequest.setUserId(data.getUserId());
            com.hk.simba.base.common.dto.response.BaseResponse<GetUserTypeUserTypeDto> userTypeResponse = userApi.getUserType(userTypeRequest);
            if (userTypeResponse.isSuccess() && userTypeResponse.getData() != null) {
                GetUserTypeUserTypeDto dto = userTypeResponse.getData();
                List<Integer> types = dto.getTypes();
                Integer userType = this.getUserType(types);
                infoVO.setType(userType);
            }
            infoVO.setUserId(data.getUserId());
            Date firstServiceTime = this.getFirstServiceTime(data.getUserId());
            infoVO.setFirstServiceTime(firstServiceTime);
        }
        return R.result(ResponseCodeEnum.SUCCESS, infoVO);
    }

    /**
     * 查询首次服务时间
     */
    private Date getFirstServiceTime(Long userId) {
        QueryContractItemBaseInfoPageRequest infoPageRequest = new QueryContractItemBaseInfoPageRequest();
        QueryContractItemBaseInfoPageUserInfoParam userRequest = new QueryContractItemBaseInfoPageUserInfoParam();
        userRequest.setUserId(userId);
        infoPageRequest.setSortByServiceTimeAsc(true);
        infoPageRequest.setUserInfoParam(userRequest);
        infoPageRequest.setPageNo(1);
        infoPageRequest.setPageSize(10);
        List<Integer> statusList = new ArrayList<>();
        //合约项状态：0=已生成，10=已分发，20=已接单，30=执行中，35=冻结中，40=已完成，100=已取消，110=已终止
        statusList.add(40);
        infoPageRequest.setStatusList(statusList);
        com.hk.simba.base.common.dto.response.BaseResponse<PageData<QueryContractItemBaseInfoPageContractItemBaseInfoDto>> baseResponse = contractItemApi.queryContractItemBaseInfoPage(infoPageRequest);
        if (baseResponse.isSuccess() && baseResponse.getData() != null) {
            PageData<QueryContractItemBaseInfoPageContractItemBaseInfoDto> pageData = baseResponse.getData();
            List<QueryContractItemBaseInfoPageContractItemBaseInfoDto> dtoList = pageData.getResults();
            if (!CollectionUtils.isEmpty(dtoList)) {
                QueryContractItemBaseInfoPageContractItemBaseInfoDto dto = dtoList.get(0);
                if (dto.getServiceTime() != null) {
                    return dto.getServiceTime();
                }
            }
        }
        return null;
    }

    /**
     * 申诉处理提醒邮件
     */
    private void sendAppealDealEmail(Violation violation) {
        Map<String, Object> params = Maps.newHashMap();
        SiteAndRegionVO vo = siteAndRegionInfoService.getSiteAndRegionInfo(violation.getSiteId(), violation.getSiteLeaderId());
        String serviceTime =
                Optional.ofNullable(violation.getServiceTime()).map(e -> DateUtils.formatDate(e, "yyyy年MM月dd日")).orElse("");
        params.put("siteName", violation.getSiteName());
        params.put("staffName", violation.getName());
        params.put("serviceTime", serviceTime);
//        params.put("orderId", violation.getOrderId());
        params.put("code", violation.getCode());
        params.put("limitDay", appealLimitDay);
        try {
            //当大区邮箱为空，直接发给质质高
            if (StringUtils.isEmpty(vo.getManagerEmail())) {
                mailService.sendTemplateMail(qualityEmail, MessageConstant.WITHOUT_LEADER + MessageConstant.APPEAL_DEAL_SUBJECT, MessageConstant.APPEAL_DEAL_TEMPLATE, params);
            } else {
                mailService.sendTemplateMail(vo.getManagerEmail(), MessageConstant.APPEAL_DEAL_SUBJECT, MessageConstant.APPEAL_DEAL_TEMPLATE, params);
            }
        } catch (TemplateException e) {
            log.error("[申诉处理]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[申诉处理]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[申诉处理]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[申诉处理]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    /**
     * 发送站点申诉处理提醒邮件
     */
    private void sendSiteAppealDealEmail(Violation violation) {
        Map<String, Object> params = Maps.newHashMap();
        SiteAndRegionVO vo = siteAndRegionInfoService.getSiteAndRegionInfo(violation.getSiteId(), violation.getSiteLeaderId());
        params.put("siteName", violation.getSiteName());
        params.put("code", violation.getCode());
        params.put("limitDay", appealLimitDay);
        try {
            //当大区邮箱为空，直接发给质质高
            if (StringUtils.isEmpty(vo.getManagerEmail())) {
                mailService.sendTemplateMail(qualityEmail, MessageConstant.WITHOUT_LEADER + MessageConstant.SITE_APPEAL_DEAL_SUBJECT, MessageConstant.SITE_APPEAL_DEAL_TEMPLATE, params);
            } else {
                mailService.sendTemplateMail(vo.getManagerEmail(), MessageConstant.SITE_APPEAL_DEAL_SUBJECT, MessageConstant.SITE_APPEAL_DEAL_TEMPLATE, params);
            }
        } catch (TemplateException e) {
            log.error("[站点申诉处理提醒]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[站点申诉处理提醒]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[站点申诉处理提醒]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[站点申诉处理提醒]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    /**
     * 中台返回的用户类型进行转化
     * 0-未付费用户，1-付费用户，2-非包年付费用户，3-包年付费用户，4-未付费用户（不含全退）5-付费用户（不含全退）
     * 6-非包年付费用户（不含全退），7-包年付费用户（不含全退），8-包年保洁用户
     */
    private Integer getUserType(List<Integer> types) {
        if (CollectionUtils.isEmpty(types)) {
            return UserType.OTHER.getValue();
        }
        if (types.contains(3) || types.contains(7) || types.contains(8)) {
            return UserType.YEAR_PAY_USER.getValue();
        }
        if (types.contains(2) || types.contains(6)) {
            return UserType.NO_YEAR_PAY_USER.getValue();
        }
        return UserType.OTHER.getValue();
    }

    private void invalidRetrain(Violation violation) {
        RetrainInvalidRequest invalidRequest = new RetrainInvalidRequest();
        invalidRequest.setOperator(violation.getModifyBy());
        invalidRequest.setReason(Constants.VIOLATION_INVALID);
        invalidRequest.setViolationId(violation.getId());
        this.retrainService.invalidRetrainByCondition(invalidRequest);
    }

    private SubmitInstanceRequest getSubmitInstanceRequest(Appeal appeal, Violation violation, AppealRequest appealRequest) {
        SubmitInstanceRequest request = new SubmitInstanceRequest();
        request.setTemplateId(approvalCode);
        request.setBizType(Constants.LICENSE_APPEAL);
        request.setBizId(appeal.getId().toString());
        request.setModule(Constants.LICENSE_APPEAL);
        request.setBizInfo(appeal.getViolationId().toString());
        String createBy = appeal.getCreateBy();
        Long submitterId = null;
        if (createBy.contains(Constants.UNDERLINE)) {
            submitterId = Long.parseLong(createBy.split(Constants.UNDERLINE)[1]);
            request.setFouracesId(submitterId);
        }
        request.setFields(this.getFieldList(appealRequest, violation));
        request.setProcessApproverRequestList(this.getApproverList(violation.getSiteId(), submitterId));
        return request;
    }

    private List<Field> getFieldList(AppealRequest appeal, Violation violation) {
        List<Field> fieldList = Lists.newArrayList();
        Field staffId = new TextField("员工ID", violation.getStaffId().toString());
        fieldList.add(staffId);
        Field staffName = new TextField("员工姓名", violation.getName());
        fieldList.add(staffName);
        Field city = new TextField("城市", violation.getCityName());
        fieldList.add(city);
        Field site = new TextField("站点", violation.getSiteName());
        fieldList.add(site);
        Field siteLeader = new TextField("站长", violation.getSiteLeaderName());
        fieldList.add(siteLeader);
        Field eventId = new TextField("事件ID", violation.getCode());
        fieldList.add(eventId);
        Field happenTime = new TextField("事件时间", DateUtils.formatDate(violation.getHappenTime(), null));
        fieldList.add(happenTime);
        Field type = new TextField("违规类型", violation.getType());
        fieldList.add(type);
        Field detail = new TextField("违规细则", violation.getDetail());
        fieldList.add(detail);
        if (!ObjectUtils.isEmpty(violation.getScore())) {
            Field score = new TextField("扣分", violation.getScore().toString());
            fieldList.add(score);
        }
        if (!ObjectUtils.isEmpty(violation.getTotalAmount())) {
            Field amount = new TextField("罚款", violation.getTotalAmount().toString());
            fieldList.add(amount);
        }
        Field desc = new TextAreaField("违规说明", violation.getDescription());
        fieldList.add(desc);
        Field remark = new TextAreaField("员工申诉理由", appeal.getRemark());
        fieldList.add(remark);
        Field siteLeaderRemark = new TextAreaField("站长申诉理由", appeal.getSiteLeaderRemark());
        fieldList.add(siteLeaderRemark);
        if (!CollectionUtils.isEmpty(appeal.getImageAnnex())) {
            List<String> imageUrls = appeal.getImageAnnex().stream().map(EventAttachment::getAttachmentUrl).collect(
                    Collectors.toList());
            Field pictureField = new PictureField("图片", imageUrls);
            fieldList.add(pictureField);
        }
        if (!CollectionUtils.isEmpty(appeal.getOtherAnnex())) {
            List<AttachmentField.Annex> annexList = Lists.newArrayList();
            appeal.getOtherAnnex().forEach(attachment -> {
                AttachmentField.Annex annex = new AttachmentField.Annex();
                annex.setFileName(attachment.getAttachmentUrl());
                annexList.add(annex);
            });
            Field attachmentField = new AttachmentField("其他附件", annexList);
            fieldList.add(attachmentField);
        }
        return fieldList;
    }

    private List<ProcessApproverRequest> getApproverList(Long siteId, Long submitter) {
        List<ProcessApproverRequest> processApproverRequestList = Lists.newArrayList();
        List<Long> list = this.getSiteApproverList(siteId, submitter);
        if (CollectionUtils.isEmpty(list)) {
            //审批人为空，都设置为自己
            list = Lists.newArrayList(submitter, submitter, submitter);
        }
        for (int i = 0; i < taskNodeNameList.size(); i++) {
            ProcessApproverRequest request = new ProcessApproverRequest();
            request.setUserIds(Lists.newArrayList(list.get(i)));
            request.setNodeName(taskNodeNameList.get(i));
            processApproverRequestList.add(request);
        }
        return processApproverRequestList;
    }

    private List<Long> getSiteApproverList(Long siteId, Long submitter) {
        List<Long> list = Lists.newArrayList();
        SiteDepartmentData departmentData = archiveManager.searchSiteDepartment(siteId);
        if (ObjectUtils.isEmpty(departmentData)) {
            return list;
        }
        String deptIdsFlag = departmentData.getDeptIdsFlag();
        if (StringUtils.isBlank(deptIdsFlag)) {
            return list;
        }
        String[] deptIds = deptIdsFlag.split(Constants.COLON);
        if (deptIds.length < 3) {
            return list;
        }
        //事业部
        Long businessDeptId = Long.parseLong(deptIds[1]);
        //大区、城市
        Long cityDeptId = departmentData.getParentId();
        DepartmentData businessDept = archiveManager.getDepartment(businessDeptId);
        Long businessApprover = this.getDirector(businessDept.getDirectorRosterIds());
        if (ObjectUtils.isEmpty(businessApprover)) {
            businessApprover = qualityApprover;
        }
        Long cityApprover = null;
        Long serviceApprover = null;
        //事业部-站点，如：福建事业部-金月子1站
        if (businessDeptId.equals(cityDeptId)) {
            serviceApprover = submitter;
            cityApprover = submitter;
        } else {
            DepartmentData cityDept = archiveManager.getDepartment(cityDeptId);
            cityApprover = this.getDirector(cityDept.getDirectorRosterIds());
            if (Constants.SERVICE_DEPT.equals(cityDept.getName())) {
                serviceApprover = cityApprover;
                cityApprover = submitter;
            } else {
                if (businessDeptId.equals(cityDept.getParentId())) {
                    serviceApprover = submitter;
                } else {
                    DepartmentData serviceDept = archiveManager.getDepartment(cityDept.getParentId());
                    serviceApprover = this.getDirector(serviceDept.getDirectorRosterIds());
                }
            }
            if (ObjectUtils.isEmpty(serviceApprover)) {
                serviceApprover = submitter;
            }
            if (ObjectUtils.isEmpty(cityApprover)) {
                cityApprover = submitter;
            }
        }
        //区域-城市审批人
        list.add(cityApprover);
        //服务部审批人
        list.add(serviceApprover);
        //事业部审批人
        list.add(businessApprover);
        return list;
    }

    private Long getDirector(String directorRosterIds) {
        if (StringUtils.isBlank(directorRosterIds)) {
            return null;
        }
        List<Long> ids = JsonUtils.fromJson(directorRosterIds, new TypeReference<List<Long>>() {
        });
        return ids.get(0);
    }

}
