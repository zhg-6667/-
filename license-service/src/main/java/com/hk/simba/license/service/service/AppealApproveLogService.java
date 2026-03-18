package com.hk.simba.license.service.service;

import com.baomidou.mybatisplus.service.IService;
import com.hk.simba.license.service.entity.AppealApproveLog;
import com.hk.simba.license.api.vo.AppealApproveLogVO;

import java.util.List;

/**
 * @author cyh
 * @date 2020/5/13/16:04
 * 申诉审核日志服务类
 */
public interface AppealApproveLogService extends IService<AppealApproveLog> {

    /**
     * 根据申诉id查询审核日志
     *
     * @param appealId
     * @param approveType
     * @return
     */
    List<AppealApproveLogVO> getAppealApproveLogList(Long appealId, Integer approveType);


    /**
     * 根据状态、审核类型、申诉id查找记录
     *
     * @param status
     * @param appealId
     * @param approveType
     * @return
     */
    List<AppealApproveLogVO> getAppealApproveLogsByCondition(Integer status, Long appealId, Integer approveType);
}


