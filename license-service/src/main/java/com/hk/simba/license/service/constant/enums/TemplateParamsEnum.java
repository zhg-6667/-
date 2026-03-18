package com.hk.simba.license.service.constant.enums;

import lombok.Getter;

/**
 * @author chenm
 * @since 2022/5/26
 */
@Getter
public enum TemplateParamsEnum {

    NAME("name"),
    VIOLATION_TYPE("violationType"),
    SCORE("score"),
    AMOUNT("amount"),
    VIOLATION_ID("violationId"),
    SERVICE_TIME("serviceTime"),
    VIOLATION_DETAIL("violationDetail"),
    LIMIT_DAY("limitDay"),
    TIMEOUT_PAY("timeoutPay")
    ;

    private final String value;

    TemplateParamsEnum(String value) {
        this.value = value;
    }
}
