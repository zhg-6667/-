package com.hk.simba.license.service.manager;

import com.hk.simba.base.common.dto.response.BaseResponse;
import com.hk.simba.license.service.constant.Constants;
import com.hk.sisyphus.light.client.account.UserApi;
import com.hk.sisyphus.light.client.account.param.FindUserByThirdUserIdRequest;
import com.hk.sisyphus.light.client.account.param.UserBasicInfoCO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class UserApiManager {
    @Value("${user-pool.employee-pool-id}")
    private Long employeePoolId;

    //根据第三方userId获取内部用户信息，默认职员池
    public UserBasicInfoCO findUserByThirdUserId(String userId, Long pooId) {
        if (ObjectUtils.isEmpty(pooId)) {
            pooId = employeePoolId;
        }
        UserApi userApi = new UserApi();
        FindUserByThirdUserIdRequest request = new FindUserByThirdUserIdRequest();
        request.setThirdUserId(userId);
        request.setPoolId(pooId);
        BaseResponse<UserBasicInfoCO> userBasicInfoCOBaseResponse = userApi.findUserByThirdUserId(request);
        if (userBasicInfoCOBaseResponse.isSuccess() && !ObjectUtils.isEmpty(userBasicInfoCOBaseResponse.getData())) {
            return userBasicInfoCOBaseResponse.getData();
        }
        return null;
    }

    //根据第三方userId获取操作人信息
    public String findOperatorThirdUserId(String userId) {
        UserBasicInfoCO user = this.findUserByThirdUserId(userId, 1569782929112956666L);
        if (ObjectUtils.isEmpty(user)) {
            return Constants.SYS;
        }
        return "M_" + user.getUserId() + "_" + user.getRealName();
    }
}
