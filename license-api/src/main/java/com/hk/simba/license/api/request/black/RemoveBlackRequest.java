package com.hk.simba.license.api.request.black;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyh
 * @date 2020/10/17/10:59
 * 移除黑名单入参
 */
@Data
public class RemoveBlackRequest implements Serializable {

    private static final long serialVersionUID = -5680429987806902342L;


    /**
     * 黑名单id
     */
    private List<Long> ids;

    /**
     * 移除理由
     */
    private String remark;

    /**
     * 操作者
     */
    private String operator;

}
