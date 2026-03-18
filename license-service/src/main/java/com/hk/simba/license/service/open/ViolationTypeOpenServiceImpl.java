package com.hk.simba.license.service.open;

import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.api.ViolationTypeOpenService;
import com.hk.simba.license.api.enums.PublishStatusEnum;
import com.hk.simba.license.api.enums.ReleaseTypeEnum;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeStatusRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeVersionPageRequest;
import com.hk.simba.license.api.vo.ViolationTypeVersionPageVO;
import com.hk.simba.license.api.vo.ViolationTypeVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.CommonStatusEnum;
import com.hk.simba.license.service.constant.enums.DeductTypeEnum;
import com.hk.simba.license.service.constant.enums.DepartmentTypeEnum;
import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.simba.license.service.entity.ViolationType;
import com.hk.simba.license.service.entity.ViolationTypeProcessMode;
import com.hk.simba.license.service.service.ViolationTypeProcessModeService;
import com.hk.simba.license.service.service.ViolationTypeService;
import com.hk.simba.license.service.utils.R;
import com.hk.simba.license.service.utils.RedisUtil;
import com.hk.simba.staff.open.enums.YesOrNotEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 羊皮
 * @description
 * @since 2020-4-10 10:09:01
 */
@Slf4j
@DubboService
public class ViolationTypeOpenServiceImpl implements ViolationTypeOpenService {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private ViolationTypeService violationTypeService;
    @Autowired
    private ViolationTypeProcessModeService violationTypeProcessModeService;

