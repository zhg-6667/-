package com.hk.simba.license.service.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.base.common.dto.response.PageData;
import com.hk.simba.base.common.error.CommonErrorCode;
import com.hk.simba.base.common.error.LicenseErrorCode;
import com.hk.simba.base.common.exception.BusinessException;
import com.hk.simba.base.util.LocalDateTimeUtils;
import com.hk.simba.staff.open.enums.WorkingStateEnum;
import com.hk.sisyphus.merope.core.staff.StaffApi;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffIdListOnJobRequest;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffListByStaffIdListRequest;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffListByStaffIdListStaffBasicDTO;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffSensitiveInfoByStaffIdRequest;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lanwx
 */
@Slf4j
@Component
public class StaffApiManager {

    @Resource
    private StaffApi staffApi;

    /**
     * 根据id获取员工信息
     *
     * @param staffId
     * @return FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO
     */
    public FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO findStaffInfoById(Long staffId) {
        FindStaffSensitiveInfoByStaffIdRequest request = new FindStaffSensitiveInfoByStaffIdRequest();
        request.setStaffId(staffId);
        BaseResponse<FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO> baseResponse = staffApi.findStaffSensitiveInfoByStaffId(request);
        if (!baseResponse.isSuccess()) {
            log.warn("调用开放平台查询员工信息出错，staffId={}, eMsg={}", staffId, baseResponse.getErrorCode().getMessage());
            throw new BusinessException(LicenseErrorCode.genCommonErrorCode(CommonErrorCode.EXTERNAL_ERROR));
        }
        return baseResponse.getData();
    }

    /**
     * 根据员工id判断是否在职
     *
     * @param staffId
     * @return boolean
     */
    public boolean isResigned(Long staffId) {
        FindStaffSensitiveInfoByStaffIdStaffSensitiveInfoDTO staff = this.findStaffInfoById(staffId);
        return WorkingStateEnum.QUIT.getValue().equals(staff.getWorkingState());
    }

    /**
     * 获取在职员工id列表
     *
     * @return List<Long>
     */
    public List<Long> findStaffIdListOnJob() {
        List<Long> staffIds = Lists.newArrayList();
        FindStaffIdListOnJobRequest request = new FindStaffIdListOnJobRequest();
        int pageNo = 1;
        long totalPage = 1;
        LocalDateTime startTime = LocalDateTimeUtils.getDayStart(LocalDateTime.now());
        LocalDateTime endTime = LocalDateTimeUtils.getDayEnd(LocalDateTime.now());
        request.setBeginTime(startTime);
        request.setEndTime(endTime);
        request.setPageSize(500);
        request.setPageNo(pageNo);
        do {
            BaseResponse<PageData<Long>> response = staffApi.findStaffIdListOnJob(request);
            if (response.isSuccess()) {
                staffIds.addAll(response.getData().getResults());
                totalPage = response.getData().getTotalPage();
            }
            pageNo++;
            request.setPageNo(pageNo);
        } while (totalPage >= pageNo);
        return staffIds;
    }

    /**
     * 根据员工id获取员工列表
     *
     * @return List<FindStaffListByStaffIdListStaffBasicDTO>
     */
    public List<FindStaffListByStaffIdListStaffBasicDTO> findStaffListByStaffIdList(List<Long> staffIdList) {
        FindStaffListByStaffIdListRequest findStaffListByStaffIdListRequest = new FindStaffListByStaffIdListRequest();
        findStaffListByStaffIdListRequest.setStaffIdList(Sets.newHashSet(staffIdList));
        BaseResponse<List<FindStaffListByStaffIdListStaffBasicDTO>> baseResponse = staffApi.findStaffListByStaffIdList(findStaffListByStaffIdListRequest);
        if (baseResponse.isSuccess() && CollectionUtils.isNotEmpty(baseResponse.getData())) {
            return baseResponse.getData();
        }
        return Lists.newArrayList();
    }
}