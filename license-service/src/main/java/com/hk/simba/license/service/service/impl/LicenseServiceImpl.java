package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hk.base.util.DateUtils;
import com.hk.simba.archive.open.RosterOpenService;
import com.hk.simba.archive.open.response.roster.RosterData;
import com.hk.simba.base.common.dto.Operator;
import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.license.api.enums.AppealStatusEnum;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.request.license.LicenseInfoRequest;
import com.hk.simba.license.api.request.license.LicenseQueryRequest;
import com.hk.simba.license.api.request.retrain.RetrainDetailRequest;
import com.hk.simba.license.api.request.retrain.RetrainInvalidRequest;
import com.hk.simba.license.api.vo.LicenseInfoVO;
import com.hk.simba.license.api.vo.LicenseVO;
import com.hk.simba.license.api.vo.RetrainVO;
import com.hk.simba.license.api.vo.SiteAndRegionVO;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.BlacklistTypeEnum;
import com.hk.simba.license.service.constant.enums.IsBlacklistEnum;
import com.hk.simba.license.service.constant.enums.LicenseStatusEnum;
import com.hk.simba.license.service.constant.enums.PositionTypeEnum;
import com.hk.simba.license.service.constant.enums.RemainScoreEnum;
import com.hk.simba.license.service.constant.enums.RetrainStatusEnum;
import com.hk.simba.license.service.constant.enums.RetrainTypeEnum;
import com.hk.simba.license.service.constant.enums.ValidEnum;
import com.hk.simba.license.service.entity.Blacklist;
import com.hk.simba.license.service.entity.BlacklistLog;
import com.hk.simba.license.service.entity.License;
import com.hk.simba.license.service.entity.LicenseExamGroup;
import com.hk.simba.license.service.entity.LicenseRevokeLog;
import com.hk.simba.license.service.entity.LicenseScoreLog;
import com.hk.simba.license.service.entity.LicenseStopOrderLog;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.manager.StopOrderMpManager;
import com.hk.simba.license.service.mapper.LicenseMapper;
import com.hk.simba.license.service.service.BlacklistLogService;
import com.hk.simba.license.service.service.BlacklistService;
import com.hk.simba.license.service.service.LicenseExamGroupService;
import com.hk.simba.license.service.service.LicenseRevokeLogService;
import com.hk.simba.license.service.service.LicenseScoreLogService;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.simba.license.service.service.LicenseStopOrderLogService;
import com.hk.simba.license.service.service.MailService;
import com.hk.simba.license.service.service.RetrainService;
import com.hk.simba.license.service.service.SiteViolationService;
import com.hk.simba.license.service.service.ViolationMessageService;
import com.hk.simba.license.service.service.ViolationService;
import com.hk.simba.license.service.utils.BeanCopyUtil;
import com.hk.simba.staff.open.enums.WorkingStateEnum;
import com.hk.sisyphus.merope.core.staff.StaffApi;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffSensitiveInfoByStaffIdRequest;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO;
import com.hk.sisyphus.merope.model.staff.stoporder.ApplyForStopOrderApplyForStopOrderDTO;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author cyh
 * @date 2020/4/26/14:19
 * 执照服务实现
 */
@Service
@Slf4j
public class LicenseServiceImpl extends ServiceImpl<LicenseMapper, License> implements LicenseService {

    @Autowired
    private LicenseRevokeLogService licenseRevokeLogService;

    @Autowired
    private LicenseScoreLogService licenseScoreLogService;

    @Autowired
    private ViolationService violationService;

    @Autowired
    private RetrainService retrainService;

    @Autowired
    private MailService mailService;

    @Autowired
    private ViolationMessageService violationMessageService;

    @Autowired
    private SiteViolationService siteViolationService;

    @Autowired
    private StopOrderMpManager stopOrderMpManager;

    @Autowired
    private LicenseStopOrderLogService licenseStopOrderLogService;

    /**
     * 剩余分数
     */
    @Value("${remain.score.one}")
    private Integer remainScoreOne;

    /**
     * 剩余分数
     */
    @Value("${remain.score.two}")
    private Integer remainScoreTwo;

    /**
     * 站长须几天内通知员工
     */
    @Value("${notify.limit.day}")
    private Integer limitDay;

    /**
     * 质质高邮箱
     */
    @Value("${quality.email}")
    private String qualityEmail;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private BlacklistLogService blacklistLogService;

    @Reference
    private RosterOpenService rosterOpenService;

    @Autowired
    private LicenseExamGroupService licenseExamGroupService;

    @Autowired
    private StaffApi staffApi;

