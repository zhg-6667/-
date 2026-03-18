package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2021/7/16/14:26
 * <p>
 * 扣分类型枚举
 */
public enum DeductTypeEnum {

    STAFF_DEDUCT(1, "员工违规扣分"),

    SITE_DEDUCT(2, "站点违规扣分");

    private final Integer value;
    private final String text;

    DeductTypeEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static DeductTypeEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (DeductTypeEnum v : values()) {
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
