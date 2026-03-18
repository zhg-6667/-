package com.hk.simba.license.service.service;

import com.hk.simba.license.service.entity.LicenseExamGroup;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 执照考试分组信息 服务类
 * </p>
 *
 * @author cyh
 * @since 2021-09-03
 */
public interface LicenseExamGroupService extends IService<LicenseExamGroup> {

    /**
     * 根据员工id查询有效分组记录(有效分组只有一条,生成执照或者离职就失效)
     *
     * @param staffId
     * @return
     */
    LicenseExamGroup findValidGroupByStaffId(Long staffId);

    /**
     * 创建或变更员工考试分组记录
     *
     * @param staffId
     * @param examId  examId
     * @param name
     * @return
     */
    LicenseExamGroup createOrUpdateLicenseExamGroup(Long staffId, String examId, String name);

    /**
     * 失效分组考试记录
     *
     * @param group
     * @return
     */
    void invalidLicenseExamGroup(LicenseExamGroup group);

}
