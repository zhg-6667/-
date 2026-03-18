package com.hk.simba.license.service.open;

import com.baomidou.mybatisplus.plugins.Page;
import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.api.RetrainConfigOpenService;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.retrain.RetrainConfigQueryRequest;
import com.hk.simba.license.api.request.retrain.RetrainConfigRequest;
import com.hk.simba.license.api.vo.RetrainConfigDetailVO;
import com.hk.simba.license.api.vo.RetrainConfigVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.CommonStatusEnum;
import com.hk.simba.license.service.constant.enums.PositionTypeEnum;
import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.simba.license.service.entity.RetrainConfig;
import com.hk.simba.license.service.service.RetrainConfigService;
import com.hk.simba.license.service.utils.R;
import com.hk.simba.license.service.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cyh
 * @date 2021/9/3/15:59
 * <p>
 * 复训配置接口
 */
@Slf4j
@DubboService
public class RetrainConfigOpenServiceImpl implements RetrainConfigOpenService {

    @Autowired
    private RetrainConfigService retrainConfigService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public BaseResponse createRetrainConfig(RetrainConfigRequest request) {
        BaseResponse baseResponse = this.checkParam(request);
        if (!baseResponse.isSuccess()) {
            return baseResponse;
        }
        List<RetrainConfig> configList = this.retrainConfigService.findListByPositionType(request.getPositionType());
        if (!CollectionUtils.isEmpty(configList)) {
            return R.result(ResponseCodeEnum.EXIST_RETRAIN_CONFIG);
        }
        this.saveConfig(request);
        this.deleteAndRefreshByPosition(request.getPositionType());
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse updateRetrainConfig(RetrainConfigRequest request) {
        BaseResponse baseResponse = this.checkParam(request);
        if (!baseResponse.isSuccess()) {
            return baseResponse;
        }
        List<RetrainConfig> configList = this.retrainConfigService.findListByPositionType(request.getPositionType());
        if (CollectionUtils.isEmpty(configList)) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        this.retrainConfigService.deleteByPositionType(request.getPositionType());
        this.saveConfig(request);
        this.deleteAndRefreshByPosition(request.getPositionType());
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse updateStatusByPositionType(RetrainConfigRequest request) {
        if (request == null || StringUtils.isBlank(request.getOperator()) || request.getStatus() == null || request.getPositionType() == null) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        CommonStatusEnum statusEnum = CommonStatusEnum.getEnumByValue(request.getStatus());
        if (statusEnum == null) {
            return R.result(ResponseCodeEnum.ERROR_PARAM);
        }
        List<RetrainConfig> configList = this.retrainConfigService.findListByPositionType(request.getPositionType());
        if (CollectionUtils.isEmpty(configList)) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        Date modifyTime = new Date();
        for (RetrainConfig config : configList) {
            config.setModifyBy(request.getOperator());
            config.setModifyTime(modifyTime);
            config.setStatus(request.getStatus());
        }
        this.retrainConfigService.updateBatchById(configList);
        this.deleteAndRefreshByPosition(request.getPositionType());
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse<PageResult<RetrainConfigVO>> list(PageRequest page, RetrainConfigQueryRequest request) {
        Page<RetrainConfigVO> retrainPage = new Page<>(page.getPageNo(), page.getPageSize());
        List<RetrainConfigVO> voList = this.retrainConfigService.getPageList(retrainPage, request);
        if (CollectionUtils.isEmpty(voList)) {
            return R.result(ResponseCodeEnum.SUCCESS, new PageResult<>());
        }
        PageResult<RetrainConfigVO> pageResult = new PageResult<>();
        pageResult.setResults(voList);
        pageResult.setCount(retrainPage.getTotal());
        pageResult.setPageNo(retrainPage.getCurrent());
        pageResult.setPageSize(retrainPage.getSize());
        pageResult.setTotalPage(retrainPage.getPages());
        return R.result(ResponseCodeEnum.SUCCESS, pageResult);
    }

    @Override
    public BaseResponse getByPositionType(RetrainConfigRequest request) {
        if (request == null || request.getPositionType() == null) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        List<RetrainConfig> configList = this.retrainConfigService.findListByPositionType(request.getPositionType());
        RetrainConfigDetailVO vo = new RetrainConfigDetailVO();
        if (CollectionUtils.isEmpty(configList)) {
            return R.result(ResponseCodeEnum.SUCCESS, vo);
        }
        List<RetrainConfigDetailVO.CityInfo> cityInfoList = new ArrayList<>();
        for (RetrainConfig config : configList) {
            RetrainConfigDetailVO.CityInfo cityInfo = new RetrainConfigDetailVO.CityInfo();
            cityInfo.setCityCode(config.getCityCode());
            cityInfo.setCityName(config.getCityName());
            cityInfoList.add(cityInfo);
        }
        vo.setCityInfoList(cityInfoList);
        vo.setPositionType(request.getPositionType());
        vo.setStatus(configList.get(0).getStatus());
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    @Override
    public BaseResponse<List<String>> getInvalidConfigCityCodeByPositionType(Integer positionType) {
        List<String> cityCodes = this.retrainConfigService.getInvalidConfigCityByPositionType(positionType);
        return R.result(ResponseCodeEnum.SUCCESS, cityCodes);
    }

    private void saveConfig(RetrainConfigRequest request) {
        List<RetrainConfigRequest.CityInfo> cityInfoList = request.getCityInfoList();
        Date now = new Date();
        List<RetrainConfig> addConfigList = new ArrayList<>();
        for (RetrainConfigRequest.CityInfo cityInfo : cityInfoList) {
            RetrainConfig config = new RetrainConfig();
            config.setPositionType(request.getPositionType());
            config.setCityCode(cityInfo.getCityCode());
            config.setCityName(cityInfo.getCityName());
            config.setStatus(request.getStatus());
            config.setCreateTime(now);
            config.setCreateBy(request.getOperator());
            addConfigList.add(config);
        }
        this.retrainConfigService.insertBatch(addConfigList);
    }

    BaseResponse checkParam(RetrainConfigRequest request) {
        if (request == null || StringUtils.isBlank(request.getOperator()) || request.getStatus() == null || request.getPositionType() == null ||
                CollectionUtils.isEmpty(request.getCityInfoList())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        CommonStatusEnum statusEnum = CommonStatusEnum.getEnumByValue(request.getStatus());
        PositionTypeEnum typeEnum = PositionTypeEnum.get(request.getPositionType());
        if (statusEnum == null || typeEnum == null) {
            return R.result(ResponseCodeEnum.ERROR_PARAM);
        }
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    private void deleteAndRefreshByPosition(Integer positionType) {
        String key = Constants.RETRAIN_POSITION_CITY + positionType;
        if (redisUtil.get(key) != null) {
            this.redisUtil.del(key);
        }
        this.getInvalidConfigCityCodeByPositionType(positionType);
    }
}
