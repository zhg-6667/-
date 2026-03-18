package com.hk.simba.license.api.enums;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 发布形式：1=立即发布；2=定时发布
 */
@Getter
public enum ReleaseTypeEnum {
    IMMEDIATELY(1, "立即发布"),
    TIMING(2, "定时发布");

    private final Integer value;
    private final String text;

    ReleaseTypeEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public static List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReleaseTypeEnum typeEnum : ReleaseTypeEnum.values()) {
            Map<String, Object> ob = Maps.newHashMap();
            ob.put("id", typeEnum.getValue());
            ob.put("value", typeEnum.getText());
            list.add(ob);
        }
        return list;
    }
}
