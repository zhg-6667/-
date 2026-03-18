package com.hk.simba.license.api;

import com.hk.simba.license.api.request.BaseRequest;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.license.LicenseInfoRequest;
import com.hk.simba.license.api.request.license.LicenseQueryRequest;
import com.hk.simba.license.api.request.license.LicenseRequest;
import com.hk.simba.license.api.request.license.LicenseRevokeRequest;
import com.hk.simba.license.api.vo.LicenseInfoVO;
import com.hk.simba.license.api.vo.LicenseVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.quark.base.dto.response.BaseResponse;

import java.util.List;

/**
 * @author cyh
 * @date 2020/4/26/14:29
 * 执照接口
 */
public interface LicenseOpenService {

    /**
     * 查询执照列表(后台分页)
     *
     * @param request
     * @param page
     * @return
     */
    BaseResponse<PageResult<LicenseVO>> list(PageRequest page, LicenseQueryRequest request);


    /**
     * 吊销执照(后台操作)
     *
     * @param request
     * @return
     */
    BaseResponse revoke(LicenseRevokeRequest request);


    /**
     * 执照吊销详情
     *
     * @param id
     * @return
     */
    BaseResponse revokeInfo(Long id);


    /**
     * 根据员工身份证号获取执照详情(app端)
     *
     * @param idCard
     * @return
     */
    BaseResponse findLicenseByIdCard(String idCard);

    /**
     * 生成执照
     *
     * @param request
     * @return
     */
    BaseResponse createLicense(BaseRequest<LicenseVO> request);


    /**
     * 更新执照
     *
     * @param licenseVO
     * @return
     */
    BaseResponse updateLicense(LicenseVO licenseVO);


    /**
     * 判断执照的有效期
     *
     * @param id
     * @return
     */
    BaseResponse judgeLicenseValidity(Long id);


    /**
     * 通过文件创建执照
     *
     * @param content
     * @param operator
     * @return
     */
    BaseResponse createLicenseByContent(String content, String operator);


    /**
     * 通过员工id查询执照
     *
     * @param staffId
     * @return
     */
    BaseResponse<LicenseInfoVO> findLicenseByStaffId(Long staffId);

    /**
     * 查询执照信息列表-根据员工id列表或者站点列表
     *
     * @param request
     * @return
     */
    BaseResponse<List<LicenseInfoVO>> findLicenseList(LicenseInfoRequest request);


    /**
     * 根据员工身份证号和员工id获取执照详情(app端)
     *
     * @param idCard
     * @param staffId
     * @return
     */
    BaseResponse findLicenseByIdCardAndStaffId(String idCard, Long staffId);


    /**
     * 更新执照状态
     *
     * @param request
     * @return
     */
    BaseResponse activeLicense(LicenseRequest request);


}
