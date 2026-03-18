package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2021/8/24/14:04
 * <p>
 * 用户类型
 */
public enum UserType {

    NO_YEAR_PAY_USER(1, "非包年付费用户"),

    YEAR_PAY_USER(2, "包年付费用户"),

    OTHER(3, "其他");

    private final int value;
    private final String text;

    UserType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static UserType getEnumByValue(Integer val) {
        if (null != val) {
            for (UserType v : values()) {
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
