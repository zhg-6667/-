package com.hk.simba.license.service.mq.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * @description @author 羊皮
 * @since 2020-4-17 11:31:08
 */
@Data
public class PaySuccessEntity implements Serializable {

    /**
     * 交易id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 交易编号
     */
    private String code;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 完成时间
     */
    private Date finishTime;
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    /**
     * 状态
     */
    private String status;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 交易订单号
     */
    private String tradeOrderCode;
    /**
     * 交易订单类型
     */
    private String tradeOrderType;
    /**
     * 描述
     */
    private String tradeSummary;
    /**
     * 交易类型
     */
    private String tradeType;
}
