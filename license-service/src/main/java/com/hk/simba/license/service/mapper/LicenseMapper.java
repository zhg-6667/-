package com.hk.simba.license.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.api.request.license.LicenseInfoRequest;
import com.hk.simba.license.api.vo.LicenseInfoVO;
import com.hk.simba.license.service.entity.License;
import com.hk.simba.license.api.request.license.LicenseQueryRequest;
import com.hk.simba.license.api.vo.LicenseVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cyh
 * @date 2020/4/26/12:37
 * 执照表Mapper接口
 */
public interface LicenseMapper extends BaseMapper<License> {

    /***
     * 按条件查询执照分页信息(后台)
     * @param page
     * @param request
     * @return
     */
    List<LicenseVO> getPageList(Page<License> page, LicenseQueryRequest request);


    /**
     * 查询执照信息列表-根据员工id列表或者站点列表
     *
     * @param param
     * @return
     */
    List<LicenseInfoVO> findLicenseList(@Param("param") LicenseInfoRequest param);


    /**
     * 通过员工id集查找执照信息
     *
     * @param staffIds
     * @return
     */
    List<License> findListByStaffIds(@Param("staffIds") List<Long> staffIds);
}
