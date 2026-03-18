package com.hk.simba.license.service.service.impl;


import com.hk.simba.license.service.service.MailService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;

/**
 * @author zengry
 * @description
 * @since 2020/3/6
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {
    private static final String REGEX = ",";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendMail(String to, String subject, String content) {
        if (StringUtils.isBlank(to)) {
            throw new IllegalArgumentException("收件人地址不能为空");
        }
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
        mailSender.send(mailMessage);
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        message.setSubject(subject);
        messageHelper.setText(content, true);
        mailSender.send(message);
    }

    @Override
    public void sendTemplateMail(String to, String subject, String templateName, Map<String, Object> params)
            throws TemplateException, IOException, MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        Template template = configurer.getConfiguration().getTemplate(templateName);
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
        helper.setText(text, true);
        mailSender.send(mimeMessage);
    }
}