    @Override
    public List<LicenseVO> getPageList(Page<License> page, LicenseQueryRequest request) {
        List<LicenseVO> voList = this.baseMapper.getPageList(page, request);
        for (LicenseVO licenseVO : voList) {
            EntityWrapper<LicenseRevokeLog> wrapper = new EntityWrapper<>();
            wrapper.eq("license_id", licenseVO.getId());
            wrapper.ge("create_time", licenseVO.getEffectiveTime());
            wrapper.orderBy("create_time", false);
            wrapper.last("limit 1");
            LicenseRevokeLog licenseRevokeLog = licenseRevokeLogService.selectOne(wrapper);
            if (licenseRevokeLog != null) {
                licenseVO.setRevokeTime(licenseRevokeLog.getCreateTime());
            }
        }

        return voList;
    }

    @Override
    public License doViolation(Violation violation) {
        License license = new License();
        license.setStaffId(violation.getStaffId());
        license.setIdCard(violation.getIdCard());
        EntityWrapper<License> wrapper = new EntityWrapper();
        wrapper.setEntity(license);
        license = this.selectOne(wrapper);
        if (license == null) {
            log.info("执照不存在violation_id={},search by staffId={} and idCard", violation.getId(), violation.getStaffId());
        } else {
            license.setPositionType(PositionTypeEnum.getType(violation.getPosition()));
            license.setGender(violation.getGender());
            license.setIdCard(violation.getIdCard());
            license.setCityCode(violation.getCityCode());
            license.setCityName(violation.getCityName());
            license.setPhone(violation.getPhone());
            license.setSiteName(violation.getSiteName());
            license.setName(violation.getName());
            license.setSiteId(violation.getSiteId());
            license.setSiteName(violation.getSiteName());
            Integer score = license.getRemainScore();
            score = score - violation.getScore();
            license.setRemainScore(score);
            //分数小于等于0，吊销执照
            if (score <= 0) {
                if (license.getStatus().equals(LicenseStatusEnum.EFFECTIVE.getCode())) {
                    license.setStatus(LicenseStatusEnum.REVOKE.getCode());
                }
                license.setReason(Constants.ZERO_DESCRIPTION);
                license.setBlack(IsBlacklistEnum.YES.getValue());
                LicenseRevokeLog licenseRevokeLog = new LicenseRevokeLog();
                licenseRevokeLog.setLicenseId(license.getId());
                licenseRevokeLog.setStaffId(license.getStaffId());
                licenseRevokeLog.setReason(Constants.ZERO_DESCRIPTION);
                licenseRevokeLog.setCreateBy(Constants.SYS);
                licenseRevokeLog.setCreateTime(new Date());
                licenseRevokeLogService.insert(licenseRevokeLog);
                this.sendLicenseRevokeEmail(license.getName(), license.getStaffId());

                //创建黑名单
                Blacklist blacklist = new Blacklist();
                blacklist.setStaffId(license.getStaffId());
                blacklist.setName(license.getName());
                blacklist.setGender(license.getGender());
                blacklist.setPhone(license.getPhone());
                blacklist.setRemark(Constants.ADD_BLACK_BY_SYSTEM);
                blacklist.setCreateTime(new Date());
                blacklist.setCreateBy(Constants.SYS);
                blacklist.setIdCard(license.getIdCard());
                blacklist.setType(BlacklistTypeEnum.REVOKE.getValue());
                blacklist.setReason("服务品质累计扣满24分");
                this.blacklistService.createBlack(blacklist);

            }
            license.setModifyBy(Constants.SYS);
            license.setModifyTime(new Date());
            this.updateById(license);
            // 分数流水
            LicenseScoreLog licenseScoreLog = new LicenseScoreLog();
            licenseScoreLog.setLicenseId(license.getId());
            licenseScoreLog.setStaffId(license.getStaffId());
            licenseScoreLog.setViolationId(violation.getId());
            licenseScoreLog.setDeductScore(-violation.getScore());
            licenseScoreLog.setReason(Constants.VIOLATION);
            licenseScoreLog.setCreateBy(Constants.SYS);
            licenseScoreLog.setCreateTime(new Date());
            licenseScoreLogService.insert(licenseScoreLog);

            //生成复训记录
            if (license.getStatus().equals(LicenseStatusEnum.EFFECTIVE.getCode())) {
                this.createRetrain(license, violation);
            }
            //若是吊销的，则同一计分周期的复训记录失效
            if (license.getStatus().equals(LicenseStatusEnum.REVOKE.getCode())) {
                this.invalidRetrain(license.getStaffId(), Constants.LICENSE_REVOKE, Constants.SYS);
            }
        }
        return license;
    }

