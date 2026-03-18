package com.hk.simba.license.api.request.license;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyh
 * @date 2020/4/26/14:32
 */
@Data
public class LicenseQueryRequest implements Serializable {

    private static final long serialVersionUID = 6188231352770304670L;
    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 名称
     */
    private String name;

    /**
     * 工种类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂 ,100=其他工种 )
     */
    private Integer positionType;

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
     * 状态(1=生效，2=吊销)
     */
    private Integer status;

    /**
     * 剩余分数最小值
     */
    private Integer minScore;

    /**
     * 剩余分数最大值
     */
    private Integer maxScore;
}
