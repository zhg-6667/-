package com.hk.simba.license.service.service.impl;

import com.aliyun.openservices.ons.api.Message;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.entity.MqMessage;
import com.hk.simba.license.service.mapper.MqMessageMapper;
import com.hk.simba.license.service.service.MqMessageService;

import java.util.Date;

import org.springframework.stereotype.Service;

/**
 * <p>
 * mq消息 服务实现类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {

    @Override
    public void saveMessage(Message msg) {
        MqMessage mqMessage = new MqMessage();
        mqMessage.setMsgId(msg.getMsgID());
        mqMessage.setTopic(msg.getTopic());
        mqMessage.setTag(msg.getTag());
        mqMessage.setBody(new String(msg.getBody()));
        mqMessage.setCreateBy(Constants.SYS);
        mqMessage.setCreateTime(new Date());
        this.insert(mqMessage);
    }
}
