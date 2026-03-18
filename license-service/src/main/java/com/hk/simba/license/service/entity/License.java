package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/4/24/15:37
 * 执照表
 */
@Data
public class License implements Serializable {
    private static final long serialVersionUID = -9188852242155096591L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 员工id
     */
    private Long staffId;


    /**
     * 乐学用户id
     */
    private String thirdUserId;

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
     * 工种类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂, 8=维修, 9=星级月嫂, 10=严选保姆,11=家庭助理, 12=家务师, 100=其他工种 )
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
    private Long siteId;

    /**
     * 所属站点
     */
    private String siteName;

    /**
     * 是否做饭家员工 0-否 1-是
     */
    @TableField("is_cooker")
    private Boolean cooker;

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

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String modifyBy;
    /**
     * 更新时间
     */
    private Date modifyTime;


}
