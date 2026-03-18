package com.hk.simba.license.api;

import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.retrain.RetrainQueryRequest;
import com.hk.simba.license.api.request.retrain.RetrainRequest;
import com.hk.simba.license.api.vo.RetrainVO;
import com.hk.simba.license.api.vo.comm.PageResult;

/**
 * @author cyh
 * @date 2020/8/3/17:00
 * 复训服务类
 */
public interface RetrainOpenService {

    /**
     * 查询复训列表(后台分页)
     *
     * @param request
     * @param page
     * @return
     */
    BaseResponse<PageResult<RetrainVO>> list(PageRequest page, RetrainQueryRequest request);


    /**
     * 根据id查询复训详情信息
     *
     * @param id
     * @return
     */
    BaseResponse findRetrainById(Long id);


    /**
     * 编辑复训信息
     *
     * @param request
     * @return
     */
    BaseResponse updateRetrain(RetrainRequest request);


}
