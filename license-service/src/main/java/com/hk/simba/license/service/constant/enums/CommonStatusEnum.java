package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2021/3/10/16:52
 */
public enum CommonStatusEnum {


    INVALID(0, "失效"),

    VALID(1, "生效");

    private final Integer value;
    private final String text;

    CommonStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static CommonStatusEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (CommonStatusEnum v : values()) {
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
