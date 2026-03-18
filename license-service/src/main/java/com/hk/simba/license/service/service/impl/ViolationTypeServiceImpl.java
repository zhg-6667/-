package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hk.simba.base.json.JsonUtils;
import com.hk.simba.license.api.request.violation.ViolationTypeRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeStatusRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeVersionPageRequest;
import com.hk.simba.license.api.vo.ViolationTypeVO;
import com.hk.simba.license.api.vo.ViolationTypeVersionPageVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.CommonStatusEnum;
import com.hk.simba.license.service.constant.enums.DepartmentTypeEnum;
import com.hk.simba.license.service.entity.ViolationType;
import com.hk.simba.license.service.entity.ViolationTypeProcessMode;
import com.hk.simba.license.service.mapper.ViolationTypeMapper;
import com.hk.simba.license.service.mq.dto.MessageEntity;
import com.hk.simba.license.service.service.ViolationTypeProcessModeService;
import com.hk.simba.license.service.service.ViolationTypeService;
import com.hk.simba.license.service.utils.RedisUtil;
import com.hk.simba.license.service.utils.YearUtil;
import com.hk.simba.staff.open.enums.YesOrNotEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 违规类型 服务实现类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Service
@Slf4j
public class ViolationTypeServiceImpl extends ServiceImpl<ViolationTypeMapper, ViolationType>
    implements ViolationTypeService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    private ViolationTypeMapper violationTypeMapper;
    @Autowired
    private ViolationTypeProcessModeService violationTypeProcessModeService;

    @Override
    public ViolationType getViolationTypeByEvent(MessageEntity entity) {
        ViolationType violationType = null;
        Map<String, ViolationType> violationTypeMap = getViolationTypeMap();
        if (violationTypeMap != null) {
            violationType = violationTypeMap.get("" + entity.getThirdConfigResultId());
            if (violationType == null) {
                violationType = violationTypeMap.get("" + entity.getSecondConfigResultId());
                if (violationType == null) {
                    violationType = violationTypeMap.get("" + entity.getConfigResultId());
                }
            }
        }
        return violationType;
    }

    public Map<String, ViolationType> getViolationTypeMap() {
        Map<String, ViolationType> violationTypeMap = null;
        violationTypeMap = (Map<String, ViolationType>)redisUtil.get(Constants.VIOLATION_TYPE_CACHE);
        if (violationTypeMap == null) {
            violationTypeMap = Maps.newHashMap();
            List<ViolationType> violationTypeList =
                this.selectList(new EntityWrapper<ViolationType>().eq("status", CommonStatusEnum.VALID.getValue()));

            if (!CollectionUtils.isEmpty(violationTypeList)) {
                for (ViolationType v : violationTypeList) {
                    violationTypeMap.put(v.getCode(), v);
                }
            }
            redisUtil.set(Constants.VIOLATION_TYPE_CACHE, violationTypeMap, 3600L);
        }
        return violationTypeMap;
    }

    @Override
    public ViolationType getViolationTypeByCode(String code) {
        List<ViolationType> violationTypeList = this.selectList(new EntityWrapper<ViolationType>().eq("code", code));
        if (!CollectionUtils.isEmpty(violationTypeList)) {
            return violationTypeList.get(0);
        }
        return null;
    }

    @Override
    public List<ViolationTypeVO> getPageList(Page<ViolationType> page, ViolationTypeRequest request) {
        return violationTypeMapper.getPageList(page, request);
    }

    @Override
    public List<String> findAllType() {
        return violationTypeMapper.findAllType();
    }

    @Override
    public List<String> findAllDetail() {
        return violationTypeMapper.findAllDetail();
    }

    @Override
    public List<String> findDetailByType(String type) {
        return this.violationTypeMapper.findDetailByType(type);
    }

    @Override
    public List<String> findByDeductType(Integer deductType) {
        return this.baseMapper.findByDeductType(deductType);
    }

    @Override
    public ViolationType getViolationTypeByCodeAndDeptType(String code, Integer departmentType) {
        ViolationType violationType = new ViolationType();
        violationType.setCode(code);
        violationType.setDepartmentType(departmentType);
        Wrapper<ViolationType> wrapper = new EntityWrapper<>(violationType).orderBy("create_time", false);
        List<ViolationType> violationTypeList = this.selectList(wrapper);
        if (!CollectionUtils.isEmpty(violationTypeList)) {
            return violationTypeList.get(0);
        }
        return null;
    }

    /**
     * 查询违规类型
     *
     * @param violationType   查询参数
     * @param ignoreId        忽略类型id
     * @param ignoreVersionId
     * @return
     */
    @Override
    public List<ViolationType> getViolationTypeListByParams(ViolationType violationType, Long ignoreId,
        Long ignoreVersionId) {
        Wrapper<ViolationType> wrapper = new EntityWrapper<>(violationType).orderBy("create_time", false);
        Optional.ofNullable(ignoreId).ifPresent(e -> wrapper.ne("id", ignoreId));
        Optional.ofNullable(ignoreVersionId).ifPresent(e -> wrapper.ne("version_id", ignoreVersionId));
        wrapper.eq("deleted", YesOrNotEnum.NOT.getValue());
        return this.selectList(wrapper);
    }

    @Override
    public ViolationType getViolationTypeByEvent(MessageEntity entity, Integer departmentType) {
        Map<String, List<ViolationType>> map = this.getViolationTypeListMapByDate(
            Optional.ofNullable(entity.getServiceTime()).orElse(entity.getCreateTime()));
        List<ViolationType> violationTypeList =
            Optional.ofNullable(entity.getThirdConfigResultId()).filter(third -> map.containsKey(third.toString()))
                .map(third -> map.get(third.toString())).orElseGet(
                    () -> Optional.ofNullable(entity.getSecondConfigResultId())
                        .filter(second -> map.containsKey(second.toString())).map(second -> map.get(second.toString()))
                        .orElseGet(() -> Optional.ofNullable(entity.getConfigResultId())
                            .filter(first -> map.containsKey(first.toString())).map(first -> map.get(first.toString()))
                            .orElse(null)));
        if (CollectionUtils.isEmpty(violationTypeList)) {
            log.info("getViolationTypeByEvent.code.NotEq,entity:{},departmentType:{}", entity, departmentType);
            return null;
        }

        // 责任部门匹配，过滤不符合条件的数据
        violationTypeList.removeIf(e -> !e.getDepartmentType().equals(departmentType));
        if (CollectionUtils.isEmpty(violationTypeList)) {
            log.info("getViolationTypeByEvent.dept.NotEq,entity:{},departmentType:{}", entity, departmentType);
            return null;
        }

        // 如果事件处理方式为空，直接返回第一条
        if (CollectionUtils.isEmpty(entity.getProcessList())) {
            log.info("getViolationTypeByEvent.event.process.isEmpty,entity:{},departmentType:{},violationTypeList:{}", entity,
                departmentType, JsonUtils.toJson(violationTypeList));
            return violationTypeList.get(0);
        }

        // 处理方式匹配
        List<Long> ids = violationTypeList.stream().map(ViolationType::getId).collect(Collectors.toList());
        Map<Long, List<ViolationTypeProcessMode>> processModeMap =
            violationTypeProcessModeService.getMapByViolationTypeIdList(ids);
        for (ViolationType type : violationTypeList) {
            // 处理方式为不限制，匹配成功，直接返回该配置
            if (YesOrNotEnum.NOT.getValue().equals(type.getProcessMode())) {
                return type;
            }

            // 处理方式为为限制，匹配处理方式名称，任意一个名称匹配则返回改配置
            List<ViolationTypeProcessMode> processModes = processModeMap.get(type.getId());
            if (CollectionUtils.isNotEmpty(processModes) && processModes.stream().map(ViolationTypeProcessMode::getName)
                .anyMatch(e -> entity.getProcessList().contains(e))) {
                return type;
            }
        }

        log.info("getViolationTypeByEvent.event.process.notEq,entity:{},departmentType:{}", entity,
            departmentType);
        return null;
    }

    private Map<String, List<ViolationType>> getViolationTypeListMap() {
        Map<String, List<ViolationType>> map =
            (Map<String, List<ViolationType>>)redisUtil.get(Constants.VIOLATION_TYPE_MAP);
        if (map != null) {
            return map;
        }

        List<ViolationType> violationTypeList = this.selectList(
            new EntityWrapper<ViolationType>().eq("status", CommonStatusEnum.VALID.getValue())
                                              .eq("deleted", YesOrNotEnum.NOT.getValue()));
        if (CollectionUtils.isNotEmpty(violationTypeList)) {
            map = violationTypeList.stream().collect(Collectors.groupingBy(ViolationType::getCode));
            redisUtil.set(Constants.VIOLATION_TYPE_MAP, map, 3600L);
        }
        return map;
    }

    private Map<String, List<ViolationType>> getViolationTypeListMapByDate(Date date) {
        Map<String, List<ViolationType>> map = this.getViolationTypeListMap();
        Map<String, List<ViolationType>> resultMap = new HashMap<>();
        if (map == null) {
            return resultMap;
        }
        map.forEach((k, v) -> {
            List<ViolationType> list = v.stream()
                                        .filter(e -> YearUtil.isBetween(date, e.getEffectiveTime(), e.getFailureTime()))
                                        .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list)) {
                resultMap.put(k, list);
            }
        });
        return resultMap;
    }

    @Override
    public ViolationType getViolationTypeByEventCodeAndDept(MessageEntity entity, Integer departmentType) {
        //先用对应的部门类型查询违规，不存在则查询部门为全部的违规是否存在
        ViolationType type = null;
        if (DepartmentTypeEnum.SERVICE_DEPT.getValue().equals(departmentType)) {
            type = this.getViolationTypeByEvent(entity, departmentType);
            if (type == null) {
                type = this.getViolationTypeByEvent(entity, DepartmentTypeEnum.ALL.getValue());
            }
        }
        if (DepartmentTypeEnum.NO_SERVICE_DEPT.getValue().equals(departmentType)) {
            type = this.getViolationTypeByEvent(entity, departmentType);
            if (type == null) {
                type = this.getViolationTypeByEvent(entity, DepartmentTypeEnum.ALL.getValue());
            }
        }
        return type;
    }

    @Override
    public PageResult<ViolationTypeVersionPageVO> findVersionPage(ViolationTypeVersionPageRequest request) {
        Page<ViolationType> page = new Page<>(request.getPageNo(), request.getPageSize());
        List<ViolationTypeVersionPageVO> list = this.violationTypeMapper.findVersionPage(page, request);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        PageResult<ViolationTypeVersionPageVO> pageResult = new PageResult<>();
        pageResult.setResults(list);
        pageResult.setCount(page.getTotal());
        pageResult.setPageNo(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setTotalPage(page.getPages());
        return pageResult;
    }

    @Override
    public List<ViolationTypeVersionPageVO> findVersionList(ViolationTypeVersionPageRequest request) {
        return this.violationTypeMapper.findVersionList(request);
    }

    @Override
    public void delete(ViolationTypeStatusRequest request) {
        violationTypeMapper.deleteViolationType(request.getId(), request.getModifyBy(), new Date());
    }

    @Override
    public void updatePublishStatus(ViolationTypeStatusRequest request) {
        request.setModifyTime(new Date());
        violationTypeMapper.updatePublishStatus(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void effectiveViolationType(ViolationTypeStatusRequest request) {
        // 失效旧的违规类型
        violationTypeMapper.invalidViolationType(request);
        // 生效违规类型
        request.setFailureTime(null);
        request.setModifyTime(new Date());
        request.setIsShow(YesOrNotEnum.YES.getValue());
        violationTypeMapper.updatePublishStatus(request);
    }

    @Override
    public ViolationType getLastVersion(Long versionId) {
        Wrapper<ViolationType> wrapper = new EntityWrapper<>();
        wrapper.eq("version_id", versionId);
        wrapper.eq("deleted", YesOrNotEnum.NOT.getValue());
        wrapper.last(" order by id desc limit 1");
        return this.selectOne(wrapper);
    }

}