    @Override
    public License staffStatusChange(License license, String status) {
        Long staffId = license.getStaffId();
        EntityWrapper<License> wrapper = new EntityWrapper();
        wrapper.setEntity(license);
        license = this.selectOne(wrapper);
        if (license != null) {
            this.updateLicenseInfo(license);
            if (String.valueOf(WorkingStateEnum.QUIT.getValue()).equals(status) ||
                    String.valueOf(WorkingStateEnum.LOST.getValue()).equals(status)) {
                if (LicenseStatusEnum.REVOKE.getCode().equals(license.getStatus())) {
                    if (Constants.ZERO_DESCRIPTION.equals(license.getReason())) {
                        license.setReason(Constants.QUIT);
                    }
                } else {
                    license.setReason(Constants.QUIT);
                }
                license.setStatus(LicenseStatusEnum.REVOKE.getCode());

                //吊销记录
                LicenseRevokeLog revokeLog = new LicenseRevokeLog();
                revokeLog.setLicenseId(license.getId());
                revokeLog.setStaffId(license.getStaffId());
                revokeLog.setCreateBy(Constants.SYS);
                revokeLog.setCreateTime(new Date());
                revokeLog.setReason(Constants.QUIT);
                licenseRevokeLogService.insert(revokeLog);
                //失效复训记录
                this.invalidRetrain(license.getStaffId(), Constants.LICENSE_REVOKE, Constants.SYS);
            }
            license.setModifyBy(Constants.SYS);
            license.setModifyTime(new Date());
            this.updateById(license);
        }
        //离职和流失失效掉考试记录
        if (String.valueOf(WorkingStateEnum.QUIT.getValue()).equals(status) ||
                String.valueOf(WorkingStateEnum.LOST.getValue()).equals(status)) {
            //失效考试分组记录
            LicenseExamGroup group = this.licenseExamGroupService.findValidGroupByStaffId(staffId);
            if (group != null) {
                group.setRemark(Constants.STAFF_LEAVE_INVALID_EXAM_GROUP);
                this.licenseExamGroupService.invalidLicenseExamGroup(group);
            }
        }
        return license;
    }

    @Override
    public License updateLicenseInfo(License license) {
        this.setCleanerInfo(license);
        return license;
    }

    @Override
    public void appealPass(Violation violation) {
        this.rollbackScore(violation, Constants.APPEAL_PASS);
    }

    private void setCleanerInfo(License license) {
        FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO dto = this.findStaffInfoByStaffId(license.getStaffId());
        if (dto == null) {
            return;
        }
        license.setPositionType(PositionTypeEnum.getType(dto.getCareerName()));
        license.setGender(dto.getSex());
        license.setIdCard(dto.getIdCard());
        license.setCityCode(dto.getCityCode());
        license.setCityName(dto.getCityName());
        license.setPhone(dto.getPhone());
        license.setName(dto.getName());
        license.setSiteId(dto.getSiteId());
        license.setSiteName(dto.getSiteName());
        if (license.getPositionType().equals(PositionTypeEnum.COOKER.getType())) {
            license.setCooker(true);
        } else {
            license.setCooker(false);
        }
    }

    private FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO findStaffInfoByStaffId(Long staffId) {
        FindStaffSensitiveInfoByStaffIdRequest f = new FindStaffSensitiveInfoByStaffIdRequest();
        f.setStaffId(staffId);
        BaseResponse<FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO> baseResponse = staffApi.findStaffSensitiveInfoByStaffId(f);
        if (!baseResponse.isSuccess() || baseResponse.getData() == null) {
            log.warn("查无此员工信息，staffId={}", staffId);
            return null;
        }
        FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO dto = baseResponse.getData();
        return dto;
    }

