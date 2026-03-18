package com.hk.simba.license.service.task;

import com.alibaba.fastjson.JSONArray;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.spring.tasktracker.JobRunnerItem;
import com.github.ltsopensource.spring.tasktracker.LTS;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.google.common.base.Stopwatch;
import com.hk.simba.license.api.vo.LicenseInfoVO;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.LicenseStatusEnum;
import com.hk.simba.license.service.manager.StaffApiManager;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.sisyphus.merope.model.staff.staff.FindStaffListByStaffIdListStaffBasicDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 执照风控停单相关：吊销停单，生效取消停单
 *
 * @author pengdl
 * @since 2024-02-03
 */
@LTS
@Service
@Slf4j
public class LicenseStopOrderTaskHandler {
    @Autowired
    private StaffApiManager staffApiManager;
    @Autowired
    private LicenseService licenseService;

    /**
     * 执照风控停单任务，获取待处理员工列表
     *
     * @param jobContext
     * @return Result
     */
    @JobRunnerItem(shardValue = "license-stop-order-task")
    public Result licenseStopOrderTask(JobContext jobContext) {
        log.info("员工执照风控停单处理任务开始:{}", jobContext);
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, String> extParams = jobContext.getJob().getExtParams();
        List<Long> staffIdList;

        if (!ObjectUtils.isEmpty(extParams) && extParams.containsKey("staffIds")) {
            staffIdList = JSONArray.parseArray(extParams.get("staffIds"), Long.class);
        } else {
            staffIdList = staffApiManager.findStaffIdListOnJob();
        }
        if (CollectionUtils.isEmpty(staffIdList)) {
            log.info("员工执照风控停单处理任务结束：{}ms", stopwatch.stop());
            return new Result(Action.EXECUTE_SUCCESS, "执行成功");
        }

        int pageSize = 500;
        int size = staffIdList.size();
        int i = 0;
        log.info("员工执照风控停单处理任务，size={}", size);
        while (i < size) {
            int start = i;
            int end = i + pageSize;
            end = end > staffIdList.size() ? staffIdList.size() : end;
            List<FindStaffListByStaffIdListStaffBasicDTO> staffList = staffApiManager.findStaffListByStaffIdList(staffIdList.subList(start, end));
            dealWidthStaffList(staffList);
            i = end;
        }
        log.info("员工执照风控停单处理任务结束：{}ms", stopwatch.stop());
        return new Result(Action.EXECUTE_SUCCESS, "执行成功");
    }

    public void dealWidthStaffList(List<FindStaffListByStaffIdListStaffBasicDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (FindStaffListByStaffIdListStaffBasicDTO staff : list) {
            if (!Constants.STAFF_ON_JOB_STATE.contains(staff.getWorkingState())) {
                continue;
            }
            LicenseInfoVO licenseInfoVO = licenseService.findLicenseByStaffId(staff.getId());
            if (ObjectUtils.isEmpty(licenseInfoVO)) {
                //员工在职，执照不存在跳过
                log.info("员工执照风控停单处理执照不存在staffId={}", staff.getId());
                licenseService.applyStopOrder(staff.getId(), licenseInfoVO);
                continue;
            }
            if (LicenseStatusEnum.EFFECTIVE.getCode().equals(licenseInfoVO.getStatus())) {
                //员工在职，执照生效。取消风控停单处理
                licenseService.endStopOrder(licenseInfoVO);
                continue;
            }
            //员工在职，执照吊销。申请风控停单处理
            licenseService.applyStopOrder(staff.getId(), licenseInfoVO);
        }
    }
}
