package com.hk.simba.license.api.request.black;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyh
 * @date 2020/10/17/14:06
 * 创建黑名单入参
 */
@Data
public class CreateBlackRequest implements Serializable {
    private static final long serialVersionUID = -8843091219099532896L;

    /**
     * 员工id
     */
    private Long staffId;
    /**
     * 操作者
     */
    private String operator;

    /**
     * 员工id集
     */
    private List<Long> staffIds;

    /**
     * 黑名单类型(1= 系统添加 , 2 = 吊销添加 , 3 = 导入添加)
     */
    private Integer type;

    /**
     * 拉入黑名单原因
     */
    private String blackListReason;

    /**
     * 拉黑备注理由
     */
    private String remark;
}
