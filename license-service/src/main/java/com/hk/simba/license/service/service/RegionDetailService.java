package com.hk.simba.license.service.service;

import com.hk.simba.license.service.entity.RegionDetail;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 站点-大区映射表 服务类
 * </p>
 *
 * @author chenjh1
 * @since 2020-08-12
 */
public interface RegionDetailService extends IService<RegionDetail> {

    /**
     * @return
     * @Description 根据站点id获取大区信息
     * @Param
     * @Author chenjh1@homeking365.com
     * @Date 2020-08-12 16:28
     **/
    RegionDetail getRegionDetailBySiteId(Long siteId);

    /**
     * @return
     * @Description 根据站点id获取大区经理邮件
     * @Param
     * @Author chenjh1@homeking365.com
     * @Date 2020-08-12 16:28
     **/
    String getManagerEmailBySiteId(Long siteId);
}
