package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2020/10/19/10:06
 * 黑名单类型
 */
public enum BlacklistTypeEnum {

    REVOKE(1, "执照吊销"),

    NEGOTIATION_REMOVE(2, "协商解除"),

    DISCHARGE(3, "辞退"),

    FIRED(4, "开除"),

    REFUSE_PROCESS_RESIGNATION(5, "拒走流程辞职"),

    ARBITRATION_OR_COMPLAINT(6, "仲裁/投诉"),

    CONSENSUS(7, " 协商一致"),

    OTHER(8, "其他");
    private final int value;
    private final String text;

    BlacklistTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static BlacklistTypeEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (BlacklistTypeEnum v : values()) {
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
