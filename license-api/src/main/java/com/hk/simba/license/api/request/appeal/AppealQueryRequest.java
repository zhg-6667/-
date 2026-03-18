package com.hk.simba.license.api.request.appeal;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author cyh
 * @date 2020/5/14/10:26
 */
@Data
public class AppealQueryRequest implements Serializable {

    private static final long serialVersionUID = -3717997397943933671L;

    /**
     * 城市编码
     */
    private String cityCode;
    /**
     * 站点id
     */
    private List<Long> siteIds;
    /**
     * 违规类型
     */
    private String type;

    /**
     * 申诉状态
     */
    private Integer status;

    /**
     * 事件id
     */
    private String code;
    /**
     * 员工id
     */
    private Long staffId;
    /**
     * 违规单号
     */
    private String orderId;
    /**
     * 事件发生开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startHappenTime;
    /**
     * 事件发生结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endHappenTime;

    /**
     * 违规状态(0=失效,1=生效)
     */
    private Integer violationStatus;

    //服务开始和结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startServiceTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endServiceTime;

    /**
     * 工种类型
     */
    private String position;

    /**
     * 支付状态:0-未缴交 , 1-已缴交, 3-超时未缴 , 4-工资缴交
     */
    private Integer payStatus;

    /**
     * 扣除分数扣分类型(1=员工违规扣分,2=站点违规扣分)
     */
    private Integer deductType;

    /**
     * 违规细则
     */
    private String detail;

    /**
     * 部门
     */
    private String department;
}
