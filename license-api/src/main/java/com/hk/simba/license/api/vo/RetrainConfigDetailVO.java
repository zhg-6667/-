package com.hk.simba.license.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyh
 * @date 2021/9/6/15:42
 * <p>
 * 复训详情
 */
@Data
public class RetrainConfigDetailVO implements Serializable {
    private static final long serialVersionUID = 8149778561066913813L;

    /**
     * 岗位类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂 , 8=维修, 9=星级月嫂, 10=严选保姆,11=家庭助理, 12=家务师, 100=其他工种)
     */
    private Integer positionType;

    private Integer status;

    private List<CityInfo> cityInfoList;

    @Data
    public static class CityInfo implements Serializable {


        private static final long serialVersionUID = 5102726966083243814L;
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
