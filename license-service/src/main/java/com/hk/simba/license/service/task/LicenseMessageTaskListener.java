package com.hk.simba.license.service.task;

import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.spring.tasktracker.JobRunnerItem;
import com.github.ltsopensource.spring.tasktracker.LTS;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.core.commons.concurrent.limiter.Stopwatch;

/**
 * @author cyh
 * @date 2020/4/30/18:43
 */
@LTS
@Service
@Slf4j
public class LicenseMessageTaskListener {

    @Autowired
    private LicenseMessageTaskHandler licenseMessageTaskHandler;

    /**
     * 定期更新执照信息
     */
    @JobRunnerItem(shardValue = "refresh-license-task")
    public Result refreshExpireLicenseJob(JobContext jobContext) {
        try {
            log.info("refresh-license-task-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            //按分页查询信息,判断有效性,自动更新
            licenseMessageTaskHandler.refreshExpireLicense();
            log.info("refresh-license-task-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("refresh-license-task-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }
}
