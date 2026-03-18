package com.hk.simba.license.api.request.violation;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ViolationPayStatusRequest
 * @Desiption 违规支付状态变更
 * @Author chenjh1@homeking365.com
 * @Date 2020-07-30 13:46
 * @Version 1.0
 **/
@Data
public class ViolationPayStatusRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 违规ids
     */
    private List<Long> ids;

    /**
     * 支付状态
     */
    private int payStatus;

    /**
     * 操作者
     */
    private String operator;
}
