package com.hk.simba.license.service.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.hk.simba.base.distributedlock.DistributedLock;
import com.hk.simba.base.distributedlock.DistributedLockFactory;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.service.AppealService;
import com.hk.simba.license.service.service.MqMessageService;
import com.hk.simba.workflow.event.WorkFlowEvent;
import com.hk.simba.workflow.open.constant.WorkFlowStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author chenjh
 * @date 2022/06/20/11:08
 * 考试系统-考试成绩MQ，执照考试消费
 */
@Component
@Slf4j
public class WorkflowStatusChangeHandler implements MessageListener {
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private AppealService appealService;
    @Resource
    private DistributedLockFactory distributedLockFactory;

    @Override
    public Action consume(Message msg, ConsumeContext consumeContext) {
        // 不是执照申诉.
        if (!Constants.LICENSE_APPEAL.equals(msg.getTag())) {
            return Action.CommitMessage;
        }
        String body = new String(msg.getBody());
        log.info("【执照申诉】收到消息,msgid={},topic={},tag:={},body={}", msg.getMsgID(), msg.getTopic(), msg.getTag(), body);
        WorkFlowEvent event = JSON.parseObject(body, WorkFlowEvent.class);
        DistributedLock lock = distributedLockFactory.newLock(String.format("APPEAL:CONSUME:%s", event.getBizId()));
        try {
            mqMessageService.saveMessage(msg);
            // 等待10秒.
            boolean isOk = lock.tryLock(10L, TimeUnit.SECONDS);
            if (BooleanUtils.isFalse(isOk)) {
                log.warn("【执照申诉】处理执照申诉消息处理等待锁超时，参数：{}", JSON.toJSONString(event));
                return Action.CommitMessage;
            }
            switch (event.getCallbackType()) {
                case Constants.WORKFLOW_TYPE_TASK:
                    this.dealWithTask(event);
                    break;
                case Constants.WORKFLOW_TYPE_INSTANCE:
                    this.dealWithInstance(event);
                    break;
                default:
                    break;
            }
            log.info("消息处理完成,msgid:{}", msg.getMsgID());
        } catch (Exception e) {
            log.error("消息处理异常，msgid:{}", msg.getMsgID(), e);
            return Action.CommitMessage;
        } finally {
            if (lock != null && lock.isLocked()) {
                lock.unlock();
            }
        }
        return Action.CommitMessage;
    }

    private void dealWithTask(WorkFlowEvent event) {
        switch (event.getStatus()) {
            case WorkFlowStatus.TERMINATE:
                appealService.refuseAppeal(event, Boolean.FALSE);
                break;
            case WorkFlowStatus.COMPLETE:
            case WorkFlowStatus.CANCEL:
                //任务取消有多种可能，1、可能是飞书实例的DONE（自动通过，此处处理该状态）；2、可能是审批撤回（在实例撤回中处理）；3、也有可能是审批实例删除或其他场景
                appealService.passAppeal(event);
                break;
            default:
                break;
        }
    }

    private void dealWithInstance(WorkFlowEvent event) {
        switch (event.getStatus()) {
            case WorkFlowStatus.CANCEL:
                //实例取消，表示审批撤回
                appealService.recallAppeal(event);
                break;
            default:
                break;
        }
    }

}
