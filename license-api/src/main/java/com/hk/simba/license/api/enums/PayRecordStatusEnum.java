package com.hk.simba.license.api.enums;

import java.util.Objects;

/**
 * @description @author 羊皮
 * @since 2020-4-14 9:29:10
 */
public enum PayRecordStatusEnum {
    /**
     * 状态0待支付、1支付成功、2超时
     */
    NO_PAY(0, "待支付"),
    /**
     * 状态0待支付、1支付成功、2超时
     */
    PAY(1, "已支付"),
    /**
     * 状态0待支付、1支付成功、2超时
     */
    OVERTIME(2, "超时");

    private final Integer value;
    private final String text;

    PayRecordStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static PayRecordStatusEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (PayRecordStatusEnum v : values()) {
                if (Objects.equals(val, v.getValue())) {
                    return v;
                }
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
