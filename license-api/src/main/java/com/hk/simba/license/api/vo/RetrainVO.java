package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import com.hk.quark.base.entity.serializer.ToStringSerializer;
import com.hk.simba.base.sensitivedata.annotation.SensitiveData;
import com.hk.simba.base.sensitivedata.annotation.SensitiveDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/8/3/16:05
 */
@Data
public class RetrainVO implements Serializable {

    private static final long serialVersionUID = 112400429606188892L;
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
     * 电话
     */
    @SensitiveData(type = SensitiveDataType.PHONE)
    private String phone;

    /**
     * 性别(0 =女，1=男)
     */
    private Integer gender;

    /**
     * 工种类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂 )
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
     * 站长id
     */
    private Long siteLeaderId;

    /**
     * 站长名字
     */
    private String siteLeaderName;
    /**
     * 站长手机号
     */
    @SensitiveData(type = SensitiveDataType.PHONE)
    private String siteLeaderPhone;

    /**
     * 违规id
     */
    private Long violationId;

    /**
     * 执照剩余分数区间类型
     */
    private Integer remainScoreType;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 状态(1=待复训,2=通过,3=未通过,4=失效)
     */
    private Integer status;

    /**
     * 复训类型(1=执照复训;2=违规复训;3=培训复训)
     */
    private Integer type;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 附件
     */
    private String annex;

    /**
     * 复训内容
     */
    private String content;

    /**
     * 复训失效原因
     */
    private String reason;

    /**
     * 复训开始时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date startTime;

    /**
     * 复训结束时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date endTime;

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
     * 复训超时时间
     */
    private Integer retrainTimeOut;

    /**
     * 状态变更时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date statusChangeTime;

    /**
     * 培训师id
     */
    private Long trainerId;

    /**
     * 培训师名称
     */
    private String trainerName;
}
