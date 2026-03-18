package com.hk.simba.license.service.task;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.hk.base.util.DateUtils;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.LicenseStatusEnum;
import com.hk.simba.license.service.entity.License;
import com.hk.simba.license.service.entity.LicenseScoreLog;
import com.hk.simba.license.service.service.LicenseScoreLogService;
import com.hk.simba.license.service.service.LicenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;

@Service
@Slf4j
public class LicenseMessageTaskHandler {
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private LicenseScoreLogService licenseScoreLogService;
    private Config messageConfig = ConfigService.getConfig(MessageConstant.MESSAGE_PROPERTIES_KEY);

    /**
     * 更新即将过期的执照信息
     */
    public void refreshExpireLicense() {
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            try {
                Page<License> page = this.getExpirePage(pageNo, pageSize);
                if (!CollectionUtils.isEmpty(page.getRecords())) {
                    currentSize = page.getRecords().size();
                    for (License tempLicense : page.getRecords()) {
                        tempLicense.setExpireTime(DateUtils.add(tempLicense.getExpireTime(), Calendar.YEAR, 1));
                        tempLicense.setModifyBy(Constants.SYS);
                        tempLicense.setModifyTime(new Date());
                        tempLicense.setRemainScore(Constants.SCORE);
                        this.licenseService.updateById(tempLicense);

                        // 分数流水
                        LicenseScoreLog licenseScoreLog = new LicenseScoreLog();
                        licenseScoreLog.setLicenseId(tempLicense.getId());
                        licenseScoreLog.setStaffId(tempLicense.getStaffId());
                        licenseScoreLog.setDeductScore(Constants.SCORE);
                        licenseScoreLog.setReason(Constants.SCORE_RESET);
                        licenseScoreLog.setCreateBy(Constants.SYS);
                        licenseScoreLog.setCreateTime(new Date());
                        licenseScoreLogService.insert(licenseScoreLog);
                    }
                }
            } catch (Exception e) {
                log.error("定时更新执照过期信息错误 , e={}", e.getMessage());
            }
        } while (currentSize == pageSize);
    }

    private Page<License> getExpirePage(int pageNo, int pageSize) {
        Page<License> page = new Page<>(pageNo, pageSize);
        License license = new License();
        license.setStatus(LicenseStatusEnum.EFFECTIVE.getCode());
        Wrapper<License> wrapper = new EntityWrapper<>(license).lt("expire_time", new Date());
        page = licenseService.selectPage(page, wrapper);
        return page;
    }

}
