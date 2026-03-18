package com.hk.simba.license.api.request.violation;

import com.hk.quark.base.annotation.check.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 文件名称：ViolationAppealRequest </p>
 * <p>
 * 文件描述：违规申诉 请求参数</p>
 * <p>
 * 版权所有：版权所有(C)2018-2099 </p>
 * <p>
 * 公司： 好慷 </p>
 * <p>
 * 内容摘要：</p>
 * <p>
 * 其他说明 </p>
 *
 * @author Chenqun
 * @version 1.0
 * @date 2020/4/13 11:22
 */
@Data
public class ViolationAppealRequest implements Serializable {
    private Long id;

    private Integer appealStatus;

    private String description;

    private String operator;
}
