package com.hk.simba.license.service.mq.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @author pengdl
 * @since 2024-02-02
 */
@Data
public class LicenseChangeEntity implements Serializable {
    /**
     * 执照ID
     */
    private Long id;

    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 是否加入黑名单(0-否 1-是)
     */
    private Integer black;

    /**
     * 剩余分数
     */
    private Integer remainScore;

    /**
     * 吊销原因
     */
    private String reason;

    /**
     * 状态(1=生效，2=吊销)
     */
    private Integer status;

    /**
     * 生效时间
     */
    private Date effectiveTime;

    /**
     * 失效效时间
     */
    private Date expireTime;

}
