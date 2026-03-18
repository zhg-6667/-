package com.hk.simba.license.api;

import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeStatusRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeVersionPageRequest;
import com.hk.simba.license.api.vo.ViolationTypeVersionPageVO;
import com.hk.simba.license.api.vo.ViolationTypeVO;
import com.hk.simba.license.api.vo.comm.PageResult;

import java.util.List;

/**
 * @author 羊皮
 * @description
 * @since 2020-4-10 10:08:31
 */
public interface ViolationTypeOpenService {

    /**
     * 清除缓存
     */
    void delViolationTypeCache();

    /**
     * 新增或保存违规类型
     *
     * @param request
     * @return
     */
    BaseResponse saveViolationType(ViolationTypeRequest request);


    /**
     * 禁用、启用违规类型
     *
     * @param request
     * @return
     */
    BaseResponse changeViolationTypeStatus(ViolationTypeRequest request);

    /**
     * 后台分页查询
     *
     * @param page
     * @param request
     * @return
     */
    BaseResponse<PageResult<ViolationTypeVO>> page(PageRequest page, ViolationTypeRequest request);


    /**
     * 查询所有违规类型
     *
     * @return
     */
    BaseResponse<List<String>> findAllType();


    /***
     * 查询所有违规细则
     * @return
     */
    BaseResponse<List<String>> findAllDetail();


    /**
     * 查询违规类型详情
     *
     * @param id
     * @return
     */
    BaseResponse<ViolationTypeVO> findDetailById(Long id);


    /***
     * 根据类型查询违规细则
     * @param type
     * @return
     */
    BaseResponse<List<String>> findDetailByType(String type);

    /***
     * 按扣分类型查询所有违规类型
     *
     * @param deductType
     * @return
     */
    BaseResponse<List<String>> findByDeductType(Integer deductType);

    /**
     * 查询违规类型版本分页
     *
     * @param request
     * @return
     */
    BaseResponse<PageResult<ViolationTypeVersionPageVO>> findVersionPage(ViolationTypeVersionPageRequest request);

    /**
     * 删除违规类型
     *
     * @param request
     * @return
     */
    BaseResponse delete(ViolationTypeStatusRequest request);

    /**
     * 禁用违规类型
     * @param request
     * @return
     */
    BaseResponse stop(ViolationTypeStatusRequest request);
}
