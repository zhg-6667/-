package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 执照考试分组信息
 * </p>
 *
 * @author cyh
 * @since 2021-09-03
 */
@Data
public class LicenseExamGroup implements Serializable {
    private static final long serialVersionUID = -4605308930884575164L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 员工id
     */
    private Long staffId;
    /**
     * 员工姓名
     */
    private String name;
    /**
     * 科目一考试id
     */
    private String firstSubjectExamId;
    /**
     * 科目四考试id
     */
    private String fourthSubjectExamId;
    /**
     * 状态(0=失效，1=生效)
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
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
     * 数据更新时间
     */
    private Date updateTime;

}
