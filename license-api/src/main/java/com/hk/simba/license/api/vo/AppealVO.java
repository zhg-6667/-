package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import com.hk.quark.base.entity.serializer.ToStringSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;


/**
 * @author cyh
 * @version 1.0
 * @date 2020/5/13
 * 申诉信息
 */
@Data
public class AppealVO implements Serializable {


    private static final long serialVersionUID = 6958401584886025501L;
    /**
     * 申诉id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 违规id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long violationId;

    /**
     * 审批实例id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long instanceId;
    /**
     * 事件编号
     */
    private String code;
    /**
     * 违规单号
     */
    private String orderId;
    /**
     * 员工id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long staffId;
    /**
     * 名称
     */
    private String name;


    /**
     * 性别
     */
    private Integer gender;

    /**
     * 是否做饭家员工 0-否 1-是
     */
    private Boolean cooker;
    /**
     * 岗位名称
     */
    private String position;
    /**
     * 员工手机
     */
    private String phone;


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
     * 扣除分数
     */
    private Integer score;

    /**
     * 罚款金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付状态:0待支付、1已支付
     */
    private Integer payStatus;

    /**
     * 状态:11申诉中(待大区审批)、12申诉成功、13申诉驳回、14撤回、15待质质高审批
     */
    private Integer status;


    /**
     * 所属城市
     */
    private String cityName;
    /**
     * 所属城市编码
     */
    private String cityCode;
    /**
     * 所属站点
     */
    private String siteName;
    /**
     * 所属站点id
     */
    private Long siteId;
    /**
     * 站长id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long siteLeaderId;
    /**
     * 站长名字
     */
    private String siteLeaderName;
    /**
     * 站长手机号
     */
    private String siteLeaderPhone;
    /**
     * 站长工号
     */
    private String siteLeaderWorkNum;
    /**
     * 申诉期限
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date deadlineTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date createTime;
    /**
     * 更新人
     */
    private String modifyBy;
    /**
     * 更新时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date modifyTime;

    /**
     * 大区审批状态
     * 0=未审批，1=通过，2=驳回
     */
    private Integer regionAppeal;

    /**
     * 质质高审批状态
     * 0=未审批，1=通过，2=驳回
     */
    private Integer qualityAppeal;

    /**
     * 违规状态(0=失效,1=生效)
     */
    private Integer violationStatus;

    /**
     * 扣分类型(1=员工违规扣分,2=站点违规扣分)
     */
    private Integer deductType;

    /**
     * 违规细则
     */
    private String detail;

    /**
     * 部门
     */
    private String department;

    /**
     * 用工模式：0=月薪制（全职），1=订单制（兼职），2=年薪制
     */
    private Integer workType;


}
