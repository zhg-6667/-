package com.hk.simba.license.service;

import com.aliyun.openservices.ons.api.Message;
import com.hk.simba.license.service.mq.EventHandler;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description @author 羊皮
 * @since 2020-4-20 16:35:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LicenseServiceApplication.class})// 指定启动类
public class SpringTest {

    @Autowired
    EventHandler eventHandler;

    //    @Test
    public void test() {
        Message msg = new Message("QA_EventNotice", "PROCESSED", "{\"search_EQULE_id\":null,\"userId\":null,\"userIdString\":null,\"search_EQULE_source\":null,\"search_EQULE_config_id\":null,\"userName\":\"梁祥胜\",\"customerManagerName\":null,\"orderId\":\"3044968\",\"configName\":\"未用工具\",\"search_EQULE_config_parent_id\":null,\"search_LIKE_content\":null,\"search_LIKE_customer\":null,\"search_EQULE_phone\":null,\"search_EQULE_orderId\":null,\"search_EQULE_workOrderId\":null,\"search_LIKE_staff\":null,\"search_IN_receive\":null,\"search_LIKE_receive_name\":null,\"search_IN_follow_up\":null,\"search_LIKE_follow_up_name\":null,\"search_IN_handler\":null,\"search_LIKE_handler_name\":null,\"search_EQULE_status\":null,\"search_LIKE_responsibility_user\":null,\"search_LIKE_remark\":null,\"search_LIKE_create_by\":null,\"search_IN_author\":null,\"search_LIKE_author_name\":null,\"search_IN_dept\":null,\"search_listAll\":null,\"search_EQULE_city\":null,\"search_EQULE_site\":null,\"search_GTE_start_time\":null,\"search_LTE_end_time\":null,\"search_LIKE_modifyBy\":null,\"search_LT_modify_time\":null,\"search_GTE_modify_time\":null,\"login_user_id\":null,\"staff\":\"210748_洪珍青,\",\"configResultName\":null,\"statusName\":\"处理完成\",\"processMode1\":null,\"processMode2\":null,\"processMode3\":null,\"eventResponsibilities\":null,\"serviceTime\":\"2020-04-01 08:00:00\",\"isNurseClue\":false,\"eventAttachmentDtos\":[{\"id\":115245,\"attachment\":\"cxy.jpg\",\"attachmentUrl\":\"http://img.homeking365.com/0d7d5482-3d6d-4ce2-bfa8-5e1b643d08c1.jpg\"},{\"id\":115246,\"attachment\":\"want-to-learn102509.jpg\",\"attachmentUrl\":\"http://img.homeking365.com/4e8303ac-2504-4a4e-8e93-5a5e004e098f.jpg\"}],\"id\":1135740,\"source\":null,\"configId\":310,\"configResultId\":-1,\"secondConfigResultId\":null,\"thirdConfigResultId\":null,\"content\":\"1335097307293551616\",\"followUp\":null,\"handler\":null,\"remindWay\":null,\"processMode\":null,\"processScheme\":null,\"processReason\":null,\"status\":3,\"responsibilityDept\":null,\"responsibilityUser\":\"\",\"remark\":\"\",\"processRemark\":null,\"useTime\":null,\"createBy\":null,\"createTime\":\"2020-04-20 16:28:24\",\"modifyBy\":null,\"modifyTime\":null,\"author\":null,\"timeout\":null,\"classid\":null,\"processCost\":null,\"callRecordId\":null,\"authorName\":null,\"authorDeptCode\":null,\"authorDeptName\":null,\"responsibilityDeptName\":null,\"responsibilityUserName\":null,\"followUpName\":null,\"handlerName\":null,\"complaintType\":null,\"feedback\":null}".getBytes());
        eventHandler.consume(msg, null);
    }

}
