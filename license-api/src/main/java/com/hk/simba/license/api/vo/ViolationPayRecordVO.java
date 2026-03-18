package com.hk.simba.license.api.vo;

import com.hk.simba.license.api.enums.ViolationPayStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * @description @author 羊皮
 * @since 2020-4-13 13:57:03
 */
@Data
public class ViolationPayRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 好慷用户体系的userId
     */
    private String userId;
    /**
     * 违规记录id
     */
    private Long violationId;
    /**
     * 状态0待支付、1支付成功、2支付失败、3超时
     */
    private Integer status;

    public String getStatusText() {
        ViolationPayStatusEnum vpse = ViolationPayStatusEnum.getEnumByValue(status);
        if (null != vpse) {
            return vpse.getText();
        }
        return "";
    }

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
