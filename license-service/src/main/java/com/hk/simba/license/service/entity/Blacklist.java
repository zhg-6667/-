package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 黑名单
 * </p>
 *
 * @author cyh
 * @since 2020-10-17
 */
@Data
public class Blacklist implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
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
     * 拉黑原因
     */
    private String reason;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别(0 =女，1=男)
     */
    private Integer gender;

    /**
     * 手机
     */
    private String phone;


    /**
     * 身份证
     */
    private String idCard;
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

}
