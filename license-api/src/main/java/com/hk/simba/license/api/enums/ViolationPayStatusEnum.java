package com.hk.simba.license.api.enums;

import java.util.Objects;

/**
 * @description @author 羊皮
 * @since 2020-4-14 9:25:43
 */
public enum ViolationPayStatusEnum {
    /**
     * 支付状态:0待缴交
     */
    NO_PAY(0, "未缴交"),
    /**
     * 支付状态:1已缴交
     */
    PAY(1, "已缴交"),
    /**
     * 支付状态:3超时未缴
     */
    TIMEOUT_PAY(3, "超时未缴"),
    /**
     * 支付状态:4工资缴交
     */
    SALARY_PAY(4, "工资缴交"),
    /**
     * 支付状态:5离职未缴
     */
    DISMISSION_NO_PAY(5, "离职未缴");

    private final int value;
    private final String text;


    ViolationPayStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static ViolationPayStatusEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (ViolationPayStatusEnum v : values()) {
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
