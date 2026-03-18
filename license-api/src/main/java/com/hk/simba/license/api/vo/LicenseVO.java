package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import com.hk.quark.base.entity.serializer.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/4/26/13:54
 * 执照VO
 */
@Data
public class LicenseVO implements Serializable {
    private static final long serialVersionUID = -7210471009179008318L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 员工id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long staffId;

    /**
     * 乐学用户id
     */
    private String thirdUserId;

    /**
     * 乐学考试mapId
     */
    private String mapId;

    /**
     * 名称
     */
    private String name;

    /**
     * 性别(0 =女，1=男)
     */
    private Integer gender;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 手机
     */
    private String phone;

    /**
     * 工种类型()
     */
    private Integer positionType;

    /**
     * 所属城市编码
     */
    private String cityCode;

    /**
     * 所属城市
     */
    private String cityName;

    /**
     * 所属站点id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long siteId;

    /**
     * 所属站点
     */
    private String siteName;

    /**
     * 是否做饭家员工 0-否 1-是
     */
    private Boolean cooker;

    /**
     * 是否加入黑名单
     */
    private Integer black;

    private String blackListReason;

    private String blackListRemark;

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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date effectiveTime;

    /**
     * 失效效时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date expireTime;

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

    /**
     * 吊销时间
     */
    private Date revokeTime;
}

