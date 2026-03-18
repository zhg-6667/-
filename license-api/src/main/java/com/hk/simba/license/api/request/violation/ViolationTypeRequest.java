package com.hk.simba.license.api.request.violation;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author cyh
 * @date 2021/3/10/11:42
 */
@Data
public class ViolationTypeRequest implements Serializable {

    private static final long serialVersionUID = -368406467345356047L;

    private Long id;
    /**
     * 编码(即事件系统的事件类型ID)
     */
    private String code;
    /**
     * 名称(最小级别分类名,如违规共有三级，则为三级分类名，违规有两级，则为二级分类名)
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 分值
     */
    private Integer score;
    /**
     * 扣分类型(1=员工违规扣分,2=站点违规扣分)
     */
    private Integer deductType;
    /**
     * 金额
     */
    private BigDecimal fee;
    /**
     * 违规类型(一级分类)
     */
    private String type;
    /**
     * 责任部门类型(1=全部;2=服务人员;3=非服务人员)
     */
    private Integer departmentType;
    /**
     * 违规详情(二级分类)
     */
    private String detail;
    /**
     * 事件类型，0普通、1保险理赔
     */
    private Integer eventType;
    /**
     * 状态，0禁用、1启用
     */
    private Integer status;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 判断事件是否结束:0=不判断；1=判断
     */
    private Integer checkEventOver;

    /**
     * 处理方式：0=不限制；1=限制
     */
    private Integer processMode;

    /**
     * 处理方式列表
     */
    private List<String> processModeNameList;

    /**
     * 生效时间
     */
    private Date effectiveTime;

    /**
     * 标题
     */
    private String title;

    /**
     * 备注
     */
    private String remark;

    /**
     * 发布形式：1=立即发布；2=定时发布
     */
    private Integer releaseType;

    /**
     * 是否创建新版本
     */
    private Boolean newVersion;

    /**
     * 版本
     */
    private String version;

    /**
     * 是否显示：0=不显示；1=显示
     */
    private Integer isShow;

    /**
     * 版本id
     */
    private Long versionId;
}
