package com.hk.simba.license.service.open;

import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.simba.license.service.constant.enums.LicenseStatusEnum;
import com.hk.simba.license.service.entity.License;
import com.hk.simba.license.service.entity.LicenseScoreLog;
import com.hk.simba.license.service.service.LicenseService;
import com.hk.simba.license.service.service.LicenseScoreLogService;
import com.hk.simba.license.api.request.license.LicenseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * LicenseOpenServiceImpl 的单元测试类
 * 只有激活执照的测试
 *
 * @author gzc
 * @date 2024/10/31
 * @description 激活执照的手动重置非空不在此做
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LicenseOpenServiceImplTest {

    @Resource
    /**
     * 注入 LicenseOpenServiceImpl 服务进行测试
     */
    private LicenseOpenServiceImpl licenseOpenService;

    @MockBean
    /**
     * Mock LicenseService 用于隔离单元测试
     */
    private LicenseService licenseService;

    @MockBean
    /**
     * Mock LicenseScoreLogService 用于隔离单元测试
     */
    private LicenseScoreLogService licenseScoreLogService;

    @BeforeEach
    /**
     * 初始化 Mock 对象
     */
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    /**
     * 测试 activeLicense 方法，当请求为 null 时，应返回错误的空参数
     */
    public void testActiveLicense_NullRequest_ReturnErrorNullParam() {
        BaseResponse response = licenseOpenService.activeLicense(null);
        assertEquals(ResponseCodeEnum.ERROR_NULL_PARAM.getCode(), response.getCode());
    }

    @Test
    /**
     * 测试 activeLicense 方法，当 licenseId 为 null 时，应返回错误的空参数
     */
    public void testActiveLicense_NullLicenseId_ReturnErrorNullParam() {
        LicenseRequest request = new LicenseRequest();
        request.setLicenseId(null);
        BaseResponse response = licenseOpenService.activeLicense(request);
        assertEquals(ResponseCodeEnum.ERROR_NULL_PARAM.getCode(), response.getCode());
    }

    @Test
    /**
     * 测试 activeLicense 方法，当 operator 为 null 时，应返回错误的空参数
     */
    public void testActiveLicense_NullOperator_ReturnErrorNullParam() {
        LicenseRequest request = new LicenseRequest();
        request.setLicenseId(1L);
        request.setOperator(null);
        BaseResponse response = licenseOpenService.activeLicense(request);
        assertEquals(ResponseCodeEnum.ERROR_NULL_PARAM.getCode(), response.getCode());
    }

    @Test
    /**
     * 测试 activeLicense 方法，当 license 未找到时，应返回错误的无记录
     */
    public void testActiveLicense_LicenseNotFound_ReturnErrorNoneRecord() {
        LicenseRequest request = new LicenseRequest();
        request.setLicenseId(1L);
        request.setOperator("operator");
        when(licenseService.selectById(1L)).thenReturn(null);
        BaseResponse response = licenseOpenService.activeLicense(request);
        assertEquals(ResponseCodeEnum.ERROR_NONE_RECORD.getCode(), response.getCode());
    }

    @Test
    /**
     * 测试 activeLicense 方法，当 license 未被撤销时，应成功
     */
    public void testActiveLicense_LicenseNotRevoked_ReturnSuccess() {
        LicenseRequest request = new LicenseRequest();
        request.setLicenseId(1L);
        request.setOperator("operator");
        License license = new License();
        license.setStatus(LicenseStatusEnum.EFFECTIVE.getCode());
        when(licenseService.selectById(1L)).thenReturn(license);
        BaseResponse response = licenseOpenService.activeLicense(request);
        assertEquals(ResponseCodeEnum.SUCCESS.getCode(), response.getCode());
    }

    @Test
    /**
     * 测试 activeLicense 方法，当 license 已被撤销时，应成功
     */
    public void testActiveLicense_LicenseRevoked_Success() {
        LicenseRequest request = new LicenseRequest();
        request.setLicenseId(1L);
        request.setOperator("operator");
        request.setScore(10);
        Date effectiveTime = new Date();
        request.setEffectiveTime(effectiveTime);
        request.setSetScore(true);
        request.setReason("reason");

        License license = new License();
        license.setStatus(LicenseStatusEnum.REVOKE.getCode());
        license.setRemainScore(20);
        license.setExpireTime(new Date());

        // 模拟 selectById 方法
        when(licenseService.selectById(1L)).thenReturn(license);

        // 模拟 updateById 方法，假设它返回 boolean
        when(licenseService.updateById(license)).thenReturn(true);

        BaseResponse response = licenseOpenService.activeLicense(request);
        assertEquals(ResponseCodeEnum.SUCCESS.getCode(), response.getCode());

        // 验证 updateById 方法被调用
        verify(licenseService).updateById(license);
    }

    @Test
    /**
     * 测试 activeLicense 方法，重置 license 分数
     */
    public void testActiveLicense_ResetLicenseScore() {
        LicenseRequest request = new LicenseRequest();
        request.setLicenseId(1L);
        request.setOperator("operator");
        request.setSetScore(false);

        License license = new License();
        license.setStatus(LicenseStatusEnum.REVOKE.getCode());
        license.setRemainScore(20);
        license.setExpireTime(new Date());

        // 模拟 selectById 方法
        when(licenseService.selectById(1L)).thenReturn(license);

        // 模拟 updateById 方法，假设它返回 boolean
        when(licenseService.updateById(license)).thenReturn(true);

        BaseResponse response = licenseOpenService.activeLicense(request);
        assertEquals(ResponseCodeEnum.SUCCESS.getCode(), response.getCode());

        // 验证 updateById 方法被调用
        verify(licenseService).updateById(license);
    }
}
