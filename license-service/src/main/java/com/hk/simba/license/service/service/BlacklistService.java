package com.hk.simba.license.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.api.request.black.BlackQueryRequest;
import com.hk.simba.license.api.vo.BlacklistVO;
import com.hk.simba.license.service.entity.Blacklist;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 黑名单 服务类
 * </p>
 *
 * @author cyh
 * @since 2020-10-17
 */
public interface BlacklistService extends IService<Blacklist> {
    /***
     * 创建黑名单
     * @param blacklist
     */
    void createBlack(Blacklist blacklist);

    /***
     * 移除黑名单
     * @param blacklist
     */
    void removeBlack(Blacklist blacklist);


    /***
     * 按条件查询黑名单列表(后台)
     * @param page
     * @param request
     * @return
     */
    List<BlacklistVO> getPageList(Page<Blacklist> page, BlackQueryRequest request);

    /***
     * 根据员工id查询黑名单
     * @param staffId
     * @return
     */
    Blacklist findByStaffId(Long staffId);

    List<Blacklist> findByStaffIds(List<Long> staffIds);
    /***
     * 根据员工身份证查询黑名单
     * @param idCard
     * @return
     */
    Blacklist findByIdCard(String idCard);

}
