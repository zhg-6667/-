package com.hk.simba.license.service.constant.enums;

import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * @author chenjh
 * @date 2022/11/28 19:34
 * 飞书审批任务节点类型-与ApproveTypeEnum对应
 */
public enum TaskNodeEnum {

    SITE_LEADER(0, "发起"),

    REGION(1, "城市或大区审批"),

    QUALITY(2, "质质高审批"),

    SERVICE(3, "服务部审批"),

    BUSINESS(4, "事业部审批");

    private final int value;
    private final String text;

    TaskNodeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static TaskNodeEnum getEnumByValue(Integer val) {
        if (ObjectUtils.isEmpty(val)) {
            return null;
        }
        for (TaskNodeEnum v : values()) {
            if (Objects.equals(val, v.getValue())) {
                return v;
            }
        }
        return null;
    }

    /**
     * 通过内容获取枚举，不存在时返回null
     *
     * @param text
     * @return
     */
    public static TaskNodeEnum getEnumByText(String text) {
        if (ObjectUtils.isEmpty(text)) {
            return null;
        }
        for (TaskNodeEnum v : values()) {
            if (Objects.equals(text, v.getText())) {
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
