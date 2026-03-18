package com.hk.simba.license.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.api.request.violation.ViolationTypeRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeStatusRequest;
import com.hk.simba.license.api.request.violation.ViolationTypeVersionPageRequest;
import com.hk.simba.license.api.vo.ViolationTypeVersionPageVO;
import com.hk.simba.license.api.vo.ViolationTypeVO;
import com.hk.simba.license.service.entity.ViolationType;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 违规类型 Mapper 接口
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
public interface ViolationTypeMapper extends BaseMapper<ViolationType> {

    /***
     * 按条件分页查询
     * @param page
     * @param query
     * @return
     */
    List<ViolationTypeVO> getPageList(Page<ViolationType> page, ViolationTypeRequest query);


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
    List<String> findByDeductType(@Param("deductType") Integer deductType);

    /***
     * 按条件分页查询
     * @param page
     * @param request
     * @return
     */
    List<ViolationTypeVersionPageVO> findVersionPage(Page<ViolationType> page, ViolationTypeVersionPageRequest request);

    /**
     * 查询版本列表
     * @param request
     * @return
     */
    List<ViolationTypeVersionPageVO> findVersionList(ViolationTypeVersionPageRequest request);

    /**
     * 删除
     *
     * @param id
     * @param modifyBy
     */
    void deleteViolationType(@Param("id") Long id, @Param("modifyBy") String modifyBy,
        @Param("modifyTime") Date modifyTime);

    /**
     * 修改发布状态
     * @param request
     */
    void updatePublishStatus(ViolationTypeStatusRequest request);

    /**
     * 失效
     * @param request
     */
    void invalidViolationType(ViolationTypeStatusRequest request);
}