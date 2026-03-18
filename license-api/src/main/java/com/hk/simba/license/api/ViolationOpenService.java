package com.hk.simba.license.api;

import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.api.request.BaseRequest;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.violation.ViolationAppealRequest;
import com.hk.simba.license.api.request.violation.ViolationApprovalRequest;
import com.hk.simba.license.api.request.violation.ViolationInvalidRequest;
import com.hk.simba.license.api.request.violation.ViolationPayStatusRequest;
import com.hk.simba.license.api.request.violation.ViolationQueryRequest;
import com.hk.simba.license.api.request.violation.ViolationRemarkRequest;
import com.hk.simba.license.api.vo.ViolationAppVO;

import java.util.List;

/**
 * @description @author 羊皮
 * @since 2020-4-10 10:06:59
 */
public interface ViolationOpenService {

    /**
     * 保存违规信息数据
     *
     * @param request
     * @return
     */
    BaseResponse save(BaseRequest<ViolationAppVO> request);

    /**
     * 编辑违规信息数据
     *
     * @param request
     * @return
     */
    BaseResponse update(BaseRequest<ViolationAppVO> request);

    /**
     * app分页查询违规信息数据
     *
     * @param request
     * @param vo
     * @return
     */
    BaseResponse page(PageRequest request, ViolationAppVO vo);

    /**
     * 后台分页查询违规信息数据
     *
     * @param request
     * @param query
     * @return
     */
    BaseResponse page(PageRequest request, ViolationQueryRequest query);

    /**
     * 根据ID违规信息数据
     *
     * @param id
     * @return
     */
    BaseResponse selectById(Long id);

    /**
     * 违规申诉(废弃,发起申诉用申诉接口startAppeal)
     *
     * @param request
     * @return
     */
    @Deprecated
    BaseResponse appeal(ViolationAppealRequest request);

    /**
     * 违规失效(后台手工失效)
     *
     * @param request
     * @return
     */
    BaseResponse invalidViolation(ViolationInvalidRequest request);

    /**
     * 违规支付状态变更
     *
     * @param request
     * @return
     */
    BaseResponse payStatusChange(ViolationPayStatusRequest request);

    /**
     * 违规备注
     *
     * @param request
     * @return
     */
    BaseResponse remarkViolation(ViolationRemarkRequest request);

    /**
     * 获取违规列表里的部门信息(去重)
     *
     * @return
     */
    BaseResponse<List<String>> findAllDepartment();

    /**
     * 审批待生效的违规事件
     *
     * @param request
     * @return
     */
    BaseResponse approveWaitValidViolation(ViolationApprovalRequest request);

    /**
     * 查询员工入职信息(如首次入职时间)
     *
     * @param staffId
     * @return
     */
    BaseResponse findStaffEntryInfo(Long staffId);
}
