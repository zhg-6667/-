package com.hk.simba.license.api.enums;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 发布状态：0=待发布版本；1=当前版本；2=历史版本
 */
@Getter
public enum PublishStatusEnum {
    PENDING(0, "待发布版本"),
    CURRENT(1, "当前版本"),
    HISTORY(2, "历史版本");

    private final Integer value;
    private final String text;

    PublishStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public static List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PublishStatusEnum typeEnum : PublishStatusEnum.values()) {
            Map<String, Object> ob = Maps.newHashMap();
            ob.put("id", typeEnum.getValue());
            ob.put("value", typeEnum.getText());
            list.add(ob);
        }
        return list;
    }
}
