package com.hk.simba.license.service.manager;

import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.base.common.error.CommonErrorCode;
import com.hk.simba.base.common.error.LicenseErrorCode;
import com.hk.simba.base.common.exception.BusinessException;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.FlashMpUserTypeEnum;
import com.hk.sisyphus.light.client.messagebox.MessageBoxTemplateSendApi;
import com.hk.sisyphus.light.client.messagebox.param.ParameterData;
import com.hk.sisyphus.light.client.messagebox.param.SendResponseData;
import com.hk.sisyphus.light.client.messagebox.param.TemplateSingleSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author chenm
 * @since 2022/5/26
 */
@Slf4j
@Service
public class MessageBoxManager {

    @Autowired
    private MessageBoxTemplateSendApi messageBoxTemplateSendApi;

    public SendResponseData singleSend(Long templateId, Long userId, Map<String, String> params) {
        TemplateSingleSendRequest request = new TemplateSingleSendRequest();
        request.setTemplateId(templateId);
        request.setBusinessId(userId.toString());
        request.setTenant(FlashMpUserTypeEnum.STAFF.getSymbol());
        request.setUserId(userId);
        request.setBusiness(MessageConstant.APP_ID);
        request.setParameters(getParameters(params));
        if (log.isInfoEnabled()) {
            log.info("MessageBoxManager.singleSend,request:{}", request);
        }
        BaseResponse<SendResponseData> response;
        try {
            response = messageBoxTemplateSendApi.templateSingleSend(request);
            if (response.isSuccess()) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("MessageBoxManager.singleSend.error,request:{}", request, e);
            throw new BusinessException(LicenseErrorCode.genCommonErrorCode(CommonErrorCode.EXTERNAL_ERROR));
        }
        throw new BusinessException(response.getErrorCode());
    }

    private List<ParameterData> getParameters(Map<String, String> params) {
        return params.entrySet().stream().map(e -> {
            ParameterData data = new ParameterData();
            data.setKey(e.getKey());
            data.setValue(e.getValue());
            return data;
        }).collect(Collectors.toList());
    }
}
