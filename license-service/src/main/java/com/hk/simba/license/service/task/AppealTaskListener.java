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
 * @date 2020/8/12/19:31
 */
@LTS
@Service
@Slf4j
public class AppealTaskListener {

    @Autowired
    private AppealTaskHandler appealTaskHandler;

    /**
     * 自动驳回超时未审批的申述
     */
    @JobRunnerItem(shardValue = "auto-reject-appeal-task")
    public Result autoRejectAppeal(JobContext jobContext) {
        try {
            log.info("auto-reject-appeal-task-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            appealTaskHandler.timeOutAppealOperate();
            appealTaskHandler.timeoutAppealApprove();
            log.info("auto-reject-appeal-task-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("auto-reject-appeal-task-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }
}
