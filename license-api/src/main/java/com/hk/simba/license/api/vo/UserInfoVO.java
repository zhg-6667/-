package com.hk.simba.license.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author cyh
 * @version 1.0
 * @date 2021/8/16
 * 用户信息
 */
@Data
public class UserInfoVO implements Serializable {
    private static final long serialVersionUID = 3195561210627934562L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户类型
     */
    private Integer type;
    /**
     * 用户首次服务时间
     */
    private Date firstServiceTime;


}
