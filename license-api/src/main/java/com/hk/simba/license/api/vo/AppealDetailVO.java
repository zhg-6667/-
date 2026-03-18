package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import com.hk.quark.base.entity.serializer.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author cyh
 * @date 2020/5/14/11:29
 * 申诉详情
 */
@Data
public class AppealDetailVO implements Serializable {

    private static final long serialVersionUID = -7195930131228535263L;


    /**
     * 违规id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long violationId;

    /**
     * 事件编号
     */
    private String code;
    /**
     * 违规单号
     */
    private String orderId;

    /**
     * 事件时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date happenTime;
    /**
     * 服务时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date serviceTime;

    /**
     * 违规类型
     */
    private String type;

    /**
     * 违规细则
     */
    private String detail;

    /**
     * 申诉信息内容
     */
    private List<AppealInfo> appealInfoList;

    /**
     * 违规状态(0=失效,1=生效)
     */
    private Integer violationStatus;

    /**
     * 审批日志记录
     */
    private List<AppealApproveLogVO> logList;

    @Data
    public static class AppealInfo implements Serializable {


        private static final long serialVersionUID = 1012782813066711691L;
        /**
         * 申诉id
         */
        @JsonSerialize(using = ToStringSerializer.class)
        private Long id;
        /**
         * 申诉id
         */
        @JsonSerialize(using = ToStringSerializer.class)
        private Long instanceId;

        /**
         * 申诉创建人(指站长)
         */
        private String appealBy;

        /**
         * 员工-申诉原因
         */
        private String appealReason;

        /**
         * 站长申诉理由
         */
        private String siteLeaderAppealReason;

        /**
         * 申诉创建时间
         */
        @JsonSerialize(using = DateTimeSerializer.class)
        private Date appealTime;

        /**
         * 大区审核者
         */
        private String regionApproveBy;

        /**
         * 大区审核(通过或者拒绝)原因
         */
        private String regionApproveReason;

        /**
         * 大区审核时间
         */
        @JsonSerialize(using = DateTimeSerializer.class)
        private Date regionApproveTime;


        /**
         * 质质高审核者
         */
        private String qualityApproveBy;

        /**
         * 质质高审核(通过或者拒绝)原因
         */
        private String qualityApproveReason;

        /**
         * 质质高审核时间
         */
        @JsonSerialize(using = DateTimeSerializer.class)
        private Date qualityApproveTime;


        /**
         * 状态:11申诉中(待大区审批)、12申诉成功、13申诉驳回、14撤回、15待质质高审批
         */
        private Integer status;

        /**
         * 用户按钮控制——大区审批状态
         * 0=未审批，1=通过，2=驳回
         */
        private Integer regionAppeal;

        /**
         * 用户按钮控制——质质高审批状态
         * 0=未审批，1=通过，2=驳回
         */
        private Integer qualityAppeal;

        /**
         * 违规id
         */
        @JsonSerialize(using = ToStringSerializer.class)
        private Long violationId;

        /**
         * 图片附件
         */
        private List<EventAttachment> imageAnnex;

        /**
         * 其他附件
         */
        private List<EventAttachment> otherAnnex;

        /**
         * 客户投诉分析
         */
        private String userComplaintAnalysis;

        /**
         * 站长申诉分析
         */
        private String siteLeaderAppealAnalysis;

        /**
         * 改善理由
         */
        private String improveAdvice;
    }
}
