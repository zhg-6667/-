package com.hk.simba.license.service.constant.enums;

/**
 * @author zengry
 * @description 统一请求响应码
 * @since 2020/3/5
 */
public enum ResponseCodeEnum {
    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 失败
     */
    FAILED(500, "失败"),
    /**
     * 参数为空
     */
    ERROR_NULL_PARAM(10001, "参数为空"),
    /**
     * 参数错误
     */
    ERROR_PARAM(10002, "参数错误"),

    /**
     * 查无记录
     */
    ERROR_NONE_RECORD(10003, "查无记录"),
    /**
     * 数据不存在或已失效
     */
    ERROR_DATA_NOT_EXISTS(10004, "数据不存在或已失效"),
    /**
     * 创建支付订单失败
     */
    ERROR_CREATE_PAY(10005, "创建支付订单失败"),
    /**
     * 创建支付订单失败
     */
    ERROR_TIMEOUT_PAY(10006, "超时未缴，缴费入口已关闭"),

    ERROR_APPEAL_STATUS(20001, "申诉状态异常"),

    EXIST_APPEAL_DATA(20002, "已存在申诉记录"),

    HAS_APPEALING(20003, "申诉中,不可重复提交"),

    DEADLINE_APPEAL(20004, "超过申诉截止日期,无法发起申诉"),

    CAN_NOT_APPEAL_RECALL(20005, "该申诉已审批,不可撤销"),

    ERROR_APPROVE_TYPE(20006, "审核人员类型不存在"),

    VIOLATION_INVALID(20007, "违规记录已失效"),

    REPEAT_VIOLATION_CODE(20008, "违规编码已存在"),

    NO_WAIT_VALID_STATUS(20009, "违规不是待生效,无法执行对应操作"),
    CAN_NOT_STOP_TYPE(20010, "该违规类型无需停用"),
    CAN_NOT_DELETE_TYPE(20011, "该违规类型已发布，无法删除"),
    EFFECTIVE_TIME_ERROR(20012, "生效时间不能小于最新版本生效时间"),
    EXIST_PUBLISH_STATUS(20013, "已存在待发布版本，不允许新增"),

    EXIST_REVOKE_STATUS(30001, "执照已吊销,无需重复操作"),

    HAS_NO_LICENSE(30003, "您还未考取执照哦"),

    EXIST_LICENSE(30004, "执照已存在"),

    FAIL_THE_EXAM(30005, "考试未通过"),

    EXIST_BLACKLIST(40001, "黑名单已存在"),

    ERROR_BLACK_TYPE(40002, "黑名单类型不存在"),

    EXIST_RETRAIN_CONFIG(50001, "该工种复训配置已存在 "),

    LICENSE_NO_EXIST(50001, "员工执照未生成，无需拉黑");


    private final int code;
    private final String message;

    ResponseCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getName(int code) {
        for (ResponseCodeEnum c : ResponseCodeEnum.values()) {
            if (c.code == code) {
                return c.message;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
