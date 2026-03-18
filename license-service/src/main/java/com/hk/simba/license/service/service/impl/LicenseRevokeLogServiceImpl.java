package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hk.simba.license.service.entity.LicenseRevokeLog;
import com.hk.simba.license.service.mapper.LicenseRevokeLogMapper;
import com.hk.simba.license.service.service.LicenseRevokeLogService;
import org.springframework.stereotype.Service;

/**
 * @author cyh
 * @date 2020/4/27/13:53
 * 吊销执照记录服务实现类
 */
@Service
public class LicenseRevokeLogServiceImpl extends ServiceImpl<LicenseRevokeLogMapper, LicenseRevokeLog> implements LicenseRevokeLogService {
}
