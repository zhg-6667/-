package com.hk.simba.license.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk.quark.base.entity.serializer.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2021/9/3/17:07
 * <p>
 * 复训配置
 */
@Data
public class RetrainConfigVO implements Serializable {

    /**
     * 配置id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 岗位类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂 , 8=维修, 9=星级月嫂, 10=严选保姆,11=家庭助理, 12=家务师, 100=其他工种)
     */
    private Integer positionType;
    /**
     * 城市编码字符集
     */
    private String cityCodeStr;
    /**
     * 所属城市字符集
     */
    private String cityNameStr;
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
}
