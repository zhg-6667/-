package com.hk.simba.license.service.mq.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description @author 羊皮
 * @since 2020-4-16 16:07:09
 */
@Data
public class MessageEntity implements Serializable {

    private String userName;
    /**
     * 关联订单号
     */
    private String orderId;
    /**
     * 关联工单号
     */
    private String workOrderId;
    private String configName;
    /**
     * 员工ID_姓名（多人逗号分隔）
     */
    private String staff;
    private String configResultName;
    private String statusName;
    /**
     * 订单服务时间
     */
    private Date serviceTime;
    /**
     * 是否做饭家
     */
    private Boolean isNurseClue;
    /**
     * 事件附件
     */
    private List<EventAttachment> eventAttachmentDtos;
    /**
     * 事件id
     */
    private String id;
    /**
     * 事件类型ID
     */
    private Long configId;
    private Long configResultId;
    private Long secondConfigResultId;
    private Long thirdConfigResultId;
    /**
     * 事件描述
     */
    private String content;
    private int status;
    /**
     * 事件创建时间
     */
    private Date createTime;

    /**
     * 处理成本
     */
    private BigDecimal processCost;

    private List<EventResponsibility> eventResponsibilities;
    /**
     * 站点信息
     * 格式:站点ID_站点名
     */
    private String site;
    /**
     * 投诉用户的id
     */
    private String userIdString;

    /**
     * 事件处理方式1
     */
    private String processMode1;

    /**
     * 事件处理方式2
     */
    private String processMode2;

    /**
     * 事件处理方式3
     */
    private String processMode3;

    /**
     * 处理方式列表
     */
    private List<String> processList;

}
