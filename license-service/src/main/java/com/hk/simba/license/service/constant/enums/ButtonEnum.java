package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2020/5/22/10:23
 */
public enum ButtonEnum {

    WAIT_APPROVE(0, "未审批"),
    PASS(1, "通过"),
    REJECT(2, "驳回");

    private final int value;
    private final String text;

    ButtonEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static ButtonEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (ButtonEnum v : values()) {
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
