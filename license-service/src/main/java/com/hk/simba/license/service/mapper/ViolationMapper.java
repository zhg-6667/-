package com.hk.simba.license.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.api.request.violation.ViolationQueryRequest;
import com.hk.simba.license.api.vo.ViolationVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 违规信息 Mapper 接口
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
public interface ViolationMapper extends BaseMapper<Violation> {
    List<ViolationVO> pageViolationList(Page<Violation> page, ViolationQueryRequest query);

    List<Violation> pageTimoutPayList(Page<Violation> page, int timeoutPay);

    /***
     * 部门信息(去重)
     * @return
     */
    List<String> findAllDepartment();

    /***
     * 根据创建时间和扣分类型分页查询
     *
     * @param page
     * @param startTime
     * @param endTime
     * @param deductType
     * @return
     */
    List<Violation> pageByCreateTimeAndDeDuctType(Page<Violation> page, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("deductType") Integer deductType);

    /**
     * 根据条件查询违规列表
     *
     * @param request
     * @return
     */
    List<Violation> findListByCondition(ViolationQueryRequest request);


}