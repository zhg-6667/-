package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * <p>
 * 违规信息
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Data
public class Violation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 违规编号
     */
    private String code;
    /**
     * 违规单号
     */
    private String orderId;
    /**
     * 订单类型 1-服务单 2-工作单
     */
    private Integer orderType;
    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 用工模式：0=月薪制（全职），1=订单制（兼职），2=年薪制
     */
    @TableField("employment_type")
    private Integer workType;

    /**
     * 员工在用户系统的id
     */
    private Long userId;
    /**
     * 名称
     */
    private String name;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 身份证
     */
    private String idCard;
    /**
     * 是否做饭家员工 0-否 1-是
     */
    @TableField("is_cooker")
    private Boolean cooker;
    /**
     * 岗位名称
     */
    private String position;
    /**
     * 手机
     */
    private String phone;
    /**
     * 所属城市
     */
    private String cityName;
    /**
     * 所属站点
     */
    private String siteName;
    /**
     * 所属城市编码
     */
    private String cityCode;
    /**
     * 所属站点id
     */
    private Long siteId;
    /**
     * 站长id
     */
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
     * 事件时间
     */
    private Date happenTime;
    /**
     * 服务时间
     */
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
     * 违规类型编号-对应事件系统
     */
    private String violationType;

    /**
     * 违规类型id
     */
    private Long violationTypeId;
    /**
     * 扣除分数
     */
    private Integer score;
    /**
     * 扣分类型(1=员工违规扣分,2=站点违规扣分)
     */
    private Integer deductType;
    /**
     * 支付订单号-流水号
     */
    private String tradeOrderCode;
    /**
     * 罚款金额
     */
    private BigDecimal totalAmount;
    /**
     * 描述
     */
    private String description;
    /**
     * 部门
     */
    private String department;
    /**
     * 附件
     */
    private String annex;
    /**
     * 申诉期限
     */
    private Date deadlineTime;
    /**
     * 支付状态:0待支付、1已缴交、3超时未缴、4工资缴交
     */
    private Integer payStatus;
    /**
     * 状态:0系统变更-失效、1已生效、2手工-失效、3审批成功-失效、4待生效
     */
    private Integer status;
    /**
     * 申诉状态:11申诉中、12申诉成功、13申诉驳回、14员工撤回
     */
    private Integer appealStatus;

    /**
     * 责任部门类型(1=全部;2=服务人员;3=非服务人员)
     */
    private Integer departmentType;

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String modifyBy;
    /**
     * 更新时间
     */
    private Date modifyTime;
    /**
     * 失效原因
     */
    private String reason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 事件类型，0普通、1保险理赔
     */
    private Integer eventType;

}
