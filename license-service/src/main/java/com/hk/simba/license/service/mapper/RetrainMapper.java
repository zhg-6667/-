package com.hk.simba.license.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.api.request.retrain.RetrainDetailRequest;
import com.hk.simba.license.api.request.retrain.RetrainQueryRequest;
import com.hk.simba.license.api.vo.RetrainVO;
import com.hk.simba.license.service.entity.Retrain;

import java.util.List;

/**
 * @author cyh
 * @date 2020/8/3/15:50
 * 复训mapper接口
 */
public interface RetrainMapper extends BaseMapper<Retrain> {
    /**
     * 查询复训列表(后台分页)
     *
     * @param page
     * @param request
     * @return
     */
    List<RetrainVO> getPageList(Page<Retrain> page, RetrainQueryRequest request);


    /***
     * 按条件查询复训信息
     * @param request
     * @return
     */
    List<RetrainVO> findRetrainByCondition(RetrainDetailRequest request);

    /***
     * 超时复训分页查询
     * @param page
     * @param timeoutPay
     * @return
     */
    List<Retrain> pageTimeoutRetrainList(Page<Retrain> page, Integer timeoutPay);
}
