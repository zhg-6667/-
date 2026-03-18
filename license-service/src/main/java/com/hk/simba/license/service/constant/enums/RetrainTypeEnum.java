package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2021/9/3/15:15
 */
public enum RetrainTypeEnum {

    LICENSE_RETRAIN(1, "执照复训"),

    VIOLATION_RETRAIN(2, "违规复训"),

    TRAINING_RETRAIN(3, "培训复训");

    private final int value;
    private final String text;

    RetrainTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static RetrainTypeEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (RetrainTypeEnum v : values()) {
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
