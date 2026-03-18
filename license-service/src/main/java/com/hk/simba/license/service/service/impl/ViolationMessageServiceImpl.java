package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.RetrainTypeEnum;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.entity.ViolationMessage;
import com.hk.simba.license.service.mapper.ViolationMessageMapper;
import com.hk.simba.license.api.enums.ViolationMessageStatusEnum;
import com.hk.simba.license.api.enums.ViolationMessageTypeEnum;
import com.hk.simba.license.service.service.ViolationMessageService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 违规消息通知 服务实现类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Service
public class ViolationMessageServiceImpl extends ServiceImpl<ViolationMessageMapper, ViolationMessage> implements ViolationMessageService {

    /**
     * 师傅扣分-事件id
     */
    @Value("${mentor.ship.event.config.id}")
    private String mentorShipEventConfigId;

    @Override
    public void saveViolationMessage(Violation violation) {
        //特殊判断---师徒制短信
        Boolean isMentor = this.isMentorShipEvent(violation.getViolationType());
        if (isMentor) {
            this.saveMentorShipMessage(violation);
        } else {
            ViolationMessage staffSms = new ViolationMessage();
            staffSms.setStaffId(violation.getStaffId());
            staffSms.setStaffPhone(violation.getPhone());
            staffSms.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            staffSms.setType(ViolationMessageTypeEnum.STAFF_SMS.getValue());
            staffSms.setViolationCode(violation.getCode());
            staffSms.setViolationId(violation.getId());
            staffSms.setCreateBy(Constants.SYS);
            staffSms.setCreateTime(new Date());
            insert(staffSms);

            ViolationMessage staffPush = new ViolationMessage();
            staffPush.setStaffId(violation.getStaffId());
            staffPush.setStaffPhone(violation.getPhone());
            staffPush.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            staffPush.setType(ViolationMessageTypeEnum.STAFF_PUSH.getValue());
            staffPush.setViolationCode(violation.getCode());
            staffPush.setViolationId(violation.getId());
            staffPush.setCreateBy(Constants.SYS);
            staffPush.setCreateTime(new Date());
            insert(staffPush);

            ViolationMessage siteLeaderSms = new ViolationMessage();
            siteLeaderSms.setStaffId(violation.getSiteLeaderId());
            siteLeaderSms.setStaffPhone(violation.getSiteLeaderPhone());
            siteLeaderSms.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            siteLeaderSms.setType(ViolationMessageTypeEnum.SITE_LEADER_SMS.getValue());
            siteLeaderSms.setViolationCode(violation.getCode());
            siteLeaderSms.setViolationId(violation.getId());
            siteLeaderSms.setCreateBy(Constants.SYS);
            siteLeaderSms.setCreateTime(new Date());
            insert(siteLeaderSms);
        }
    }

    /**
     * 异步保存违规消息通知
     * @param violation 违规消息
     */
    @Async("asyncExecutor")
    @Override
    public void asyncSaveViolationMessage(Violation violation) {
        this.saveViolationMessage(violation);
    }

    @Override
    public void invalidViolationMessage(Violation violation) {
        //排除复训的短息,其失效的逻辑在复训失效时才失效
        List<Integer> types = new ArrayList<>();
        types.add(ViolationMessageTypeEnum.RETRAIN_STAFF_SMS.getValue());
        types.add(ViolationMessageTypeEnum.RETRAIN_STAFF_PUSH.getValue());
        types.add(ViolationMessageTypeEnum.VIOLATION_RETRAIN_STAFF_SMS.getValue());
        types.add(ViolationMessageTypeEnum.VIOLATION_RETRAIN_STAFF_PUSH.getValue());
        types.add(ViolationMessageTypeEnum.TRAINING_RETRAIN_STAFF_SMS.getValue());
        types.add(ViolationMessageTypeEnum.TRAINING_RETRAIN_STAFF_PUSH.getValue());
        List<ViolationMessage> violationMessageList = this.selectList(new EntityWrapper<ViolationMessage>()
                .eq("violation_id", violation.getId()).eq("status", ViolationMessageStatusEnum.WAITTING.getValue()).notIn("type", types));
        if (!CollectionUtils.isEmpty(violationMessageList)) {
            this.batchInvalidViolationMessage(violationMessageList, violation.getModifyBy());
        }
    }

