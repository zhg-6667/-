package com.hk.simba.license.api.request.violation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 文件名称：ViolationQueryRequest </p>
 * <p>
 * 文件描述：条件类型</p>
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
 * @date 2020/4/15 11:03
 */
@Data
public class ViolationQueryRequest implements Serializable {
    private String cityCode;
    /**
     * 所属站点id
     */
    private List<Long> siteIds;
    private String type;
    private Integer status;
    private List<Integer> appealStatus;
    private String code;
    private Long staffId;
    private String orderId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startHappenTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endHappenTime;

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

    /**
     * 违规状态
     */
    private List<Integer> statusList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startJudgmentTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endJudgmentTime;
}
