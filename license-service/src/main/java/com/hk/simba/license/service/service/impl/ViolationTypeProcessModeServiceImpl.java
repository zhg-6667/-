package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hk.base.util.modules.BeanMapper;
import com.hk.simba.license.service.entity.ViolationTypeProcessMode;
import com.hk.simba.license.service.mapper.ViolationTypeProcessModeMapper;
import com.hk.simba.license.service.service.ViolationTypeProcessModeService;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 违规类型处理方式 服务实现类
 * </p>
 *
 * @author chenm
 * @since 2022-04-13
 */
@Service
public class ViolationTypeProcessModeServiceImpl
    extends ServiceImpl<ViolationTypeProcessModeMapper, ViolationTypeProcessMode>
    implements ViolationTypeProcessModeService {

    @Override
    public void saveOrUpdate(Long violationTypeId, List<String> processModeNameList, String operator) {
        Wrapper<ViolationTypeProcessMode> wrapper = new EntityWrapper<>();
        wrapper.eq("violation_type_id", violationTypeId);
        List<ViolationTypeProcessMode> oldProcessModeList = this.selectList(wrapper);
        Map<String, ViolationTypeProcessMode> processModeMap = oldProcessModeList.stream()
            .collect(Collectors.toMap(ViolationTypeProcessMode::getName, Function.identity(), (k1, k2) -> k1));
        Date date = new Date();
        List<ViolationTypeProcessMode> saveList = processModeNameList.stream().map(e -> {
            ViolationTypeProcessMode processMode;
            ViolationTypeProcessMode oldProcessMode = processModeMap.get(e);
            if (oldProcessMode != null) {
                processMode = BeanMapper.map(oldProcessMode, ViolationTypeProcessMode.class);
                processMode.setModifyTime(date);
                processMode.setModifyBy(operator);
            } else {
                processMode = new ViolationTypeProcessMode();
                processMode.setViolationTypeId(violationTypeId);
                processMode.setName(e);
                processMode.setCreateTime(date);
                processMode.setCreateBy(operator);
            }
            return processMode;
        }).collect(Collectors.toList());
        // 更新处理方式
        if (CollectionUtils.isNotEmpty(saveList)) {
            this.insertOrUpdateBatch(saveList);
        }

        // 删除处理方式
        List<Long> removeIds = oldProcessModeList.stream().filter(e -> !processModeNameList.contains(e.getName()))
            .map(ViolationTypeProcessMode::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(removeIds)) {
            this.deleteBatchIds(removeIds);
        }
    }

    @Override
    public List<ViolationTypeProcessMode> findListByViolationTypeIdList(List<Long> violationTypeIdList) {
        Wrapper<ViolationTypeProcessMode> wrapper = new EntityWrapper<>();
        wrapper.in("violation_type_id", violationTypeIdList);
        return this.selectList(wrapper);
    }

    @Override
    public Map<Long, List<ViolationTypeProcessMode>> getMapByViolationTypeIdList(List<Long> violationTypeIdList) {
        List<ViolationTypeProcessMode> list = findListByViolationTypeIdList(violationTypeIdList);
        return list.stream().collect(Collectors.groupingBy(ViolationTypeProcessMode::getViolationTypeId));
    }
}
