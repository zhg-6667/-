package com.hk.simba.license.service.task;

import com.hk.simba.license.service.LicenseServiceApplication;
import org.junit.Test;
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
public class TaskHandlerTest {

    @Autowired
    ViolationMessageTaskHandler violationMessageTaskHandler;

    @Test
    public void test() {
        violationMessageTaskHandler.sendStaffPush();
    }

}
