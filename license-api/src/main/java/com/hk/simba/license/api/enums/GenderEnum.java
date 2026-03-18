package com.hk.simba.license.api.enums;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文件名称：GenderEnum </p>
 * <p>
 * 文件描述：条件类型</p>
 * <p>
 * 版权所有：版权所有(C)2018-2099 </p>
 * <p>
 * 公司： 好慷 </p>
 * <p>
 * 内容摘要：</p>
 * <p>
 * 其他说明 </p>
 *
 * @author Chenqun
 * @version 1.0
 * @date 2020/4/16 10:58
 */
public enum GenderEnum {
    /**
     * 性别
     */
    WOMAN(0, "女"),
    MAN(1, "男");

    private final int value;
    private final String text;

    private GenderEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    GenderEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public static GenderEnum getEnum(int value) {
        GenderEnum[] genderEnums = values();
        int size = genderEnums.length;

        for (int i = 0; i < size; i++) {
            GenderEnum genderEnum = genderEnums[i];
            if (value == genderEnum.getValue()) {
                return genderEnum;
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (GenderEnum genderEnum : GenderEnum.values()) {
            Map<String, Object> ob = Maps.newHashMap();
            ob.put("id", genderEnum.getValue());
            ob.put("value", genderEnum.getText());
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

