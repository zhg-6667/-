package com.hk.simba.license.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.api.request.retrain.RetrainConfigQueryRequest;
import com.hk.simba.license.api.vo.RetrainConfigVO;
import com.hk.simba.license.service.entity.RetrainConfig;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 复训配置表 Mapper 接口
 * </p>
 *
 * @author cyh
 * @since 2021-09-03
 */
public interface RetrainConfigMapper extends BaseMapper<RetrainConfig> {

    /**
     * 根据岗位类型获取配置列表
     *
     * @param positionType
     * @return
     */
    List<RetrainConfig> findListByPositionType(@Param("positionType") Integer positionType);

    /***
     * 按条件查询配置分页信息
     * @param page
     * @param request
     * @return
     */
    List<RetrainConfigVO> getPageList(Page<RetrainConfigVO> page, RetrainConfigQueryRequest request);

    /***
     * 根据岗位类型获取有效的复训配置对应的城市
     *
     * @param positionType
     * @return
     */
    List<String> getInvalidConfigCityByPositionType(@Param("positionType") Integer positionType);

    /***
     * 根据岗位类型删除配置
     *
     * @param positionType
     * @return
     */
    void deleteByPositionType(@Param("positionType") Integer positionType);

}