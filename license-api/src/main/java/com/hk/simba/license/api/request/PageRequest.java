package com.hk.simba.license.api.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengry
 * @description
 * @since 2020/3/5
 */

@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 8102614128238863536L;
    private Integer pageNo = 1;
    private Integer pageSize = 10;

}
