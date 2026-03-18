package com.hk.simba.license.service.task;

import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.base.json.JsonUtils;
import com.hk.simba.license.api.AppealOpenService;
import com.hk.simba.license.api.request.appeal.AppealDetailRequest;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.ApproveTypeEnum;
import com.hk.simba.license.service.entity.Appeal;
import com.hk.simba.license.service.service.AppealService;
import com.hk.simba.workflow.event.WorkFlowEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author cyh
 * @date 2020/8/12/18:26
 * 审批定时任务处理
 */
@Service
@Slf4j
public class AppealTaskHandler {
    /**
     * 发起申诉超时提醒
     */
    @Value("${approve.limit.day}")
    private int approveLimitDay;
    /**
     * 飞书审批超时自动拒绝时间
     */
    @Value("${appeal.approveTimeoutDay}")
    private int approveTimeoutDay;

    @Autowired
    private AppealService appealService;

    @Autowired
    private AppealOpenService appealOpenService;

    /***
     * 申诉超时提醒，大区自动驳回
     *
     */
    @Deprecated
    public void timeOutAppealOperate() {
        log.info("timeOutAppealOperate");
        int pageSize = 20;
        //每次都取第一页
        int pageNo = 0;
        int currentSize = 0;
        do {
            Page<Appeal> page = new Page<>(pageNo, pageSize);
            List<Appeal> appealList = appealService.waitRegionApprovePageList(page, approveLimitDay);
            if (CollectionUtils.isEmpty(appealList)) {
                break;
            }
            currentSize = appealList.size();
            for (Appeal appeal : appealList) {
                try {
                    AppealDetailRequest request = new AppealDetailRequest();
                    request.setViolationId(appeal.getViolationId());
                    request.setAppealId(appeal.getId());
                    request.setOperator(Constants.SYS);
                    request.setRemark(Constants.TIME_OUT_REJECT);
                    request.setApproveType(ApproveTypeEnum.REGION.getValue());
                    request.setUserComplaintAnalysis(Constants.TIME_OUT_REJECT);
                    request.setSiteLeaderAppealAnalysis(Constants.TIME_OUT_REJECT);
                    this.appealOpenService.refuseAppeal(request);
                } catch (Exception e) {
                    log.error("timeOutAppealOperate-error:", e);
                }
            }
        } while (currentSize == pageSize);
    }

    /***
     * 申诉超时提醒，大区自动驳回
     *
     */
    public void timeoutAppealApprove() {
        log.info("timeoutAppealApprove");
        List<Appeal> appealList = this.appealService.timeoutAppealApprove(approveTimeoutDay);
        log.info("timeoutAppealApprove, appealList={}", JsonUtils.toJson(appealList));
        if (CollectionUtils.isEmpty(appealList)) {
            return;
        }
        appealList.forEach(appeal -> {
            try {
                WorkFlowEvent workFlowEvent = new WorkFlowEvent();
                workFlowEvent.setBizId(appeal.getId().toString());
                workFlowEvent.setBizType(Constants.LICENSE_APPEAL);
                appealService.refuseAppeal(workFlowEvent, Boolean.TRUE);
            } catch (Exception e) {
                log.error("timeoutAppealApprove-error:", e);
            }
        });
    }
}