    @Override
    public void rollbackScore(Violation violation, String reason) {
        License license = new License();
        license.setStaffId(violation.getStaffId());
        EntityWrapper<License> wrapper = new EntityWrapper();
        wrapper.setEntity(license);
        license = this.selectOne(wrapper);

        if (license != null) {
            Date nowTime = new Date();
            Date violatioTime = violation.getCreateTime();
            // 违规时间是在上一周期不计入分数，在本周期内才叠加分数
            Date lastExpireTime = DateUtils.add(license.getExpireTime(), Calendar.YEAR, -1);

            Integer remainScore = license.getRemainScore();
            Integer violationScore = violation.getScore();
            Integer appealScore = remainScore + violationScore;
            appealScore = appealScore > Constants.SCORE ? Constants.SCORE : appealScore;

            //执照因为分数小于等于0而吊销
            if (LicenseStatusEnum.REVOKE.getCode().equals(license.getStatus()) && Constants.ZERO_DESCRIPTION.equals(license.getReason())) {
                //因为违规吊销新计分周期未进行执照分数和状态重置，在申诉成功时重置
                if (appealScore > 0) {
                    //判断员工状态
                    FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO dto = this.findStaffInfoByStaffId(license.getStaffId());
                    if (dto == null || !dto.getWorkingState().equals(WorkingStateEnum.QUIT.getValue())) {
                        license.setStatus(LicenseStatusEnum.EFFECTIVE.getCode());
                    }
                    if (nowTime.compareTo(license.getExpireTime()) > 0) {

                        //取在新计分周期申诉中的违规记录
                        Integer score = 0;
                        Violation violationExit = new Violation();
                        violationExit.setStaffId(license.getStaffId());
                        violationExit.setIdCard(license.getIdCard());
                        violationExit.setStatus(StatusEnum.VALID.getValue());
                        violationExit.setAppealStatus(AppealStatusEnum.APPEALING.getValue());
                        Page<Violation> page = new Page<>(0, 20);
                        Wrapper<Violation> wrapperViolation = new EntityWrapper<>(violationExit);
                        page = violationService.selectPage(page, wrapperViolation);
                        if (!CollectionUtils.isEmpty(page.getRecords())) {
                            for (Violation vi : page.getRecords()) {
                                if (vi.getCreateTime().compareTo(license.getExpireTime()) > 0
                                    && !vi.getId().equals(violation.getId())) {
                                    score += vi.getScore();
                                }
                            }
                        }
                        license.setRemainScore(Constants.SCORE - score);//重置分数时扣去新违规的分数
                        license.setExpireTime(DateUtils.add(license.getExpireTime(), Calendar.YEAR, 1));

                        // 分数流水
                        LicenseScoreLog licenseScoreLog = new LicenseScoreLog();
                        licenseScoreLog.setLicenseId(license.getId());
                        licenseScoreLog.setStaffId(license.getStaffId());
                        licenseScoreLog.setDeductScore(Constants.SCORE);//若是申诉成功分数累加
                        licenseScoreLog.setReason(Constants.SCORE_RESET);
                        licenseScoreLog.setCreateBy(Constants.SYS);
                        licenseScoreLog.setCreateTime(new Date());
                        licenseScoreLogService.insert(licenseScoreLog);
                    } else {
                        license.setRemainScore(appealScore);
                    }
                } else {
                    license.setRemainScore(appealScore);
                }
            } else {
                //生效状态-上一计分周期违规扣分不累加回去
                if (violatioTime.compareTo(lastExpireTime) >= 0) {
                    license.setRemainScore(appealScore);
                }
            }

            license.setModifyTime(new Date());
            license.setModifyBy(Constants.SYS);
            this.updateById(license);

            // 分数流水
            LicenseScoreLog licenseScoreLog = new LicenseScoreLog();
            licenseScoreLog.setLicenseId(license.getId());
            licenseScoreLog.setStaffId(license.getStaffId());
            licenseScoreLog.setViolationId(violation.getId());
            licenseScoreLog.setDeductScore(violation.getScore());//申诉成功分数累加
            licenseScoreLog.setReason(reason);
            licenseScoreLog.setCreateBy(Constants.SYS);
            licenseScoreLog.setCreateTime(new Date());
            licenseScoreLogService.insert(licenseScoreLog);

            //分数回滚，先按剩余分数判断，是否满足生成复训条件，再查询对应的复训的剩余分数类型是否存在;不存在新增,同时失效掉旧的复训记录
            this.createOrInvalidRetrain(license, violation);

            //判断剩余分数是否大于0，且吊销原因为：剩余分数为0，查看是否有黑名单,有的话移除
            if (license.getRemainScore() > 0 && StringUtils.isNotBlank(license.getReason()) && Constants.ZERO_DESCRIPTION.equals(license.getReason())) {
                if (license.getBlack() != null && license.getBlack().equals(IsBlacklistEnum.YES.getValue())) {
                    license.setBlack(IsBlacklistEnum.NO.getValue());
                    license.setModifyTime(new Date());
                    license.setModifyBy(Constants.SYS);
                    this.updateById(license);
                }
                this.removeBlack(license.getStaffId());
            }
        }
    }

    @Override
    public String getSiteLeaderEmail(Long siteLeaderId) {
        if (siteLeaderId == null) {
            log.warn("站长id为空");
            return null;
        }
        BaseResponse<RosterData> baseResponse = this.rosterOpenService.findRosterById(siteLeaderId);
        if (baseResponse.isSuccess() && baseResponse.getData() != null) {
            RosterData data = baseResponse.getData();
            if (StringUtils.isNotBlank(data.getEmail())) {
                return data.getEmail();
            }
        }
        return null;
    }

    /**
     * 构建复训信息
     */
    private RetrainVO buildRetrainInfo(License license, Violation violation, Integer remainScoreType, Integer type) {
        RetrainVO vo = new RetrainVO();
        vo.setStaffId(license.getStaffId());
        vo.setName(license.getName());
        vo.setPhone(license.getPhone());
        vo.setGender(license.getGender());
        vo.setPositionType(license.getPositionType());
        vo.setCityCode(license.getCityCode());
        vo.setCityName(license.getCityName());
        vo.setSiteId(license.getSiteId());
        vo.setSiteName(license.getSiteName());
        vo.setSiteLeaderId(violation.getSiteLeaderId());
        vo.setSiteLeaderName(violation.getSiteLeaderName());
        vo.setSiteLeaderPhone(violation.getSiteLeaderPhone());
        vo.setViolationId(violation.getId());
        vo.setStatus(RetrainStatusEnum.WAIT_RETRAIN.getValue());
        vo.setIdCard(license.getIdCard());
        vo.setCreateBy(Constants.SYS);
        vo.setCreateTime(new Date());
        vo.setType(type);
        if (remainScoreType != null) {
            vo.setRemainScoreType(remainScoreType);
        }
        return vo;
    }

