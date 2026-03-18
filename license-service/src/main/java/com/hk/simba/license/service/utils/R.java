package com.hk.simba.license.service.utils;

import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.quark.base.dto.response.BaseResponse;

/**
 * @author zengry
 * @description
 * @since 2020/3/5
 */
public class R {
    public static BaseResponse result(ResponseCodeEnum codeEnum) {
        return new BaseResponse(codeEnum.getCode(), codeEnum.getMessage());
    }

    public static BaseResponse result(ResponseCodeEnum codeEnum, Object data) {
        return new BaseResponse(codeEnum.getCode(), codeEnum.getMessage(), data);
    }
}
