package com.hk.simba.license.api.request.retrain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cyh
 * @date 2020/8/5/15:04
 */
@Data
public class RetrainDetailRequest implements Serializable {

    private static final long serialVersionUID = 1818649475103617607L;
    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 违规id
     */
    private Long violationId;


    /**
     * 状态
     */
    private Integer status;

    /**
     * 剩余分数类型
     */
    private Integer remainScoreType;


    /**
     * 创建时间开始
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startCreateTime;

    /**
     * 创建时间结束
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endCreateTime;


}
