package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.hk.simba.license.api.request.retrain.RetrainConfigQueryRequest;
import com.hk.simba.license.api.vo.RetrainConfigVO;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.entity.RetrainConfig;
import com.hk.simba.license.service.mapper.RetrainConfigMapper;
import com.hk.simba.license.service.service.RetrainConfigService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hk.simba.license.service.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 复训配置表 服务实现类
 * </p>
 *
 * @author cyh
 * @since 2021-09-03
 */
@Service
public class RetrainConfigServiceImpl extends ServiceImpl<RetrainConfigMapper, RetrainConfig> implements RetrainConfigService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<RetrainConfig> findListByPositionType(Integer type) {
        return this.baseMapper.findListByPositionType(type);
    }

    @Override
    public List<RetrainConfigVO> getPageList(Page<RetrainConfigVO> page, RetrainConfigQueryRequest request) {
        return this.baseMapper.getPageList(page, request);
    }

    @Override
    public List<String> getInvalidConfigCityByPositionType(Integer positionType) {
        String key = Constants.RETRAIN_POSITION_CITY + positionType;
        if (redisUtil.get(key) != null) {
            List<String> cityCodeList = (List<String>) redisUtil.get(key);
            return cityCodeList;
        }
        List<String> cityCodes = this.baseMapper.getInvalidConfigCityByPositionType(positionType);
        if (!CollectionUtils.isEmpty(cityCodes)) {
            redisUtil.set(key, cityCodes, 60 * 60 * 24 * 5L);
        }
        return cityCodes;
    }

    @Override
    public void deleteByPositionType(Integer positionType) {
        this.baseMapper.deleteByPositionType(positionType);
    }

}
