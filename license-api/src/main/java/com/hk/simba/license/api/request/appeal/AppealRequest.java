package com.hk.simba.license.api.request.appeal;

import com.hk.simba.license.api.vo.EventAttachment;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyh
 * @date 2020/5/13/16:48
 * 申诉请求参数
 */
@Data
public class AppealRequest implements Serializable {

    private static final long serialVersionUID = -1693393626715843379L;
    /**
     * 违规编号
     */
    private Long violationId;

    /**
     * 申诉备注(原因)
     */
    private String remark;

    /**
     * 备注-站长申诉原因
     */
    private String siteLeaderRemark;

    /**
     * 操作者
     */
    private String operator;


    /**
     * 图片附件
     */
    private List<EventAttachment> imageAnnex;

    /**
     * 其他附件
     */
    private List<EventAttachment> otherAnnex;

}
