package com.hk.simba.license.service.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.api.LicenseOpenService;
import com.hk.simba.license.api.request.BaseRequest;
import com.hk.simba.license.api.vo.LicenseVO;
import com.hk.simba.license.service.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author cyh
 * @date 2020/4/29/21:08
 * 乐学考试通过MQ消费，2022-07即将由考试系统取代类型，详见LicenseExamHandler
 */
@Component
@Slf4j
public class LicenseHandler implements MessageListener {
    @Autowired
    private MqMessageService mqMessageService;

    @Autowired
    private LicenseOpenService licenseOpenService;


    @Override
    public Action consume(Message msg, ConsumeContext consumeContext) {
        String body = new String(msg.getBody());
        log.info("收到消息,msgid={},topic={},tag:={},body={}", msg.getMsgID(), msg.getTopic(), msg.getTag(), body);

        try {
            if (Constants.PASS_LICENSE.equalsIgnoreCase(msg.getTag())) {
                mqMessageService.saveMessage(msg);
                Map<String, Object> params = JSON.parseObject(msg.getBody(), Map.class);
                //cargo系统发过来的通过执照学习地图的用户编号
                String userId = params.get("userId").toString();
                String mapId = params.get("mapId").toString();
                BaseRequest<LicenseVO> request = new BaseRequest<LicenseVO>();
                LicenseVO vo = new LicenseVO();
                vo.setThirdUserId(userId);
                vo.setMapId(mapId);
                vo.setCreateBy(Constants.SYS);
                request.setData(vo);
                this.licenseOpenService.createLicense(request);
            }
            log.info("消息处理完成,msgid:{}", msg.getMsgID());
        } catch (Exception e) {
            log.error("消息处理异常，msgid:{}", msg.getMsgID(), e);
            throw e;
        }
        return Action.CommitMessage;
    }

}
