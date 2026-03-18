package com.hk.simba.license.api;

import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.appeal.AppealDetailRequest;
import com.hk.simba.license.api.request.appeal.AppealQueryRequest;
import com.hk.simba.license.api.request.appeal.AppealRequest;
import com.hk.simba.license.api.vo.AppealDetailVO;
import com.hk.simba.license.api.vo.AppealVO;
import com.hk.simba.license.api.vo.comm.PageResult;

/**
 * @author 羊皮
 * @description
 * @since 2020-4-10 9:23:05
 */
public interface AppealOpenService {
    /**
     * 发起申诉
     *
     * @param request
     * @return
     */
    BaseResponse startAppeal(AppealRequest request);

    /**
     * 查询执照列表(后台分页)
     *
     * @param request
     * @param page
     * @return
     */
    BaseResponse<PageResult<AppealVO>> page(PageRequest page, AppealQueryRequest request);

    /**
     * 申诉详情
     *
     * @param request
     * @return
     */
    BaseResponse<AppealDetailVO> appealDetail(AppealDetailRequest request);

    /**
     * 申诉详情
     *
     * @param request
     * @return
     */
    BaseResponse<AppealDetailVO> appealApprovelDetail(AppealDetailRequest request);


    /**
     * 申诉撤销
     *
     * @param request
     * @return
     */
    BaseResponse recallAppeal(AppealDetailRequest request);


    /**
     * 通过申诉
     *
     * @param request
     * @return
     */
    BaseResponse passAppeal(AppealDetailRequest request);


    /**
     * 驳回申诉
     *
     * @param request
     * @return
     */
    BaseResponse refuseAppeal(AppealDetailRequest request);

    /**
     * 违规单号对应的用户信息
     *
     * @param orderId 违规单号
     * @return
     */
    BaseResponse userInfo(Long orderId);

}
