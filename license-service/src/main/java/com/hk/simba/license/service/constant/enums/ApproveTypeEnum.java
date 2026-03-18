package com.hk.simba.license.service.constant.enums;

import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * @author cyh
 * @date 2020/5/13/12:34
 * 审批人员类型
 */
public enum ApproveTypeEnum {

    SITE_LEADER(0, "站长"),

    REGION(1, "城市或大区"),

    QUALITY(2, "质质高"),

    SERVICE(3, "服务部"),

    BUSINESS(4, "事业部");

    private final int value;
    private final String text;

    ApproveTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static ApproveTypeEnum getEnumByValue(Integer val) {
        if (ObjectUtils.isEmpty(val)) {
            return null;
        }
        for (ApproveTypeEnum v : values()) {
            if (Objects.equals(val, v.getValue())) {
                return v;
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
