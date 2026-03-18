package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 执照风控停单记录
 *
 * @author chenjh1
 * @date 2024-03-12 15:35
 **/
@Data
public class LicenseStopOrderLog implements Serializable {

    private static final long serialVersionUID = 1;
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
     * 停单记录id
     */
    private Long stopOrderId;

    /**
     * 停单状态：0=失效，1=生效
     */
    private Integer stopOrderStatus;

    /**
     * 停单开始时间
     */
    private LocalDateTime stopOrderBeginTime;

    /**
     * 停单结束时间
     */
    private LocalDateTime stopOrderEndTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

}
