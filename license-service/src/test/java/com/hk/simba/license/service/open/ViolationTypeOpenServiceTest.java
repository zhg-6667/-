package com.hk.simba.license.service.open;

import com.hk.simba.license.api.ViolationTypeOpenService;
import com.hk.simba.license.api.request.violation.ViolationTypeRequest;
import com.hk.simba.license.service.LicenseServiceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author chenm
 * @since 2024/10/28
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LicenseServiceApplication.class})// 指定启动类
public class ViolationTypeOpenServiceTest {
    @Autowired
    private ViolationTypeOpenService violationTypeOpenService;

    @Test
    public void test() {
        ViolationTypeRequest request = new ViolationTypeRequest();

//        violationTypeOpenService.saveViolationType();
    }
}
