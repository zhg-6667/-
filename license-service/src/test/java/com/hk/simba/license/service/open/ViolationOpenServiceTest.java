package com.hk.simba.license.service.open;

import com.google.common.collect.Lists;
import com.hk.simba.license.api.ViolationOpenService;
import com.hk.simba.license.api.request.violation.ViolationPayStatusRequest;
import com.hk.simba.license.service.LicenseServiceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LicenseServiceApplication.class})// 指定启动类
public class ViolationOpenServiceTest {
    @Autowired
    ViolationOpenService violationOpenService;

    @Test
    public void payStatusChangeTest() {
        ViolationPayStatusRequest request = new ViolationPayStatusRequest();
        request.setPayStatus(5);
        request.setOperator("M_1463920200789262336_测试");
        request.setIds(Lists.newArrayList(29L));
        violationOpenService.payStatusChange(request);
    }
}
