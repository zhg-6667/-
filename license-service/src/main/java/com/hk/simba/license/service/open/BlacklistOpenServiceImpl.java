package com.hk.simba.license.service.open;

import com.baomidou.mybatisplus.plugins.Page;
import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.quark.base.util.ReplaceUtils;
import com.hk.simba.license.api.BlacklistOpenService;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.black.BlackQueryRequest;
import com.hk.simba.license.api.request.black.CreateBlackRequest;
import com.hk.simba.license.api.request.black.RemoveBlackRequest;
import com.hk.simba.license.api.vo.BlacklistVO;
import com.hk.simba.license.api.vo.LicenseInfoVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.simba.license.service.constant.enums.BlacklistTypeEnum;
import com.hk.simba.license.service.constant.enums.IsBlacklistEnum;
import com.hk.simba.license.service.constant.enums.LicenseStatusEnum;
import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.simba.license.service.entity.Blacklist;
import com.hk.simba.license.service.entity.BlacklistLog;
import com.hk.simba.license.service.entity.License;
import com.hk.simba.license.service.entity.LicenseRevokeLog;
import com.hk.simba.license.service.service.BlacklistLogService;
import com.hk.simba.license.service.service.BlacklistService;
import com.hk.simba.license.service.service.LicenseRevokeLogService;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.simba.license.service.utils.BeanCopyUtil;
import com.hk.simba.license.service.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cyh
 * @date 2020/10/17/11:31
 * 黑名单open服务接口
 */
@Slf4j
@DubboService
public class BlacklistOpenServiceImpl implements BlacklistOpenService {

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private BlacklistLogService blacklistLogService;

    @Autowired
    private LicenseRevokeLogService licenseRevokeLogService;


    @Override
    public BaseResponse page(PageRequest page, BlackQueryRequest request) {
        Page<Blacklist> blacklistPage = new Page<>(page.getPageNo(), page.getPageSize());
        List<BlacklistVO> voList = blacklistService.getPageList(blacklistPage, request);
        if (CollectionUtils.isEmpty(voList)) {
            return R.result(ResponseCodeEnum.SUCCESS, new PageResult<>());
        }
        for (BlacklistVO vo : voList) {
            vo.setPhone(ReplaceUtils.replacePhone(vo.getPhone()));
            vo.setIdCard(ReplaceUtils.replaceIdCard(vo.getIdCard()));
        }
        PageResult<BlacklistVO> pageResult = new PageResult<>();
        pageResult.setResults(voList);
        pageResult.setCount(blacklistPage.getTotal());
        pageResult.setPageNo(blacklistPage.getCurrent());
        pageResult.setPageSize(blacklistPage.getSize());
        pageResult.setTotalPage(blacklistPage.getPages());
        return R.result(ResponseCodeEnum.SUCCESS, pageResult);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse removeBlack(RemoveBlackRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()) || StringUtils.isBlank(request.getOperator())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        List<Blacklist> list = this.blacklistService.selectBatchIds(request.getIds());
        if (CollectionUtils.isEmpty(list)) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        Date now = new Date();
        List<BlacklistLog> logList = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        list.forEach(black -> {
            ids.add(black.getId());
            BlacklistLog log = new BlacklistLog();
            log.setStaffId(black.getStaffId());
            log.setCreateBy(request.getOperator());
            log.setCreateTime(now);
            if (StringUtils.isNotBlank(request.getRemark())) {
                log.setRemark(request.getRemark());
            }
            log.setType(black.getType());
            log.setOperateType("删除");
            logList.add(log);
        });
        this.blacklistService.deleteBatchIds(ids);
        this.blacklistLogService.insertBatch(logList);
        List<Long> staffIds = list.stream().map(Blacklist::getStaffId).collect(Collectors.toList());
        List<License> licenseList = this.licenseService.findListByStaffIds(staffIds);
        List<License> tempList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(licenseList)) {
            licenseList.forEach(license -> {
                if (license.getBlack() != null && license.getBlack().equals(IsBlacklistEnum.YES.getValue())) {
                    license.setBlack(IsBlacklistEnum.NO.getValue());
                    license.setModifyBy(request.getOperator());
                    license.setModifyTime(now);
                    tempList.add(license);
                }
            });
        }
        if (!CollectionUtils.isEmpty(tempList)) {
            this.licenseService.updateBatchById(tempList);
        }
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse createBlack(CreateBlackRequest request) {
        if (StringUtils.isBlank(request.getOperator()) || request.getType() == null) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        //员工id和员工id集不能同时为空
        if (request.getStaffId() == null && CollectionUtils.isEmpty(request.getStaffIds())) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        if (BlacklistTypeEnum.getEnumByValue(request.getType()) == null) {
            return R.result(ResponseCodeEnum.ERROR_BLACK_TYPE);
        }
        //只传一个员工id
        if (request.getStaffId() != null && CollectionUtils.isEmpty(request.getStaffIds())) {
            return this.createBlackInfo(request);
        }
        List<Long> ids = request.getStaffIds();
        if (!CollectionUtils.isEmpty(ids)) {
            for (Long id : ids) {
                request.setStaffId(id);
                this.createBlackInfo(request);
            }
        }
        return R.result(ResponseCodeEnum.SUCCESS);
    }


