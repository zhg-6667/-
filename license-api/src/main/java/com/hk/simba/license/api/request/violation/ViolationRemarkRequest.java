package com.hk.simba.license.api.request.violation;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cyh
 * @date 2021/3/5/17:21
 * 违规备注入参
 */
@Data
public class ViolationRemarkRequest implements Serializable {
    private static final long serialVersionUID = -7450300527933497109L;

    /**
     * 违规id
     */
    private Long id;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作者
     */
    private String operator;
}
