package com.hk.simba.license.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hk.simba.license.service.entity.AppealApproveLog;
import com.hk.simba.license.api.vo.AppealApproveLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cyh
 * @date 2020/5/13/15:57
 * 申述审批日志接口
 */
public interface AppealApproveLogMapper extends BaseMapper<AppealApproveLog> {
    /**
     * 根据申诉id查询审核日志
     *
     * @param appealId
     * @param approveType
     * @return
     */
    List<AppealApproveLogVO> getAppealApproveLogList(@Param("appealId") Long appealId, @Param("approveType") Integer approveType);


    /**
     * 根据状态、审核类型、申诉id查找记录
     *
     * @param status
     * @param appealId
     * @param approveType
     * @return
     */
    List<AppealApproveLogVO> getAppealApproveLogsByCondition(@Param("status") Integer status, @Param("appealId") Long appealId, @Param("approveType") Integer approveType);
}
