package com.hk.simba.license.service.open;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hk.base.util.modules.BeanMapper;
import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.quark.base.util.ReplaceUtils;
import com.hk.simba.license.api.ViolationOpenService;
import com.hk.simba.license.api.enums.AppealStatusEnum;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.enums.ViolationPayStatusEnum;
import com.hk.simba.license.api.request.BaseRequest;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.retrain.RetrainInvalidRequest;
import com.hk.simba.license.api.request.violation.ViolationAppealRequest;
import com.hk.simba.license.api.request.violation.ViolationApprovalRequest;
import com.hk.simba.license.api.request.violation.ViolationInvalidRequest;
import com.hk.simba.license.api.request.violation.ViolationPayStatusRequest;
import com.hk.simba.license.api.request.violation.ViolationQueryRequest;
import com.hk.simba.license.api.request.violation.ViolationRemarkRequest;
import com.hk.simba.license.api.vo.ViolationAppVO;
import com.hk.simba.license.api.vo.ViolationVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.ApproveStatusEnum;
import com.hk.simba.license.service.constant.enums.DeadlineStatusEnum;
import com.hk.simba.license.service.constant.enums.DeductTypeEnum;
import com.hk.simba.license.service.constant.enums.HasAppealEnum;
import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.simba.license.service.constant.enums.RetrainTypeEnum;
import com.hk.simba.license.service.entity.Appeal;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.manager.StaffApiManager;
import com.hk.simba.license.service.service.AppealService;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.simba.license.service.service.RetrainService;
import com.hk.simba.license.service.service.SiteViolationService;
import com.hk.simba.license.service.service.ViolationMailService;
import com.hk.simba.license.service.service.ViolationMessageService;
import com.hk.simba.license.service.service.ViolationService;
import com.hk.simba.license.service.utils.R;
import com.hk.simba.license.service.utils.RedisUtil;
import com.hk.simba.license.service.utils.YearUtil;
import com.hk.sisyphus.merope.core.staff.StaffApi;
import com.hk.sisyphus.merope.model.staff.staff.SearchEntryRecordEntryRecordDTO;
import com.hk.sisyphus.merope.model.staff.staff.SearchEntryRecordRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @description @author 羊皮
 * @since 2020-4-10 10:34:16
 */
@Slf4j
@DubboService
public class ViolationOpenServiceImpl implements ViolationOpenService {

    @Autowired
    private ViolationService violationService;

    @Autowired
    private AppealService appealService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private ViolationMessageService violationMessageService;

    @Autowired
    private RetrainService retrainService;

    @Autowired
    private SiteViolationService siteViolationService;

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private ViolationMailService violationMailService;

    @Resource
    private StaffApiManager staffApiManager;

    /**
     * 违规申诉期限
     */
    @Value("${violation.deadline.amount}")
    private Integer deadlineAmount;

    @Autowired
    private StaffApi staffApi;

