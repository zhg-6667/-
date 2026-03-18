package com.hk.simba.license.api.enums;

import java.util.Objects;

/**
 * <p>
 * 文件名称：StatusEnum </p>
 * <p>
 * 文件描述：通用状态枚举</p>
 * <p>
 * 版权所有：版权所有(C)2018-2099 </p>
 * <p>
 * 公司： 好慷 </p>
 * <p>
 * 内容摘要：</p>
 * <p>
 * 其他说明 </p>
 *
 * @author Chenqun
 * @version 1.0
 * @date 2020/4/10 17:26
 */
public enum StatusEnum {
    /**
     * 违规状态枚举 0-失效
     */
    INVALID(0, "系统变更-失效"),
    /**
     * 1-生效
     */
    VALID(1, "生效"),
    /**
     * 2-手工失效
     */
    HAND_INVALID(2, "手工-失效"),
    /**
     * 3-审批成功失效
     */
    APPEAL_INVALID(3, "审批成功-失效"),
    /**
     * 4-待生效
     */
    WAIT_VALID(4, "待生效");

    private final int value;
    private final String text;

    StatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static StatusEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (StatusEnum v : values()) {
                if (Objects.equals(val, v.getValue())) {
                    return v;
                }
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
