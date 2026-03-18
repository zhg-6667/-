package com.hk.simba.license.api.vo;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.DateTimeSerializer;
import com.hk.quark.base.entity.serializer.ToStringSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.hk.simba.base.sensitivedata.annotation.SensitiveData;
import com.hk.simba.base.sensitivedata.annotation.SensitiveDataType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 文件名称：ViolationVo </p>
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
 * @date 2020/4/14 0:06
 */
@Data
public class ViolationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 事件编号
     */
    private String code;
    /**
     * 违规单号
     */
    private String orderId;
    /**
     * 员工id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long staffId;
    /**
     * 名称
     */
    private String name;
    /**
     * 事件时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date happenTime;
    /**
     * 服务时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date serviceTime;
    /**
     * 违规类型
     */
    private String type;
    /**
     * 违规细则
     */
    private String detail;
    /**
     * 扣除分数
     */
    private Integer score;
    /**
     * 支付订单号-流水号
     */
    private String tradeOrderCode;
    /**
     * 罚款金额
     */
    private BigDecimal totalAmount;
    /**
     * 描述
     */
    private String description;
    /**
     * 支付状态:0待支付、1缴费中、2已支付
     */
    private Integer payStatus;
    /**
     * 创建时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date createTime;
    /**
     * 状态:1生效、0失效
     */
    private Integer status;

    /**
     * 申诉状态：申诉状态:10未申诉、11申诉中、12申诉成功、13申诉驳回、14员工撤回
     */
    private Integer appealStatus;
    /**
     * 后台页面展示状态
     */
    private Integer violationStatus;

    /**
     * 0=过期失效，1=有效
     */
    private Integer deadlineStatus;
    /**
     * 附件
     */
    private String annex;
    /**
     * 失效原因
     */
    private String reason;

    /**
     * 是否发起过申诉(0=否，1=是)
     */
    private Integer hasAppeal;

    public List<EventAttachment> getImageAnnex() {
        if (StringUtils.isBlank(annex)) {
            return null;
        }
        List<EventAttachment> arr = JSONArray.parseArray(annex, EventAttachment.class);
        for (Iterator<EventAttachment> it = arr.iterator(); it.hasNext(); ) {
            EventAttachment next = it.next();
            if (!isImage(next.getAttachment())) {
                it.remove();
            }
        }
        return arr;
    }

    public List<EventAttachment> getOtherAnnex() {
        if (StringUtils.isBlank(annex)) {
            return null;
        }
        List<EventAttachment> arr = JSONArray.parseArray(annex, EventAttachment.class);
        for (Iterator<EventAttachment> it = arr.iterator(); it.hasNext(); ) {
            EventAttachment next = it.next();
            if (isImage(next.getAttachment())) {
                it.remove();
            }
        }
        return arr;
    }

    private boolean isImage(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        name = name.toLowerCase();
        String[] extension = new String[]{".bmp", ".jpg", ".jpeg", ".png", ".tif", ".gif", ".pcx", ".tga", ".exif", ".fpx", ".svg", ".psd", ".cdr", ".pcd", ".dxf", ".ufo", ".eps", ".ai", ".raw", ".wmf", ".webp"};
        for (String s : extension) {
            if (name.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 申诉期限
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date deadlineTime;

    /**
     * 创建人
     */
    private String createBy;
    /**
     * 更新人
     */
    private String modifyBy;
    /**
     * 更新时间
     */
    @JsonSerialize(using = DateTimeSerializer.class)
    private Date modifyTime;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 身份证
     */
    private String idCard;
    /**
     * 是否做饭家员工 0-否 1-是
     */
    private Boolean cooker;
    /**
     * 岗位名称
     */
    private String position;
    /**
     * 手机
     */
    @SensitiveData(type = SensitiveDataType.PHONE)
    private String phone;
    /**
     * 所属城市
     */
    private String cityName;
    /**
     * 所属城市编码
     */
    private String cityCode;
    /**
     * 所属站点
     */
    private String siteName;
    /**
     * 所属站点id
     */
    private Long siteId;
    /**
     * 站长id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long siteLeaderId;
    /**
     * 站长名字
     */
    private String siteLeaderName;
    /**
     * 站长手机号
     */
    @SensitiveData(type = SensitiveDataType.PHONE)
    private String siteLeaderPhone;
    /**
     * 站长工号
     */
    private String siteLeaderWorkNum;

    /**
     * 申诉时填写的备注
     */
    private String appealRemark;

    /**
     * 申诉id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long appealId;

    /**
     * 申诉时审批实例id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long appealInstanceId;

    /**
     * 备注
     */
    private String remark;
    /**
     * 扣分类型(1=员工违规扣分,2=站点违规扣分)
     */
    private Integer deductType;
    /**
     * 部门
     */
    private String department;

    /**
     * 员工首次入职时间
     */
    private Date firstEntryTime;

    /**
     * 责任部门类型(1=全部;2=服务人员;3=非服务人员)
     */
    private Integer departmentType;

    /**
     * 事件类型，0普通、1保险理赔
     */
    private Integer eventType;

    /**
     *  用工模式：0=月薪制（全职），1=订单制（兼职），2=年薪制
     */
    private Integer workType;
}
