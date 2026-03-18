package com.hk.simba.license.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.archive.open.RosterOpenService;
import com.hk.simba.archive.open.response.roster.RosterData;
import com.hk.simba.license.api.enums.AppealStatusEnum;
import com.hk.simba.license.api.enums.PayRecordStatusEnum;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.request.retrain.RetrainDetailRequest;
import com.hk.simba.license.api.request.violation.ViolationQueryRequest;
import com.hk.simba.license.api.vo.LicenseInfoVO;
import com.hk.simba.license.api.vo.RetrainVO;
import com.hk.simba.license.api.vo.SiteAndRegionVO;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.DeductTypeEnum;
import com.hk.simba.license.service.constant.enums.LicenseStatusEnum;
import com.hk.simba.license.service.constant.enums.RetrainStatusEnum;
import com.hk.simba.license.service.constant.enums.RetrainTypeEnum;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.entity.ViolationType;
import com.hk.simba.license.service.mq.dto.EventAttachment;
import com.hk.simba.license.service.mq.dto.MessageEntity;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.simba.license.service.service.MailService;
import com.hk.simba.license.service.service.RetrainService;
import com.hk.simba.license.service.service.SiteViolationService;
import com.hk.simba.license.service.service.ViolationMessageService;
import com.hk.simba.license.service.service.ViolationService;
import com.hk.simba.license.service.utils.RedisUtil;
import com.hk.simba.license.service.utils.YearUtil;
import com.hk.simba.workorder.open.SiteOpenService;
import com.hk.simba.workorder.open.request.GetByMiddleSiteIdRequest;
import com.hk.simba.workorder.open.respone.SiteDetailDto;
import com.hk.sisyphus.merope.core.quarkmdds.SiteApi;
import com.hk.sisyphus.merope.model.quarkmdds.site.GetSiteDetailRequest;
import com.hk.sisyphus.merope.model.quarkmdds.site.GetSiteDetailSiteDetailDto;
import com.hk.sisyphus.merope.model.quarkmdds.site.QuerySiteListByParamRequest;
import com.hk.sisyphus.merope.model.quarkmdds.site.QuerySiteListByParamSiteDto;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 违规信息 服务实现类
 * </p>
 *
 * @author lancw
 * @since 2021-07-19
 */
@Slf4j
@Service
public class SiteViolationServiceImpl implements SiteViolationService {
    @Autowired
    private SiteApi siteApi;
    @Reference
    private SiteOpenService siteOpenService;
    @Reference
    private RosterOpenService rosterOpenService;
    @Autowired
    private ViolationService violationService;
    @Value("${quality.email}")
    private String qualityEmail;
    @Autowired
    private MailService mailService;
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private RetrainService retrainService;
    @Autowired
    private ViolationMessageService violationMessageService;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 站点管理违规-人员培训不到位
     */
    @Value("${site.violation.inadequate.training.ids}")
    private String siteInadequateTrainingIds;
    /**
     * 违规申诉期限
     */
    @Value("${violation.deadline.amount}")
    private Integer deadlineAmount;

    /**
     * 站点违规-站点禁止行为
     */
    @Value("${site.violation.forbidden.result.ids}")
    private String siteForbiddenResultIds;

