package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/4/26/11:48
 * 执照表扣(加)分记录
 */
@Data
public class LicenseScoreLog implements Serializable {
    private static final long serialVersionUID = -8282358768358485461L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 执照id
     */
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
    private Long violationId;

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
