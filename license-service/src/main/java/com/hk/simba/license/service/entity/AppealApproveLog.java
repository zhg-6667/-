package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/5/13/15:51
 * 申诉审核日志
 */
@Data
public class AppealApproveLog implements Serializable {


    private static final long serialVersionUID = 3793547096002039824L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /***
     * 申诉id
     */
    private Long appealId;

    /**
     * 违规编号
     */
    private Long violationId;

    /***
     * 审批类型(0=站长,1=大区,2=质质高)
     */
    private Integer approveType;

    /**
     * 审批状态(11=待大区审批;12=审批成功、13=审批驳回;14=撤回(站长撤回);15=待质质高审批)
     */
    private Integer status;

    /**
     * 备注(如拒绝，同意原因)
     */
    private String remark;

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;

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
