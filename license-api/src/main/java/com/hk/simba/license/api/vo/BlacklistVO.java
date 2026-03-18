package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/10/17/10:26
 * 黑名单信息
 */
@Data
public class BlacklistVO implements Serializable {

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
     * 拉黑原因说明
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
     * 工种类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂 )
     */
    private Integer positionType;

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
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date createTime;


}
