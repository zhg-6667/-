package com.hk.simba.license.service.service;

import com.hk.simba.license.service.entity.Violation;

/**
 * @author chenm
 * @since 2022/4/27
 */
public interface ViolationMailService {

    /**
     * 给培训师发送，违规通知邮件
     *
     * @param violation
     * @return
     */
    void sendViolationNotifyEmailToTrainTeacher(Violation violation);

    /**
     * 给培训师发送，违规通知邮件（异步）
     *
     * @param violation
     * @return
     */
    void asyncSendViolationNotifyEmailToTrainTeacher(Violation violation);
}
