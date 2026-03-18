package com.hk.simba.license.service.mq;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.hk.simba.license.api.vo.LicenseVO;
import com.hk.simba.license.service.constant.enums.PositionTypeEnum;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.simba.staff.StaffMoveEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author cyh
 * @date 2020/12/17/13:54
 * 员工异动变更
 */
@Component
@Slf4j
public class StaffMoveChangeHandler implements MessageListener {
    @Autowired
    private LicenseService licenseService;
    private static final String MOVE_SUCCESS = "MOVE_SUCCESS";
    @Override
    @Transactional
    public Action consume(Message msg, ConsumeContext context) {
        String body = new String(msg.getBody());
        log.info("收到消息,msgid:{},topic:{},tag:{},body:{}", msg.getMsgID(), msg.getTopic(), msg.getTag(), body);
        if (!MOVE_SUCCESS.equals(msg.getTag())) {
            return Action.CommitMessage;
        }
        try {
            StaffMoveEvent staffMoveEvent = JSONObject.parseObject(body, StaffMoveEvent.class);
            if (staffMoveEvent == null) {
                log.warn("staffMoveEvent为空");
                return Action.CommitMessage;
            }
            LicenseVO vo = new LicenseVO();
            if (staffMoveEvent.getStaffId() != null) {
                vo.setStaffId(staffMoveEvent.getStaffId());
            }
            if (staffMoveEvent.getSiteId() != null) {
                vo.setSiteId(staffMoveEvent.getSiteId());
            }
            if (StringUtils.isNotBlank(staffMoveEvent.getSiteName())) {
                vo.setSiteName(staffMoveEvent.getSiteName());
            }
            if (StringUtils.isNotBlank(staffMoveEvent.getCityCode())) {
                vo.setCityCode(staffMoveEvent.getCityCode());
            }
            if (StringUtils.isNotBlank(staffMoveEvent.getCityName())) {
                vo.setCityName(staffMoveEvent.getCityName());
            }
            if (StringUtils.isNotBlank(staffMoveEvent.getCareerName())) {
                Integer type = PositionTypeEnum.getType(staffMoveEvent.getCareerName());
                if (!type.equals(PositionTypeEnum.OTHER.getType())) {
                    vo.setPositionType(type);
                }
                if (type.equals(PositionTypeEnum.COOKER.getType())) {
                    vo.setCooker(true);
                }
            }
            licenseService.updateLicenseInfoWhenStaffMove(vo);
            log.info("消息处理完成,msgid:{}", msg.getMsgID());
        } catch (Exception e) {
            log.error("消息处理异常，msgid:{}", msg.getMsgID(), e);
            throw e;
        }
        return Action.CommitMessage;
    }


}
