package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2020/8/5/13:48
 * 一个周期类的剩余分数
 */
public enum RemainScoreEnum {

    SCORE_REGION_ONE(1, "大于0,小于等于8"),

    SCORE_REGION_TWO(2, "大于8,小于等于16");


    private final int value;
    private final String text;

    RemainScoreEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static RemainScoreEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (RemainScoreEnum v : values()) {
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
