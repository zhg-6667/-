package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * <p>
 * 违规消息通知
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Data
public class ViolationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 违规id
     */
    private Long violationId;

    /**
     * 违规编号
     */
    private String violationCode;
    /**
     * 员工id
     */
    private Long staffId;
    /**
     * 员工手机号
     */
    private String staffPhone;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * ums消息key
     */
    private String messageKey;
    /**
     * 重试次数
     */
    private Integer tryTimes;
    /**
     * 消息类型：1员工短信、2、员工推送、3站长短信
     */
    private Integer type;
    /**
     * 发送内容
     */
    private String content;
    /**
     * 状态:0待发送、1已发送、2发送失败
     */
    private Integer status;
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
