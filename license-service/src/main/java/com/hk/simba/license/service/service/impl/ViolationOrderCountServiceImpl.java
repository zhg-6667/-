package com.hk.simba.license.service.service.impl;

import com.hk.simba.license.service.entity.ViolationOrderCount;
import com.hk.simba.license.service.mapper.ViolationOrderCountMapper;
import com.hk.simba.license.service.service.ViolationOrderCountService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 违规订单统计 服务实现类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Service
public class ViolationOrderCountServiceImpl extends ServiceImpl<ViolationOrderCountMapper, ViolationOrderCount> implements ViolationOrderCountService {

}
