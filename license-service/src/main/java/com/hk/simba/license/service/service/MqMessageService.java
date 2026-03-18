package com.hk.simba.license.service.service;

import com.aliyun.openservices.ons.api.Message;
import com.baomidou.mybatisplus.service.IService;
import com.hk.simba.license.service.entity.MqMessage;

/**
 * <p>
 * mq消息 服务类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
public interface MqMessageService extends IService<MqMessage> {

    /**
     * 保存消息队列信息
     *
     * @param msg
     */
    void saveMessage(Message msg);
}
