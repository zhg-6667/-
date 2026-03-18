package com.hk.simba.license.api;

import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.black.BlackQueryRequest;
import com.hk.simba.license.api.request.black.CreateBlackRequest;
import com.hk.simba.license.api.request.black.RemoveBlackRequest;

/**
 * @author cyh
 * @date 2020/10/17/11:28
 * 黑名单接口
 */
public interface BlacklistOpenService {

    /**
     * 后台分页查询
     *
     * @param page
     * @param request
     * @return
     */
    BaseResponse page(PageRequest page, BlackQueryRequest request);


    /**
     * 移除黑名单
     *
     * @param request
     * @return
     */
    BaseResponse removeBlack(RemoveBlackRequest request);


    /**
     * 添加黑名单
     *
     * @param request
     * @return
     */
    BaseResponse createBlack(CreateBlackRequest request);


    /***
     * 根据员工身份证查询黑名单
     * @param idCard
     * @return
     */
    BaseResponse findByIdCard(String idCard);


    /***
     * 根据员工id查询黑名单
     * @param staffId
     * @return
     */
    BaseResponse findByStaffId(Long staffId);
}


