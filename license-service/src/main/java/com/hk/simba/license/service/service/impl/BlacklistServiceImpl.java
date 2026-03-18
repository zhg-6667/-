package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.api.request.black.BlackQueryRequest;
import com.hk.simba.license.api.vo.BlacklistVO;
import com.hk.simba.license.service.entity.Blacklist;
import com.hk.simba.license.service.entity.BlacklistLog;
import com.hk.simba.license.service.mapper.BlacklistMapper;
import com.hk.simba.license.service.service.BlacklistLogService;
import com.hk.simba.license.service.service.BlacklistService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author cyh
 * @since 2020-10-17
 */
@Service
public class BlacklistServiceImpl extends ServiceImpl<BlacklistMapper, Blacklist> implements BlacklistService {

    @Autowired
    private BlacklistLogService blacklistLogService;

    @Override
    public void createBlack(Blacklist blacklist) {
        Blacklist temp = this.findByStaffId(blacklist.getStaffId());
        if (temp == null) {
            this.insert(blacklist);
            BlacklistLog log = new BlacklistLog();
            log.setStaffId(blacklist.getStaffId());
            log.setCreateBy(blacklist.getCreateBy());
            log.setCreateTime(new Date());
            log.setRemark(blacklist.getRemark());
            log.setType(blacklist.getType());
            log.setReason(blacklist.getReason());
            log.setOperateType("新增");
            this.blacklistLogService.insert(log);
        }
    }


    @Override
    public void removeBlack(Blacklist blacklist) {
        Blacklist temp = this.selectById(blacklist.getId());
        if (temp != null) {
            this.deleteById(blacklist.getId());
            BlacklistLog log = new BlacklistLog();
            log.setStaffId(temp.getStaffId());
            log.setCreateBy(blacklist.getCreateBy());
            log.setCreateTime(new Date());
            if (StringUtils.isNotBlank(blacklist.getRemark())) {
                log.setRemark(blacklist.getRemark());
            }
            log.setType(temp.getType());
            log.setOperateType("删除");
            this.blacklistLogService.insert(log);
        }
    }

    @Override
    public List<BlacklistVO> getPageList(Page<Blacklist> page, BlackQueryRequest request) {
        return this.baseMapper.getPageList(page, request);
    }

    @Override
    public Blacklist findByStaffId(Long staffId) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("staff_id", staffId);
        List<Blacklist> list = this.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<Blacklist> findByStaffIds(List<Long> staffIds) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.in("staff_id", staffIds);
        List<Blacklist> list = this.selectList(entityWrapper);
        return list;
    }

    @Override
    public Blacklist findByIdCard(String idCard) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("id_card", idCard).orderBy("create_time", false);
        List<Blacklist> list = this.selectList(entityWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }
}
