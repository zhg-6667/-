package com.hk.simba.license.api.request.retrain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author cyh
 * @date 2020/8/3/16:21
 */
@Data
public class RetrainQueryRequest implements Serializable {

    private static final long serialVersionUID = -3393150994026260295L;
    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 名称
     */
    private String name;

    /**
     * 工种类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂 )
     */
    private Integer positionType;

    /**
     * 复训类型(1=执照复训;2=违规复训;3=培训复训)
     */
    private Integer type;

    /**
     * 所属城市编码
     */
    private String cityCode;

    /**
     * 所属城市
     */
    private String cityName;

    /**
     * 所属站点id
     */
    private List<Long> siteIds;
    /**
     * 所属站点
     */
    private String siteName;

    /**
     * 状态(1=待复训,2=通过,3=未通过,4=失效)
     */
    private Integer status;

    /**
     * 复训创建开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startCreateTime;

    /**
     * 复训创建结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endCreateTime;


}
