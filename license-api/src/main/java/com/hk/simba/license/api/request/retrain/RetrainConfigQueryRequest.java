package com.hk.simba.license.api.request.retrain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cyh
 * @date 2021/9/3/17:11
 */
@Data
public class RetrainConfigQueryRequest implements Serializable {
    private static final long serialVersionUID = 8808466722419057128L;

    /**
     * 岗位类型(1=保洁师 , 2=精洁师, 3=收纳师 , 4=净宠师 , 5=租赁管家 , 6=做饭师 , 7=月嫂 , 8=维修, 9=星级月嫂, 10=严选保姆,11=家庭助理, 12=家务师, 100=其他工种)
     */
    private Integer positionType;

    /**
     * 状态(0=失效，1=生效)
     */
    private Integer status;

}
