package com.hk.simba.license.service.manager;

import com.google.common.collect.Lists;
import com.hk.quark.base.util.JsonUtil;
import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.TaskNodeEnum;
import com.hk.simba.x.lark.open.api.LarkInstanceOpenService;
import com.hk.simba.x.lark.open.request.CancelInstanceRequest;
import com.hk.simba.x.lark.open.request.GetInstanceRequest;
import com.hk.simba.x.lark.open.request.RejectInstanceRequest;
import com.hk.simba.x.lark.open.response.InstanceResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 工作流相关接口
 *
 * @author :chenjh1
 * @date : 2022/11/23 15:31
 */
@Slf4j
@Component
public class LarkInstanceManager {

    @DubboReference(timeout = 30000)
    private LarkInstanceOpenService larkInstanceOpenService;

    public InstanceResponse getInstanceDetail(String instanceCode) {
        GetInstanceRequest request = new GetInstanceRequest();
        request.setInstanceCode(instanceCode);
        BaseResponse<InstanceResponse> response = larkInstanceOpenService.getInstanceDetail(request);
        if (response.isSuccess()) {
            InstanceResponse instanceResponse = response.getData();
            log.info("getInstanceDetail,id={},detail={}", instanceCode, JsonUtil.objectToJSON(instanceResponse));
            return instanceResponse;
        } else {
            log.error("getInstanceDetail_error={}", JsonUtil.objectToJSON(response));
        }
        return null;
    }

    public String reject(String instanceCode, InstanceResponse instance, String comment) {
        log.info("instance_reject, instanceCode={}", instanceCode);
        RejectInstanceRequest request = new RejectInstanceRequest();
        request.setApprovalCode(instance.getApprovalCode());
        request.setInstanceCode(instanceCode);
        InstanceResponse.Task task = this.getLastTask(instance);
        request.setOpenId(task.getOpenId());
        request.setUserId(task.getUserId());
        request.setTaskId(task.getId());
        request.setComment(comment);
        BaseResponse<String> response = larkInstanceOpenService.reject(request);
        if (response.isSuccess()) {
            log.info("instance_reject,response={}", response.getData());
            return response.getData();
        } else {
            log.error("instance_reject_error={}", JsonUtil.objectToJSON(response));
        }
        return null;
    }

    public String cancel(String instanceCode, InstanceResponse instance) {
        log.info("instance_cancel, instanceCode={}", instanceCode);
        CancelInstanceRequest request = new CancelInstanceRequest();
        request.setApprovalCode(instance.getApprovalCode());
        request.setInstanceCode(instanceCode);
        InstanceResponse.Task task = this.getLastTask(instance);
        request.setOpenId(task.getOpenId());
        request.setUserId(task.getUserId());
        request.setNotifyStarter(Boolean.TRUE);
        BaseResponse<String> response = larkInstanceOpenService.cancel(request);
        if (response.isSuccess()) {
            log.info("instance_cancel,response={}", response.getData());
            return response.getData();
        } else {
            log.error("instance_cancel_error={}", JsonUtil.objectToJSON(response));
        }
        return null;
    }

    public InstanceResponse.Task getLastTask(InstanceResponse instance) {
        if (ObjectUtils.isEmpty(instance)) {
            return null;
        }
        List<InstanceResponse.Task> taskList = instance.getTaskList();
        if (CollectionUtils.isEmpty(taskList)) {
            return null;
        }
        return taskList.get(taskList.size() - 1);
    }

    public String getLastTaskName(InstanceResponse instance) {
        if (ObjectUtils.isEmpty(instance)) {
            return null;
        }
        List<InstanceResponse.Task> taskList = instance.getTaskList();
        if (CollectionUtils.isEmpty(taskList)) {
            return null;
        }
        InstanceResponse.Task task = taskList.get(taskList.size() - 1);
        return task.getNodeName();
    }

    public Boolean isTaskPass(InstanceResponse instance, String taskNodeName) {
        if (ObjectUtils.isEmpty(instance) || StringUtils.isEmpty(taskNodeName)) {
            return false;
        }
        List<InstanceResponse.Task> taskList = instance.getTaskList();
        if (CollectionUtils.isEmpty(taskList)) {
            return false;
        }
        boolean flag = false;
        for (InstanceResponse.Task task : taskList) {
            if (taskNodeName.equals(task.getNodeName())) {
                if (Constants.WORKFLOW_TASK_APPROVED.equals(task.getStatus())) {
                    return true;
                } else if (Constants.WORKFLOW_TASK_DONE.equals(task.getStatus())) {
                    //不直接结束，只有任务节点都为DNOE才返回true
                    flag = Boolean.TRUE;
                } else if (Constants.WORKFLOW_TASK_REJECTED.equals(task.getStatus())) {
                    return false;
                }
            }
        }
        return flag;
    }

    public String getTaskComment(InstanceResponse instance, TaskNodeEnum taskNodeEnum) {
        String comment = null;
        if (ObjectUtils.isEmpty(instance) || ObjectUtils.isEmpty(taskNodeEnum)) {
            return comment;
        }
        List<InstanceResponse.Task> taskList = instance.getTaskList();
        List<InstanceResponse.Timeline> timelineList = instance.getTimelineList();
        if (CollectionUtils.isEmpty(taskList) || CollectionUtils.isEmpty(timelineList)) {
            return comment;
        }
        List<String> taskIdList = Lists.newArrayList();
        for (InstanceResponse.Task task : taskList) {
            if (taskNodeEnum.getText().equals(task.getNodeName())) {
                taskIdList.add(task.getId());
            }
        }
        if (CollectionUtils.isEmpty(taskIdList)) {
            return comment;
        }
        for (InstanceResponse.Timeline timeline : timelineList) {
            if (taskIdList.contains(timeline.getTaskId()) && StringUtils.hasLength(timeline.getComment())) {
                comment = timeline.getComment();
                break;
            }
        }
        return comment;
    }
}
