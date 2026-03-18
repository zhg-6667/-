package com.hk.simba.license.service.manager;

import com.google.common.collect.Lists;
import com.hk.simba.archive.open.DepartmentOpenService;
import com.hk.simba.archive.open.DepartmentSiteRelationOpenService;
import com.hk.simba.archive.open.response.department.DepartmentData;
import com.hk.simba.archive.open.response.department.SiteDepartmentData;
import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.base.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 档案相关接口
 *
 * @author :chenjh1
 * @date : 2022/11/23 15:31
 */
@Slf4j
@Component
public class ArchiveManager {

    @DubboReference
    private DepartmentSiteRelationOpenService departmentSiteRelationOpenService;

    @DubboReference
    private DepartmentOpenService departmentOpenService;

    public List<SiteDepartmentData> batchSearchSiteDepartment(List<Long> siteIdList) {
        List<SiteDepartmentData> list = Lists.newArrayList();
        if (CollectionUtils.isEmpty(siteIdList)) {
            return list;
        }
        BaseResponse<List<SiteDepartmentData>> response = departmentSiteRelationOpenService.batchSearchSiteDepartment(siteIdList);
        if (response.isSuccess() && CollectionUtils.isNotEmpty(response.getData())) {
            list = response.getData();
        }
        return list;
    }

    public SiteDepartmentData searchSiteDepartment(Long siteId) {
        List<SiteDepartmentData> list = this.batchSearchSiteDepartment(Lists.newArrayList(siteId));
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    public DepartmentData getDepartment(Long deptId) {
        BaseResponse<DepartmentData> response = departmentOpenService.detail(deptId);
        log.info("getDepartment={}", JsonUtils.toJson(response));
        return response.getData();
    }

}