    private BaseResponse createBlackInfo(CreateBlackRequest request) {
        //填充身份证信息
        LicenseInfoVO vo = this.licenseService.findLicenseByStaffId(request.getStaffId());
        if (null == vo) {
            return R.result(ResponseCodeEnum.LICENSE_NO_EXIST);
        }
        License license = new License();
        license.setId(vo.getId());
        if (vo.getStatus().equals(LicenseStatusEnum.EFFECTIVE.getCode())) {
            license.setStatus(LicenseStatusEnum.REVOKE.getCode());
            //生成吊销记录
            LicenseRevokeLog licenseRevokeLog = new LicenseRevokeLog();
            licenseRevokeLog.setLicenseId(license.getId());
            licenseRevokeLog.setStaffId(license.getStaffId());
            licenseRevokeLog.setCreateTime(new Date());
            licenseRevokeLog.setCreateBy(request.getOperator());
            licenseRevokeLog.setReason(BlacklistTypeEnum.getEnumByValue(request.getType()).getText());
            licenseRevokeLogService.insert(licenseRevokeLog);
        }
        license.setReason(BlacklistTypeEnum.getEnumByValue(request.getType()).getText());
        license.setModifyBy(request.getOperator());
        license.setBlack(IsBlacklistEnum.YES.getValue());
        this.licenseService.updateById(license);

        //创建黑名单
        Blacklist blacklist = new Blacklist();
        blacklist.setStaffId(request.getStaffId());
        blacklist.setName(vo.getName());
        blacklist.setGender(vo.getGender());
        blacklist.setPhone(vo.getPhone());
        blacklist.setCreateTime(new Date());
        blacklist.setCreateBy(request.getOperator());
        blacklist.setIdCard(vo.getIdCard());
        blacklist.setType(request.getType());
        blacklist.setReason(request.getBlackListReason());
        blacklist.setRemark(request.getRemark());
        this.blacklistService.createBlack(blacklist);
        return R.result(ResponseCodeEnum.SUCCESS);
    }

    @Override
    public BaseResponse findByIdCard(String idCard) {
        Blacklist blacklist = this.blacklistService.findByIdCard(idCard);
        if (blacklist == null) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        //填充黑名单信息信息
        LicenseInfoVO vo = this.licenseService.findLicenseByStaffId(blacklist.getStaffId());
        if (null == vo) {
            return R.result(ResponseCodeEnum.HAS_NO_LICENSE);
        }
        BlacklistVO blacklistVO = new BlacklistVO();
        BeanCopyUtil.copyPropertiesIgnoreNull(blacklist, blacklistVO);
        blacklistVO.setName(vo.getName());
        blacklistVO.setPhone(vo.getPhone());
        return R.result(ResponseCodeEnum.SUCCESS, blacklistVO);
    }

    @Override
    public BaseResponse findByStaffId(Long staffId) {
        Blacklist blacklist = this.blacklistService.findByStaffId(staffId);
        if (blacklist == null) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        //填充黑名单信息信息
        LicenseInfoVO vo = this.licenseService.findLicenseByStaffId(blacklist.getStaffId());
        if (null == vo) {
            return R.result(ResponseCodeEnum.HAS_NO_LICENSE);
        }
        BlacklistVO blacklistVO = new BlacklistVO();
        BeanCopyUtil.copyPropertiesIgnoreNull(blacklist, blacklistVO);
        blacklistVO.setName(vo.getName());
        blacklistVO.setPhone(vo.getPhone());
        return R.result(ResponseCodeEnum.SUCCESS, blacklistVO);
    }
}
