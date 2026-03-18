package com.hk.simba.license.service.constant.enums;

/**
 * @author cyh
 * @date 2020/3/19/18:38
 * 工种类型
 */
public enum PositionTypeEnum {
    CLEANING(1, "保洁师"),
    CLEANER(2, "精洁师"),
    STORAGE(3, "收纳师"),
    PET(4, "洁宠师"),
    LEASER(5, "租赁管家"),
    COOKER(6, "做饭师"),
    MATERNITY(7, "月嫂"),
    MAINTENANCE(8, "维修"),
    STAR_MATERNITY_MATRON(9, "星级月嫂"),
    STRICT_NANNY(10, "严选保姆"),
    FAMILY_ASSISTANT(11, "家庭助理"),
    HOMEWORKER(12, "家务师"),
    WASH_CARE(13, "洗护师"),
    CLEAN_CARE(14, "精洁养护师"),
    COOKING_NANNY(15, "做饭保姆"),
    ACCOMPANYING(16, "陪行师"),
    CHAPERONAGE(17, "陪护师"),
    MOVER(18, "搬家师"),
    SORTER(19, "整理师"),
    CAREGIVER(20, "照护师"),
    PRIMARY_CLEANING(22, "初级保洁师"),
    CARETAKER(23, "打理师"),
    BREEDER(24, "喂养师"),
    CERTIFIED_NANNY(25, "认证保姆"),
    INSPECTOR(26, "检测师"),
    CERTIFIED_PARENTING_TEACHER(27, "认证育儿嫂"),
    CERTIFIED_MATRON(28, "认证月嫂"),
    OTHER(100, "其他工种");

    private Integer type;
    private String value;

    PositionTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public static String getValue(int type) {
        for (PositionTypeEnum c : PositionTypeEnum.values()) {
            if (c.type == type) {
                return c.value;
            }
        }
        return null;
    }

    public static Integer getType(String value) {
        //TODO 改为数据库存储
        for (PositionTypeEnum typeEnum : PositionTypeEnum.values()) {
            if (typeEnum.getValue().startsWith(value)) {
                return typeEnum.getType();
            }
        }
        return PositionTypeEnum.OTHER.getType();
    }

    public static PositionTypeEnum get(int type) {
        for (PositionTypeEnum typeEnum : PositionTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public Integer getType() {
        return type;
    }
}