    @Override
    public Violation createSiteViolationInfo(MessageEntity entity, Long siteId, ViolationType vt) {
        GetSiteDetailRequest request = new GetSiteDetailRequest();
        request.setSiteId(siteId);
        BaseResponse<GetSiteDetailSiteDetailDto> response = this.siteApi.getSiteDetail(request);
        if (!response.isSuccess()) {
            log.info("【调用开发平台查询站点信息出错】，siteId={}, eMsg={}", siteId, response.getMessage());
            return null;
        }
        Violation vo = new Violation();
        GetSiteDetailSiteDetailDto dto = response.getData();
        vo.setCode(entity.getId());
        vo.setCityCode(dto.getCityCode());
        vo.setCityName(dto.getCityName());
        vo.setSiteId(siteId);
        vo.setSiteName(dto.getSiteName());
        vo.setHappenTime(entity.getCreateTime());
        vo.setScore(vt.getScore());
        vo.setTotalAmount(vt.getFee());
        vo.setType(vt.getType());
        vo.setDetail(vt.getDetail());
        vo.setCreateBy(Constants.SYS);
        vo.setCreateTime(new Date());
        vo.setViolationType(vt.getCode());
        vo.setDepartmentType(vt.getDepartmentType());
        vo.setEventType(vt.getEventType());
        vo.setViolationTypeId(vt.getId());
        if (StringUtils.isNotBlank(entity.getContent())) {
            vo.setDescription(entity.getContent());
        }
        if (StringUtils.isNotBlank(entity.getWorkOrderId())) {
            vo.setOrderId(entity.getWorkOrderId());
        }
        vo.setDeductType(DeductTypeEnum.SITE_DEDUCT.getValue());
        vo.setAnnex(getAnnex(entity.getEventAttachmentDtos()));
        vo.setPayStatus(PayRecordStatusEnum.PAY.getValue());
        //判断是否是服务禁忌,若是,待质质高审核通过才生效
        String resultIds = Constants.COMMA + siteForbiddenResultIds + Constants.COMMA;
        String code = Constants.COMMA + vt.getCode() + Constants.COMMA;
        if (resultIds.contains(code)) {
            vo.setStatus(StatusEnum.WAIT_VALID.getValue());
        } else {
            vo.setStatus(StatusEnum.VALID.getValue());
        }
        vo.setAppealStatus(AppealStatusEnum.NO_APPEAL.getValue());
        //设置申诉过期时间
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        Date deadLine = YearUtil.getNextDayZeroTime(now, deadlineAmount);
        vo.setDeadlineTime(deadLine);
        if (entity.getServiceTime() != null) {
            vo.setServiceTime(entity.getServiceTime());
        }
        //构建站点信息
        this.createSiteLeaderInfo(vo);
        return vo;
    }

    @Override
    public Violation createSiteLeaderInfo(Violation vo) {
        GetByMiddleSiteIdRequest request = new GetByMiddleSiteIdRequest();
        request.setMiddleSiteId(vo.getSiteId());
        com.hk.simba.base.common.dto.response.BaseResponse<SiteDetailDto> response = this.siteOpenService.getSiteDetailByMiddleSiteId(request);
        if (!response.isSuccess()) {
            log.info("【调用工单查询站点详情出错】，siteId={}, eMsg={}", vo.getSiteId(), response.getErrorCode().getMessage());
            return vo;
        }
        SiteDetailDto dto = response.getData();
        vo.setSiteLeaderId(dto.getLeaderId());
        vo.setSiteLeaderName(dto.getLeaderName());
        QuerySiteListByParamSiteDto siteDto = this.findSiteBySiteId(vo.getSiteId());
        if (siteDto != null && StringUtils.isNotBlank(siteDto.getProviderName())) {
            vo.setDepartment(siteDto.getProviderName());
        }
        com.hk.simba.base.common.dto.response.BaseResponse<RosterData> baseResponse = this.rosterOpenService.findRosterById(dto.getLeaderId());
        if (baseResponse.isSuccess() && baseResponse.getData() != null) {
            RosterData data = baseResponse.getData();
            vo.setSiteLeaderPhone(data.getPhone());
            vo.setSiteLeaderWorkNum(data.getWorkNum());
        }
        return vo;
    }

    private String getAnnex(List<EventAttachment> eas) {
        if (!CollectionUtils.isEmpty(eas)) {
            return JSONArray.toJSONString(eas);
        }
        return null;
    }

    private QuerySiteListByParamSiteDto findSiteBySiteId(Long siteId) {
        QuerySiteListByParamRequest siteRequest = new QuerySiteListByParamRequest();
        List<Long> siteIdList = new ArrayList<>();
        siteIdList.add(siteId);
        siteRequest.setSiteIdList(siteIdList);
        BaseResponse<List<QuerySiteListByParamSiteDto>> response = siteApi.querySiteListByParam(siteRequest);
        if (response.isSuccess() && !CollectionUtils.isEmpty(response.getData())) {
            List<QuerySiteListByParamSiteDto> list = response.getData();
            QuerySiteListByParamSiteDto dto = list.get(0);
            return dto;
        }
        return null;
    }