    @Override
    public ViolationMessage saveStaffRepeatNotifyMessage(Violation violation) {
        //构建未支付再次提醒员工短信
        ViolationMessage staffSms = new ViolationMessage();
        staffSms.setViolationCode(violation.getCode());
        staffSms.setStaffId(violation.getStaffId());
        staffSms.setType(ViolationMessageTypeEnum.NO_PAY_STAFF_SMS.getValue());
        Wrapper<ViolationMessage> violationWrapper = new EntityWrapper<>(staffSms).orderBy("id", false);
        ViolationMessage tempMsg = this.selectOne(violationWrapper);
        if (tempMsg == null) {
            staffSms.setStaffPhone(violation.getPhone());
            staffSms.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            staffSms.setCreateBy(Constants.SYS);
            staffSms.setViolationId(violation.getId());
            staffSms.setCreateTime(new Date());
            staffSms.setTryTimes(0);
            this.insert(staffSms);
            return staffSms;
        }
        return tempMsg;


    }

    @Override
    public ViolationMessage saveLeaderRepeatNotifyMessage(Violation violation) {
        //构建未支付再次提醒站长短信
        ViolationMessage siteLeaderSms = new ViolationMessage();
        siteLeaderSms.setViolationCode(violation.getCode());
        siteLeaderSms.setStaffId(violation.getSiteLeaderId());
        siteLeaderSms.setType(ViolationMessageTypeEnum.NO_PAY_SITE_LEADER_SMS.getValue());
        Wrapper<ViolationMessage> violationWrapper = new EntityWrapper<>(siteLeaderSms).orderBy("id", false);
        ViolationMessage tempMsg = this.selectOne(violationWrapper);
        if (tempMsg == null) {
            siteLeaderSms.setStaffPhone(violation.getSiteLeaderPhone());
            siteLeaderSms.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
            siteLeaderSms.setCreateBy(Constants.SYS);
            siteLeaderSms.setViolationId(violation.getId());
            siteLeaderSms.setCreateTime(new Date());
            siteLeaderSms.setTryTimes(0);
            this.insert(siteLeaderSms);
            return siteLeaderSms;
        }
        return tempMsg;
    }


    private void saveMentorShipMessage(Violation violation) {
        ViolationMessage staffSms = new ViolationMessage();
        staffSms.setStaffId(violation.getStaffId());
        staffSms.setStaffPhone(violation.getPhone());
        staffSms.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
        staffSms.setType(ViolationMessageTypeEnum.MENTOR_SHIP_SMS.getValue());
        staffSms.setViolationCode(violation.getCode());
        staffSms.setViolationId(violation.getId());
        staffSms.setCreateBy(Constants.SYS);
        staffSms.setCreateTime(new Date());
        insert(staffSms);


        ViolationMessage staffPush = new ViolationMessage();
        staffPush.setStaffId(violation.getStaffId());
        staffPush.setStaffPhone(violation.getPhone());
        staffPush.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
        staffPush.setType(ViolationMessageTypeEnum.MENTOR_SHIP_PUSH.getValue());
        staffPush.setViolationCode(violation.getCode());
        staffPush.setViolationId(violation.getId());
        staffPush.setCreateBy(Constants.SYS);
        staffPush.setCreateTime(new Date());
        insert(staffPush);

        ViolationMessage siteLeaderSms = new ViolationMessage();
        siteLeaderSms.setStaffId(violation.getSiteLeaderId());
        siteLeaderSms.setStaffPhone(violation.getSiteLeaderPhone());
        siteLeaderSms.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
        siteLeaderSms.setType(ViolationMessageTypeEnum.MENTOR_SHIP_LEADER_SMS.getValue());
        siteLeaderSms.setViolationCode(violation.getCode());
        siteLeaderSms.setViolationId(violation.getId());
        siteLeaderSms.setCreateBy(Constants.SYS);
        siteLeaderSms.setCreateTime(new Date());
        insert(siteLeaderSms);
    }

