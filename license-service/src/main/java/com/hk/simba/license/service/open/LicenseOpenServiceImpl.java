package com.hk.simba.license.service.open;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hk.base.util.DateUtils;
import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.quark.base.util.ReplaceUtils;
import com.hk.simba.archive.open.RosterOpenService;
import com.hk.simba.archive.open.response.roster.RosterData;
import com.hk.simba.license.api.LicenseOpenService;
import com.hk.simba.license.api.request.BaseRequest;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.license.LicenseInfoRequest;
import com.hk.simba.license.api.request.license.LicenseQueryRequest;
import com.hk.simba.license.api.request.license.LicenseRequest;
import com.hk.simba.license.api.request.license.LicenseRevokeRequest;
import com.hk.simba.license.api.request.retrain.RetrainInvalidRequest;
import com.hk.simba.license.api.vo.LicenseInfoVO;
import com.hk.simba.license.api.vo.LicenseVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.*;
import com.hk.simba.license.service.entity.*;
import com.hk.simba.license.service.service.*;
import com.hk.simba.license.service.utils.BeanCopyUtil;
import com.hk.simba.license.service.utils.R;
import com.hk.simba.license.service.utils.YearUtil;
import com.hk.sisyphus.merope.core.staff.StaffApi;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffSensitiveInfoByStaffIdRequest;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO;
import com.hk.sisyphus.merope.model.staff.staff.SearchStaffByConditionRequest;
import com.hk.sisyphus.merope.model.staff.staff.SearchStaffByConditionStaffBasicDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author cyh
 * @date 2020/4/26/14:39
 */
@Slf4j
@DubboService
public class LicenseOpenServiceImpl implements LicenseOpenService {
    /**
     * 执照服务
     */
    @Autowired
    private LicenseService licenseService;

    /**
     * 执照加减分记录
     */
    @Autowired
    private LicenseScoreLogService licenseScoreLogService;

    /**
     * 吊销执照记录服务
     */
    @Autowired
    private LicenseRevokeLogService licenseRevokeLogService;

    /**
     * 复训服务
     */
    @Autowired
    private RetrainService retrainService;

    @Autowired
    private BlacklistService blacklistService;

    @Reference
    private RosterOpenService rosterOpenService;

    @Autowired
    private LicenseExamGroupService licenseExamGroupService;

    @Autowired
    private StaffApi staffApi;

