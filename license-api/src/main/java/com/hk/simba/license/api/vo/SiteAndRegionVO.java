package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cyh
 * @date 2021/3/4/16:48
 * 站点和大区信息
 */
@Data
public class SiteAndRegionVO implements Serializable {

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
    private String siteLeaderPhone;

    /**
     * 站长邮箱
     */
    private String siteLeaderEmail;

    /**
     * 大区经理-名字
     */
    private String manager;
    /**
     * 大区经理-id
     */
    private Long managerId;
    /**
     * 大区经理-邮箱
     */
    private String managerEmail;
    /**
     * 大区经理-手机号
     */
    private String managerPhone;

    /**
     * 责任培训师id
     */
    private Long trainingTeacherId;

    /**
     * 责任培训师名称
     */
    private String trainingTeacherName;

    /**
     * 责任培训师手机号
     */
    private String trainingTeacherPhone;

    /**
     * 责任培训师邮箱
     */
    private String trainingTeacherEmail;

}
