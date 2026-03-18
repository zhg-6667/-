package com.hk.simba.license.service.constant;

/**
 * @ClassName MessageConstant
 * @Description 短信消息常量参数
 * @Author chenjh1@homeking365.com
 * @Date 2020-03-14 16:28
 * @Version 1.0
 **/
public class MessageConstant {

    public static final String APP_ID = "service-license";

    public static final String MESSAGE_PROPERTIES_KEY = "message.properties";

    public static final String PAGE_SIZE_KEY = "message.page.size";
    public static final int PAGE_SIZE = 100;

    public static final String STAFF_SMS_TEMPLATE_KEY = "message.staff.sms.template";
    public static final String STAFF_SMS_TEMPLATE = "质质高执照违规员工短信模板";

    public static final String STAFF_PUSH_TEMPLATE_KEY = "message.staff.push.template";
    public static final String STAFF_PUSH_TEMPLATE = "质质高执照违规员工推送模板";

    public static final String STAFF_PUSH_TITLE_KEY = "message.staff.push.title";
    public static final String STAFF_PUSH_TITLE = "违规通知";

    public static final String STAFF_PUSH_CONTENT_KEY = "message.staff.push.content";
    public static final String STAFF_PUSH_CONTENT = "【好慷服务法】尊敬的{0}，您因{1}触犯“{2}-{3}”被记录违规，服务执照扣{4}分，惩戒款{5}元，请于十日内通过家在好慷缴交。如有异议，即日起{6}日内向站长申请申诉。<a href=\"https://license.homeking365.com/Details?id={7}\">查看详情</a>";

    public static final String STAFF_PUSH_CREATEBY_KEY = "message.staff.push.createBy";
    public static final String STAFF_PUSH_CREATEBY = "系统通知";

    public static final String SITELEADER_SMS_TEMPLATE_KEY = "message.siteLeader.sms.template";
    public static final String SITELEADER_SMS_TEMPLATE = "质质高执照违规站长短信模板";

    public static final String APPEAL_LIMIT_DAY_KEY = "message.appeal.limit.day";
    public static final int APPEAL_LIMIT_DAY = 5;

    public static final String TRYTIMES_LIMIT_KEY = "message.trytimes.limit";
    public static final int TRYTIMES_LIMIT = 3;

    public static final String RETRAIN_NOTIFY_SUBJECT = "复训通知";
    public static final String RETRAIN_NOTIFY_TEMPLATE = "retrain-notify-template.ftl";

    public static final String RETRAIN_RESULT_SUBJECT = "复训结果提醒";
    public static final String RETRAIN_RESULT_TEMPLATE = "retrain-result-template.ftl";

    public static final String VIOLATION_INVALID_SUBJECT = "违规失效提醒";
    public static final String VIOLATION_INVALID_TEMPLATE = "violation-invalid-template.ftl";

    public static final String APPEAL_DEAL_SUBJECT = "申诉处理提醒";
    public static final String APPEAL_DEAL_TEMPLATE = "appeal-deal-template.ftl";

    public static final String SITE_APPEAL_DEAL_SUBJECT = "站点申诉处理提醒";
    public static final String SITE_APPEAL_DEAL_TEMPLATE = "site-appeal-deal-template.ftl";

    public static final String APPEAL_RESULT_SUBJECT = "申诉结果提醒";
    public static final String APPEAL_RESULT_TEMPLATE = "appeal-result-template.ftl";

    public static final String SITE_APPEAL_RESULT_SUBJECT = "站点申诉结果提醒";
    public static final String SITE_APPEAL_RESULT_TEMPLATE = "site-appeal-result-template.ftl";

    public static final String LICENSE_REVOKE_SUBJECT = "执照吊销提醒";
    public static final String LICENSE_REVOKE_TEMPLATE = "license-revoke-template.ftl";

    public static final String WITHOUT_LEADER = "【无对应负责人】";

    public static final String SITE_VIOLATION_SUBJECT = "站点责任违规提醒";
    public static final String SITE_VIOLATION_TEMPLATE = "site-violation-template.ftl";

    public static final String SITE_VIOLATION_TRIAL_SUBJECT = "站点责任违规提醒【试运行】";
    public static final String SITE_VIOLATION_TRIAL_TEMPLATE = "site-violation-trial-template.ftl";

    public static final String STAFF_FORBIDDEN_SUBJECT = "服务禁忌违规审批提醒";
    public static final String STAFF_FORBIDDEN_TEMPLATE = "staff-forbidden-template.ftl";

    public static final String SITE_FORBIDDEN_SUBJECT = "服务禁忌违规审批提醒";
    public static final String SITE_FORBIDDEN_TEMPLATE = "site-forbidden-template.ftl";

    public static final String VIOLATION_TEACHER_NOTIFY_SUBJECT = "质质高执照违规培训师提醒";
    public static final String VIOLATION_TEACHER_NOTIFY_TEMPLATE = "violation-teacher-notify-template.ftl";

    public static final String RETRAIN_TEACHER_NOTIFY_SUBJECT = "质质高复训培训师提醒";
    public static final String RETRAIN_TEACHER_NOTIFY_TEMPLATE = "retrain-teacher-notify-template.ftl";

    public static final String RETRAIN_TIMEOUT_TEACHER_NOTIFY_SUBJECT = "质质高执照超时未复训提醒";
    public static final String RETRAIN_TIMEOUT_TEACHER_NOTIFY_TEMPLATE = "retrain-timeout-teacher-notify-template.ftl";

}
