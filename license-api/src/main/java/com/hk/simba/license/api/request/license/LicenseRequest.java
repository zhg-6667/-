package com.hk.simba.license.api.request.license;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2021/3/16/18:10
 */
@Data
public class LicenseRequest implements Serializable {

    private static final long serialVersionUID = -574973130845434885L;
    /**
     * 操作者
     */
    private String operator;

    /**
     * 执照id
     */
    private Long licenseId;

    /**
     * 理由
     */
    private String reason;

    /**
     * 是否需要设置执照分
     */
    private Boolean setScore;

    /**
     * 执照分
     */
    private Integer score;

    /**
     * 执照开始日期
     */
    private Date effectiveTime;
}
