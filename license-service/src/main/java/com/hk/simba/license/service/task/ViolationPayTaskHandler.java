package com.hk.simba.license.service.task;

import com.baomidou.mybatisplus.plugins.Page;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.hk.simba.license.api.enums.ViolationPayStatusEnum;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.service.ViolationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @ClassName ViolationMessageTaskHandler
 * @Description LTS 违规信息调度处理
 * @Author chenjh1@homeking365.com
 * @Date 2020-04-17 0:56
 * @Version 1.0
 **/
@Service
@Slf4j
public class ViolationPayTaskHandler {

    /**
     * 超时未缴
     */
    @Value("${violation.timeout.pay}")
    private int timeoutPay;
    @Autowired
    ViolationService violationService;

    private Config messageConfig = ConfigService.getConfig(MessageConstant.MESSAGE_PROPERTIES_KEY);

    public void timeoutPayChangeStatus() {
        log.info("timeoutPayChangeStatus");
        int pageSize = messageConfig.getIntProperty(MessageConstant.PAGE_SIZE_KEY, MessageConstant.PAGE_SIZE);
        int pageNo = 0; //每次都取第一页
        int currentSize = 0;
        do {
            Page<Violation> page = new Page<>(pageNo, pageSize);
            List<Violation> list = violationService.pageTimoutPayList(page, timeoutPay);
            if (!CollectionUtils.isEmpty(list)) {
                currentSize = list.size();
                for (Violation violation : list) {
                    violation.setPayStatus(ViolationPayStatusEnum.TIMEOUT_PAY.getValue());
                    violation.setModifyTime(new Date());
                    violation.setModifyBy(Constants.SYS);
                    violationService.updateById(violation);
                }
            }
        } while (currentSize == pageSize);
    }
}
