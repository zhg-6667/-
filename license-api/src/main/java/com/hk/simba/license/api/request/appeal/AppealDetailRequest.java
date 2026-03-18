package com.hk.simba.license.api.request.appeal;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cyh
 * @date 2020/5/14/13:43
 */
@Data
public class AppealDetailRequest implements Serializable {
    private static final long serialVersionUID = -8849394659954081119L;

    /**
     * 违规编号
     */
    private Long violationId;


    /**
     * 申诉id
     */
    private Long appealId;


    /**
     * 操作者
     */
    private String operator;

    /**
     * 备注
     */
    private String remark;

    /***
     * 审批类型(0=站长,1=大区,2=质质高)
     */
    private Integer approveType;

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
