package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * <p>
 * 申诉信息表
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Data
public class Appeal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 违规编号
     */
    private Long violationId;
    /**
     * 审批实例id
     */
    private Long instanceId;
    /**
     * 申诉时间
     */
    private Date appealTime;
    /**
     * 备注-员工申诉原因
     */
    private String remark;

    /**
     * 备注-站长申诉原因
     */
    private String siteLeaderRemark;
    /**
     * 审核时间
     */
    private Date dealTime;
    /**
     * 状态:11申诉中(待大区审批)、12申诉成功、13申诉驳回、14员工撤回、15待质质高审批
     */
    private Integer status;

    /**
     * 附件
     */
    private String annex;

    /**
     * 撤回时间
     */
    private Date recallTime;

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
