package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 复训配置表
 * </p>
 *
 * @author cyh
 * @since 2021-09-03
 */
@Data
public class RetrainConfig implements Serializable {
    private static final long serialVersionUID = -6655263871018759333L;

    /**
     * 配置id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 岗位类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂 , 8=维修, 9=星级月嫂, 10=严选保姆,11=家庭助理, 12=家务师, 100=其他工种)
     */
    private Integer positionType;
    /**
     * 城市编码
     */
    private String cityCode;
    /**
     * 所属城市
     */
    private String cityName;
    /**
     * 状态(0=失效，1=生效)
     */
    private Integer status;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String modifyBy;
    /**
     * 更新时间
     */
    private Date modifyTime;
    /**
     * 数据更新时间
     */
    private Date updateTime;

}
