package com.hk.simba.license.service.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.api.request.black.BlackQueryRequest;
import com.hk.simba.license.api.vo.BlacklistVO;
import com.hk.simba.license.service.entity.Blacklist;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 黑名单 Mapper 接口
 * </p>
 *
 * @author cyh
 * @since 2020-10-17
 */
public interface BlacklistMapper extends BaseMapper<Blacklist> {

    /***
     * 按条件查询黑名单列表(后台)
     * @param page
     * @param request
     * @return
     */
    List<BlacklistVO> getPageList(Page<Blacklist> page, BlackQueryRequest request);

}