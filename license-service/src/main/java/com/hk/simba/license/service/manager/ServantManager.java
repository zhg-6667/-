package com.hk.simba.license.service.manager;

import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.base.common.exception.BusinessException;
import com.hk.simba.staff.open.StaffOpenService;
import com.hk.simba.staff.open.request.StaffDetailRequest;
import com.hk.simba.staff.open.response.StaffDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 服务者平台
 *
 * @author pengdl
 * @since 2024-01-03
 */
@Slf4j
@Service
public class ServantManager {
    @DubboReference
    private StaffOpenService staffOpenService;

    public StaffDetailDto getStaffDetail(Long staffId) {
        StaffDetailRequest staffDetailRequest = new StaffDetailRequest();
        staffDetailRequest.setId(staffId);
        BaseResponse<StaffDetailDto> response = staffOpenService.getStaffDetail(staffDetailRequest);
        if (response.isSuccess()) {
            return response.getData();
        }
        log.error("获取员工{}详情失败：{}", staffId, response);
        throw new BusinessException(response.getErrorCode());
    }

}
