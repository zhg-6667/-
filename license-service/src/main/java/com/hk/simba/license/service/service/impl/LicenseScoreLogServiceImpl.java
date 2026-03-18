package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hk.simba.license.service.entity.LicenseScoreLog;
import com.hk.simba.license.service.mapper.LicenseScoreLogMapper;
import com.hk.simba.license.service.service.LicenseScoreLogService;
import org.springframework.stereotype.Service;

/**
 * @author cyh
 * @date 2020/4/26/14:23
 * 执照扣分记录服务实现类
 */
@Service
public class LicenseScoreLogServiceImpl extends ServiceImpl<LicenseScoreLogMapper, LicenseScoreLog> implements LicenseScoreLogService {
}