    @Override
    public SiteAndRegionVO getSiteLeaderInfo(Long siteLeaderId) {
        com.hk.simba.base.common.dto.response.BaseResponse<RosterData> baseResponse = this.rosterOpenService.findRosterById(siteLeaderId);
        if (baseResponse.isSuccess() && baseResponse.getData() != null) {
            SiteAndRegionVO vo = new SiteAndRegionVO();
            RosterData data = baseResponse.getData();
            vo.setSiteLeaderEmail(data.getEmail());
            vo.setSiteLeaderPhone(data.getPhone());
            return vo;
        }
        return null;
    }

    @Override
    public void invalidAndChangeInvalidType(String code, ViolationType newType) {
        ViolationQueryRequest request = new ViolationQueryRequest();
        request.setCode(code);
        request.setDeductType(DeductTypeEnum.SITE_DEDUCT.getValue());
        List<Violation> list = this.violationService.findListByCondition(request);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Violation> validList = new ArrayList<>();
        List<Violation> inValidList = new ArrayList<>();
        for (Violation vo : list) {
            if (vo.getStatus().equals(StatusEnum.WAIT_VALID.getValue()) || vo.getStatus().equals(StatusEnum.VALID.getValue())) {
                validList.add(vo);
            }
            if (vo.getStatus().equals(StatusEnum.HAND_INVALID.getValue()) || vo.getStatus().equals(StatusEnum.APPEAL_INVALID.getValue())) {
                inValidList.add(vo);
            }
        }
        if (!CollectionUtils.isEmpty(validList)) {
            this.invalidSiteViolation(validList);
        }
        if (!CollectionUtils.isEmpty(inValidList)) {
            this.changeInvalidType(newType, inValidList);
        }
    }

    /**
     * 失效站点违规
     */
    private void invalidSiteViolation(List<Violation> violationList) {
        for (Violation v : violationList) {
            if (v.getStatus().equals(StatusEnum.VALID.getValue()) || v.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
                v.setStatus(StatusEnum.INVALID.getValue());
                v.setModifyTime(new Date());
                v.setModifyBy(Constants.SYS);
                v.setReason(Constants.INVALID_BY_SYSTEM);
                this.violationService.updateById(v);
            }
        }
    }

    /**
     * 更改失效类型
     */
    private void changeInvalidType(ViolationType newType, List<Violation> violationList) {
        List<Violation> tempList = new ArrayList<>();
        Date date = new Date();
        for (Violation vo : violationList) {
            //违规类型未改变，则状态不变更
            if (newType != null) {
                if (StringUtils.isNotBlank(newType.getCode()) && newType.getCode().equals(vo.getViolationType())) {
                    continue;
                }
            }
            if (vo.getStatus().equals(StatusEnum.HAND_INVALID.getValue()) || vo.getStatus().equals(StatusEnum.APPEAL_INVALID.getValue())) {
                vo.setStatus(StatusEnum.INVALID.getValue());
                vo.setModifyTime(date);
                vo.setModifyBy(Constants.SYS);
                vo.setReason(Constants.INVALID_BY_SYSTEM);
                tempList.add(vo);
            }
        }
        if (!CollectionUtils.isEmpty(tempList)) {
            this.violationService.updateBatchById(tempList);
        }
    }

