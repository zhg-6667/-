package com.hk.simba.license.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.service.entity.Appeal;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hk.simba.license.api.request.appeal.AppealQueryRequest;
import com.hk.simba.license.api.vo.AppealVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 申诉信息表 Mapper 接口
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
public interface AppealMapper extends BaseMapper<Appeal> {

    /**
     * 后台分页查询
     *
     * @param request
     * @param page
     * @return
     */
    List<AppealVO> selectPageList(Page<Appeal> page, AppealQueryRequest request);


    /**
     * 分页查询(按申诉事件查询待大区审核)
     *
     * @param page
     * @param timeoutAppeal
     * @return
     */
    List<Appeal> waitRegionApprovePageList(Page<Appeal> page, @Param("timeoutAppeal") int timeoutAppeal);


    /**
     * 申诉审批，飞书审批超时
     *
     * @param approveTimeoutDay
     * @return
     */
    List<Appeal> timeoutAppealApprove(int approveTimeoutDay);
}