    @Override
    public void delViolationTypeCache() {
        redisUtil.del(Constants.VIOLATION_TYPE_CACHE);
        redisUtil.del(Constants.VIOLATION_TYPE_MAP_CACHE);
        redisUtil.del(Constants.VIOLATION_TYPE_MAP);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse saveViolationType(ViolationTypeRequest request) {
        log.info("saveViolationType,request：{}", request);
        if (!this.checkParam(request)) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        DeductTypeEnum deductTypeEnum = DeductTypeEnum.getEnumByValue(request.getDeductType());
        DepartmentTypeEnum departmentTypeEnum = DepartmentTypeEnum.getEnumByValue(request.getDepartmentType());
        if (deductTypeEnum == null || departmentTypeEnum == null) {
            return R.result(ResponseCodeEnum.ERROR_PARAM);
        }

        BaseResponse response;
        //id为空获取创建新版本是新增
        if (null == request.getId() || Boolean.TRUE.equals(request.getNewVersion())) {
            ViolationType lastVersion = null;
            if (null != request.getId()) {
                ViolationType violationType = violationTypeService.selectById(request.getId());
                lastVersion = this.violationTypeService.getLastVersion(violationType.getVersionId());
                request.setVersionId(violationType.getVersionId());
            }

            // 已存在待发布版本，不允许新增
            if (lastVersion != null && PublishStatusEnum.PENDING.getValue().equals(lastVersion.getPublishStatus())) {
                return R.result(ResponseCodeEnum.EXIST_PUBLISH_STATUS);
            }

            // 版本发布时间不能小于上一版本发布时间
            if (lastVersion != null && ReleaseTypeEnum.TIMING.getValue().equals(request.getReleaseType())
                && lastVersion.getEffectiveTime() != null && lastVersion.getEffectiveTime()
                                                                        .after(request.getEffectiveTime())) {
                return R.result(ResponseCodeEnum.EFFECTIVE_TIME_ERROR);
            }
            request.setVersion(
                this.newVersion(Optional.ofNullable(lastVersion).map(ViolationType::getVersion).orElse(null)));
            response = save(request);
        } else {
            response = update(request);
        }

        if (!response.isSuccess()) {
            return response;
        }

        // 删除缓存
        this.delViolationTypeCache();
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    private BaseResponse save(ViolationTypeRequest request) {
        log.info("saveViolationType.save,request：{}", request);
        //部门为全部的,code不能重复; 部门类型为其他的则部门+code须唯一
        // 责任部门+事件code+处理方式唯一性校验
        BaseResponse baseResponse = this.checkRepeat(request);
        if (!baseResponse.isSuccess()) {
            return baseResponse;
        }
        ViolationType v = new ViolationType();
        BeanUtils.copyProperties(request, v);

        if (ReleaseTypeEnum.IMMEDIATELY.getValue().equals(request.getReleaseType())) {
            v.setEffectiveTime(new Date());
            v.setPublishStatus(PublishStatusEnum.CURRENT.getValue());
            // 修改当前版本为历史版本
            this.updateCurrentToHistory(v, request.getOperator());
        } else {
            v.setPublishStatus(PublishStatusEnum.PENDING.getValue());
        }

        // 立即发布和id为空时设置为显示
        if (ReleaseTypeEnum.IMMEDIATELY.getValue().equals(request.getReleaseType()) || request.getId() == null) {
            v.setIsShow(YesOrNotEnum.YES.getValue());
        } else {
            v.setIsShow(YesOrNotEnum.NOT.getValue());
        }

        v.setStatus(YesOrNotEnum.YES.getValue());
        v.setCreateTime(new Date());
        v.setCreateBy(request.getOperator());
        this.violationTypeService.insert(v);
        // 版本id为空，设置版本id为记录id
        if (null == v.getVersionId()) {
            v.setVersionId(v.getId());
            violationTypeService.updateById(v);
        }

        // 处理方式为需要限制，新增处理方式
        if (YesOrNotEnum.YES.getValue().equals(request.getProcessMode()) && !CollectionUtils.isEmpty(
            request.getProcessModeNameList())) {
            List<ViolationTypeProcessMode> processModeList = request.getProcessModeNameList().stream().map(e -> {
                ViolationTypeProcessMode processMode = new ViolationTypeProcessMode();
                processMode.setName(e);
                processMode.setViolationTypeId(v.getId());
                processMode.setCreateBy(request.getOperator());
                processMode.setCreateTime(v.getCreateTime());
                return processMode;
            }).collect(Collectors.toList());
            violationTypeProcessModeService.insertBatch(processModeList);
        }
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    /**
     * 修改当前版本为历史版本
     * @param v
     * @param operator
     */
    private void updateCurrentToHistory(ViolationType v, String operator) {
        ViolationTypeVersionPageRequest versionPageRequest = new ViolationTypeVersionPageRequest();
        versionPageRequest.setVersionId(v.getVersionId());
        versionPageRequest.setIsShow(YesOrNotEnum.YES.getValue());
        List<ViolationTypeVersionPageVO> versionList = violationTypeService.findVersionList(versionPageRequest);
        for (ViolationTypeVersionPageVO vo : versionList) {
            ViolationTypeStatusRequest statusRequest = new ViolationTypeStatusRequest();
            statusRequest.setId(vo.getId());
            if (!PublishStatusEnum.HISTORY.getValue().equals(vo.getPublishStatus())) {
                statusRequest.setPublishStatus(PublishStatusEnum.HISTORY.getValue());
                statusRequest.setFailureTime(v.getEffectiveTime());
            }
            statusRequest.setIsShow(YesOrNotEnum.NOT.getValue());
            statusRequest.setModifyBy(operator);
            violationTypeService.updatePublishStatus(statusRequest);
        }
    }

    private BaseResponse update(ViolationTypeRequest request) {
        log.info("saveViolationType.update,request：{}", request);
        ViolationType violationType = this.violationTypeService.selectById(request.getId());
        if (violationType == null) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }

        // 待发布版本可以修改发布时间
        if (PublishStatusEnum.PENDING.getValue().equals(violationType.getPublishStatus())) {
            violationType.setEffectiveTime(new Date());
            violationType.setReleaseType(request.getReleaseType());
        }
        violationType.setName(request.getName());
        violationType.setDescription(request.getDescription());
        violationType.setDetail(request.getDetail());
        violationType.setType(request.getType());
        violationType.setScore(request.getScore());
        violationType.setFee(request.getFee());
        violationType.setTitle(request.getTitle());
        violationType.setRemark(request.getRemark());
        violationType.setModifyBy(request.getOperator());
        violationType.setModifyTime(new Date());
        this.violationTypeService.updateById(violationType);

        // 待发布版本，修改发布方式为立即发布，修改原当前版本为历史版本
        if (PublishStatusEnum.PENDING.getValue().equals(violationType.getPublishStatus())
            && ReleaseTypeEnum.IMMEDIATELY.getValue().equals(request.getReleaseType())) {
            ViolationTypeStatusRequest statusRequest = new ViolationTypeStatusRequest();
            statusRequest.setId(violationType.getId());
            statusRequest.setVersionId(violationType.getVersionId());
            statusRequest.setPublishStatus(PublishStatusEnum.CURRENT.getValue());
            statusRequest.setFailureTime(violationType.getEffectiveTime());
            statusRequest.setModifyBy(request.getOperator());
            violationTypeService.effectiveViolationType(statusRequest);
        }
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    private String newVersion(String version) {
        if (StringUtils.isEmpty(version)) {
            return "1.0";
        }
        return version.substring(0, version.indexOf(".")) + "." + (
            Integer.parseInt(version.substring(version.indexOf(".") + 1)) + 1);
    }

    /**
     * 参数校验
     */
    private Boolean checkParam(ViolationTypeRequest request) {
        if (request == null || StringUtils.isBlank(request.getOperator()) || StringUtils.isBlank(request.getCode())
            || StringUtils.isBlank(request.getName()) || StringUtils.isBlank(request.getDetail())
            || request.getEventType() == null || request.getScore() == null || StringUtils.isBlank(request.getType())
            || request.getFee() == null || request.getDepartmentType() == null) {
            return false;
        }
        // 如果限制处理方式，则处理方式列表不能为空
        if (YesOrNotEnum.YES.getValue().equals(request.getProcessMode()) && CollectionUtils.isEmpty(
            request.getProcessModeNameList())) {
            return false;
        }
        return true;

    }

    @Override
    public BaseResponse changeViolationTypeStatus(ViolationTypeRequest request) {
        if (null == request || request.getId() == null || StringUtils.isBlank(request.getOperator())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        ViolationType type = this.violationTypeService.selectById(request.getId());
        if (type == null) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        if (type.getStatus().equals(CommonStatusEnum.VALID.getValue())) {
            type.setStatus(CommonStatusEnum.INVALID.getValue());
        } else {
            type.setStatus(CommonStatusEnum.VALID.getValue());
        }
        type.setModifyBy(request.getOperator());
        type.setModifyTime(new Date());
        this.violationTypeService.updateById(type);
        //更新缓存
        this.delViolationTypeCache();
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse<PageResult<ViolationTypeVO>> page(PageRequest page, ViolationTypeRequest request) {
        Page<ViolationType> typePage = new Page<>(page.getPageNo(), page.getPageSize());
        List<ViolationTypeVO> voList = this.violationTypeService.getPageList(typePage, request);
        if (CollectionUtils.isEmpty(voList)) {
            return R.result(ResponseCodeEnum.SUCCESS, new PageResult<>());
        }
        PageResult<ViolationTypeVO> pageResult = new PageResult<>();
        pageResult.setResults(voList);
        pageResult.setCount(typePage.getTotal());
        pageResult.setPageNo(typePage.getCurrent());
        pageResult.setPageSize(typePage.getSize());
        pageResult.setTotalPage(typePage.getPages());
        return R.result(ResponseCodeEnum.SUCCESS, pageResult);
    }

    @Override
    public BaseResponse<List<String>> findAllType() {
        List<String> typeList = this.violationTypeService.findAllType();
        return R.result(ResponseCodeEnum.SUCCESS, typeList);
    }

    @Override
    public BaseResponse<List<String>> findAllDetail() {
        List<String> detailList = this.violationTypeService.findAllDetail();
        return R.result(ResponseCodeEnum.SUCCESS, detailList);
    }

    @Override
    public BaseResponse<ViolationTypeVO> findDetailById(Long id) {
        if (null == id) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        ViolationType vt = this.violationTypeService.selectById(id);
        ViolationTypeVO vo = new ViolationTypeVO();
        BeanUtils.copyProperties(vt, vo);
        // 处理方式为1=限制，查询处理方式名称
        if (YesOrNotEnum.YES.getValue().equals(vo.getProcessMode())) {
            List<ViolationTypeProcessMode> processModes =
                violationTypeProcessModeService.findListByViolationTypeIdList(Lists.newArrayList(vo.getId()));
            vo.setProcessModeNameList(
                processModes.stream().map(ViolationTypeProcessMode::getName).collect(Collectors.toList()));
        }
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    @Override
    public BaseResponse<List<String>> findDetailByType(String type) {
        if (StringUtils.isBlank(type)) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        return R.result(ResponseCodeEnum.SUCCESS, this.violationTypeService.findDetailByType(type));
    }

    @Override
    public BaseResponse<List<String>> findByDeductType(Integer deductType) {
        if (null == deductType) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        List<String> list = this.violationTypeService.findByDeductType(deductType);
        return R.result(ResponseCodeEnum.SUCCESS, list);
    }

    private BaseResponse checkRepeat(ViolationTypeRequest request) {
        // 先查询相同事件编码的数据，再校验其他参数
        ViolationType checkParams = new ViolationType();
        checkParams.setCode(request.getCode());
        List<ViolationType> repeatList =
            violationTypeService.getViolationTypeListByParams(checkParams, request.getId(), request.getVersionId());
        if (CollectionUtils.isEmpty(repeatList)) {
            return R.result(ResponseCodeEnum.SUCCESS);
        }

        log.info("checkRepeat,request:{},repeatList:{}", request, repeatList);
        // 取出匹配时间区间的数据
        Date date = ReleaseTypeEnum.IMMEDIATELY.getValue().equals(request.getReleaseType()) ? new Date() :
            request.getEffectiveTime();
        repeatList = repeatList.stream()
                               .filter(t -> t.getFailureTime() == null || t.getFailureTime().after(date))
                               .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(repeatList)) {
            return R.result(ResponseCodeEnum.SUCCESS);
        }

        // 责任部门为全部，且处理方式为不限制
        if (DepartmentTypeEnum.ALL.getValue().equals(request.getDepartmentType()) && YesOrNotEnum.NOT.getValue()
            .equals(request.getProcessMode())) {
            log.warn("checkRepeat.checkAll,repeatList:{},request:{}", repeatList, request);
            return R.result(ResponseCodeEnum.REPEAT_VIOLATION_CODE);
        }

        // 查询重复事件编码的处理方式
        List<ViolationTypeProcessMode> processModeList =
            violationTypeProcessModeService.findListByViolationTypeIdList(
                repeatList.stream().map(ViolationType::getId).collect(Collectors.toList()));
        Map<Long, List<ViolationTypeProcessMode>> processModeMap =
            processModeList.stream().collect(Collectors.groupingBy(ViolationTypeProcessMode::getViolationTypeId));

        for (ViolationType type : repeatList) {
            List<String> processModeNames =
                Optional.ofNullable(processModeMap.get(type.getId())).orElse(Collections.emptyList()).stream()
                    .map(ViolationTypeProcessMode::getName).collect(Collectors.toList());

            // 责任部门为全部，或者责任部门相同
            if (DepartmentTypeEnum.ALL.getValue().equals(request.getDepartmentType()) || type.getDepartmentType()
                .equals(request.getDepartmentType()) || DepartmentTypeEnum.ALL.getValue().equals(type.getDepartmentType())) {
                // 存在处理方式为不限制
                if (YesOrNotEnum.NOT.getValue().equals(type.getProcessMode())) {
                    log.warn("checkRepeat.checkProcessMode,repeatList:{},request:{}", repeatList, request);
                    return R.result(ResponseCodeEnum.REPEAT_VIOLATION_CODE);
                }

                if (CollectionUtils.isNotEmpty(request.getProcessModeNameList()) && request.getProcessModeNameList()
                    .stream().anyMatch(processModeNames::contains)) {
                    // 存在相同的处理方式
                    log.warn("checkRepeat.checkProcessModeName,repeatList:{},request:{}", repeatList, request);
                    return R.result(ResponseCodeEnum.REPEAT_VIOLATION_CODE);
                }
            }
        }
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse<PageResult<ViolationTypeVersionPageVO>> findVersionPage(
        ViolationTypeVersionPageRequest request) {
        PageResult<ViolationTypeVersionPageVO> pageResult = violationTypeService.findVersionPage(request);
        if (pageResult == null) {
            return R.result(ResponseCodeEnum.SUCCESS, new PageResult<>());
        }
        for (ViolationTypeVersionPageVO result : pageResult.getResults()) {
            result.setVersion("v"+result.getVersion());
        }
        return R.result(ResponseCodeEnum.SUCCESS, pageResult);
    }

    @Override
    public BaseResponse delete(ViolationTypeStatusRequest request) {
        ViolationType violationType = violationTypeService.selectById(request.getId());
        if (violationType == null) {
            return R.result(ResponseCodeEnum.ERROR_DATA_NOT_EXISTS);
        }

        if (!PublishStatusEnum.PENDING.getValue().equals(violationType.getPublishStatus())) {
            return R.result(ResponseCodeEnum.CAN_NOT_DELETE_TYPE);
        }
        violationTypeService.delete(request);
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse stop(ViolationTypeStatusRequest request) {
        ViolationType violationType = violationTypeService.selectById(request.getId());
        if (violationType == null) {
            return R.result(ResponseCodeEnum.ERROR_DATA_NOT_EXISTS);
        }
        if (!PublishStatusEnum.CURRENT.getValue().equals(violationType.getPublishStatus())) {
            return R.result(ResponseCodeEnum.CAN_NOT_STOP_TYPE);
        }

        ViolationTypeVersionPageRequest versionPageRequest = new ViolationTypeVersionPageRequest();
        versionPageRequest.setVersionId(violationType.getVersionId());
        versionPageRequest.setPublishStatus(PublishStatusEnum.PENDING.getValue());
        List<ViolationTypeVersionPageVO> versionList = violationTypeService.findVersionList(versionPageRequest);
        if (CollectionUtils.isNotEmpty(versionList)) {
            request.setIsShow(YesOrNotEnum.NOT.getValue());
            versionList.sort(Comparator.comparing(ViolationTypeVersionPageVO::getEffectiveTime,
                Comparator.nullsLast(Comparator.naturalOrder())));
            ViolationTypeVersionPageVO vo = versionList.get(0);
            ViolationTypeStatusRequest pendingRequest = new ViolationTypeStatusRequest();
            pendingRequest.setId(vo.getId());
            pendingRequest.setPublishStatus(PublishStatusEnum.PENDING.getValue());
            pendingRequest.setModifyBy(Constants.SYS);
            pendingRequest.setModifyTime(new Date());
            pendingRequest.setIsShow(YesOrNotEnum.YES.getValue());
            violationTypeService.updatePublishStatus(pendingRequest);
        }
        request.setPublishStatus(PublishStatusEnum.HISTORY.getValue());
        request.setFailureTime(new Date());
        violationTypeService.updatePublishStatus(request);
        return R.result(ResponseCodeEnum.SUCCESS);
    }
}
