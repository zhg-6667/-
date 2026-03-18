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
public class ViolationMessageTaskListener {

    @Autowired
    private ViolationMessageTaskHandler violationMessageTaskHandler;

    @JobRunnerItem(shardValue = "staff-sms-task")
    public Result runStaffSMSJob(JobContext jobContext) {
        try {

            log.info("runStaffSMSJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendStaffSMS();
            log.info("runStaffSMSJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runStaffSMSJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }

    @JobRunnerItem(shardValue = "staff-push-task")
    public Result runStaffPushJob(JobContext jobContext) {
        try {

            log.info("runStaffPushJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendStaffPush();
            log.info("runStaffPushJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runStaffPushJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }

    @JobRunnerItem(shardValue = "siteLeader-sms-task")
    public Result runSiteLeaderSMSJob(JobContext jobContext) {
        try {

            log.info("runSiteLeaderSMSJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendSiteLeaderSMS();
            log.info("runSiteLeaderSMSJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runSiteLeaderSMSJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }

    @JobRunnerItem(shardValue = "no-pay-sms-task")
    public Result runNoPaySmsJob(JobContext jobContext) {
        try {
            log.info("runNoPaySmsJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendNoPayMsg();
            log.info("runNoPaySmsJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runNoPaySmsJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }

    @JobRunnerItem(shardValue = "mentor-sms-task")
    public Result runMentorSmsJob(JobContext jobContext) {
        try {
            log.info("runMentorSmsJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendMentorShipSms();
            log.info("runMentorSmsJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runMentorSmsJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }


    @JobRunnerItem(shardValue = "mentor-push-task")
    public Result runMentorPushJob(JobContext jobContext) {
        try {
            log.info("runMentorPushJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendMentorShipAppPush();
            log.info("runMentorPushJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runMentorPushJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }

    @JobRunnerItem(shardValue = "mentor-site-leader-sms-task")
    public Result runMentorSiteLeaderSmsJob(JobContext jobContext) {
        try {
            log.info("runMentorSiteLeaderSmsJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendMentorShipLeaderSms();
            log.info("runMentorSiteLeaderSmsJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runMentorSiteLeaderSmsJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }


    @JobRunnerItem(shardValue = "retrain-staff-sms-task")
    public Result runRetrainStaffSmsJob(JobContext jobContext) {
        try {
            log.info("runRetrainStaffSmsJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendRetrainStaffSms();
            violationMessageTaskHandler.sendViolationRetrainStaffSms();
            violationMessageTaskHandler.sendTrainingRetrainStaffSms();
            log.info("runRetrainStaffSmsJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runRetrainStaffSmsJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }

    @JobRunnerItem(shardValue = "retrain-staff-push-task")
    public Result runRetrainStaffPushJob(JobContext jobContext) {
        try {
            log.info("runRetrainStaffPushJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendRetrainStaffAppPush();
            violationMessageTaskHandler.sendViolationRetrainStaffAppPush();
            violationMessageTaskHandler.sendTrainingRetrainStaffAppPush();
            log.info("runRetrainStaffPushJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runRetrainStaffPushJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }

    /**
     * 超时未复训-提醒培训师
     */
    @JobRunnerItem(shardValue = "timeout-retrain-teacher-task")
    public Result runTimeOutRetrainTeacherJob(JobContext jobContext) {
        try {
            log.info("runTimeOutRetrainTeacherJob-start={}", jobContext.getJob());
            Stopwatch stopwatch = Stopwatch.createStarted();
            violationMessageTaskHandler.sendTimeOutRetrainEmailToTeacher();
            log.info("runTimeOutRetrainTeacherJob-end 耗时={}ms", stopwatch.stop());
        } catch (Exception e) {
            log.error("runTimeOutRetrainTeacherJob-error", e);
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "消息发送成功");
    }
}
