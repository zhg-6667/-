package com.hk.simba.license.api.request.license;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cyh
 * @date 2020/4/27/11:30
 * 执照吊销请求参数
 */
@Data
public class LicenseRevokeRequest implements Serializable {

    private static final long serialVersionUID = -7369332238804019918L;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 执照id
     */
    private Long licenseId;

    /**
     * 吊销理由
     */
    private String reason;

    /**
     * 是否加入黑名单(1=是，0=否)
     */
    private Integer black;

    /**
     * 拉黑类型
     */
    private Integer type;

    /**
     * 拉黑原因
     */
    private String blackListReason;

    /**
     * 拉入黑名单原因为其他的时候传入
     */
    private String remark;

}