    @Override
    public BaseResponse save(BaseRequest<ViolationAppVO> request) {
        log.debug("[保存违规记录请求:{}]", JSON.toJSONString(request));
        ViolationAppVO vo = request.getData();
        if (null == vo) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        Violation ent = new Violation();
        BeanUtils.copyProperties(request.getData(), ent);
        violationService.insert(ent);
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse update(BaseRequest<ViolationAppVO> request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BaseResponse page(PageRequest request, ViolationAppVO vo) {
        Page<Violation> page = new Page<>(request.getPageNo(), request.getPageSize());
        Violation ent = new Violation();
        BeanUtils.copyProperties(vo, ent);
        Wrapper<Violation> wrap = new EntityWrapper<>(ent);
        wrap.orderBy("id", false);
        wrap.ne("status", StatusEnum.WAIT_VALID.getValue());
        page = violationService.selectPage(page, wrap);
        List<ViolationAppVO> result = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(page.getRecords())) {
            for (Violation record : page.getRecords()) {
                ViolationAppVO v = new ViolationAppVO();
                BeanUtils.copyProperties(record, v);
                result.add(v);
            }
        }
        PageResult<ViolationAppVO> pageResult = new PageResult<>();
        pageResult.setResults(result);
        pageResult.setCount(page.getTotal());
        pageResult.setPageNo(page.getCurrent());
        pageResult.setPageSize(page.getSize());

        pageResult.setTotalPage(page.getPages());
        return R.result(ResponseCodeEnum.SUCCESS, pageResult);
    }

    @Override
    public BaseResponse page(PageRequest request, ViolationQueryRequest query) {
        Page<Violation> page = new Page<>(request.getPageNo(), request.getPageSize());
        List<ViolationVO> violationVOList = violationService.queryPage(page, query);
        if (CollectionUtils.isEmpty(violationVOList)) {
            return R.result(ResponseCodeEnum.SUCCESS, new PageResult<>());
        }
        for (ViolationVO v : violationVOList) {
            if (StringUtils.isNotBlank(v.getPhone())) {
                v.setPhone(ReplaceUtils.replacePhone(v.getPhone()));
            }
            v.setViolationStatus(v.getAppealStatus());
            //判断是否过期
            if (this.judgeDeadline(v)) {
                v.setDeadlineStatus(DeadlineStatusEnum.VALID.getValue());
            } else {
                v.setDeadlineStatus(DeadlineStatusEnum.INVALID.getValue());
            }
            //是否发起过申诉
            if (AppealStatusEnum.NO_APPEAL.getValue() == v.getAppealStatus()) {
                v.setHasAppeal(HasAppealEnum.NO.getValue());
            } else {
                v.setHasAppeal(HasAppealEnum.YES.getValue());
            }
        }
        PageResult<ViolationVO> pageResult = new PageResult<>();
        pageResult.setResults(violationVOList);
        pageResult.setCount(page.getTotal());
        pageResult.setPageNo(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setTotalPage(page.getPages());
        return R.result(ResponseCodeEnum.SUCCESS, pageResult);
    }

    @Override
    public BaseResponse selectById(Long id) {
        Violation result = violationService.selectById(id);
        if (null == result) {
            return R.result(ResponseCodeEnum.SUCCESS, "");
        }
        ViolationVO vo = new ViolationVO();
        BeanMapper.copy(result, vo);
        //有申诉过，则返回申诉状态，否则返回基础状态
        vo.setViolationStatus(vo.getAppealStatus());
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse appeal(ViolationAppealRequest request) {
        //前置判断
        if (null == request.getId() || null == request.getAppealStatus()) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        if (AppealStatusEnum.APPEAL_REJECT.getValue() != request.getAppealStatus()
                && AppealStatusEnum.APPEAL_PASS.getValue() != request.getAppealStatus()) {
            return R.result(ResponseCodeEnum.ERROR_PARAM);
        }
        Long id = request.getId();
        Violation violation = violationService.selectById(id);
        if (null == violation || StatusEnum.INVALID.getValue() == violation.getStatus() ||
                StatusEnum.HAND_INVALID.getValue() == violation.getStatus() ||
                StatusEnum.APPEAL_INVALID.getValue() == violation.getStatus()) {
            return R.result(ResponseCodeEnum.ERROR_DATA_NOT_EXISTS);
        }
        if (AppealStatusEnum.NO_APPEAL.getValue() != violation.getAppealStatus()) {
            return R.result(ResponseCodeEnum.ERROR_APPEAL_STATUS);
        }
        Appeal existAppeal = appealService.selectOne(new EntityWrapper<Appeal>().eq("violation_id", violation.getId()));
        if (null != existAppeal) {
            return R.result(ResponseCodeEnum.EXIST_APPEAL_DATA);
        }
        //数据操作
        Appeal appeal = new Appeal();
        appeal.setViolationId(violation.getId());
        appeal.setAppealTime(new Date());
        appeal.setDealTime(new Date());
        appeal.setRemark(request.getDescription());
        appeal.setCreateBy(request.getOperator());
        appeal.setCreateTime(new Date());
        appeal.setStatus(request.getAppealStatus());
        appealService.insert(appeal);

        int status = StatusEnum.VALID.getValue();
        if (AppealStatusEnum.APPEAL_PASS.getValue() == request.getAppealStatus()) {
            status = StatusEnum.APPEAL_INVALID.getValue();
            if (violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                licenseService.appealPass(violation);
            }
        }
        violation.setStatus(status);
        violation.setAppealStatus(request.getAppealStatus());
        violation.setModifyBy(request.getOperator());
        violation.setModifyTime(new Date());
        violationService.updateById(violation);
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    /**
     * 申述过期判断
     */
    private boolean judgeDeadline(ViolationVO vo) {
        if (vo.getDeadlineTime() != null && vo.getDeadlineTime().after(new Date())) {
            return true;
        } else {
            if (vo.getAppealStatus().equals(AppealStatusEnum.APPEAL_REVOKE.getValue())) {
                List<Appeal> appealList = this.appealService.selectList(new EntityWrapper<Appeal>().
                        eq("violation_id", vo.getId()).
                        eq("status", ApproveStatusEnum.APPEAL_RECALL.getValue()).orderBy("create_time", false));
                if (!CollectionUtils.isEmpty(appealList)) {
                    Date recallTime = appealList.get(0).getRecallTime();
                    if (recallTime != null && YearUtil.isToday(recallTime)) {
                        return true;
                    }

                }

            }
        }
        return false;
    }

    @Override
    public BaseResponse invalidViolation(ViolationInvalidRequest request) {
        if (StringUtils.isBlank(request.getReason()) || request.getIds().isEmpty() || StringUtils.isBlank(request.getOperator())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        List<Violation> violList = violationService.selectBatchIds(request.getIds());
        if (CollectionUtils.isEmpty(violList)) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        Date date = new Date();
        List<Violation> tempList = new ArrayList<>();
        violList.forEach(violation -> {
            if (StatusEnum.VALID.getValue() == violation.getStatus()) {
                violation.setReason(request.getReason());
                violation.setModifyBy(request.getOperator());
                violation.setModifyTime(date);
                violation.setStatus(StatusEnum.HAND_INVALID.getValue());
                appealService.refuseAppealForViolationInvalid(violation.getId());
                tempList.add(violation);
                //分数回滚
                if (violation.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue())) {
                    this.licenseService.rollbackScore(violation, request.getReason());
                }
                //失效掉培训复训
                if (violation.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue())) {
                    RetrainInvalidRequest invalidRequest = new RetrainInvalidRequest();
                    invalidRequest.setOperator(request.getOperator());
                    invalidRequest.setReason(Constants.VIOLATION_INVALID);
                    invalidRequest.setViolationId(violation.getId());
                    invalidRequest.setType(RetrainTypeEnum.TRAINING_RETRAIN.getValue());
                    this.retrainService.invalidRetrainByCondition(invalidRequest);
                }
            }

        });
        if (!CollectionUtils.isEmpty(tempList)) {
            this.violationService.updateBatchById(tempList);
            this.invalidViolationMessage(tempList, request.getOperator());

        }
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse<?> payStatusChange(ViolationPayStatusRequest request) {
        String operator = request.getOperator();
        if (CollectionUtils.isEmpty(request.getIds()) || StringUtils.isBlank(operator)) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        int payStatus = request.getPayStatus();
        if (!this.checkParamPayStatus(payStatus)) {
            return R.result(ResponseCodeEnum.ERROR_PARAM);
        }
        List<Violation> violations = violationService.selectBatchIds(request.getIds());
        if (CollectionUtils.isEmpty(violations)) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        Date date = new Date();
        List<Violation> saveList = Lists.newArrayListWithExpectedSize(violations.size());
        for (Violation violation : violations) {
            if (!this.checkCurrentPayStatus(violation.getPayStatus())) {
                continue;
            }
            // check staff whether resigned.
            if (ViolationPayStatusEnum.DISMISSION_NO_PAY.getValue() == payStatus) {
                if (!staffApiManager.isResigned(violation.getStaffId())) {
                    continue;
                }
            }
            violation.setPayStatus(payStatus);
            violation.setModifyBy(operator);
            violation.setModifyTime(date);
            saveList.add(violation);
        }
        if (CollectionUtils.isEmpty(saveList)) {
            return R.result(ResponseCodeEnum.SUCCESS);
        }
        this.violationService.updateBatchById(saveList);
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    /**
     * parameter pay status must be salary_pay,timeout_pay,dismission_no_pay.
     */
    private boolean checkParamPayStatus(int payStatus) {
        return ViolationPayStatusEnum.SALARY_PAY.getValue() == payStatus ||
                ViolationPayStatusEnum.TIMEOUT_PAY.getValue() == payStatus ||
                ViolationPayStatusEnum.DISMISSION_NO_PAY.getValue() == payStatus;
    }

    /**
     * current pay status must be no_pay,salary_pay,timeout_pay,dismission_no_pay.
     */
    private boolean checkCurrentPayStatus(int payStatus) {
        return ViolationPayStatusEnum.NO_PAY.getValue() == payStatus ||
                this.checkParamPayStatus(payStatus);
    }

    /**
     * 失效短信信息,复训的短信单独处理
     */
    private void invalidViolationMessage(List<Violation> violations, String operator) {
        if (CollectionUtils.isEmpty(violations)) {
            return;
        }
        violations.forEach(violation -> {
            violation.setModifyBy(operator);
            this.violationMessageService.invalidViolationMessage(violation);
        });
    }

    @Override
    public BaseResponse remarkViolation(ViolationRemarkRequest request) {
        if (request.getId() == null || StringUtils.isBlank(request.getOperator()) || StringUtils.isBlank(request.getRemark())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        Violation v = this.violationService.selectById(request.getId());
        if (null == v) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        v.setRemark(request.getRemark());
        v.setModifyTime(new Date());
        v.setModifyBy(request.getOperator());
        this.violationService.updateById(v);
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse<List<String>> findAllDepartment() {
        List<String> list = this.violationService.findAllDepartment();
        return R.result(ResponseCodeEnum.SUCCESS, list);
    }

    @Override
    public BaseResponse approveWaitValidViolation(ViolationApprovalRequest request) {
        if (request == null || request.getId() == null || request.getStatus() == null || StringUtils.isBlank(request.getReason()) || StringUtils.isBlank(request.getOperator())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        if (!request.getStatus().equals(StatusEnum.VALID.getValue()) && !request.getStatus().equals(StatusEnum.HAND_INVALID.getValue())) {
            return R.result(ResponseCodeEnum.ERROR_PARAM);
        }
        Violation v = this.violationService.selectById(request.getId());
        if (null == v) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        if (!v.getStatus().equals(StatusEnum.WAIT_VALID.getValue())) {
            return R.result(ResponseCodeEnum.NO_WAIT_VALID_STATUS);
        }
        //重置申诉期限
        if (request.getStatus().equals(StatusEnum.VALID.getValue())) {
            Date deadLine = YearUtil.getNextDayZeroTime(new Date(), deadlineAmount);
            v.setDeadlineTime(deadLine);
            StringBuffer remark = new StringBuffer();
            String remarkStr = MessageFormat.format("[审批备注:{0}];", request.getReason());
            if (StringUtils.isNotBlank(v.getRemark())) {
                remark.append(v.getRemark()).append(remarkStr);
            } else {
                remark.append(remarkStr);
            }
            v.setRemark(remark.toString());
        }
        if (request.getStatus().equals(StatusEnum.HAND_INVALID.getValue())) {
            v.setReason(request.getReason());
        }
        v.setStatus(request.getStatus());
        v.setModifyTime(new Date());
        v.setModifyBy(request.getOperator());
        this.violationService.updateById(v);
        //员工违规的，生效后生成违规信息
        if (v.getDeductType().equals(DeductTypeEnum.STAFF_DEDUCT.getValue()) && v.getStatus().equals(StatusEnum.VALID.getValue())) {
            this.licenseService.doViolation(v);
            this.violationMessageService.saveViolationMessage(v);
            //给培训师发送，违规通知邮件
            this.violationMailService.sendViolationNotifyEmailToTrainTeacher(v);
        }
        //站点违规，生成复训
        if (v.getDeductType().equals(DeductTypeEnum.SITE_DEDUCT.getValue())) {
            String key = Constants.SITE_VIOLATION_ID + v.getId();
            List<Long> staffIds = new ArrayList<>();
            if (redisUtil.get(key) != null) {
                staffIds = (List<Long>) redisUtil.get(key);
                redisUtil.del(key);
            }
            if (!CollectionUtils.isEmpty(staffIds)) {
                for (Long staffId : staffIds) {
                    v.setStaffId(staffId);
                    Boolean isSiteRetrainRule = this.siteViolationService.isSiteRetrainRule(v);
                    if (isSiteRetrainRule) {
                        this.siteViolationService.createSiteRetrain(v);
                    }
                }
            }
        }
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse findStaffEntryInfo(Long staffId) {
        if (staffId == null) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        SearchEntryRecordRequest request = new SearchEntryRecordRequest();
        List<Long> staffIds = new ArrayList<>();
        staffIds.add(staffId);
        request.setStaffIds(staffIds);
        com.hk.simba.base.common.dto.response.BaseResponse<List<SearchEntryRecordEntryRecordDTO>> baseResponse = staffApi.searchEntryRecord(request);
        if (baseResponse.isSuccess() && !CollectionUtils.isEmpty(baseResponse.getData())) {
            List<SearchEntryRecordEntryRecordDTO> dtoList = baseResponse.getData();
            dtoList.sort(Comparator.comparing(SearchEntryRecordEntryRecordDTO::getEntryTime));
            SearchEntryRecordEntryRecordDTO dto = dtoList.get(0);
            return R.result(ResponseCodeEnum.SUCCESS, dto);
        }
        return R.result(ResponseCodeEnum.SUCCESS, new SearchEntryRecordEntryRecordDTO());
    }
}
