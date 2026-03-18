package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/4/27/12:57
 * 吊销记录
 */
@Data
public class LicenseRevokeLog implements Serializable {

    private static final long serialVersionUID = 2973173224082046478L;
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
     * 吊销原因
     */
    private String reason;

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;

}
