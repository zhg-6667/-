package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Maps;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.entity.RegionDetail;
import com.hk.simba.license.service.entity.RegionDetail;
import com.hk.simba.license.service.mapper.RegionDetailMapper;
import com.hk.simba.license.service.service.RegionDetailService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hk.simba.license.service.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 站点-大区映射表 服务实现类
 * </p>
 *
 * @author chenjh1
 * @since 2020-08-12
 */
@Service
@Slf4j
public class RegionDetailServiceImpl extends ServiceImpl<RegionDetailMapper, RegionDetail> implements RegionDetailService {


    @Autowired
    RedisUtil redisUtil;

    @Override
    public RegionDetail getRegionDetailBySiteId(Long siteId) {
        Map<String, RegionDetail> regionDetailMap = getRegionDetailMap();
        if (regionDetailMap != null) {
            return regionDetailMap.get(siteId.toString());
        }
        return null;
    }

    @Override
    public String getManagerEmailBySiteId(Long siteId) {
        RegionDetail regionDetail = getRegionDetailBySiteId(siteId);
        if (regionDetail != null) {
            return regionDetail.getManagerEmail();
        }
        return null;
    }

    public Map<String, RegionDetail> getRegionDetailMap() {
        Map<String, RegionDetail> regionDetailMap = null;
        regionDetailMap = (Map<String, RegionDetail>) redisUtil.get(Constants.REGION_DETAIL_CACHE);
        if (regionDetailMap == null) {
            regionDetailMap = Maps.newHashMap();
            List<RegionDetail> regionDetailList = this.selectList(new EntityWrapper<RegionDetail>());
            if (!CollectionUtils.isEmpty(regionDetailList)) {
                for (RegionDetail regionDetail : regionDetailList) {
                    regionDetailMap.put(regionDetail.getSiteId().toString(), regionDetail);
                }
            }
            redisUtil.set(Constants.REGION_DETAIL_CACHE, regionDetailMap, 3600L);
        }
        return regionDetailMap;
    }
}
