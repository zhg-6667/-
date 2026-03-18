package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2020/8/11/17:17
 * 截止日期
 */
public enum DeadlineStatusEnum {
    /**
     * 0-已过截止日期
     */
    INVALID(0, "失效"),
    /**
     * 1-未到截止日期
     */
    VALID(1, "生效");

    private final int value;
    private final String text;

    DeadlineStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static DeadlineStatusEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (DeadlineStatusEnum v : values()) {
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
