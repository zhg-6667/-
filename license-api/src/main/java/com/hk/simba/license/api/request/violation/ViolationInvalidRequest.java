package com.hk.simba.license.api.request.violation;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyh
 * @date 2020/6/22/14:32
 */
@Data
public class ViolationInvalidRequest implements Serializable {
    private static final long serialVersionUID = 4637138391209382547L;
    /**
     * 违规ids
     */
    private List<Long> ids;

    /**
     * 失效原因
     */
    private String reason;

    /**
     * 操作者
     */
    private String operator;
}
