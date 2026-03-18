package com.hk.simba.license.api.enums;

import java.util.Objects;

/**
 * <p>
 * 文件名称：AppealStatusEnum </p>
 * <p>
 * 文件描述：违规记录申诉状态</p>
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
 * @date 2020/4/13 9:31
 */
public enum AppealStatusEnum {
    /**
     * 违规记录申诉状态 10-未申诉
     */
    NO_APPEAL(10, "未申诉"),
    /**
     * 11-申诉中
     */
    APPEALING(11, "申诉中"),
    /**
     * 12-申诉成功
     */
    APPEAL_PASS(12, "申诉成功"),
    /**
     * 13-申诉驳回
     */
    APPEAL_REJECT(13, "申诉驳回"),
    /**
     * 14-员工(站长)撤回
     */
    APPEAL_REVOKE(14, "撤回");

    private final int value;
    private final String text;

    AppealStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static AppealStatusEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (AppealStatusEnum v : values()) {
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
