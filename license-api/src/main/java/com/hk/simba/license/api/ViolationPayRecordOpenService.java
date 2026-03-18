package com.hk.simba.license.api;

import com.hk.simba.license.api.request.BaseRequest;
import com.hk.simba.license.api.vo.ViolationPayRecordVO;
import com.hk.quark.base.dto.response.BaseResponse;

/**
 * @description @author 羊皮
 * @since 2020-4-10 10:08:12
 */
public interface ViolationPayRecordOpenService {

    /**
     * 创建支付记录,如果存在未支付的记录直接返回对应的支付链接
     *
     * @param request violationId,createBy必填
     * @return
     */
    BaseResponse createPayRecord(BaseRequest<ViolationPayRecordVO> request);
}
