package com.hk.simba.license.api.enums;

import java.util.Objects;

/**
 * @ClassName ViolationMessageStatusEnum
 * @Desiption 违规消息状态枚举类
 * @Author chenjh1@homeking365.com
 * @Date 2020-04-17 1:41
 * @Version 1.0
 **/
public enum ViolationMessageStatusEnum {

    WAITTING(0, "待发送"),

    SENDED(1, "已发送"),

    FAILD(2, "发送失败"),

    NO_SEND(3, "不发送");

    private final int value;
    private final String text;

    ViolationMessageStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static ViolationMessageStatusEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (ViolationMessageStatusEnum v : values()) {
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
