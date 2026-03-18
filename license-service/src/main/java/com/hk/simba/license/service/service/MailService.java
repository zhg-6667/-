package com.hk.simba.license.service.service;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

/**
 * @author zengry
 * @description 邮件发送服务
 * @since 2020/3/6
 */
public interface MailService {

    /**
     * 发送简单邮件
     *
     * @param to      用户邮箱
     * @param subject 主题
     * @param content 文本内容
     */
    void sendMail(String to, String subject, String content);

    /**
     * 发送H5邮件
     *
     * @param to      用户邮箱
     * @param subject 主题
     * @param content 模本内容
     */
    void sendHtmlMail(String to, String subject, String content) throws MessagingException;

    /**
     * 发送模板邮件
     *
     * @param to           用户邮箱
     * @param subject      主题
     * @param templateName 模本名称
     * @param params       模本参数
     */
    void sendTemplateMail(String to, String subject, String templateName, Map<String, Object> params)
            throws TemplateException, IOException, MessagingException;

}
