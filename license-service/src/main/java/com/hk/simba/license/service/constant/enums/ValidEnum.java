package com.hk.simba.license.service.constant.enums;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 生效枚举
 *
 * @author chenjh1
 * @date 2024-03-12 15:49
 **/
public enum ValidEnum {

    VALID(1, "生效"),

    INVALID(0, "失效");

    private final Integer value;
    private final String text;

    ValidEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static ValidEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (ValidEnum v : values()) {
                if (Objects.equals(val, v.getValue())) {
                    return v;
                }
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ValidEnum statusEnum : ValidEnum.values()) {
            Map<String, Object> ob = Maps.newHashMap();
            ob.put("id", statusEnum.getValue());
            ob.put("value", statusEnum.getText());
            list.add(ob);
        }
        return list;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}

