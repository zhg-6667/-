package com.hk.simba.license.service.open;

import com.hk.simba.license.api.RegionDetailOpenService;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName RegionDetailOpenServiceImpl
 * @Desiption 站点大区映射表接口
 * @Author chenjh1@homeking365.com
 * @Date 2020-08-12 16:28
 * @Version 1.0
 **/
@Slf4j
@DubboService
public class RegionDetailOpenServiceImpl implements RegionDetailOpenService {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void delRegionDetailCache() {
        redisUtil.del(Constants.REGION_DETAIL_CACHE);
    }
}
