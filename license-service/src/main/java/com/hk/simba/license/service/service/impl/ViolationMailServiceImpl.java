package com.hk.simba.license.service.service.impl;

import com.google.common.collect.Maps;
import com.hk.base.util.DateUtils;
import com.hk.simba.license.api.vo.SiteAndRegionVO;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.service.MailService;
import com.hk.simba.license.service.service.SiteViolationService;
import com.hk.simba.license.service.service.ViolationMailService;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

/**
 * @author chenm
 * @since 2022/4/27
 */
@Slf4j
@Service
public class ViolationMailServiceImpl implements ViolationMailService {

    @Autowired
    private SiteViolationService siteViolationService;
    @Autowired
    private MailService mailService;

    @Override
    public void sendViolationNotifyEmailToTrainTeacher(Violation violation) {
        //给培训师发邮件
        SiteAndRegionVO vo = siteViolationService.getTrainTeacherInfo(violation.getSiteId());
        if (StringUtils.isBlank(vo.getTrainingTeacherEmail())) {
            return;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("teacherName", vo.getTrainingTeacherName() + "");
        params.put("siteName", violation.getSiteName());
        String staffName = "";
        if (StringUtils.isNotBlank(violation.getName())) {
            staffName = violation.getName();
        }
        params.put("staffName", staffName);
        String serviceTime = "";
        if (violation.getServiceTime() != null) {
            serviceTime = DateUtils.formatDate(violation.getServiceTime(), "yyyy年MM月dd日");
        }
        params.put("serviceTime", serviceTime);
        params.put("type", violation.getType());
        params.put("detail", violation.getDetail());
        params.put("score", "" + violation.getScore());
        try {
            mailService.sendTemplateMail(vo.getTrainingTeacherEmail(), MessageConstant.VIOLATION_TEACHER_NOTIFY_SUBJECT, MessageConstant.VIOLATION_TEACHER_NOTIFY_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[给培训师发送违规邮件通知]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[给培训师发送违规邮件通知]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[给培训师发送违规邮件通知]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[给培训师发送违规邮件通知]邮件发送失败:{}", e.getMessage(), e);
        }
    }

    @Async("asyncExecutor")
    @Override
    public void asyncSendViolationNotifyEmailToTrainTeacher(Violation violation) {
        this.sendViolationNotifyEmailToTrainTeacher(violation);
    }
}
