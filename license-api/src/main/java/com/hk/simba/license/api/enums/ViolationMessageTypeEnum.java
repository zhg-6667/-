package com.hk.simba.license.api.enums;

import java.util.Objects;

/**
 * @ClassName ViolationMessageStatusEnum
 * @Desiption 违规消息类型枚举类
 * @Author chenjh1@homeking365.com
 * @Date 2020-04-17 1:41
 * @Version 1.0
 **/
public enum ViolationMessageTypeEnum {

    STAFF_SMS(1, "员工短信"),

    STAFF_PUSH(2, "员工推送"),

    SITE_LEADER_SMS(3, "站长短信"),

    NO_PAY_STAFF_SMS(4, "未支付-员工短信"),

    NO_PAY_SITE_LEADER_SMS(5, "未支付-站长短信"),

    MENTOR_SHIP_SMS(6, "师徒制-师傅短信"),

    MENTOR_SHIP_PUSH(7, "师徒制-师傅推送"),

    MENTOR_SHIP_LEADER_SMS(8, "师徒制-站长短信"),

    RETRAIN_STAFF_SMS(9, "执照复训-员工短信"),

    RETRAIN_STAFF_PUSH(10, "执照复训-员工推送"),

    VIOLATION_RETRAIN_STAFF_SMS(11, "违规复训-员工短信"),

    VIOLATION_RETRAIN_STAFF_PUSH(12, "违规复训-员工推送"),

    TRAINING_RETRAIN_STAFF_SMS(13, "培训复训-员工短信"),

    TRAINING_RETRAIN_STAFF_PUSH(14, "培训复训-员工推送");

    private final int value;
    private final String text;

    ViolationMessageTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static ViolationMessageTypeEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (ViolationMessageTypeEnum v : values()) {
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
