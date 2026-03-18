package com.hk.simba.license.service.service;

import com.baomidou.mybatisplus.service.IService;
import com.hk.simba.license.service.entity.LicenseStopOrderLog;

import java.util.List;

/**
 * 执照风控停单记录服务类
 *
 * @author chenjh1
 * @date 2024-03-12 15:43
 **/
public interface LicenseStopOrderLogService extends IService<LicenseStopOrderLog> {

    /**
     * 获取生效的风控停单列表
     *
     * @param staffId
     * @return List<LicenseStopOrderLog>
     */
    List<LicenseStopOrderLog> getValidListByStaffId(Long staffId);

    /**
     * 初始化并保存停单记录
     *
     * @param staffId
     * @param licenseId
     * @param reason
     * @return List<LicenseStopOrderLog>
     */
    LicenseStopOrderLog initSave(Long staffId, Long licenseId, String reason);
}
