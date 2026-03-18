package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * <p>
 * 违规支付流水
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Data
public class ViolationPayRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 好慷用户体系的userId
     */
    private Long userId;
    /**
     * 违规记录id
     */
    private Long violationId;
    /**
     * 状态0待支付、1支付成功、2支付失败、3超时
     */
    private Integer status;
    /**
     * 待支付订单号，为唯一标识
     */
    private String tradeOrderCode;
    /**
     * 支付金额
     */
    private BigDecimal totalAmount;
    /**
     * 支付超时时间
     */
    private Date expiretTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date modifyTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 更新人
     */
    private String modifyBy;
    /**
     * 支付收银台链接
     */
    private String payUrl;

}
