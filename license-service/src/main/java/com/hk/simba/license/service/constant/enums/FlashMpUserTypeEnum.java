package com.hk.simba.license.service.constant.enums;

import lombok.Getter;

/**
 * @author chenm
 * @since 2022/5/26
 */
@Getter
public enum FlashMpUserTypeEnum {

    STAFF(1, "员工", "STAFF"),
    EMPLOYEE(2, "职员", "EMPLOYEE"),
    CUSTOMER(3, "用户", "CUSTOMER");

    private final Integer value;
    private final String text;
    private final String symbol;

    FlashMpUserTypeEnum(Integer value, String text, String symbol) {
        this.value = value;
        this.text = text;
        this.symbol = symbol;
    }
}
