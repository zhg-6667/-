package com.hk.simba.license.service.manager;

import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.base.common.exception.BusinessException;
import com.hk.simba.workflow.open.api.WorkFlowInstanceOpenService;
import com.hk.simba.workflow.open.request.SubmitInstanceRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * 工作流相关接口
 *
 * @author :chenjh1
 * @date : 2022/11/23 15:31
 */
@Slf4j
@Component
public class WorkflowManager {

    @DubboReference(timeout = 30000)
    private WorkFlowInstanceOpenService workFlowInstanceOpenService;

    public Long submit(SubmitInstanceRequest request) {
        if (ObjectUtils.isEmpty(request)) {
            return null;
        }
        BaseResponse<Long> baseResponse = workFlowInstanceOpenService.submit(request);
        if (!baseResponse.isSuccess()) {
            throw new BusinessException(baseResponse.getErrorCode());
        }
        return baseResponse.getData();
    }

    public String getEngineInstanceIdByBizIdAndBizType(String bizId, String bizType) {
        BaseResponse<String> baseResponse = workFlowInstanceOpenService.getEngineInstanceIdByBizIdAndBizType(bizId, bizType);
        if (!baseResponse.isSuccess()) {
            throw new BusinessException(baseResponse.getErrorCode());
        }
        return baseResponse.getData();
    }
}
