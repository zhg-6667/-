package com.hk.simba.license.service.service.impl;

import com.hk.simba.archive.open.DepartmentSiteRelationOpenService;
import com.hk.simba.archive.open.request.staff.SiteContactRequest;
import com.hk.simba.archive.open.response.staff.SiteContactData;
import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.license.api.vo.SiteAndRegionVO;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.service.SiteAndRegionInfoService;
import com.hk.simba.license.service.utils.BeanCopyUtil;
import com.hk.simba.license.service.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cyh
 * @date 2021/3/5/11:43
 */
@Service
@Slf4j
public class SiteAndRegionInfoServiceImpl implements SiteAndRegionInfoService {
    @Autowired
    RedisUtil redisUtil;
    @Reference
    private DepartmentSiteRelationOpenService relationOpenService;

    @Override
    public SiteAndRegionVO getSiteAndRegionInfo(Long siteId, Long siteLeaderId) {
        if (null == siteId || null == siteLeaderId) {
            return null;
        }
        String key = Constants.SITE_LEADER_REGION_CACHE + siteId;
        SiteAndRegionVO vo = (SiteAndRegionVO) redisUtil.get(key);
        if (vo != null) {
            return vo;
        }
        vo = new SiteAndRegionVO();
        SiteContactRequest request = new SiteContactRequest();
        request.setSiteId(siteId);
        request.setSiteLeaderId(siteLeaderId);
        BaseResponse<SiteContactData> baseResponse = this.relationOpenService.findSiteContactInfo(request);
        if (baseResponse.isSuccess() && baseResponse.getData() != null) {
            BeanCopyUtil.copyPropertiesIgnoreNull(baseResponse.getData(), vo);
            redisUtil.set(key, vo, 3600L);
        }
        return vo;
    }
}