    /**
     * 判断是否是师徒制--事件
     **/
    private Boolean isMentorShipEvent(String code) {
        if (StringUtils.isNotBlank(mentorShipEventConfigId)) {
            String[] str = mentorShipEventConfigId.split(",");
            if (str != null && str.length > 0) {
                for (String s : str) {
                    if (s.equals(code)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void saveRetrainMessage(Violation violation, Integer retrainType) {
        //复训短信信息
        ViolationMessage retrainStaffMsg = new ViolationMessage();
        retrainStaffMsg.setStaffId(violation.getStaffId());
        retrainStaffMsg.setStaffPhone(violation.getPhone());
        retrainStaffMsg.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
        if (retrainType.equals(RetrainTypeEnum.VIOLATION_RETRAIN.getValue())) {
            retrainStaffMsg.setType(ViolationMessageTypeEnum.VIOLATION_RETRAIN_STAFF_SMS.getValue());
        } else if (retrainType.equals(RetrainTypeEnum.TRAINING_RETRAIN.getValue())) {
            retrainStaffMsg.setType(ViolationMessageTypeEnum.TRAINING_RETRAIN_STAFF_SMS.getValue());
        } else {
            retrainStaffMsg.setType(ViolationMessageTypeEnum.RETRAIN_STAFF_SMS.getValue());
        }
        retrainStaffMsg.setViolationCode(violation.getCode());
        retrainStaffMsg.setViolationId(violation.getId());
        retrainStaffMsg.setCreateBy(Constants.SYS);
        retrainStaffMsg.setCreateTime(new Date());
        this.insert(retrainStaffMsg);

        //复训短信信息
        ViolationMessage retrainStaffPush = new ViolationMessage();
        retrainStaffPush.setStaffId(violation.getStaffId());
        retrainStaffPush.setStaffPhone(violation.getPhone());
        retrainStaffPush.setStatus(ViolationMessageStatusEnum.WAITTING.getValue());
        if (retrainType.equals(RetrainTypeEnum.VIOLATION_RETRAIN.getValue())) {
            retrainStaffPush.setType(ViolationMessageTypeEnum.VIOLATION_RETRAIN_STAFF_PUSH.getValue());
        } else if (retrainType.equals(RetrainTypeEnum.TRAINING_RETRAIN.getValue())) {
            retrainStaffPush.setType(ViolationMessageTypeEnum.TRAINING_RETRAIN_STAFF_PUSH.getValue());
        } else {
            retrainStaffPush.setType(ViolationMessageTypeEnum.RETRAIN_STAFF_PUSH.getValue());
        }
        retrainStaffPush.setViolationCode(violation.getCode());
        retrainStaffPush.setViolationId(violation.getId());
        retrainStaffPush.setCreateBy(Constants.SYS);
        retrainStaffPush.setCreateTime(new Date());
        this.insert(retrainStaffPush);
    }

    @Override
    public void invalidMessageByViolationIdAndType(Long violationId, List<Integer> types, String modify) {
        List<ViolationMessage> violationMessageList = this.selectList(new EntityWrapper<ViolationMessage>()
                .eq("violation_id", violationId).eq("status", ViolationMessageStatusEnum.WAITTING.getValue()).in("type", types));
        if (!CollectionUtils.isEmpty(violationMessageList)) {
            this.batchInvalidViolationMessage(violationMessageList, modify);
        }
    }

    @Override
    public void batchInvalidViolationMessage(List<ViolationMessage> violationMessageList, String modify) {
        if (!CollectionUtils.isEmpty(violationMessageList)) {
            Date modifyTime = new Date();
            violationMessageList.stream().forEach(violationMessage -> {
                violationMessage.setStatus(ViolationMessageStatusEnum.NO_SEND.getValue());
                violationMessage.setModifyBy(modify);
                violationMessage.setModifyTime(modifyTime);
            });
            this.updateBatchById(violationMessageList);
        }
    }
}