    /**
     * 生成复训(目前考虑同一个计分周期,跨周期未考虑)
     * 执照复训> 违规复训
     */
    private void createRetrain(License license, Violation violation) {
        //todo 计分周期记得考虑
        Integer remainScore = license.getRemainScore();
        Date startTime = DateUtils.add(license.getExpireTime(), Calendar.YEAR, -1);
        Date endTime = license.getExpireTime();
        RetrainDetailRequest request = new RetrainDetailRequest();
        request.setStaffId(license.getStaffId());
        request.setStartCreateTime(startTime);
        request.setEndCreateTime(endTime);
        //剩余分数介于(0,8]
        if (remainScore > 0 && remainScore <= remainScoreOne) {
            request.setRemainScoreType(RemainScoreEnum.SCORE_REGION_ONE.getValue());
            List<RetrainVO> voList = this.retrainService.findRetrainByCondition(request);
            if (CollectionUtils.isEmpty(voList)) {
                //生成执照复训
                this.retrainService.saveRetrain(this.buildRetrainInfo(license, violation, RemainScoreEnum.SCORE_REGION_ONE.getValue(), RetrainTypeEnum.LICENSE_RETRAIN.getValue()));
                this.sendMsgAndEmail(violation, RetrainTypeEnum.LICENSE_RETRAIN.getValue());
            } else {
                //生成违规复训
                RetrainVO vo = this.buildRetrainInfo(license, violation, null, RetrainTypeEnum.VIOLATION_RETRAIN.getValue());
                Boolean saveRetrain = this.retrainService.saveViolationRetrain(vo);
                if (saveRetrain) {
                    this.sendMsgAndEmail(violation, RetrainTypeEnum.VIOLATION_RETRAIN.getValue());
                }
            }
            return;
        }
        //剩余分数介于(8,16]
        if (remainScore > remainScoreOne && remainScore <= remainScoreTwo) {
            request.setRemainScoreType(RemainScoreEnum.SCORE_REGION_TWO.getValue());
            List<RetrainVO> voList = this.retrainService.findRetrainByCondition(request);
            if (CollectionUtils.isEmpty(voList)) {
                //生成执照复训
                this.retrainService.saveRetrain(this.buildRetrainInfo(license, violation, RemainScoreEnum.SCORE_REGION_TWO.getValue(), RetrainTypeEnum.LICENSE_RETRAIN.getValue()));
                this.sendMsgAndEmail(violation, RetrainTypeEnum.LICENSE_RETRAIN.getValue());
            } else {
                //生成违规复训
                RetrainVO vo = this.buildRetrainInfo(license, violation, null, RetrainTypeEnum.VIOLATION_RETRAIN.getValue());
                Boolean saveRetrain = this.retrainService.saveViolationRetrain(vo);
                if (saveRetrain) {
                    this.sendMsgAndEmail(violation, RetrainTypeEnum.VIOLATION_RETRAIN.getValue());
                }
            }
            return;
        }
        //剩余分数介于(16,24]或小于0
        if ((remainScore > remainScoreTwo && remainScore <= Constants.SCORE) || remainScore <= 0) {
            RetrainVO vo = this.buildRetrainInfo(license, violation, null, RetrainTypeEnum.VIOLATION_RETRAIN.getValue());
            Boolean saveRetrain = this.retrainService.saveViolationRetrain(vo);
            if (saveRetrain) {
                this.sendMsgAndEmail(violation, RetrainTypeEnum.VIOLATION_RETRAIN.getValue());
            }
        }
    }

    /**
     * 失效复训记录(执照吊销情况)
     */
    private void invalidRetrain(Long staffId, String reason, String operator) {
        RetrainInvalidRequest invalidRequest = new RetrainInvalidRequest();
        invalidRequest.setStaffId(staffId);
        invalidRequest.setOperator(operator);
        invalidRequest.setReason(reason);
        this.retrainService.invalidRetrainByCondition(invalidRequest);
    }

    /***
     * 创建或失效复训记录(分数回滚的时候使用)
     * 分数回滚，先按剩余分数判断，是否满足生成复训条件，再查询对应的复训的剩余分数类型是否存在;不存在则新增,同时失效掉旧的复训记录
     *
     */

