package com.hk.simba.license.service.constant.enums;

/**
 * @author cyh
 * @description 执照状态(1 = 生效 ， 2 = 吊销)
 * @since 2020/4/26
 */
public enum LicenseStatusEnum {
    EFFECTIVE(1, "生效"),
    REVOKE(2, "吊销");

    private Integer code;
    private String name;

    LicenseStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(int code) {
        for (LicenseStatusEnum c : LicenseStatusEnum.values()) {
            if (c.code == code) {
                return c.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }
}
