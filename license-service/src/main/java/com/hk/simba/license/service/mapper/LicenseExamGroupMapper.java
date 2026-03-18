package com.hk.simba.license.service.mapper;

import com.hk.simba.license.service.entity.LicenseExamGroup;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 执照考试分组信息 Mapper 接口
 * </p>
 *
 * @author cyh
 * @since 2021-09-03
 */
public interface LicenseExamGroupMapper extends BaseMapper<LicenseExamGroup> {

    /**
     * 根据员工id查询有效分组记录(有效分组只有一条,生成执照或者离职就失效)
     *
     * @param staffId
     * @return
     */
    LicenseExamGroup findValidGroupByStaffId(@Param("staffId") Long staffId);

}