    private void createOrInvalidRetrain(License license, Violation violation) {
        Integer remainScore = license.getRemainScore();
        Date startTime = DateUtils.add(license.getExpireTime(), Calendar.YEAR, -1);
        Date endTime = license.getExpireTime();
        RetrainDetailRequest request = new RetrainDetailRequest();
        request.setStaffId(license.getStaffId());
        request.setStartCreateTime(startTime);
        request.setEndCreateTime(endTime);
        request.setRemainScoreType(RemainScoreEnum.SCORE_REGION_ONE.getValue());
        List<RetrainVO> voList = this.retrainService.findRetrainByCondition(request);
        if (remainScore > 0 && remainScore <= remainScoreOne) {
            if (CollectionUtils.isEmpty(voList) && license.getStatus().equals(LicenseStatusEnum.EFFECTIVE.getCode())) {
                this.retrainService.saveRetrain(this.buildRetrainInfo(license, violation, RemainScoreEnum.SCORE_REGION_ONE.getValue(), RetrainTypeEnum.LICENSE_RETRAIN.getValue()));
                //生成复训短信并给站长发邮箱
                this.sendMsgAndEmail(violation, RetrainTypeEnum.LICENSE_RETRAIN.getValue());
            }
        } else if (remainScore > remainScoreOne && remainScore <= remainScoreTwo) {
            //判断是原先是否存在remainScoreType=1的复训记录,存在失效
            if (!CollectionUtils.isEmpty(voList)) {
                this.invalidRetrainByIds(voList);
            }
            request.setRemainScoreType(RemainScoreEnum.SCORE_REGION_TWO.getValue());
            List<RetrainVO> tempList = this.retrainService.findRetrainByCondition(request);
            if (CollectionUtils.isEmpty(tempList) && license.getStatus().equals(LicenseStatusEnum.EFFECTIVE.getCode())) {
                this.retrainService.saveRetrain(this.buildRetrainInfo(license, violation, RemainScoreEnum.SCORE_REGION_TWO.getValue(), RetrainTypeEnum.LICENSE_RETRAIN.getValue()));
                //生成复训短信并给站长发邮箱
                this.sendMsgAndEmail(violation, RetrainTypeEnum.LICENSE_RETRAIN.getValue());
            }

        } else if (remainScore > remainScoreTwo && remainScore <= Constants.SCORE) {
            request.setRemainScoreType(RemainScoreEnum.SCORE_REGION_TWO.getValue());
            List<RetrainVO> tempList = this.retrainService.findRetrainByCondition(request);
            if (!CollectionUtils.isEmpty(voList)) {
                this.invalidRetrainByIds(voList);
            }
            if (!CollectionUtils.isEmpty(tempList)) {
                this.invalidRetrainByIds(tempList);
            }
        }
        //查看是否存在违规复训,存在则失效
        RetrainInvalidRequest invalidRequest = new RetrainInvalidRequest();
        invalidRequest.setStaffId(license.getStaffId());
        invalidRequest.setViolationId(violation.getId());
        invalidRequest.setOperator(Constants.SYS);
        invalidRequest.setReason(Constants.VIOLATION_INVALID);
        invalidRequest.setType(RetrainTypeEnum.VIOLATION_RETRAIN.getValue());
        this.retrainService.invalidRetrainByCondition(invalidRequest);
    }

    /**
     * 生成复训短信并给站长和培训师发邮件
     */
    private void sendMsgAndEmail(Violation violation, Integer retrainType) {
        //生成复训短信
        this.violationMessageService.saveRetrainMessage(violation, retrainType);
        //发送复训邮件
        String email = this.getSiteLeaderEmail(violation.getSiteLeaderId());
        String retrainText = RetrainTypeEnum.getEnumByValue(retrainType).getText();
        if (StringUtils.isNotBlank(email)) {

            this.sendRetrainNotifyEmail(violation.getName(), email, retrainText);
        }
        //给培训师发复训邮件
        this.sendRetrainNotifyEmailToTrainTeacher(violation.getName(), violation.getSiteName(), retrainText, violation.getSiteId());
    }

