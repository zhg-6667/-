package com.hk.simba.license.service.task;

import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Maps;
import com.hk.base.util.DateUtils;
import com.hk.simba.license.api.vo.SiteAndRegionVO;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.DeductTypeEnum;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.service.MailService;
import com.hk.simba.license.service.service.SiteViolationService;
import com.hk.simba.license.service.service.ViolationService;
import com.hk.simba.license.service.utils.RedisUtil;
import com.hk.simba.license.service.utils.YearUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author cyh
 * @date 2021/7/21/14:09
 * <p>
 * 站点违规定时任务处理器
 */
@Service
@Slf4j
public class SiteViolationTaskHandler {

    /**
     * 站点违规邮件提醒时间配置
     */
    @Value("${site.violation.email.time}")
    private Integer siteViolationEmailTime;

    /**
     * 站点违规试运行邮件提醒时间配置
     */
    @Value("${site.violation.trial.email.time}")
    private Integer siteViolationTrialEmailTime;

    /**
     * 发送站点违规邮件的code,即非试运行的事件
     */
    @Value("${site.violation.formal.code}")
    private String siteViolationFormalCode;

    @Autowired
    private MailService mailService;
    @Autowired
    private ViolationService violationService;
    @Autowired
    private SiteViolationService siteViolationService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 发送站点违规邮件提醒
     */
    public void sendSiteViolationEmail() {
        log.info("sendSiteViolationEmail()");
        int pageSize = MessageConstant.PAGE_SIZE;
        //每次都取第一页
        int pageNo = 0;
        int currentSize = 0;
//        String codes = Constants.COMMA + siteViolationFormalCode + Constants.COMMA;
        Date endTime = YearUtil.getDateTime(siteViolationEmailTime, 0, 0);
        Date startTime = DateUtils.addDays(endTime, -1);
        do {
            Page<Violation> page = new Page<>(pageNo, pageSize);
            List<Violation> list = violationService.pageByCreateTimeAndDeDuctType(page, startTime, endTime, DeductTypeEnum.SITE_DEDUCT.getValue());
            if (!CollectionUtils.isEmpty(list)) {
                currentSize = list.size();
                for (Violation violation : list) {
//                    String code = Constants.COMMA + violation.getViolationType() + Constants.COMMA;
//                    if (codes.contains(code)) {
//                    }
                    //发邮件
                    this.createAndSendSiteViolationEmail(violation);
                }
            }
        } while (currentSize == pageSize);
    }

    /**
     * 发送站点责任违规提醒【试运行】
     */
    public void sendSiteViolationTrialEmail() {
        log.info("sendSiteViolationTrialEmail()");
        //每次都取第一页
        int pageNo = 0;
        int currentSize = 0;
        int pageSize = MessageConstant.PAGE_SIZE;
        String codes = Constants.COMMA + siteViolationFormalCode + Constants.COMMA;
        Date endTime = YearUtil.getDateTime(siteViolationTrialEmailTime, 0, 0);
        Date startTime = DateUtils.addDays(endTime, -1);
        do {
            Page<Violation> page = new Page<>(pageNo, pageSize);
            List<Violation> list = violationService.pageByCreateTimeAndDeDuctType(page, startTime, endTime, DeductTypeEnum.SITE_DEDUCT.getValue());
            if (!CollectionUtils.isEmpty(list)) {
                currentSize = list.size();
                for (Violation violation : list) {
                    String code = Constants.COMMA + violation.getViolationType() + Constants.COMMA;
                    if (!codes.contains(code)) {
                        //发邮件
                        this.createAndSendSiteViolationTrialEmail(violation);
                    }
                }
            }
        } while (currentSize == pageSize);
    }

    private void createAndSendSiteViolationEmail(Violation v) {
        //判断是否已发送过邮件
        String key = Constants.SEND_SITE_VIOLATION_RECORD + v.getId();
        if (redisUtil.get(key) != null) {
            return;
        }
        //构建参数
        Map<String, Object> params = Maps.newHashMap();
        if (v.getServiceTime() != null) {
            String serviceTime = DateUtils.formatDate(v.getServiceTime(), "yyyy年MM月dd日");
            params.put("serviceTime", serviceTime);
        }
        params.put("siteName", v.getSiteName());
        params.put("type", v.getType());
        params.put("detail", v.getDetail());
        params.put("score", "" + v.getScore());
        try {
            String email = this.getSiteLeaderEmail(v.getSiteLeaderId());
            if (StringUtils.isNotBlank(email)) {
                mailService.sendTemplateMail(email, MessageConstant.SITE_VIOLATION_SUBJECT, MessageConstant.SITE_VIOLATION_TEMPLATE, params);
                redisUtil.set(key, v.getId(), 60 * 60 * 24 * 2L);
            } else {
                log.info("[站点责任违规提醒],发送的邮箱为空: violationId = {}, siteId = {}, siteLeaderName = {}", v.getId(), v.getSiteId(), v.getSiteLeaderName());
            }
        } catch (TemplateException e) {
            log.error("[站点责任违规提醒]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[站点责任违规提醒]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[站点责任违规提醒]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[站点责任违规提醒]邮件发送失败:{}", e.getMessage(), e);
        }
    }


    private void createAndSendSiteViolationTrialEmail(Violation v) {
        //判断是否已发送过邮件
        String key = Constants.SEND_SITE_VIOLATION_TRIAL_RECORD + v.getId();
        if (redisUtil.get(key) != null) {
            return;
        }
        //构建参数
        Map<String, Object> params = Maps.newHashMap();
        params.put("siteName", v.getSiteName());
        if (v.getServiceTime() != null) {
            String serviceTime = DateUtils.formatDate(v.getServiceTime(), "yyyy年MM月dd日");
            params.put("serviceTime", serviceTime);
        }
        params.put("type", v.getType());
        params.put("detail", v.getDetail());
        params.put("score", "" + v.getScore());
        try {
            String email = this.getSiteLeaderEmail(v.getSiteLeaderId());
            if (StringUtils.isNotBlank(email)) {
                mailService.sendTemplateMail(email, MessageConstant.SITE_VIOLATION_TRIAL_SUBJECT, MessageConstant.SITE_VIOLATION_TRIAL_TEMPLATE, params);
                redisUtil.set(key, v.getId(), 60 * 60 * 24 * 2L);
            } else {
                log.info("[站点责任违规提醒,试运行],发送的邮箱为空: violationId = {}, siteId = {}, siteLeaderName = {}", v.getId(), v.getSiteId(), v.getSiteLeaderName());
            }
        } catch (TemplateException e) {
            log.error("[站点责任违规提醒,试运行],模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[站点责任违规提醒,试运行],模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[站点责任违规提醒,试运行],邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[站点责任违规提醒,试运行],邮件发送失败:{}", e.getMessage(), e);
        }
    }

    private String getSiteLeaderEmail(Long siteLeaderId) {
        String key = Constants.SITE_LEADER_EMAIL + siteLeaderId;
        String email = "";
        if (redisUtil.get(key) != null) {
            email = (String) redisUtil.get(key);
        } else {
            SiteAndRegionVO vo = siteViolationService.getSiteLeaderInfo(siteLeaderId);
            if (vo != null && StringUtils.isNotBlank(vo.getSiteLeaderEmail())) {
                email = vo.getSiteLeaderEmail();
                redisUtil.set(key, email, 60 * 60 * 24 * 1L);
            }
        }
        return email;
    }
}
