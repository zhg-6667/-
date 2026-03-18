package com.hk.simba.license.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.hk.simba.license.api.request.license.LicenseInfoRequest;
import com.hk.simba.license.api.request.license.LicenseQueryRequest;
import com.hk.simba.license.api.vo.LicenseInfoVO;
import com.hk.simba.license.api.vo.LicenseVO;
import com.hk.simba.license.service.entity.License;
import com.hk.simba.license.service.entity.Violation;

import java.util.List;

/**
 * @author cyh
 * @date 2020/4/26/14:16
 * 执照服务类
 */
public interface LicenseService extends IService<License> {

    /***
     * 按条件查询执照分页信息(后台)
     * @param page
     * @param request
     * @return
     */
    List<LicenseVO> getPageList(Page<License> page, LicenseQueryRequest request);

    /***
     * 违规处理
     * @param violation
     * @return
     */
    License doViolation(Violation violation);

    /***
     * 到员工变更状态
     * @param license
     * @return
     */
    License staffStatusChange(License license, String status);

    /***
     * 到员工系统更新执照信息
     * @param license
     * @return
     */
    License updateLicenseInfo(License license);

    /***
     * 申诉通过处理
     * @param violation
     * @return
     */
    void appealPass(Violation violation);

    /***
     * 回滚执照分数
     * @param violation
     * @param reason
     * @return
     */
    void rollbackScore(Violation violation, String reason);

    /***
     * 获取站长邮箱
     * @param siteLeaderId
     * @return
     */
    String getSiteLeaderEmail(Long siteLeaderId);

    /**
     * 通过员工id查询执照
     *
     * @param staffId
     * @return
     */
    LicenseInfoVO findLicenseByStaffId(Long staffId);


    /**
     * 查询执照信息列表-根据员工id列表或者站点列表
     *
     * @param request
     * @return
     */
    List<LicenseInfoVO> findLicenseList(LicenseInfoRequest request);


    /**
     * 通过员工id集查找执照信息
     *
     * @param staffIds
     * @return
     */
    List<License> findListByStaffIds(List<Long> staffIds);

    /**
     * 员工异动时，变更执照信息
     *
     * @param licenseVO
     * @return
     */
    void updateLicenseInfoWhenStaffMove(LicenseVO licenseVO);


    /***
     * 获取站长手机
     * @param siteLeaderId
     * @return
     */
    String getSiteLeaderPhone(Long siteLeaderId);

    /***
     * 通过身份证，激活执照
     * @param idCard
     * @return
     */
    void activateLicenseByIdCard(String idCard);

    /***
     * 发送复训通知邮件
     * @param staffName
     * @param email
     * @param retrainType
     * @return
     */
    void sendRetrainNotifyEmail(String staffName, String email, String retrainType);

    /***
     *  给站点培训师发-复训邮件
     */
    void sendRetrainNotifyEmailToTrainTeacher(String staffName, String siteName, String retrainType, Long siteId);

    /**
     * 执照吊销-执行停单
     *
     * @param staffId
     * @param licenseInfoVO
     * @return void
     */
    void applyStopOrder(Long staffId, LicenseInfoVO licenseInfoVO);

    /**
     * 执照激活-取消停单
     *
     * @param licenseInfoVO
     * @return void
     */
    void endStopOrder(LicenseInfoVO licenseInfoVO);
}


