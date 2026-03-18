package com.hk.simba.license.service.manager;

import com.hk.simba.base.common.dto.Operator;
import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.base.common.error.CommonErrorCode;
import com.hk.simba.base.common.error.LicenseErrorCode;
import com.hk.simba.base.common.exception.BusinessException;
import com.hk.sisyphus.base.exception.ApiException;
import com.hk.sisyphus.merope.core.staff.StopOrderApi;
import com.hk.sisyphus.merope.model.staff.stoporder.ApplyForStopOrderApplyForStopOrderDTO;
import com.hk.sisyphus.merope.model.staff.stoporder.ApplyForStopOrderRequest;
import com.hk.sisyphus.merope.model.staff.stoporder.EndStopOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 分控停单接口
 *
 * @author pengdl
 * @date 2024-02-03 14:45
 **/
@Service
@Slf4j
public class StopOrderMpManager {

    /**
     * 停单申请
     *
     * @param outerId
     * @param staffId
     * @param startTime
     * @param operator
     * @return ApplyForStopOrderApplyForStopOrderDTO
     */
    public ApplyForStopOrderApplyForStopOrderDTO apply(Long outerId, Long staffId, LocalDateTime startTime,
        Operator operator) {
        StopOrderApi stopOrderApi = new StopOrderApi();
        ApplyForStopOrderRequest applyForStopOrderRequest = new ApplyForStopOrderRequest();
        applyForStopOrderRequest.setOuterId(outerId);
        applyForStopOrderRequest.setType(5);
        applyForStopOrderRequest.setStaffId(staffId);
        applyForStopOrderRequest.setStopOrderBeginTime(startTime);
        applyForStopOrderRequest.setOperator(operator);

        try {
            BaseResponse<ApplyForStopOrderApplyForStopOrderDTO> response =
                stopOrderApi.applyForStopOrder(applyForStopOrderRequest);
            log.info("method applyForStopOrder req = {},resp={}", applyForStopOrderRequest, response);
            if (!response.isSuccess()) {
                throw new BusinessException(response.getErrorCode());
            }
            return response.getData();
        } catch (ApiException e) {
            log.error("Exception when calling StopOrderApi#applyForStopOrder,message={}", e.getMessage(), e);
            throw new BusinessException(LicenseErrorCode.genCommonErrorCode(CommonErrorCode.EXTERNAL_ERROR));
        }
    }

    /**
     * 结束停单
     *
     * @param stopOrderId
     * @param stopTime
     * @param operator
     */
    public void finish(Long stopOrderId, LocalDateTime stopTime, Operator operator) {
        StopOrderApi stopOrderApi = new StopOrderApi();
        EndStopOrderRequest endStopOrderRequest = new EndStopOrderRequest();
        endStopOrderRequest.setId(stopOrderId);
        endStopOrderRequest.setStopOrderEndTime(stopTime);
        endStopOrderRequest.setActualStopOrderEndTime(stopTime);
        endStopOrderRequest.setProcessMode(1);
        endStopOrderRequest.setOperator(operator);
        try {
            BaseResponse response = stopOrderApi.endStopOrder(endStopOrderRequest);
            log.info("method finishStopOrder req = {},resp={}", endStopOrderRequest, response);
            if (!response.isSuccess()) {
                throw new BusinessException(response.getErrorCode());
            }
        } catch (ApiException e) {
            log.error("Exception when calling StopOrderApi#finishStopOrder,message={}", e.getMessage(), e);
            throw new BusinessException(LicenseErrorCode.genCommonErrorCode(CommonErrorCode.EXTERNAL_ERROR));
        }
    }
}
