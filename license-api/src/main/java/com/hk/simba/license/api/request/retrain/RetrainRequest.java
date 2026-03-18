package com.hk.simba.license.api.request.retrain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hk.simba.license.api.vo.EventAttachment;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author cyh
 * @date 2020/8/3/16:34
 */
@Data
public class RetrainRequest implements Serializable {
    private static final long serialVersionUID = 7660655582890734259L;

    /**
     * 复训id
     */
    private Long id;

    /**
     * 分数
     */
    private Integer score;


    /**
     * 复训开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;


    /**
     * 复训结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 复训内容
     */
    private String content;

    /**
     * 操作者
     */
    private String operator;


    /**
     * 状态(1=待复训,2=通过,3=未通过,4=失效)
     */
    private Integer status;

    /**
     * 图片附件
     */
    private List<EventAttachment> imageAnnex;

    /**
     * 其他附件
     */
    private List<EventAttachment> otherAnnex;

    /**
     * 培训师id
     */
    private Long trainerId;

    /**
     * 培训师名称
     */
    private String trainerName;
}
