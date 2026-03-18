package com.hk.simba.license.service.mq;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.entity.ViolationPayRecord;
import com.hk.simba.license.service.mq.dto.PaySuccessEntity;
import com.hk.simba.license.api.enums.PayRecordStatusEnum;
import com.hk.simba.license.api.enums.ViolationPayStatusEnum;
import com.hk.simba.license.service.service.MqMessageService;
import com.hk.simba.license.service.service.ViolationPayRecordService;
import com.hk.simba.license.service.service.ViolationService;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @description @author 羊皮
 * @since 2020-4-17 11:18:47
 */
@Component
@Slf4j
public class PaySuccessHandler implements MessageListener {

    @Autowired
    private ViolationService violationService;
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private ViolationPayRecordService violationPayRecordService;
    private static final String TRADE_ORDER_CODE = "trade_order_code";

    @Override
    @Transactional
    public Action consume(Message msg, ConsumeContext context) {
        String body = new String(msg.getBody());
        log.info("收到消息,msgid:{},topic:{},tag:{},body:{}", msg.getMsgID(), msg.getTopic(), msg.getTag(), body);
        try {
            if (Constants.PAY_MESSAGE_TAG.equalsIgnoreCase(msg.getTag())) {
                mqMessageService.saveMessage(msg);
            }

            PaySuccessEntity pse = JSONObject.parseObject(body, PaySuccessEntity.class);
            String tradeOrderCode = pse.getTradeOrderCode();
            EntityWrapper<ViolationPayRecord> wrapper = new EntityWrapper<>();
            wrapper.eq(TRADE_ORDER_CODE, tradeOrderCode);
            ViolationPayRecord vpr = violationPayRecordService.selectOne(wrapper);
            Date now = new Date();
            if (!ObjectUtils.isEmpty(vpr) && !PayRecordStatusEnum.PAY.getValue().equals(vpr.getStatus())) {
                log.info("根据支付成功消息更新支付记录，{}", vpr);
                vpr.setStatus(PayRecordStatusEnum.PAY.getValue());
                vpr.setModifyTime(now);
                vpr.setModifyBy(Constants.SYS);
                log.info("更新后，{}", vpr);
                violationPayRecordService.updateById(vpr);
            }
            EntityWrapper<Violation> wrapper2 = new EntityWrapper<>();
            wrapper2.eq(TRADE_ORDER_CODE, tradeOrderCode);
            Violation violation = violationService.selectOne(wrapper2);
            if (ObjectUtils.isEmpty(violation) && !ObjectUtils.isEmpty(vpr)) {
                //违规找不到的情况下使用支付订单绑定的违规id查询，避免一条违规对应多条支付订单号导致违规的支付状态未正确更新。如产线违规id=27627的数据
                violation = violationService.selectById(vpr.getViolationId());
            }
            if (!ObjectUtils.isEmpty(violation) && !PayRecordStatusEnum.PAY.getValue().equals(violation.getPayStatus())) {
                violation.setPayStatus(ViolationPayStatusEnum.PAY.getValue());
                violation.setModifyBy(Constants.SYS);
                violation.setModifyTime(new Date());
                violationService.updateById(violation);
            }
            log.info("消息处理完成,msgid:{}", msg.getMsgID());
        } catch (Exception e) {
            log.error("消息处理异常，msgid:{}", msg.getMsgID(), e);
            throw e;
        }
        return Action.CommitMessage;
    }

}
