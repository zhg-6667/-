package com.hk.simba.license.service.service;

import com.hk.simba.license.service.entity.ViolationTypeProcessMode;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 违规类型处理方式 服务类
 * </p>
 *
 * @author chenm
 * @since 2022-04-13
 */
public interface ViolationTypeProcessModeService extends IService<ViolationTypeProcessMode> {

    void saveOrUpdate(Long violationTypeId, List<String> processModeNameList, String operator);

    List<ViolationTypeProcessMode> findListByViolationTypeIdList(List<Long> violationTypeIdList);

    Map<Long, List<ViolationTypeProcessMode>> getMapByViolationTypeIdList(List<Long> violationTypeIdList);
}
