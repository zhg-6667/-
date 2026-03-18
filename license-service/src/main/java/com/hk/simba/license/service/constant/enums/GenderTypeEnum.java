package com.hk.simba.license.service.constant.enums;

/**
 * @author cyh
 * @date 2020/4/26/17:03
 * 性别(0 =女，1=男)
 */
public enum GenderTypeEnum {
    GIRL(0, "女"),
    BOY(1, "男");

    private Integer type;
    private String value;

    GenderTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getName(int type) {
        for (GenderTypeEnum c : GenderTypeEnum.values()) {
            if (c.type == type) {
                return c.value;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public Integer getType() {
        return type;
    }
}
