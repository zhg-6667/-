package com.hk.simba.license.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.hk.simba.license.api.request.violation.ViolationTypeRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeStatusRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeVersionPageRequest;
import com.hk.simba.license.api.vo.ViolationTypeVersionPageVO;
import com.hk.simba.license.api.vo.ViolationTypeVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.simba.license.service.entity.ViolationType;
import com.hk.simba.license.service.mq.dto.MessageEntity;

import java.util.List;

/**
 * <p>
 * 违规类型 服务类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
public interface ViolationTypeService extends IService<ViolationType> {

    /**
     * @return
     * @Description 根据事件详情中的级别匹配违规类型
     * @Param
     * @Author chenjh1@homeking365.com
     * @Date 2020-08-04 14:48
     **/
    ViolationType getViolationTypeByEvent(MessageEntity entity);

    /**
     * 根据code查询违规类型
     *
     * @param code
     */
    ViolationType getViolationTypeByCode(String code);


    /***
     * 按条件查询列表(后台)
     * @param page
     * @param request
     * @return
     */
    List<ViolationTypeVO> getPageList(Page<ViolationType> page, ViolationTypeRequest request);


    /***
     * 查询所有违规类型
     * @return
     */
    List<String> findAllType();


    /***
     * 查询所有违规细则
     * @return
     */
    List<String> findAllDetail();


    /***
     * 根据类型查询违规细则
     * @param type
     * @return
     */
    List<String> findDetailByType(String type);

    /***
     * 按扣分类型查询所有违规类型
     * @param deductType
     * @return
     */
    List<String> findByDeductType(Integer deductType);

    /**
     * 根据code和departmentType查询违规类型
     *
     * @param code
     * @param departmentType
     * @return
     */
    ViolationType getViolationTypeByCodeAndDeptType(String code, Integer departmentType);

    /**
     * 查询违规类型（唯一性校验）
     *
     * @param violationType
     * @param ignoreId
     * @param ignoreVersionId
     * @return
     */
    List<ViolationType> getViolationTypeListByParams(ViolationType violationType, Long ignoreId, Long ignoreVersionId);

    /**
     * @return
     * @Description 根据事件详情中的级别匹配违规类型
     * @Param
     **/
    ViolationType getViolationTypeByEvent(MessageEntity entity, Integer departmentType);

    /**
     * @return
     * @Description 根据事件中code和部门类型查询违规类型
     * @Param
     **/
    ViolationType getViolationTypeByEventCodeAndDept(MessageEntity entity, Integer departmentType);

    /**
     * 查询违规类型版本分页
     *
     * @param request
     * @return
     */
    PageResult<ViolationTypeVersionPageVO> findVersionPage(ViolationTypeVersionPageRequest request);

    /**
     * 查询违规类型版本列表
     * @param request
     * @return
     */
    List<ViolationTypeVersionPageVO> findVersionList(ViolationTypeVersionPageRequest request);

    /**
     * 删除
     * @param request
     */
    void delete(ViolationTypeStatusRequest request);

    /**
     * 禁用
     * @param request
     */
    void updatePublishStatus(ViolationTypeStatusRequest request);

    /**
     * 启用
     * @param request
     */
    void effectiveViolationType(ViolationTypeStatusRequest request);

    ViolationType getLastVersion(Long versionId);
}
