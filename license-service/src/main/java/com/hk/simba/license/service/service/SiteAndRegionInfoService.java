package com.hk.simba.license.service.service;

import com.hk.simba.license.api.vo.SiteAndRegionVO;

/**
 * @author cyh
 * @date 2021/3/4/16:43
 * 站点或者大区信息服务类
 */
public interface SiteAndRegionInfoService {
    /**
     * 通过站点id和站长id获取站长和大区信息
     *
     * @param siteId
     * @param siteLeaderId
     * @return
     */
    SiteAndRegionVO getSiteAndRegionInfo(Long siteId, Long siteLeaderId);

}
