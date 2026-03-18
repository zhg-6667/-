package com.hk.simba.license.service.task;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hk.simba.license.api.enums.PublishStatusEnum;
import com.hk.simba.license.api.request.violation.ViolationTypeStatusRequest;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.entity.ViolationType;
import com.hk.simba.license.service.mapper.ViolationTypeMapper;
import com.hk.simba.license.service.service.ViolationTypeService;
import com.hk.simba.staff.open.enums.YesOrNotEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author chenm
 * @since 2024/10/27
 **/
@Component
@Slf4j
public class ViolationTypeTaskHandler {
    @Autowired
    private ViolationTypeService violationTypeService;
    @Autowired
    private ViolationTypeMapper violationTypeMapper;

    /**
     * 生效违规类型
     *
     * @param param
     * @return
     */
    @XxlJob("effectiveViolationType")
    public ReturnT<String> effectiveViolationType(String param) {
        log.info("effectiveViolationType.begin,param:{}", param);
        Wrapper<ViolationType> wrapper = new EntityWrapper<>();
        wrapper.eq("publish_status", PublishStatusEnum.PENDING.getValue());
        wrapper.eq("deleted", YesOrNotEnum.NOT.getValue());
        wrapper.le("effective_time", new Date());
        List<ViolationType> list = violationTypeMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            log.info("effectiveViolationType.end.noData,param:{}", param);
            return ReturnT.SUCCESS;
        }

        for (ViolationType type : list) {
            ViolationTypeStatusRequest request = new ViolationTypeStatusRequest();
            request.setVersionId(type.getVersionId());
            request.setPublishStatus(PublishStatusEnum.CURRENT.getValue());
            request.setFailureTime(type.getEffectiveTime());
            request.setId(type.getId());
            request.setModifyBy(Constants.SYS);
            violationTypeService.effectiveViolationType(request);
        }
        log.info("effectiveViolationType.end,param:{}", param);
        return ReturnT.SUCCESS;
    }
}
