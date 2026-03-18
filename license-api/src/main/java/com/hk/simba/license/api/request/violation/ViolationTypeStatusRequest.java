package com.hk.simba.license.api.request.violation;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chenm
 * @since 2024/10/27
 **/
@Data
public class ViolationTypeStatusRequest implements Serializable {

    private Long id;

    private Integer publishStatus;

    /**
     * 失效时间
     */
    private Date failureTime;

    private Integer isShow;

    private String modifyBy;

    private Date modifyTime;

    private Long versionId;
}
