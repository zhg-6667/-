package com.hk.simba.license.service.config;

import com.hk.sisyphus.api.core.flash.MessageApi;
import com.hk.sisyphus.base.ApiClient;
import com.hk.sisyphus.light.client.messagebox.MessageBoxTemplateSendApi;
import com.hk.sisyphus.merope.core.contract.ContractItemApi;
import com.hk.sisyphus.merope.core.mpworkorder.ServiceWorkOrderApi;
import com.hk.sisyphus.merope.core.quarkmdds.SiteApi;
import com.hk.sisyphus.merope.core.sas.UserApi;
import com.hk.sisyphus.merope.core.staff.StaffApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cyh
 * @date 2020/10/20/19:15
 * 中台开放接口配置
 */
@Configuration
public class SisyphusApiConfig {

    @Bean
    public StaffApi staffApi(ApiClient apiClient) {
        return new StaffApi(apiClient);
    }

    @Bean
    public SiteApi siteApi(ApiClient apiClient) {
        return new SiteApi(apiClient);
    }

    @Bean
    public UserApi userApi(ApiClient apiClient) {
        return new UserApi(apiClient);
    }

    @Bean
    public ServiceWorkOrderApi serviceWorkOrderApi(ApiClient apiClient) {
        return new ServiceWorkOrderApi(apiClient);
    }

    @Bean
    public ContractItemApi contractItemApi(ApiClient apiClient) {
        return new ContractItemApi(apiClient);
    }

    @Bean
    public MessageApi messageApi(ApiClient apiClient) {
        return new MessageApi(apiClient);
    }

    @Bean
    public MessageBoxTemplateSendApi messageBoxTemplateSendApi(ApiClient apiClient) {
        return new MessageBoxTemplateSendApi(apiClient);
    }
}
