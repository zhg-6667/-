package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * <p>
 * 违规类型
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Data
public class ViolationType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 分值
     */
    private Integer score;
    /**
     * 扣分类型(1=员工违规扣分,2=站点违规扣分)
     */
    private Integer deductType;
    /**
     * 金额
     */
    private BigDecimal fee;
    /**
     * 违规类型
     */
    private String type;
    /**
     * 责任部门类型(1=全部;2=服务人员;3=非服务人员)
     */
    private Integer departmentType;
    /**
     * 违规详情
     */
    private String detail;
    /**
     * 事件类型，0默认、1保险理赔
     */
    private Integer eventType;
    /**
     * 状态，0禁用、1启用
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

    /**
     * 判断事件是否结束:0=不判断；1=判断
     */
    private Integer checkEventOver;

    /**
     * 处理方式：0=不限制；1=限制
     */
    private Integer processMode;

    /**
     * 生效时间
     */
    private Date effectiveTime;

    /**
     * 失效时间
     */
    private Date failureTime;

    /**
     * 删除状态：0=未删除；1=已删除
     */
    private Integer deleted;

    /**
     * 是否显示：0=不显示；1=显示
     */
    private Integer isShow;

    /**
     * 标题
     */
    private String title;

    /**
     * 备注
     */
    private String remark;

    /**
     * 发布状态：0=待发布版本；1=当前版本；2=历史版本
     */
    private Integer publishStatus;

    /**
     * 版本号
     */
    private String version;

    /**
     * 版本id
     */
    private Long versionId;

    /**
     * 发布形式：1=立即发布；2=定时发布
     */
    private Integer releaseType;
}
