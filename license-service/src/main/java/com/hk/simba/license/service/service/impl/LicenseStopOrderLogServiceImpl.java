package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.ValidEnum;
import com.hk.simba.license.service.entity.LicenseStopOrderLog;
import com.hk.simba.license.service.mapper.LicenseStopOrderLogMapper;
import com.hk.simba.license.service.service.LicenseStopOrderLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 执照风控停单记录服务实现类
 *
 * @author chenjh1
 * @date 2024-03-12 15:44
 **/
@Service
public class LicenseStopOrderLogServiceImpl extends ServiceImpl<LicenseStopOrderLogMapper, LicenseStopOrderLog> implements
    LicenseStopOrderLogService {

    @Override
    public List<LicenseStopOrderLog> getValidListByStaffId(Long staffId) {
        EntityWrapper<LicenseStopOrderLog> wrapper = new EntityWrapper();
        wrapper.eq("staff_id", staffId).eq("stop_order_status", ValidEnum.VALID.getValue());
        return selectList(wrapper);
    }

    @Override
    public LicenseStopOrderLog initSave(Long staffId, Long licenseId, String reason) {
        LicenseStopOrderLog stopOrderLog = new LicenseStopOrderLog();
        // 执行停单
        long plusDay = 1;
        if (Constants.ZERO_DESCRIPTION.equals(reason)) {
            //分数扣为0分，预留7天申述
            plusDay = 7;
        }
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now().plusDays(plusDay), LocalTime.MIN);
        stopOrderLog.setStaffId(staffId);
        stopOrderLog.setLicenseId(licenseId);
        stopOrderLog.setReason(reason);
        stopOrderLog.setStopOrderStatus(ValidEnum.VALID.getValue());
        stopOrderLog.setStopOrderBeginTime(startTime);
        stopOrderLog.setCreateBy(Constants.SYS);
        stopOrderLog.setCreateTime(LocalDateTime.now());
        insert(stopOrderLog);
        return stopOrderLog;
    }
}
