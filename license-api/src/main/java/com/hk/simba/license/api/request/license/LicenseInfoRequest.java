package com.hk.simba.license.api.request.license;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyh
 * @date 2020/4/26/14:32
 */
@Data
public class LicenseInfoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 工种类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂， 100=其他工种 )
     */
    private Integer positionType;

    /**
     * 所属站点id
     */
    private Long siteId;

    /**
     * 状态(1=生效，2=吊销)
     */
    private Integer status;

    /**
     * 员工ids
     */
    private List<Long> staffIds;

    /**
     * 所属站点ids
     */
    private List<Long> siteIds;
}
