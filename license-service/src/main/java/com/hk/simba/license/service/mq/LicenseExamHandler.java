package com.hk.simba.license.service.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.hk.simba.archive.open.RosterOpenService;
import com.hk.simba.archive.open.response.roster.RosterData;
import com.hk.simba.dict.exam.PassEnum;
import com.hk.simba.dict.exam.StudentTypeEnum;
import com.hk.simba.license.api.LicenseOpenService;
import com.hk.simba.license.api.request.BaseRequest;
import com.hk.simba.license.api.vo.LicenseVO;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.service.MqMessageService;
import com.hk.simba.x.exam.event.ExamResultEvent;
import com.hk.sisyphus.merope.core.staff.StaffApi;
import com.hk.sisyphus.merope.model.staff.staff.SearchStaffByConditionRequest;
import com.hk.sisyphus.merope.model.staff.staff.SearchStaffByConditionStaffBasicDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author chenjh
 * @date 2022/06/20/11:08
 * 考试系统-考试成绩MQ，执照考试消费
 */
@Component
@Slf4j
public class LicenseExamHandler implements MessageListener {
    @Autowired
    private MqMessageService mqMessageService;

    @Autowired
    private LicenseOpenService licenseOpenService;
    @DubboReference
    private RosterOpenService rosterOpenService;
    @Autowired
    private StaffApi staffApi;

    @Value("${license.exam.type}")
    private String licenseExamType;

    @Override
    public Action consume(Message msg, ConsumeContext consumeContext) {
        String body = new String(msg.getBody());
        log.info("收到消息,msgid={},topic={},tag:={},body={}", msg.getMsgID(), msg.getTopic(), msg.getTag(), body);

        try {
            if (!licenseExamType.equals(msg.getTag())) {
                return Action.CommitMessage;
            }

            ExamResultEvent event = JSON.parseObject(body, ExamResultEvent.class);
            if (PassEnum.NO.getValue().equals(event.getIsPass())) {
                return Action.CommitMessage;
            }
            mqMessageService.saveMessage(msg);
            //cargo系统发过来的通过执照学习地图的用户编号
            String studentId = event.getStudentId().toString();
            String mapId = event.getExamId().toString();
            BaseRequest<LicenseVO> request = new BaseRequest<LicenseVO>();
            LicenseVO vo = new LicenseVO();
            if (StudentTypeEnum.STAFF.getValue().equals(event.getStudentType())) {
                vo.setThirdUserId(studentId);
            } else {
                vo.setThirdUserId(this.getStaffIdByRosterId(event.getStudentId()));
            }
            vo.setMapId(mapId);
            vo.setCreateBy(Constants.SYS);
            request.setData(vo);
            this.licenseOpenService.createLicense(request);
            log.info("消息处理完成,msgid:{}", msg.getMsgID());
        } catch (Exception e) {
            log.error("消息处理异常，msgid:{}", msg.getMsgID(), e);
            throw e;
        }
        return Action.CommitMessage;
    }


    /***
     * 根据职员id返回其员工id
     *
     */
    private String getStaffIdByRosterId(Long rosterId) {
        com.hk.simba.base.common.dto.response.BaseResponse<RosterData> response = rosterOpenService.findRosterById(rosterId);
        if (response.isSuccess() && response.getData() != null) {
            RosterData data = response.getData();
            //或是H开头，则填充其员工信息
            SearchStaffByConditionRequest request = new SearchStaffByConditionRequest();
            request.setIdCard(data.getIdCard());
            com.hk.simba.base.common.dto.response.BaseResponse<SearchStaffByConditionStaffBasicDTO> baseResponse = staffApi.searchStaffByCondition(request);
            SearchStaffByConditionStaffBasicDTO dto = baseResponse.getData();
            if (baseResponse.isSuccess() && dto != null) {
                return dto.getId().toString();
            }
        }
        return null;
    }
}
