package com.hk.simba.license.service.service;

import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.entity.ViolationMessage;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 违规消息通知 服务类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
public interface ViolationMessageService extends IService<ViolationMessage> {

    void saveViolationMessage(Violation violation);

    /**
     * 异步保存违规消息通知
     * @param violation 违规消息
     */
    void asyncSaveViolationMessage(Violation violation);

    /**
     * 短信失效-复训的失效单独处理
     *
     * @param violation
     */
    void invalidViolationMessage(Violation violation);

    /**
     * 7天未支付，短信重提醒员工
     *
     * @param violation
     * @return
     */
    ViolationMessage saveStaffRepeatNotifyMessage(Violation violation);

    /**
     * 7天未支付，短信重提醒站长
     *
     * @param violation
     * @return
     */
    ViolationMessage saveLeaderRepeatNotifyMessage(Violation violation);


    /**
     * 复训短信提醒
     *
     * @param violation
     * @param retrainType
     * @return
     */
    void saveRetrainMessage(Violation violation, Integer retrainType);

    /**
     * 按违规id和短信类型失效短息
     *
     * @param violationId
     * @param types
     * @param modify
     * @return
     */
    void invalidMessageByViolationIdAndType(Long violationId, List<Integer> types, String modify);

    /**
     * 批量失效违规短信
     *
     * @param violationMessageList
     * @param modify
     */
    void batchInvalidViolationMessage(List<ViolationMessage> violationMessageList, String modify);
}
