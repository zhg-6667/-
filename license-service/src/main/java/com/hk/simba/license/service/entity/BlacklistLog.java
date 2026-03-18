package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 黑名单日志
 * </p>
 *
 * @author cyh
 * @since 2020-10-17
 */
@Data
public class BlacklistLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 黑名单类型(1= 系统添加 , 2 = 吊销添加 , 3 = 导入添加)
     * 系统(执照剩余分数小于0) , (手动吊销) , 导入(后台导入)
     */
    private Integer type;

    /**
     * 拉黑原因描述
     */
    private String reason;
    /**
     * 备注
     */
    private String remark;
    /**
     * 操作类型（添加、移除）
     */
    private String operateType;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;


}
