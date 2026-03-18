package com.hk.simba.license.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.hk.simba.license.api.request.retrain.RetrainDetailRequest;
import com.hk.simba.license.api.request.retrain.RetrainInvalidRequest;
import com.hk.simba.license.api.request.retrain.RetrainQueryRequest;
import com.hk.simba.license.api.vo.RetrainVO;
import com.hk.simba.license.service.entity.Retrain;

import java.util.List;

/**
 * @author cyh
 * @date 2020/8/3/16:57
 * 复训服务类
 */
public interface RetrainService extends IService<Retrain> {

    /***
     * 按条件查询执照分页信息(后台)
     * @param page
     * @param request
     * @return
     */
    List<RetrainVO> getPageList(Page<Retrain> page, RetrainQueryRequest request);

    /***
     * 保存复训信息
     * @param vo
     */
    void saveRetrain(RetrainVO vo);

    /***
     * 按条件查询复训信息(非失效的)
     * @param request
     * @return
     */
    List<RetrainVO> findRetrainByCondition(RetrainDetailRequest request);


    /***
     * 失效复训记录(只有待复训才失效)
     * @param request
     */
    void invalidRetrainByCondition(RetrainInvalidRequest request);


    /***
     * 根据id失效复训记录
     * @param request
     */
    void invalidRetrainByIds(RetrainInvalidRequest request);

    /***
     * 是否保存违规复训
     * @param vo
     * @return
     */
    Boolean saveViolationRetrain(RetrainVO vo);

    /***
     * 超时复训分页查询
     * @param page
     * @param timeoutPay
     * @return
     */
    List<Retrain> pageTimeoutRetrainList(Page<Retrain> page, Integer timeoutPay);


}
