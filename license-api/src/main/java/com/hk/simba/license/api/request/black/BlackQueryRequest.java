package com.hk.simba.license.api.request.black;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cyh
 * @date 2020/10/17/10:38
 * 黑名单分页查询入参
 */
@Data
public class BlackQueryRequest implements Serializable {
    private static final long serialVersionUID = -1382908274047018514L;

    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 名称
     */
    private String name;

    /**
     * 手机
     */
    private String phone;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 黑名单类型(1= 系统添加 , 2 = 吊销添加 , 3 = 导入添加)
     * 系统(执照剩余分数小于0) , (手动吊销) , 导入(后台导入)
     */
    private Integer type;

}
