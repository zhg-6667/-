package com.hk.simba.license.service.mq.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @description @author 羊皮
 * @since 2020-4-16 16:17:34
 */
@Data
public class EventAttachment implements Serializable {

    /**
     * 附件id
     */
    private String id;
    /**
     * 文件名称
     */
    private String attachment;
    /**
     * 访问链接
     */
    private String attachmentUrl;
}
