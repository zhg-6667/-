package com.hk.simba.license.api;

import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.retrain.RetrainConfigQueryRequest;
import com.hk.simba.license.api.request.retrain.RetrainConfigRequest;
import com.hk.simba.license.api.vo.RetrainConfigVO;
import com.hk.simba.license.api.vo.comm.PageResult;

import java.util.List;

/**
 * @author cyh
 * @date 2021/9/3/15:57
 * <p>
 * 复训配置接口
 */
public interface RetrainConfigOpenService {

    /**
     * 创建配置
     *
     * @param request
     * @return
     */
    BaseResponse createRetrainConfig(RetrainConfigRequest request);

    /**
     * 变更配置
     *
     * @param request
     * @return
     */
    BaseResponse updateRetrainConfig(RetrainConfigRequest request);

    /**
     * 根据岗位类型变更配置状态
     *
     * @param request
     * @return
     */
    BaseResponse updateStatusByPositionType(RetrainConfigRequest request);

    /**
     * 查询执照列表(后台分页)
     *
     * @param request
     * @param page
     * @return
     */
    BaseResponse<PageResult<RetrainConfigVO>> list(PageRequest page, RetrainConfigQueryRequest request);

    /**
     * 根据岗位类型获取配置信息
     *
     * @param request
     * @return
     */
    BaseResponse getByPositionType(RetrainConfigRequest request);

    /***
     * 根据岗位类型获取有效的复训配置对应的城市code
     *
     * @param positionType
     * @return
     */
    BaseResponse<List<String>> getInvalidConfigCityCodeByPositionType(Integer positionType);
}
