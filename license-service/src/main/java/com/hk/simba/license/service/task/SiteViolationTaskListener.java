package com.hk.simba.license.service.task;

import com.github.ltsopensource.core.commons.concurrent.limiter.Stopwatch;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.spring.tasktracker.JobRunnerItem;
import com.github.ltsopensource.spring.tasktracker.LTS;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cyh
 * @date 2021/7/21/14:10
 * <p>
 * 站点违规定时任务
 */
@LTS
@Service
@Slf4j
public class SiteViolationTaskListener {

    @Autowired
    private SiteViolationTaskHandler siteViolationTaskHandler;

    @JobRunnerItem(shardValue = "site-violation-email-task")
    public Result runSiteViolationEmailJob(JobContext jobContext) {
        try {
            log.info("runSiteViolationEmailJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            siteViolationTaskHandler.sendSiteViolationEmail();
            log.info("runSiteViolationEmailJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runSiteViolationEmailJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }

    @JobRunnerItem(shardValue = "site-violation-trial-email-task")
    public Result runSiteViolationTrialEmailJob(JobContext jobContext) {
        try {
            log.info("runSiteViolationTrialEmailJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            // 试运行已结束
            // siteViolationTaskHandler.sendSiteViolationTrialEmail();
            log.info("runSiteViolationTrialEmailJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runSiteViolationTrialEmailJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }
}
