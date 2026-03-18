package com.hk.simba.license.api.request.violation;

import com.hk.simba.license.api.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author chenm
 * @since 2024/10/25
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ViolationTypeVersionPageRequest extends PageRequest implements Serializable {

    private Long versionId;

    private Integer isShow;

    private Integer publishStatus;
}
