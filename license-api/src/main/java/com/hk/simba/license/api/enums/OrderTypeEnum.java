package com.hk.simba.license.api.enums;

import java.util.Objects;

/**
 * @description @author 羊皮
 * @since 2020-4-21 15:56:08
 */
public enum OrderTypeEnum {

    /**
     * 订单类型 1-服务单 2-工作单
     */
    TYPE_1(1, "服务单"),
    /**
     * 订单类型 1-服务单 2-工作单
     */
    TYPE_2(2, "工作单");

    private final int value;
    private final String text;

    OrderTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static OrderTypeEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (OrderTypeEnum v : values()) {
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
