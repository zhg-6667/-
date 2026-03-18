package com.hk.simba.license.service.constant.enums;

import java.util.Objects;

/**
 * @author cyh
 * @date 2020/5/13/12:34
 * 审批状态枚举
 */
public enum ApproveStatusEnum {


    WAIT_REGION_APPROVE(11, "待大区审批"),

    APPROVE_SUCCESS(12, "审批成功"),

    APPROVE_REJECT(13, "大区驳回"),

    APPEAL_RECALL(14, "撤回"),

    WAIT_QUALITY_APPROVE(15, "待质质高审批"),

    QUALITY_REJECT(16, "质质高驳回"),

    WAIT_SERVICE_APPROVE(17, "待服务部审批"),

    SERVICE_REJECT(18, "服务部驳回"),

    WAIT_BUSINESS_APPROVE(19, "待事业部审批"),

    BUSINESS_REJECT(20, "事业部驳回");

    private final int value;
    private final String text;

    ApproveStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 通过值获取枚举，不存在时返回null
     *
     * @param val
     * @return
     */
    public static ApproveStatusEnum getEnumByValue(Integer val) {
        if (null != val) {
            for (ApproveStatusEnum v : values()) {
                if (Objects.equals(val, v.getValue())) {
                    return v;
                }
            }
        }
        return null;
    }

    public static ApproveStatusEnum getPassAppealStatus(Integer status) {
        ApproveStatusEnum curStatus = ApproveStatusEnum.getEnumByValue(status);
        ApproveStatusEnum nextStatus = null;
        switch (curStatus) {
            case WAIT_REGION_APPROVE:
                nextStatus = ApproveStatusEnum.WAIT_SERVICE_APPROVE;
                break;
            case WAIT_SERVICE_APPROVE:
                nextStatus = ApproveStatusEnum.WAIT_BUSINESS_APPROVE;
                break;
            case WAIT_BUSINESS_APPROVE:
                nextStatus = ApproveStatusEnum.WAIT_QUALITY_APPROVE;
                break;
            case WAIT_QUALITY_APPROVE:
                nextStatus = ApproveStatusEnum.APPROVE_SUCCESS;
                break;
            default:
                break;
        }
        return nextStatus;
    }

    public static ApproveStatusEnum getRefuseAppealStatus(Integer status) {
        ApproveStatusEnum curStatus = ApproveStatusEnum.getEnumByValue(status);
        ApproveStatusEnum nextStatus = null;
        switch (curStatus) {
            case WAIT_REGION_APPROVE:
                nextStatus = ApproveStatusEnum.APPROVE_REJECT;
                break;
            case WAIT_SERVICE_APPROVE:
                nextStatus = ApproveStatusEnum.SERVICE_REJECT;
                break;
            case WAIT_BUSINESS_APPROVE:
                nextStatus = ApproveStatusEnum.BUSINESS_REJECT;
                break;
            case WAIT_QUALITY_APPROVE:
                nextStatus = ApproveStatusEnum.QUALITY_REJECT;
                break;
            default:
                break;
        }
        return nextStatus;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
