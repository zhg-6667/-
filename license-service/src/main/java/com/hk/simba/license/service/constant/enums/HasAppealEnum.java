package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2020/6/22/17:58
 */
public enum HasAppealEnum {

    NO(0, "未发起申诉"),
    YES(1, "已发起申诉");

    private final int value;
    private final String text;

    HasAppealEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static HasAppealEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (HasAppealEnum v : values()) {
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
