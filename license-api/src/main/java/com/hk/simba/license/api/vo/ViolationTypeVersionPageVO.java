package com.hk.simba.license.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chenm
 * @since 2024/10/25
 **/
@Data
public class ViolationTypeVersionPageVO implements Serializable {

    /**
     * 违规类型id
     */
    private Long id;

    /**
     * 版本号
     */
    private String version;

    /**
     * 发布状态：0=待发布版本；1=当前版本；2=历史版本
     */
    private Integer publishStatus;

    /**
     * 标题
     */
    private String title;

    /**
     * 生效时间
     */
    private Date effectiveTime;

    /**
     * 失效时间
     */
    private Date failureTime;

    /**
     * 修改人
     */
    private String modifyBy;

    private Date modifyTime;
}
