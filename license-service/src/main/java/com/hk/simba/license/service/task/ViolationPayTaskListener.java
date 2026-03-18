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
 * @ClassName ViolationMessageTaskListener
 * @Description LTS 违规信息调度监听
 * @Author chenjh1@homeking365.com
 * @Date 2020-04-16 16:28
 * @Version 1.0
 **/
@LTS
@Service
@Slf4j
public class ViolationPayTaskListener {

    @Autowired
    private ViolationPayTaskHandler violationPayTaskHandler;

    @JobRunnerItem(shardValue = "timeout-pay-task")
    public Result runTimeoutPayJob(JobContext jobContext) {
        try {

            log.info("runTimeoutPayJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationPayTaskHandler.timeoutPayChangeStatus();
            log.info("runTimeoutPayJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runTimeoutPayJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }
}
