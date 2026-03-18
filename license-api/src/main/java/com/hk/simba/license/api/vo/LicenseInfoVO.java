package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import com.hk.quark.base.entity.serializer.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName LicenseInfoVO
 * @Desiption 执照信息
 * @Author chenjh1@homeking365.com
 * @Date 2020-09-18 10:55
 * @Version 1.0
 **/
@Data
public class LicenseInfoVO implements Serializable {
    private static final long serialVersionUID = -7210471009179008318L;
    /**
     * 执照id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 员工id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long staffId;

    /**
     * 名称
     */
    private String name;

    /**
     * 工种类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂， 100=其他工种)
     */
    private Integer positionType;

    /**
     * 性别(0 =女，1=男)
     */
    private Integer gender;

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
     * 剩余分数
     */
    private Integer remainScore;

    /**
     * 状态(1=生效，2=吊销)
     */
    private Integer status;

    /**
     * 吊销原因
     */
    private String reason;

    /**
     * 生效时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date effectiveTime;

    /**
     * 失效时间（下一清分日期）
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date expireTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date modifyTime;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 电话
     */
    private String phone;

    /**
     * 所属城市编码
     */
    private String cityCode;

    /**
     * 所属城市
     */
    private String cityName;

    /**
     * 是否是做饭家
     */
    private Boolean cooker;

}

