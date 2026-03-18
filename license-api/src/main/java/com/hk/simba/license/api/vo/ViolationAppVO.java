package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.simba.license.api.enums.AppealStatusEnum;
import com.hk.simba.license.api.enums.OrderTypeEnum;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.enums.ViolationPayStatusEnum;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import com.hk.quark.base.entity.serializer.ToStringSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * @description @author 羊皮
 * @since 2020-4-10 10:17:16
 */
@Data
public class ViolationAppVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long staffId;
    /**
     * 名称
     */
    private String name;
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
     * 扣除分数
     */
    private Integer score;
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
     * 申诉状态:11申诉中、12申诉成功、13申诉驳回、14员工撤回
     */
    private Integer appealStatus;
    /**
     * 状态:0已失效、1、生效
     */
    private Integer status;
    /**
     * 支付状态:0待支付、2已支付
     */
    private Integer payStatus;

    public String getPayStatusText() {
        ViolationPayStatusEnum prse = ViolationPayStatusEnum.getEnumByValue(payStatus);
        if (null != prse) {
            return prse.getText();
        }
        return "";
    }

    public String getAppealStatusText() {
        AppealStatusEnum ase = AppealStatusEnum.getEnumByValue(payStatus);
        if (null != ase) {
            return ase.getText();
        }
        return "";
    }

    public String getStatusText() {
        StatusEnum se = StatusEnum.getEnumByValue(payStatus);
        if (null != se) {
            return se.getText();
        }
        return "";
    }

    public String getOrderTypeText() {
        OrderTypeEnum ote = OrderTypeEnum.getEnumByValue(payStatus);
        if (null != ote) {
            return ote.getText();
        }
        return "";
    }

    /**
     * 创建时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date createTime;
    // 以下字段app端不需要展示
    /**
     * 附件
     */
    @JsonIgnore
    private String annex;
    /**
     * 申诉期限
     */
    @JsonIgnore
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date deadlineTime;

    /**
     * 创建人
     */
    @JsonIgnore
    private String createBy;
    /**
     * 更新人
     */
    @JsonIgnore
    private String modifyBy;
    /**
     * 更新时间
     */
    @JsonIgnore
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date modifyTime;
    /**
     * 性别
     */
    @JsonIgnore
    private Integer gender;
    /**
     * 身份证
     */
    @JsonIgnore
    private String idCard;
    /**
     * 是否做饭家员工 0-否 1-是<p>
     * 不能使用基础数据类型boolean，请求参数会默认为false
     */
    @JsonIgnore
    private Boolean cooker;
    /**
     * 手机
     */
    @JsonIgnore
    private String phone;
    /**
     * 所属城市
     */
    @JsonIgnore
    private String cityName;
    /**
     * 所属站点
     */
    @JsonIgnore
    private String siteName;
    /**
     * 所属城市
     */
    @JsonIgnore
    private String cityCode;
    /**
     * 所属站点
     */
    @JsonIgnore
    private Long siteId;
    /**
     * 站长id
     */
    @JsonIgnore
    @JsonSerialize(using = ToStringSerializer.class)
    private Long siteLeaderId;
    /**
     * 站长名字
     */
    @JsonIgnore
    private String siteLeaderName;
    /**
     * 站长手机号
     */
    @JsonIgnore
    private String siteLeaderPhone;

    /**
     * 扣分类型(1=员工违规扣分,2=站点违规扣分)
     */
    private Integer deductType;


    /**
     * 用工模式：0=月薪制（全职），1=订单制（兼职），2=年薪制
     */
    private Integer workType;

}
