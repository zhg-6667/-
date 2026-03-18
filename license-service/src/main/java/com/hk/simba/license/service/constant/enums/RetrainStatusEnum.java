package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2020/8/3/17:27
 * 复训状态
 */
public enum RetrainStatusEnum {


    WAIT_RETRAIN(1, "待复训"),

    PASS(2, "通过"),

    FAIL(3, "未通过"),

    INVALID(4, "失效");


    private final int value;
    private final String text;

    RetrainStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static RetrainStatusEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (RetrainStatusEnum v : values()) {
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