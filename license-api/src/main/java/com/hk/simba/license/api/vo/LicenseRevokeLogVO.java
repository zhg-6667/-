package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import com.hk.quark.base.entity.serializer.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/4/27/14:01
 */
@Data
public class LicenseRevokeLogVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 员工id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long staffId;

    /**
     * 执照id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long licenseId;


    /**
     * 吊销原因
     */
    private String reason;

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date createTime;
}
