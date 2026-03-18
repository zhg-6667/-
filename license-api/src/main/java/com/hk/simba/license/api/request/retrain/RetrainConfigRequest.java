package com.hk.simba.license.api.request.retrain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyh
 * @date 2021/9/3/16:00
 */
@Data
public class RetrainConfigRequest implements Serializable {
    private static final long serialVersionUID = -8897376124232394891L;

    /**
     * Id
     */
    private Long id;

    /**
     * 岗位类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂 , 8=维修, 9=星级月嫂, 10=严选保姆,11=家庭助理, 12=家务师, 100=其他工种)
     */
    private Integer positionType;

    /**
     * 状态(0=失效，1=生效)
     */
    private Integer status;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 城市信息
     */
    private List<CityInfo> cityInfoList;

    @Data
    public static class CityInfo implements Serializable {
        private static final long serialVersionUID = -8173493054917338297L;
        /**
         * 城市编码
         */
        private String cityCode;

        /**
         * 所属城市
         */
        private String cityName;
    }

}
