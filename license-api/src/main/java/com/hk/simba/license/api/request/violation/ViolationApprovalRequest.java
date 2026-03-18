package com.hk.simba.license.api.request.violation;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cyh
 * @date 2021/8/10/16:45\
 * <p>
 * 违规审批入参
 */
@Data
public class ViolationApprovalRequest implements Serializable {
    private static final long serialVersionUID = 967436646297330454L;

    /**
     * 违规id
     */
    private Long id;

    /**
     * 原因
     */
    private String reason;

    /**
     * 状态（1=生效，2=手工-失效）
     */
    private Integer status;

    /**
     * 操作者
     */
    private String operator;
}
