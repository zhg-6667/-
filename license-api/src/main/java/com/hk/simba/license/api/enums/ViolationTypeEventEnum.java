package com.hk.simba.license.api.enums;

import java.util.Objects;

/**
 * @ClassName ViolationTypeEventEnum
 * @Desiption 违规类型事件类型枚举类
 * @Author chenjh1@homeking365.com
 * @Date 2020-04-17 1:41
 * @Version 1.0
 **/
public enum ViolationTypeEventEnum {

    INSURANCE(1, "保险理赔"),

    NORMAL(0, "普通");

    private final int value;
    private final String text;

    ViolationTypeEventEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static ViolationTypeEventEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (ViolationTypeEventEnum v : values()) {
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
