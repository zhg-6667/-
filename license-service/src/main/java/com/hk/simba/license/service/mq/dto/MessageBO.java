package com.hk.simba.license.service.mq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenm
 * @since 2022/5/6
 */
@Data
public class MessageBO implements Serializable {
    private static final long serialVersionUID = -2089922728833491363L;

    private String msgId;

    private String tag;

    private MessageEntity messageEntity;
}