    /**
     * 给质质高-发送站点服务禁忌审批提醒邮件
     *
     * @param violation
     * @return
     */
    @Override
    public void sendSiteForbiddenEmail(Violation violation) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("code", violation.getCode());
        params.put("type", violation.getType());
        params.put("detail", violation.getDetail());
        try {
            mailService.sendTemplateMail(qualityEmail, MessageConstant.SITE_FORBIDDEN_SUBJECT, MessageConstant.SITE_FORBIDDEN_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[站点-服务禁忌违规审批提醒]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[站点-服务禁忌违规审批提醒]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[站点-服务禁忌违规审批提醒]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[站点-服务禁忌违规审批提醒]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    @Override
    public void createSiteRetrain(Violation violation) {
        LicenseInfoVO licenseInfoVO = this.licenseService.findLicenseByStaffId(violation.getStaffId());
        if (licenseInfoVO == null) {
            return;
        }
        RetrainDetailRequest request = new RetrainDetailRequest();
        request.setStaffId(violation.getStaffId());
        request.setViolationId(violation.getId());
        List<RetrainVO> voList = this.retrainService.findRetrainByCondition(request);
        if (licenseInfoVO.getStatus().equals(LicenseStatusEnum.EFFECTIVE.getCode()) && CollectionUtils.isEmpty(voList)) {
            RetrainVO vo = new RetrainVO();
            vo.setStaffId(violation.getStaffId());
            vo.setName(licenseInfoVO.getName());
            vo.setPhone(licenseInfoVO.getPhone());
            vo.setGender(licenseInfoVO.getGender());
            vo.setPositionType(licenseInfoVO.getPositionType());
            vo.setCityCode(violation.getCityCode());
            vo.setCityName(violation.getCityName());
            vo.setSiteId(violation.getSiteId());
            vo.setSiteName(violation.getSiteName());
            vo.setSiteLeaderId(violation.getSiteLeaderId());
            vo.setSiteLeaderName(violation.getSiteLeaderName());
            vo.setSiteLeaderPhone(violation.getSiteLeaderPhone());
            vo.setViolationId(violation.getId());
            vo.setStatus(RetrainStatusEnum.WAIT_RETRAIN.getValue());
            vo.setIdCard(licenseInfoVO.getIdCard());
            vo.setCreateBy(Constants.SYS);
            vo.setCreateTime(new Date());
            vo.setType(RetrainTypeEnum.TRAINING_RETRAIN.getValue());
            this.retrainService.saveRetrain(vo);
            //生成培训复训短信(由于站点违规不记录员工信息,所以需设置员工电话)
            violation.setPhone(licenseInfoVO.getPhone());
            this.violationMessageService.saveRetrainMessage(violation, RetrainTypeEnum.TRAINING_RETRAIN.getValue());
            String email = this.licenseService.getSiteLeaderEmail(violation.getSiteLeaderId());
            if (StringUtils.isNotBlank(email)) {
                this.licenseService.sendRetrainNotifyEmail(licenseInfoVO.getName(), email, RetrainTypeEnum.TRAINING_RETRAIN.getText());
            }

            //给培训师发复训邮件
            this.licenseService.sendRetrainNotifyEmailToTrainTeacher(licenseInfoVO.getName(), violation.getSiteName(), RetrainTypeEnum.TRAINING_RETRAIN.getText(), violation.getSiteId());
        }
    }

    @Override
    public Boolean isSiteRetrainRule(Violation violation) {
        //违规为[站点管理违规-人员培训不到位],则创建站点违规对应的复训(站点违规不一定有员工id)
        if (StringUtils.isBlank(siteInadequateTrainingIds)) {
            return false;
        }
        String violationCodes = Constants.COMMA + siteInadequateTrainingIds + Constants.COMMA;
        if (violation.getStatus().equals(StatusEnum.VALID.getValue()) && violation.getStaffId() != null &&
                violationCodes.contains(violation.getViolationType())) {
            return true;
        }
        return false;
    }

    @Override
    public SiteAndRegionVO getTrainTeacherInfo(Long siteId) {
        String key = Constants.SITE_TRAINING_TEACHER_INFO + siteId;
        if (redisUtil.get(key) != null) {
            SiteAndRegionVO vo = (SiteAndRegionVO) redisUtil.get(key);
            return vo;
        }
        SiteAndRegionVO vo = new SiteAndRegionVO();
        GetByMiddleSiteIdRequest request = new GetByMiddleSiteIdRequest();
        request.setMiddleSiteId(siteId);
        com.hk.simba.base.common.dto.response.BaseResponse<SiteDetailDto> response = this.siteOpenService.getSiteDetailByMiddleSiteId(request);
        if (!response.isSuccess()) {
            return vo;
        }
        SiteDetailDto dto = response.getData();
        if (dto.getTrainingTeacherId() != null) {
            vo.setSiteId(siteId);
            vo.setTrainingTeacherId(dto.getTrainingTeacherId());
            com.hk.simba.base.common.dto.response.BaseResponse<RosterData> baseResponse = this.rosterOpenService.findRosterById(dto.getTrainingTeacherId());
            if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                RosterData data = baseResponse.getData();
                vo.setTrainingTeacherPhone(data.getPhone());
                vo.setTrainingTeacherEmail(data.getEmail());
                vo.setTrainingTeacherName(data.getName());
            }
            redisUtil.set(key, vo, 3600);
        }
        return vo;
    }
}
