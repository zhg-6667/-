//package com.hk.simba.license.service.mq;
//
//import com.alibaba.fastjson.JSONObject;
//import com.aliyun.openservices.ons.api.Action;
//import com.aliyun.openservices.ons.api.ConsumeContext;
//import com.aliyun.openservices.ons.api.Message;
//import com.aliyun.openservices.ons.api.MessageListener;
//import com.hk.simba.license.service.entity.License;
//import com.hk.simba.license.service.service.LicenseService;
//import com.hk.simba.license.service.service.MqMessageService;
//import com.hk.sbs.dto.staff.StaffWorkStatusChangeDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * @ClassName NurseStaffStatusChangeHandler
// * @Desiption 做饭家-员工状态变更通知
// * @Author chenjh1@homeking365.com
// * @Date 2020-04-30 15:59
// * @Version 1.0
// **/
//@Component
//@Slf4j
//public class NurseStaffStatusChangeHandler implements MessageListener {
//
//    @Autowired
//    private LicenseService licenseService;
//    @Autowired
//    private MqMessageService mqMessageService;
//
//    @Override
//    @Transactional
//    public Action consume(Message msg, ConsumeContext context) {
//        String body = new String(msg.getBody());
//        log.info("收到消息,msgid:{},topic:{},tag:{},body:{}", msg.getMsgID(), msg.getTopic(), msg.getTag(), body);
//        try {
//
//            mqMessageService.saveMessage(msg);
//
//            StaffWorkStatusChangeDto staffWorkStatusChangeDto = JSONObject.parseObject(body, StaffWorkStatusChangeDto.class);
//            String staffId = staffWorkStatusChangeDto.getStaffId();
//            License license = new License();
//            license.setStaffId(Long.parseLong(staffId));
//            license.setCooker(Boolean.TRUE);
//            licenseService.staffStatusChange(license, staffWorkStatusChangeDto.getStatus());
//
//            log.info("消息处理完成,msgid:{}", msg.getMsgID());
//        } catch (Exception e) {
//            log.error("消息处理异常，msgid:{}", msg.getMsgID(), e);
//            throw e;
//        }
//        return Action.CommitMessage;
//    }
//
//}
