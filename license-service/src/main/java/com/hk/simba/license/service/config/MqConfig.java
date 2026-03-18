package com.hk.simba.license.service.config;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.hk.simba.license.service.mq.EventHandler;
import com.hk.simba.license.service.mq.LicenseExamHandler;
import com.hk.simba.license.service.mq.LicenseHandler;
import com.hk.simba.license.service.mq.PaySuccessHandler;
import com.hk.simba.license.service.mq.StaffMoveChangeHandler;
import com.hk.simba.license.service.mq.StaffStatusChangeHandler;
import com.hk.simba.license.service.mq.WorkflowStatusChangeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * @description @author 羊皮
 * @since 2020-4-15 17:40:40
 */
@Configuration
@Slf4j
public class MqConfig {

    @Value("${mq.consumerId}")
    private String consumerId;
    @Value("${mq.accessKey}")
    private String accessKey;
    @Value("${mq.secretKey}")
    private String secretKey;
    @Value("${mq.namesrvAddr}")
    private String mqNameSvrAddr;
    @Value("${mq.event.topic}")
    private String eventTopic;
    @Value("${mq.pay.success.topic}")
    private String paySuccessTopic;
    @Value("${mq.staff.status.change.topic}")
    private String staffStatusChangeTopic;
    @Value("${mq.staff.move.change.topic}")
    private String staffMoveChangeTopic;
    @Value("${mq.cargo.topic.zzg.license}")
    private String zzgLicenseTopic;
    @Value("${mq.exam.result.topic}")
    private String examResultTopic;
    @Value("${mq.workFlowStatusChange.topic}")
    private String workFlowStatusChangeTopic;

    private static final String TAG_ALL = "*";
    private static Consumer consumer;
    @Autowired
    private EventHandler eventHandler;
    @Autowired
    private PaySuccessHandler paySuccessHandler;
    @Autowired
    private StaffStatusChangeHandler staffStatusChangedHandler;
    @Autowired
    private StaffMoveChangeHandler staffMoveChangeHandler;
    @Autowired
    private LicenseExamHandler licenseExamHandler;
    @Autowired
    private LicenseHandler licenseHandler;
    @Autowired
    private WorkflowStatusChangeHandler workflowStatusChangeHandler;

    @PostConstruct
    public void init() {
        log.info("初始化启动消费者！");
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.GROUP_ID, consumerId);
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, mqNameSvrAddr);
        consumer = ONSFactory.createConsumer(properties);
        consumer.subscribe(eventTopic, TAG_ALL, eventHandler);
        consumer.subscribe(paySuccessTopic, TAG_ALL, paySuccessHandler);
        consumer.subscribe(staffStatusChangeTopic, TAG_ALL, staffStatusChangedHandler);
        consumer.subscribe(zzgLicenseTopic, TAG_ALL, licenseHandler);
        consumer.subscribe(staffMoveChangeTopic, TAG_ALL, staffMoveChangeHandler);
        consumer.subscribe(examResultTopic, TAG_ALL, licenseExamHandler);
        consumer.subscribe(workFlowStatusChangeTopic, TAG_ALL, workflowStatusChangeHandler);
        consumer.start();
        log.info("消费者启动成功");
    }

    /**
     * 初始化消费者
     *
     * @return
     */
    public Consumer getconsumer() {
        return consumer;
    }

}
