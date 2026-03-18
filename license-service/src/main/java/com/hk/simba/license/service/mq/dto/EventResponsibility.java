package com.hk.simba.license.service.mq.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * @description @author 羊皮
 * @since 2020-4-16 16:17:34
 */
@Data
public class EventResponsibility implements Serializable {
    /**
     * 责任部门
     */
    private Long responsibilityDept;

    /**
     * 责任部门名
     */
    private String responsibilityDeptName;

    /**
     * 责任人
     */
    private Long responsibilityUser;

    /**
     * 责任人名
     */
    private String responsibilityUserName;

}