    @Override
    public BaseResponse<PageResult<LicenseVO>> list(PageRequest page, LicenseQueryRequest request) {
        Page<License> licensePage = new Page<>(page.getPageNo(), page.getPageSize());
        List<LicenseVO> voList = licenseService.getPageList(licensePage, request);
        if (CollectionUtils.isEmpty(voList)) {
            return R.result(ResponseCodeEnum.SUCCESS, new PageResult<>());
        }
        // 查询拉黑情况
        Map<Long, Blacklist> blacklistMap =new HashMap<>();
        List<Long> staffIds= voList.stream().map(LicenseVO::getStaffId).collect(Collectors.toList());
        List<Blacklist> blacklists = blacklistService.findByStaffIds(staffIds);
        if (!CollectionUtils.isEmpty(blacklists)){
            blacklistMap = blacklists.stream().collect(Collectors.toMap(Blacklist::getStaffId, b -> b));
        }
        for (LicenseVO vo : voList) {
            Blacklist black = blacklistMap.get(vo.getStaffId());
            if (Objects.nonNull(black)){
                vo.setBlack(1);
                vo.setBlackListReason(black.getReason());
                vo.setBlackListRemark(black.getRemark());
            }
            vo.setPhone(ReplaceUtils.replacePhone(vo.getPhone()));
            vo.setIdCard(ReplaceUtils.replaceIdCard(vo.getIdCard()));
        }

        PageResult<LicenseVO> pageResult = new PageResult<>();
        pageResult.setResults(voList);
        pageResult.setCount(licensePage.getTotal());
        pageResult.setPageNo(licensePage.getCurrent());
        pageResult.setPageSize(licensePage.getSize());
        pageResult.setTotalPage(licensePage.getPages());
        return R.result(ResponseCodeEnum.SUCCESS, pageResult);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse revoke(LicenseRevokeRequest request) {
        log.info("[吊销执照请求request={}]", JSON.toJSONString(request));
        if (null == request.getLicenseId() || StringUtils.isBlank(request.getReason()) || request.getBlack() == null) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        IsBlacklistEnum blackEnum = IsBlacklistEnum.getEnumByValue(request.getBlack());
        if (blackEnum == null) {
            return R.result(ResponseCodeEnum.ERROR_PARAM);
        }
        License license = this.licenseService.selectById(request.getLicenseId());
        if (null == license) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        if (license.getStatus().equals(LicenseStatusEnum.EFFECTIVE.getCode())) {
            license.setModifyBy(request.getOperator());
            license.setModifyTime(new Date());
            license.setStatus(LicenseStatusEnum.REVOKE.getCode());
            license.setReason(request.getReason());
            license.setBlack(request.getBlack());
            this.licenseService.updateById(license);

            //生成吊销记录
            LicenseRevokeLog licenseRevokeLog = new LicenseRevokeLog();
            licenseRevokeLog.setLicenseId(license.getId());
            licenseRevokeLog.setStaffId(license.getStaffId());
            licenseRevokeLog.setCreateTime(new Date());
            licenseRevokeLog.setCreateBy(request.getOperator());
            licenseRevokeLog.setReason(request.getReason());
            this.licenseRevokeLogService.insert(licenseRevokeLog);

            //失效复训记录
            RetrainInvalidRequest invalidRequest = new RetrainInvalidRequest();
            invalidRequest.setStaffId(license.getStaffId());
            invalidRequest.setOperator(request.getOperator());
            invalidRequest.setReason(Constants.LICENSE_REVOKE);
            this.retrainService.invalidRetrainByCondition(invalidRequest);

            //手工吊销--加入黑名单;
            if (request.getBlack().equals(IsBlacklistEnum.YES.getValue())) {
                Blacklist blacklist = new Blacklist();
                blacklist.setStaffId(license.getStaffId());
                blacklist.setName(license.getName());
                blacklist.setGender(license.getGender());
                blacklist.setPhone(license.getPhone());
                blacklist.setRemark(request.getRemark());
                blacklist.setCreateTime(new Date());
                blacklist.setCreateBy(request.getOperator());
                blacklist.setIdCard(license.getIdCard());
                blacklist.setType(request.getType());
                blacklist.setReason(request.getBlackListReason());
                this.blacklistService.createBlack(blacklist);
            }
        } else {
            return R.result(ResponseCodeEnum.EXIST_REVOKE_STATUS);
        }
        return R.result(ResponseCodeEnum.SUCCESS);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse revokeInfo(Long id) {
        if (null == id) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        License license = this.licenseService.selectById(id);
        if (null == license) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        LicenseVO vo = new LicenseVO();
        BeanCopyUtil.copyProperties(license, vo);
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    @Override
    public BaseResponse findLicenseByIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        List<License> licenseList = this.licenseService.selectList(
                new EntityWrapper<License>()
                        .eq("id_card", idCard).orderBy("create_time", false));

        if (CollectionUtils.isEmpty(licenseList)) {
            return R.result(ResponseCodeEnum.HAS_NO_LICENSE);
        }
        LicenseVO vo = new LicenseVO();
        BeanCopyUtil.copyProperties(licenseList.get(0), vo);
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    @Override
    public BaseResponse createLicense(BaseRequest<LicenseVO> request) {
        log.info("[生成执照请求request={}]", JSON.toJSONString(request));
        LicenseVO vo = request.getData();
        if (null == vo || StringUtils.isBlank(vo.getThirdUserId())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        Long staffId = this.getStaffId(vo.getThirdUserId());
        if (staffId == null) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        List<License> licenseList = this.licenseService.selectList(
                new EntityWrapper<License>()
                        .eq("staff_id", staffId));
        if (CollectionUtils.isEmpty(licenseList)) {
            //创建执照
            vo.setStaffId(staffId);
            this.saveLicenseExamGroupAndLicense(vo);
            return R.result(ResponseCodeEnum.SUCCESS);

        }
        //若是已离职，再入职[科目一喝科目四]考试通过的话，员工id不变时，更新状态，吊销事由清空，判断分数是否同一周期，是分数不变，否的话分数变为24分
        License license = licenseList.get(0);
        if (license.getStatus().equals(LicenseStatusEnum.REVOKE.getCode()) && license.getRemainScore() > 0 &&
                StringUtils.isNotBlank(license.getReason()) && license.getReason().equals(Constants.QUIT)) {
            //判断考试是否通过
            LicenseExamGroup group = this.licenseExamGroupService.createOrUpdateLicenseExamGroup(staffId, vo.getMapId(), license.getName());
            if (group != null && group.getStatus().equals(CommonStatusEnum.VALID.getValue()) && StringUtils.isNotBlank(group.getFirstSubjectExamId())
                    && StringUtils.isNotBlank(group.getFourthSubjectExamId())) {
                this.licenseService.updateById(this.repeatEmployee(license));
                //失效掉执照分组考试记录
                group.setRemark(Constants.INVALID_EXAM_GROUP);
                this.licenseExamGroupService.invalidLicenseExamGroup(group);
                return R.result(ResponseCodeEnum.SUCCESS);
            }
            return R.result(ResponseCodeEnum.FAIL_THE_EXAM);
        }

        log.info("执照已存在,ThirdUserId={}", vo.getThirdUserId());
        return R.result(ResponseCodeEnum.EXIST_LICENSE);
    }


    @Override
    public BaseResponse updateLicense(LicenseVO licenseVO) {
        if (null == licenseVO.getId()) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        License license = this.licenseService.selectById(licenseVO.getId());
        if (null == license) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        BeanCopyUtil.copyPropertiesIgnoreNull(licenseVO, license);
        this.licenseService.updateById(license);
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse judgeLicenseValidity(Long id) {
        if (null == id) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        License license = this.licenseService.selectById(id);
        if (null == license) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        if (new Date().before(license.getExpireTime())) {
            return R.result(ResponseCodeEnum.SUCCESS, true);
        } else {
            return R.result(ResponseCodeEnum.SUCCESS, false);
        }
    }

    /**
     * 保存执照信息
     * 乐学考试成绩的用户编号以K、N、H开头
     */
    private void saveLicenseInfo(LicenseVO vo) {
        String thirdUserId = vo.getThirdUserId();
        if (StringUtils.isNotBlank(thirdUserId)) {
            License license = new License();
            if (thirdUserId.startsWith(Constants.N)) {
                license.setCooker(true);
            } else {
                license.setCooker(false);
            }
            license.setStaffId(vo.getStaffId());
            license.setStatus(LicenseStatusEnum.EFFECTIVE.getCode());
            license.setThirdUserId(vo.getThirdUserId());
            license.setRemainScore(Constants.SCORE);
            license.setCreateTime(new Date());
            if (StringUtils.isNotBlank(vo.getCreateBy())) {
                license.setCreateBy(vo.getCreateBy());
            }
            Date zero = YearUtil.getCurrentDayZeroTime();
            license.setEffectiveTime(zero);
            license.setExpireTime(DateUtils.add(zero, Calendar.YEAR, 1));
            license = this.licenseService.updateLicenseInfo(license);
            //若站点的信息为空，说明获取员工信息失败
            if (license.getSiteId() == null) {
                log.warn("获取站点失败，不生成执照，staffId={}", license.getStaffId());
                return;
            }
            this.licenseService.insert(license);
            // 分数流水
            LicenseScoreLog licenseScoreLog = new LicenseScoreLog();
            licenseScoreLog.setLicenseId(license.getId());
            licenseScoreLog.setStaffId(license.getStaffId());
            licenseScoreLog.setDeductScore(Constants.SCORE);
            licenseScoreLog.setReason(Constants.SCORE_INIT);
            licenseScoreLog.setCreateBy(Constants.SYS);
            licenseScoreLog.setCreateTime(new Date());
            licenseScoreLogService.insert(licenseScoreLog);
        }
    }

    /***
     * 保存执照分组考试信息并生成执照
     */
    private void saveLicenseExamGroupAndLicense(LicenseVO vo) {
        //填充员工信息
        FindStaffSensitiveInfoByStaffIdRequest f = new FindStaffSensitiveInfoByStaffIdRequest();
        f.setStaffId(vo.getStaffId());
        com.hk.simba.base.common.dto.response.BaseResponse<FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO> baseResponse = staffApi.findStaffSensitiveInfoByStaffId(f);
        if (baseResponse.isSuccess()) {
            FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO dto = baseResponse.getData();
            vo.setName(dto.getName());
        }
        //创建更新分组考试
        LicenseExamGroup group = this.licenseExamGroupService.createOrUpdateLicenseExamGroup(vo.getStaffId(), vo.getMapId(), vo.getName());
        if (group != null && group.getStatus().equals(CommonStatusEnum.VALID.getValue()) && StringUtils.isNotBlank(group.getFirstSubjectExamId())
                && StringUtils.isNotBlank(group.getFourthSubjectExamId())) {
            this.saveLicenseInfo(vo);
            //失效掉执照分组考试记录
            group.setRemark(Constants.INVALID_EXAM_GROUP);
            this.licenseExamGroupService.invalidLicenseExamGroup(group);
        }
    }


    /**
     * 若是已离职，再入职的话，员工id不变时，更新状态，吊销事由清空，
     * 判断分数是否同一周期，是分数不变，否的话分数变为24分
     */
    private License repeatEmployee(License license) {
        if (license.getStatus().equals(LicenseStatusEnum.REVOKE.getCode())) {
            license.setReason("");
            license.setModifyTime(new Date());
            license.setModifyBy(Constants.SYS);
            license.setStatus(LicenseStatusEnum.EFFECTIVE.getCode());
            //判断分数是否同一周期，是分数不变,否则重新计分
            if (new Date().after(license.getExpireTime())) {
                license.setRemainScore(Constants.SCORE);
                Date zero = YearUtil.getCurrentDayZeroTime();
                license.setExpireTime(DateUtils.add(zero, Calendar.YEAR, 1));
                //生成分数流水
                LicenseScoreLog licenseScoreLog = new LicenseScoreLog();
                licenseScoreLog.setLicenseId(license.getId());
                licenseScoreLog.setStaffId(license.getStaffId());
                licenseScoreLog.setDeductScore(Constants.SCORE);
                licenseScoreLog.setReason(Constants.SCORE_RESET);
                licenseScoreLog.setCreateBy(Constants.SYS);
                licenseScoreLog.setCreateTime(new Date());
                licenseScoreLogService.insert(licenseScoreLog);
            }
        }
        return license;
    }


    @Override
    public BaseResponse createLicenseByContent(String content, String operator) {
        if (StringUtils.isNotBlank(content)) {
            String[] staffIds = content.split(",");
            for (String staffId : staffIds) {
                Long tempStaffId = this.getStaffId(staffId);
                if (null == tempStaffId) {
                    return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
                }
                List<License> licenseList = this.licenseService.selectList(
                        new EntityWrapper<License>()
                                .eq("staff_id", tempStaffId));
                if (CollectionUtils.isEmpty(licenseList)) {
                    //创建执照
                    LicenseVO vo = new LicenseVO();
                    vo.setThirdUserId(staffId);
                    vo.setCreateBy(operator);
                    vo.setStaffId(tempStaffId);
                    this.saveLicenseInfo(vo);
                    //失效考试记录
                    LicenseExamGroup group = this.licenseExamGroupService.findValidGroupByStaffId(tempStaffId);
                    if (group != null) {
                        group.setRemark(Constants.INVALID_EXAM_GROUP);
                        this.licenseExamGroupService.invalidLicenseExamGroup(group);
                    }
                }
            }
            return R.result(ResponseCodeEnum.SUCCESS);
        }
        return R.result(ResponseCodeEnum.FAILED);
    }

    @Override
    public BaseResponse<LicenseInfoVO> findLicenseByStaffId(Long staffId) {
        LicenseInfoVO vo = this.licenseService.findLicenseByStaffId(staffId);
        if (null == vo) {
            return R.result(ResponseCodeEnum.HAS_NO_LICENSE);
        }
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    @Override
    public BaseResponse<List<LicenseInfoVO>> findLicenseList(LicenseInfoRequest request) {
        List<LicenseInfoVO> voList = this.licenseService.findLicenseList(request);
        return R.result(ResponseCodeEnum.SUCCESS, voList);
    }


    @Override
    public BaseResponse findLicenseByIdCardAndStaffId(String idCard, Long staffId) {
        if (StringUtils.isBlank(idCard) || staffId == null) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        List<License> licenseList = this.licenseService.selectList(
                new EntityWrapper<License>()
                        .eq("id_card", idCard).eq("staff_id", staffId).orderBy("create_time", false));

        if (CollectionUtils.isEmpty(licenseList)) {
            return R.result(ResponseCodeEnum.HAS_NO_LICENSE);
        }
        LicenseVO vo = new LicenseVO();
        BeanCopyUtil.copyProperties(licenseList.get(0), vo);
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    private Long getStaffId(String thirdUserId) {
        if (StringUtils.isBlank(thirdUserId)) {
            return null;
        }
        if (thirdUserId.startsWith(Constants.N) || thirdUserId.startsWith(Constants.K)) {
            String staffId = thirdUserId.substring(1, thirdUserId.length());
            return Long.valueOf(staffId);
        } else if (thirdUserId.startsWith(Constants.H)) {
            return this.activateLicenseAndGetStaffId(thirdUserId);
        } else {
            Long tempStaffId = Long.parseLong(thirdUserId);
            return tempStaffId;
        }
    }

    /***
     * 若是职员,则激活其对应的员工执照,返回其员工id
     *
     */
    private Long activateLicenseAndGetStaffId(String thirdUserId) {
        com.hk.simba.base.common.dto.response.BaseResponse<RosterData> response = this.rosterOpenService.findRosterByWorkNum(thirdUserId);
        if (response.isSuccess() && response.getData() != null) {
            RosterData data = response.getData();
            //或是H开头，则填充其员工信息
            SearchStaffByConditionRequest request = new SearchStaffByConditionRequest();
            request.setIdCard(data.getIdCard());
            com.hk.simba.base.common.dto.response.BaseResponse<SearchStaffByConditionStaffBasicDTO> baseResponse = staffApi.searchStaffByCondition(request);
            if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                SearchStaffByConditionStaffBasicDTO dto = baseResponse.getData();
                return dto.getId();
            }
        }
        return null;
    }

    /**
     * 激活执照
     *
     * @param request 执照请求对象，包含执照ID和操作者信息
     * @return 基础响应对象，包含响应码和消息
     */
    @Override
    public BaseResponse activeLicense(LicenseRequest request) {
        // 参数校验
        if (request == null || request.getLicenseId() == null || request.getSetScore() == null || StringUtils.isBlank(request.getOperator())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }

        // 查询执照
        License license = this.licenseService.selectById(request.getLicenseId());
        if (license == null) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }

        // 执照状态是否被吊销
        if (!license.getStatus().equals(LicenseStatusEnum.REVOKE.getCode())) {
            return R.result(ResponseCodeEnum.SUCCESS);
        }

        // 设置执照状态为生效
        license.setStatus(LicenseStatusEnum.EFFECTIVE.getCode());
        license.setModifyTime(new Date());
        license.setModifyBy(request.getOperator());

        // 设置原因
        if (StringUtils.isNotBlank(request.getReason())) {
            license.setReason(request.getReason());
        }

        // 更新执照分和开始日期
        if (request.getSetScore()) {
            updateLicenseWithScore(license, request);
        } else if (new Date().after(license.getExpireTime())) {
            resetLicenseScore(license);
        }

        // 更新执照
        this.licenseService.updateById(license);

        // 返回响应
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    /**
     * 更新执照分数和有效期开始时间
     *
     * @param license 执照对象
     * @param request 执照请求对象，包含分数和生效时间
     */
    private void updateLicenseWithScore(License license, LicenseRequest request) {
        license.setRemainScore(request.getScore());
        license.setEffectiveTime(request.getEffectiveTime());
        // 根据传进来的时间设置过期日期
        license.setExpireTime(DateUtils.add(request.getEffectiveTime(), Calendar.YEAR, 1));
        createScoreLog(license, request.getScore(), Constants.SCORE_RESET);
    }

    /**
     * 重置执照分数和有效期
     *
     * @param license 执照对象
     */
    private void resetLicenseScore(License license) {
        license.setRemainScore(Constants.SCORE);
        Date zero = YearUtil.getCurrentDayZeroTime();
        license.setExpireTime(DateUtils.add(zero, Calendar.YEAR, 1));
        createScoreLog(license, Constants.SCORE, Constants.SCORE_RESET);
    }

    /**
     * 创建执照分数日志
     *
     * @param license 执照对象
     * @param score 更改的分数
     * @param reason 更改原因
     */
    private void createScoreLog(License license, int score, String reason) {
        // 创建一个LicenseScoreLog对象，用于记录积分变动日志
        LicenseScoreLog licenseScoreLog = new LicenseScoreLog();
        // 设置积分变动的原因
        licenseScoreLog.setReason(reason);
        // 设置关联的许可证ID
        licenseScoreLog.setLicenseId(license.getId());
        // 设置关联的员工ID
        licenseScoreLog.setStaffId(license.getStaffId());
        // 设置扣除的积分值
        licenseScoreLog.setDeductScore(score);
        // 设置日志的创建者为系统
        licenseScoreLog.setCreateBy(Constants.SYS);
        // 设置日志的创建时间为当前时间
        licenseScoreLog.setCreateTime(new Date());

        // 调用服务层方法，将积分变动日志插入数据库
        licenseScoreLogService.insert(licenseScoreLog);
    }

}
