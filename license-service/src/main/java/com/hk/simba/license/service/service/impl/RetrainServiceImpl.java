package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hk.simba.license.api.enums.ViolationMessageTypeEnum;
import com.hk.simba.license.api.request.retrain.RetrainDetailRequest;
import com.hk.simba.license.api.request.retrain.RetrainInvalidRequest;
import com.hk.simba.license.api.request.retrain.RetrainQueryRequest;
import com.hk.simba.license.api.vo.RetrainVO;
import com.hk.simba.license.service.constant.enums.RetrainStatusEnum;
import com.hk.simba.license.service.constant.enums.RetrainTypeEnum;
import com.hk.simba.license.service.entity.Retrain;
import com.hk.simba.license.service.mapper.RetrainMapper;
import com.hk.simba.license.service.service.RetrainConfigService;
import com.hk.simba.license.service.service.RetrainService;
import com.hk.simba.license.service.service.ViolationMessageService;
import com.hk.simba.license.service.utils.BeanCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author cyh
 * @date 2020/8/3/16:58
 * 复训实现类
 */
@Service
@Slf4j
public class RetrainServiceImpl extends ServiceImpl<RetrainMapper, Retrain> implements RetrainService {

    @Autowired
    private ViolationMessageService violationMessageService;

    @Autowired
    private RetrainConfigService retrainConfigService;

    @Override
    public List<RetrainVO> getPageList(Page<Retrain> page, RetrainQueryRequest request) {
        return this.baseMapper.getPageList(page, request);
    }

    @Override
    public void saveRetrain(RetrainVO vo) {
        if (vo == null) {
            return;
        }
        Retrain retrain = new Retrain();
        BeanCopyUtil.copyPropertiesIgnoreNull(vo, retrain);
        this.insert(retrain);
    }

    @Override
    public List<RetrainVO> findRetrainByCondition(RetrainDetailRequest request) {
        return this.baseMapper.findRetrainByCondition(request);
    }


    @Override
    public void invalidRetrainByCondition(RetrainInvalidRequest request) {
        Map<String, Object> map = Maps.newHashMap();
        if (request.getStaffId() != null) {
            map.put("staff_id", request.getStaffId());
        }
        if (request.getId() != null) {
            map.put("id", request.getId());
        }
        if (request.getType() != null) {
            map.put("type", request.getType());
        }
        if (request.getViolationId() != null) {
            map.put("violation_id", request.getViolationId());
        }
        map.put("status", RetrainStatusEnum.WAIT_RETRAIN.getValue());
        List<Retrain> retrainList = this.selectByMap(map);
        if (!CollectionUtils.isEmpty(retrainList)) {
            Date date = new Date();
            for (Retrain retrain : retrainList) {
                retrain.setModifyBy(request.getOperator());
                retrain.setModifyTime(date);
                retrain.setStatus(RetrainStatusEnum.INVALID.getValue());
                retrain.setReason(request.getReason());
                if (retrain.getStatusChangeTime() == null) {
                    retrain.setStatusChangeTime(date);
                }
            }
            this.updateBatchById(retrainList);
            this.invalidMessageWhenInvalidRetrains(retrainList);
        }
    }

    @Override
    public void invalidRetrainByIds(RetrainInvalidRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            return;
        }
        List<Retrain> retrainList = this.selectBatchIds(request.getIds());
        if (CollectionUtils.isEmpty(retrainList)) {
            return;
        }
        Date date = new Date();
        List<Retrain> tempList = new ArrayList<>();
        for (Retrain retrain : retrainList) {
            if (retrain.getStatus().equals(RetrainStatusEnum.WAIT_RETRAIN.getValue())) {
                retrain.setModifyBy(request.getOperator());
                retrain.setModifyTime(date);
                retrain.setStatus(RetrainStatusEnum.INVALID.getValue());
                retrain.setReason(request.getReason());
                if (retrain.getStatusChangeTime() == null) {
                    retrain.setStatusChangeTime(date);
                }
                this.updateById(retrain);
                tempList.add(retrain);
            }
        }
        this.invalidMessageWhenInvalidRetrains(tempList);
    }

    @Override
    public Boolean saveViolationRetrain(RetrainVO vo) {
        //只有满足违规复训的才生成违规复训
        List<String> cityCodes = this.retrainConfigService.getInvalidConfigCityByPositionType(vo.getPositionType());
        if (!CollectionUtils.isEmpty(cityCodes) && cityCodes.contains(vo.getCityCode())) {
            this.saveRetrain(vo);
            return true;
        }
        return false;
    }

    @Override
    public List<Retrain> pageTimeoutRetrainList(Page<Retrain> page, Integer timeoutPay) {
        return this.baseMapper.pageTimeoutRetrainList(page, timeoutPay);
    }

    /**
     * 失效复训记录时失效短信
     */
    private void invalidMessageWhenInvalidRetrains(List<Retrain> retrainList) {
        if (!CollectionUtils.isEmpty(retrainList)) {
            List<Integer> types = new ArrayList<>();
            types.add(ViolationMessageTypeEnum.RETRAIN_STAFF_SMS.getValue());
            types.add(ViolationMessageTypeEnum.RETRAIN_STAFF_PUSH.getValue());

            List<Integer> violationTypes = new ArrayList<>();
            violationTypes.add(ViolationMessageTypeEnum.VIOLATION_RETRAIN_STAFF_SMS.getValue());
            violationTypes.add(ViolationMessageTypeEnum.VIOLATION_RETRAIN_STAFF_PUSH.getValue());

            List<Integer> trainingTypes = new ArrayList<>();
            trainingTypes.add(ViolationMessageTypeEnum.TRAINING_RETRAIN_STAFF_SMS.getValue());
            trainingTypes.add(ViolationMessageTypeEnum.TRAINING_RETRAIN_STAFF_PUSH.getValue());
            for (Retrain retrain : retrainList) {
                if (retrain.getType().equals(RetrainTypeEnum.VIOLATION_RETRAIN.getValue())) {
                    this.violationMessageService.invalidMessageByViolationIdAndType(retrain.getViolationId(), violationTypes, retrain.getModifyBy());
                } else if (retrain.getType().equals(RetrainTypeEnum.TRAINING_RETRAIN.getValue())) {
                    this.violationMessageService.invalidMessageByViolationIdAndType(retrain.getViolationId(), trainingTypes, retrain.getModifyBy());
                } else {
                    this.violationMessageService.invalidMessageByViolationIdAndType(retrain.getViolationId(), types, retrain.getModifyBy());
                }
            }
        }
    }
}
