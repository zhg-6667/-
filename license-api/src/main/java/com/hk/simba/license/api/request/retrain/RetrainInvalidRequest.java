package com.hk.simba.license.api.request.retrain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyh
 * @date 2020/8/4/18:32
 */
@Data
public class RetrainInvalidRequest implements Serializable {
    private static final long serialVersionUID = -1226700013259488023L;

    /**
     * 员工id
     */
    private Long staffId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 理由
     */
    private String reason;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 复训id
     */
    private Long id;

    /**
     * 剩余分数类型
     */
    private Integer remainScoreType;


    /**
     * 复训id集合
     */
    private List<Long> ids;

    /**
     * 复训类型(1=执照复训;2=违规复训;3=培训复训)
     */
    private Integer type;

    /**
     * 违规id
     */
    private Long violationId;


}
