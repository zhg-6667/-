package com.hk.simba.license.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.api.request.violation.ViolationQueryRequest;
import com.hk.simba.license.api.vo.ViolationVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 违规信息 服务类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
public interface ViolationService extends IService<Violation> {

    /**
     * 后台查询分页方法
     *
     * @param page
     * @param query
     * @return
     */
    List<ViolationVO> queryPage(Page<Violation> page, ViolationQueryRequest query);


    /**
     * @param code
     * @param staffId
     * @return
     */
    List<Violation> findListByCodeAndStaffId(String code, Long staffId);

    /***
     * 查询有效的未付款的记录
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<Violation> findValidAndNoPayViolation(int pageNo, int pageSize);


    /**
     * 后台查询超时未缴列表分页方法
     *
     * @param page
     * @param timeoutPay
     * @return
     */
    List<Violation> pageTimoutPayList(Page<Violation> page, int timeoutPay);


    /**
     * 根据事件code和status查询违规列表
     *
     * @param code
     * @param status
     * @return
     */
    List<Violation> findListByCodeAndStatus(String code, Integer status);


    /**
     * 根据事件code查询违规列表
     *
     * @param code
     * @return
     */
    List<Violation> findListByCode(String code);

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
     * @param deDuctType
     * @return
     */
    List<Violation> pageByCreateTimeAndDeDuctType(Page<Violation> page, Date startTime, Date endTime, Integer deDuctType);

    /**
     * 根据条件查询违规列表
     *
     * @param request
     * @return
     */
    List<Violation> findListByCondition(ViolationQueryRequest request);

    /**
     * 给质质高-发送服务禁忌审批提醒邮件
     *
     * @param violation
     * @param responsibilityDeptName
     * @return
     */
    void sendStaffForbiddenEmail(Violation violation, String responsibilityDeptName);

}
