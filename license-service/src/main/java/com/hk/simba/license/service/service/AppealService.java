package com.hk.simba.license.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.hk.simba.license.api.request.appeal.AppealQueryRequest;
import com.hk.simba.license.api.vo.AppealVO;
import com.hk.simba.license.service.entity.Appeal;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.workflow.event.WorkFlowEvent;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 申诉信息表 服务类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
public interface AppealService extends IService<Appeal> {

    /**
     * 后台分页查询
     *
     * @param request
     * @param page
     * @return
     */
    List<AppealVO> selectPageList(Page<Appeal> page, AppealQueryRequest request);


    /**
     * 按发起申诉时间，查询待大区审批的申诉记录
     *
     * @param timeoutAppeal
     * @param page
     * @return
     */
    List<Appeal> waitRegionApprovePageList(Page<Appeal> page, int timeoutAppeal);

    /**
     * 申诉审批，飞书审批超时
     *
     * @param approveTimeoutDay
     * @return
     */
    List<Appeal> timeoutAppealApprove(int approveTimeoutDay);

    /**
     * 飞书审批-申诉撤销
     *
     * @param event
     * @return
     */
    void recallAppeal(WorkFlowEvent event);

    /**
     * 飞书审批-通过申诉
     *
     * @param event
     * @return
     */
    void passAppeal(WorkFlowEvent event);

    /**
     * 飞书审批-驳回申诉
     *
     * @param event
     * @return
     */
    void refuseAppeal(WorkFlowEvent event, Boolean isTimeout);


    /**
     * 违规失效-撤回飞书审批
     *
     * @param violationId
     * @return
     */
    void refuseAppealForViolationInvalid(Long violationId);

    void sendAppealResultEmail(Violation violation, Date appealTime, String result);

    void sendSiteAppealResultEmail(Violation violation, Date appealTime, String result);
}
