package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import com.hk.quark.base.entity.serializer.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/4/26/14:09
 */
@Data
public class LicenseScoreLogVO implements Serializable {

    private static final long serialVersionUID = -3049519008027576806L;

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
     * 扣掉(加)的分数,扣掉用负的表示,加的用正号
     */
    private Integer deductScore;

    /**
     * 原因
     */
    private String reason;

    /**
     * 违规id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long violationId;

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date createTime;
    /**
     * 更新人
     */
    private String modifyBy;
    /**
     * 更新时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date modifyTime;
}
