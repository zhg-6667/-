package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * <p>
 * mq消息
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Data
public class MqMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * mq消息id
     */
    private String msgId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息标识
     */
    private String tag;
    /**
     * body消息内容
     */
    private String body;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String modifyBy;
    /**
     * 更新时间
     */
    private Date modifyTime;

}
