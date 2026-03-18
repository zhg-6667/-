package com.hk.simba.license.service.constant.enums;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author cyh
 * @date 2020/10/22/14:39
 */
public enum IsBlacklistEnum {

    YES(1, "是"),

    NO(0, "否");

    private final int value;
    private final String text;

    IsBlacklistEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static IsBlacklistEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (IsBlacklistEnum v : values()) {
                if (Objects.equals(val, v.getValue())) {
                    return v;
                }
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (IsBlacklistEnum statusEnum : IsBlacklistEnum.values()) {
            Map<String, Object> ob = Maps.newHashMap();
            ob.put("id", statusEnum.getValue());
            ob.put("value", statusEnum.getText());
            list.add(ob);
        }
        return list;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}

