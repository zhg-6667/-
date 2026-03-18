package com.hk.simba.license.service.task;

import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.google.common.collect.Maps;
import com.hk.simba.license.service.LicenseServiceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @description @author 羊皮
 * @since 2020-4-20 16:35:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LicenseServiceApplication.class})// 指定启动类
public class LicenseStopOrderTaskHandlerTest {

    @Autowired
    LicenseStopOrderTaskHandler licenseStopOrderTaskHandler;

    @Test
    public void licenseStopOrderTaskTest() {
        JobContext jobContext = new JobContext();
        Job job = new Job();
        Map<String, String> extParams = Maps.newHashMap();
        extParams.put("staffIds", "[202070, 201447, 368647]");
        job.setExtParams(extParams);
        jobContext.setJob(job);
        licenseStopOrderTaskHandler.licenseStopOrderTask(jobContext);
        assertTrue(1 == 1);
    }

}