    /**
     * 发送复训通知邮件
     */
    @Override
    public void sendRetrainNotifyEmail(String staffName, String email, String retrainType) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("staffName", staffName);
        String createTime = DateUtils.formatDate(new Date(), "yyyy年MM月dd日");
        params.put("createTime", createTime);
        params.put("limitDay", limitDay);
        params.put("retrainType", retrainType);
        try {
            mailService.sendTemplateMail(email, MessageConstant.RETRAIN_NOTIFY_SUBJECT, MessageConstant.RETRAIN_NOTIFY_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[复训通知]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[复训通知]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[复训通知]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[复训通知]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    /**
     * 发送执照吊销邮件
     */
    private void sendLicenseRevokeEmail(String staffName, Long staffId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("staffName", staffName);
        params.put("staffId", staffId.toString());
        try {
            mailService.sendTemplateMail(qualityEmail, MessageConstant.LICENSE_REVOKE_SUBJECT, MessageConstant.LICENSE_REVOKE_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[执照吊销]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[执照吊销]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[执照吊销]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[执照吊销]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    /***
     *  给站点培训师发邮件
     */
    @Override
    public void sendRetrainNotifyEmailToTrainTeacher(String staffName, String siteName, String retrainType, Long siteId) {
        //给培训师发邮件
        SiteAndRegionVO vo = siteViolationService.getTrainTeacherInfo(siteId);
        if (StringUtils.isBlank(vo.getTrainingTeacherEmail())) {
            return;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("teacherName", vo.getTrainingTeacherName() + "");
        params.put("siteName", siteName);
        params.put("staffName", staffName);
        String createTime = DateUtils.formatDate(new Date(), "yyyy年MM月dd日");
        params.put("createTime", createTime);
        params.put("limitDay", limitDay);
        params.put("retrainType", retrainType);
        try {
            mailService.sendTemplateMail(vo.getTrainingTeacherEmail(), MessageConstant.RETRAIN_TEACHER_NOTIFY_SUBJECT, MessageConstant.RETRAIN_TEACHER_NOTIFY_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[给培训师发送复训邮件通知]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[给培训师发送复训邮件通知]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[给培训师发送复训邮件通知]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[给培训师发送复训邮件通知]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    private void invalidRetrainByIds(List<RetrainVO> voList) {
        List<Long> ids = voList.stream().map(RetrainVO::getId).collect(Collectors.toList());
        RetrainInvalidRequest invalidRequest = new RetrainInvalidRequest();
        invalidRequest.setIds(ids);
        invalidRequest.setOperator(Constants.SYS);
        invalidRequest.setReason(Constants.VIOLATION_INVALID);
        this.retrainService.invalidRetrainByIds(invalidRequest);
    }

    @Override
    public LicenseInfoVO findLicenseByStaffId(Long staffId) {
        EntityWrapper<License> licenseEntityWrapper = new EntityWrapper();
        licenseEntityWrapper.eq("staff_id", staffId).orderBy("create_time", false);
        List<License> licenseList = this.selectList(licenseEntityWrapper);
        LicenseInfoVO vo = new LicenseInfoVO();
        if (!CollectionUtils.isEmpty(licenseList)) {
            BeanCopyUtil.copyPropertiesIgnoreNull(licenseList.get(0), vo);
            return vo;
        }
        return null;
    }

    @Override
    public List<LicenseInfoVO> findLicenseList(LicenseInfoRequest request) {
        return this.baseMapper.findLicenseList(request);
    }

    private void removeBlack(Long staffId) {
        Blacklist blacklist = this.blacklistService.findByStaffId(staffId);
        if (blacklist != null && blacklist.getType().equals(BlacklistTypeEnum.REVOKE.getValue())) {
            blacklistService.deleteById(blacklist.getId());
            //创建黑名单日志
            BlacklistLog log = new BlacklistLog();
            log.setStaffId(blacklist.getStaffId());
            log.setCreateBy(Constants.SYS);
            log.setCreateTime(new Date());
            log.setRemark(Constants.REMOVE_BLACK_BY_SYSTEM);
            log.setType(blacklist.getType());
            log.setOperateType("删除");
            this.blacklistLogService.insert(log);
        }
    }

    @Override
    public List<License> findListByStaffIds(List<Long> staffIds) {
        if (CollectionUtils.isEmpty(staffIds)) {
            return null;
        }
        return this.baseMapper.findListByStaffIds(staffIds);

    }

    @Override
    public void updateLicenseInfoWhenStaffMove(LicenseVO licenseVO) {
        License license = new License();
        license.setStaffId(licenseVO.getStaffId());
        EntityWrapper<License> wrapper = new EntityWrapper();
        wrapper.setEntity(license);
        license = this.selectOne(wrapper);
        if (license != null) {
            Boolean isUpdate = false;
            if (licenseVO.getSiteId() != null && !license.getSiteId().equals(licenseVO.getSiteId())) {
                license.setSiteId(licenseVO.getSiteId());
                license.setSiteName(licenseVO.getSiteName());
                isUpdate = true;
            }
            if (licenseVO.getCityCode() != null && !license.getCityCode().equals(licenseVO.getCityCode())) {
                license.setCityCode(licenseVO.getCityCode());
                license.setCityName(licenseVO.getCityName());
                isUpdate = true;
            }
            if (licenseVO.getPositionType() != null && !license.getPositionType().equals(licenseVO.getPositionType())) {
                license.setPositionType(licenseVO.getPositionType());
                isUpdate = true;
            }
            if (isUpdate) {
                license.setModifyBy(Constants.SYS);
                license.setModifyTime(new Date());
                this.updateById(license);
            }
        }
    }

    @Override
    public String getSiteLeaderPhone(Long siteLeaderId) {
        if (siteLeaderId == null) {
            log.warn("站长id为空");
            return null;
        }
        BaseResponse<RosterData> baseResponse = this.rosterOpenService.findRosterById(siteLeaderId);
        if (baseResponse.isSuccess() && baseResponse.getData() != null) {
            RosterData data = baseResponse.getData();
            if (StringUtils.isNotBlank(data.getPhone())) {
                return data.getPhone();
            }
        }
        return null;
    }

    @Override
    public void activateLicenseByIdCard(String idCard) {
        List<License> licenseList = this.selectList(new EntityWrapper<License>().eq("id_card", idCard));
        if (!CollectionUtils.isEmpty(licenseList)) {
            for (License license : licenseList) {
                if (license.getStaffId() > 0 && license.getStatus().equals(LicenseStatusEnum.REVOKE.getCode())
                    && license.getRemainScore() > 0 && StringUtils.isNotBlank(license.getReason())
                    && license.getReason().equals(Constants.QUIT)) {
                    license.setStatus(LicenseStatusEnum.EFFECTIVE.getCode());
                    license.setModifyBy(Constants.SYS);
                    license.setModifyTime(new Date());
                    license.setReason("激活职员对应的员工执照");
                    this.updateById(license);
                }
            }
        }
    }

    @Override
    public void applyStopOrder(Long staffId, LicenseInfoVO license) {
        try {
            LicenseStopOrderLog stopOrderLog = getLicenseStopOrderLog(staffId, license);
            if (ObjectUtils.isEmpty(stopOrderLog)) {
                return;
            }
            log.info("对员工：{}，进行风控停单", staffId);
            // 执行停单
            ApplyForStopOrderApplyForStopOrderDTO apply;
            LocalDateTime startTime = stopOrderLog.getStopOrderBeginTime();
            if (startTime.isBefore(LocalDateTime.now())) {
                startTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
            }
            apply = stopOrderMpManager.apply(stopOrderLog.getId(), staffId, startTime,
                Operator.stringToOperator(Constants.SYS));
            stopOrderLog.setStopOrderId(apply.getId());
            stopOrderLog.setStopOrderBeginTime(startTime);
            stopOrderLog.setStopOrderStatus(ValidEnum.VALID.getValue());
            stopOrderLog.setModifyBy(Constants.SYS);
            stopOrderLog.setModifyTime(LocalDateTime.now());
            licenseStopOrderLogService.updateById(stopOrderLog);
        } catch (Exception e) {
            log.error("applyStopOrder staffId={}, error:", staffId, e);
        }
    }

    @Override
    public void endStopOrder(LicenseInfoVO licenseInfoVO) {
        try {
            // 查询执照停单记录
            List<LicenseStopOrderLog> logList = licenseStopOrderLogService.getValidListByStaffId(licenseInfoVO.getStaffId());
            if (CollectionUtils.isEmpty(logList)) {
                return;
            }
            log.info("对员工：{}，解除执照分控停单", licenseInfoVO.getStaffId());
            logList.forEach(licenseStopOrderLog -> {
                endStopOrderByLog(licenseStopOrderLog);
            });
        } catch (Exception e) {
            log.error("endStopOrder staffId={}, error:", licenseInfoVO.getStaffId(), e);
        }
    }

    /**
     * 获取执照吊销记录
     *
     * @param staffId
     * @param license
     * @return void
     */
    private LicenseStopOrderLog getLicenseStopOrderLog(Long staffId, LicenseInfoVO license) {
        Long licenseId = null;
        String reason = null;
        if (!ObjectUtils.isEmpty(license)) {
            licenseId = license.getId();
            reason = license.getReason();
        }
        if (ObjectUtils.isEmpty(reason)) {
            reason = Constants.LICENSE_NOT_FOUND;
        }
        List<LicenseStopOrderLog> logList = licenseStopOrderLogService.getValidListByStaffId(staffId);
        if (CollectionUtils.isEmpty(logList)) {
            return licenseStopOrderLogService.initSave(staffId, licenseId, reason);
        }
        LicenseStopOrderLog licenseStopOrderLog = null;
        for (LicenseStopOrderLog stopOrderLog: logList) {
            if (ObjectUtils.isEmpty(stopOrderLog.getStopOrderId())) {
                //没有停单成功，则重新停单
                licenseStopOrderLog = stopOrderLog;
                continue;
            } else {
                //已停单成功，不重新触发
                return null;
            }
        }
        return licenseStopOrderLog;
    }

    /**
     * 根据记录解除风控停单
     *
     * @param licenseStopOrderLog
     * @return void
     */
    private void endStopOrderByLog(LicenseStopOrderLog licenseStopOrderLog) {
        try {
            if (!ObjectUtils.isEmpty(licenseStopOrderLog.getStopOrderId())) {
                // 结束停单
                LocalDateTime stopTime = LocalDateTime.now().plusMinutes(5);
                stopOrderMpManager.finish(licenseStopOrderLog.getStopOrderId(), stopTime, Operator.stringToOperator(Constants.SYS));
                licenseStopOrderLog.setStopOrderEndTime(stopTime);
            }
            licenseStopOrderLog.setStopOrderStatus(ValidEnum.INVALID.getValue());
            licenseStopOrderLog.setModifyTime(LocalDateTime.now());
            licenseStopOrderLog.setModifyBy(Constants.SYS);
            licenseStopOrderLogService.updateById(licenseStopOrderLog);
        } catch (Exception e) {
            log.error("endStopOrderByLog error:", e);
        }
    }
}
