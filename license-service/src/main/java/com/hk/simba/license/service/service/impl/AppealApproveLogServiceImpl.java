package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hk.simba.license.service.entity.AppealApproveLog;
import com.hk.simba.license.service.mapper.AppealApproveLogMapper;
import com.hk.simba.license.api.vo.AppealApproveLogVO;
import com.hk.simba.license.service.service.AppealApproveLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cyh
 * @date 2020/5/13/16:06
 * 申诉审核日志实现类
 */
@Service
public class AppealApproveLogServiceImpl extends ServiceImpl<AppealApproveLogMapper, AppealApproveLog> implements AppealApproveLogService {
    @Override
    public List<AppealApproveLogVO> getAppealApproveLogList(Long appealId, Integer approveType) {
        return this.baseMapper.getAppealApproveLogList(appealId, approveType);
    }

    @Override
    public List<AppealApproveLogVO> getAppealApproveLogsByCondition(Integer status, Long appealId, Integer approveType) {
        return this.baseMapper.getAppealApproveLogsByCondition(status, appealId, approveType);
    }
}
