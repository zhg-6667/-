package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2021/11/11/11:38
 * <p>
 * 责任部门类型
 */
public enum DepartmentTypeEnum {

    ALL(1, "全部"),

    SERVICE_DEPT(2, "服务人员"),

    NO_SERVICE_DEPT(3, "非服务人员");

    private final Integer value;
    private final String text;

    DepartmentTypeEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static DepartmentTypeEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (DepartmentTypeEnum v : values()) {
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
