package com.hk.simba.license.service.constant;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @ClassName Constants
 * @Desiption 公共常量
 * @Author chenjh1@homeking365.com
 * @Date 2020-04-17 16:57
 * @Version 1.0
 */
public class Constants {

    public static final String SYS = "A_0_System";
    /**
     * 用户注册来源
     */
    public static final String USER_SOURCE = "license";
    /**
     * 支付消息队列tag
     */
    public static final String PAY_MESSAGE_TAG = "LICENSE_BILL";

    /**
     * cargo中的乐学执照学习地图考试tag
     */
    public static final String PASS_LICENSE = "PASS_LICENSE";

    /**
     * 保洁员工用户Id开头(由cargo发过来已通过考试的)
     */
    public static final String K = "K";

    /**
     * 保姆员工用户Id开头(由cargo发过来已通过考试的)
     */
    public static final String N = "N";

    /**
     * 职员用户Id开头(由cargo发过来已通过考试的)
     */
    public static final String H = "H";

    /**
     * 执照分数24分
     */
    public static final Integer SCORE = 24;

    /**
     * 执照分数24分
     */
    public static final String ZERO_DESCRIPTION = "剩余分数为0";

    /**
     * 违规
     */
    public static final String VIOLATION = "违规";

    /**
     * 申诉通过
     */
    public static final String APPEAL_PASS = "申诉通过";

    /**
     * 申诉通过
     */
    public static final String QUIT = "已离职";

    /**
     * 分数重置
     */
    public static final String SCORE_RESET = "分数重置";

    /**
     * 分数重置
     */
    public static final String SCORE_INIT = "分数初始化";

    /**
     * 分数重置
     */
    public static final String QUALITY_APPROVE = "质质高审核通过,违规失效";

    /**
     * 系统自动失效
     */
    public static final String INVALID_BY_SYSTEM = "事件系统变更，系统自动失效";

    /**
     * 违规类型缓存
     */
    public static final String VIOLATION_TYPE_CACHE = "violation_type_cache";

    /**
     * 站点大区映射表缓存
     */
    public static final String REGION_DETAIL_CACHE = "region_detail_cache";

    public static final String LICENSE_REVOKE = "执照已吊销";


    public static final String VIOLATION_INVALID = "违规已失效";

    public static final String TIME_OUT_REJECT = "超时未审批，系统自动驳回";

    public static final String VIOLATION_INVAILD_REJECT = "违规失效，系统自动驳回";

    public static final String ADD_BLACK_BY_HAND_REVOKE_LICENSE = "手工吊销执照,添加黑名单";

    public static final String ADD_BLACK_BY_SYSTEM = "剩余分数为0,系统吊销执照,自动添加黑名单";


    public static final String REMOVE_BLACK_BY_SYSTEM = "分数回滚,系统自动移除黑名单";

    /***
     * 站长和大区信息缓存
     */
    public static final String SITE_LEADER_REGION_CACHE = "site_leader_region_cache:";

    public static final String COMMA = ",";

    public static final String UNDERLINE = "_";

    public static final String BLANK = " ";

    public static final String COLON = ":";

    public static final String SITE_LEADER_EMAIL = "site_leader_email:";

    public static final String SEND_SITE_VIOLATION_RECORD = "send_site_violation_record:";

    public static final String SEND_SITE_VIOLATION_TRIAL_RECORD = "send_site_violation_trial_record:";

    public static final String INVALID_EXAM_GROUP = "执照生成,失效考试记录";
    public static final String STAFF_LEAVE_INVALID_EXAM_GROUP = "员工离职,失效考试记录";

    /**
     * 复训配置
     */
    public static final String RETRAIN_POSITION_CITY = "Retrain_Position_City:";

    /**
     * 站点违规-员工id缓存
     */
    public static final String SITE_VIOLATION_ID = "Site_Violation_Id:";

    /**
     * 违规类型map缓存
     */
    public static final String VIOLATION_TYPE_MAP_CACHE = "violation_type_map_cache";

    /**
     * 违规类型集合map缓存
     */
    public static final String VIOLATION_TYPE_MAP = "violation:type:map";

    /**
     * 站点培训师信息缓存
     */
    public static final String SITE_TRAINING_TEACHER_INFO = "Site_TrainingTeacher_Info:";

    /**
     * 复训超时提醒前缀
     */
    public static final String RETRAIN_TIMEOUT_REMIND_ID = "Retrain_Timeout_Remind_Id:";

    /**
     * 服务部
     */
    public static final String SERVICE_DEPT = "服务部";

    /**
     * 执照申述
     */
    public static final String LICENSE_APPEAL = "license_appeal";

    /**
     * 回调类型-实例
     */
    public static final String WORKFLOW_TYPE_INSTANCE = "instance";

    /**
     * 回调类型-任务
     */
    public static final String WORKFLOW_TYPE_TASK = "task";

    /**
     * 回调实例状态-审批中
     */
    public static final String WORKFLOW_INSTANCE_PENDING = "PENDING";

    /**
     * 回调实例状态-已通过
     */
    public static final String WORKFLOW_INSTANCE_APPROVED = "APPROVED";

    /**
     * 回调实例状态-已通过
     */
    public static final String WORKFLOW_TASK_APPROVED = "APPROVED";

    /**
     * 回调实例状态-已拒绝
     */
    public static final String WORKFLOW_TASK_REJECTED = "REJECTED";

    /**
     * 回调实例状态-自动通过
     */
    public static final String WORKFLOW_TASK_DONE = "DONE";

    /**
     * 员工在职状态集合
     */
    public static final List<Integer> STAFF_ON_JOB_STATE= Lists.newArrayList(3,4);

    /**
     * 执照不存在
     */
    public static final String LICENSE_NOT_FOUND = "执照不存在";
}
