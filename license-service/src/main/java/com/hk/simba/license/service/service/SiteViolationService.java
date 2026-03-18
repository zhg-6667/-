package com.hk.simba.license.service.service;

import com.hk.simba.license.api.vo.SiteAndRegionVO;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.entity.ViolationType;
import com.hk.simba.license.service.mq.dto.MessageEntity;

/**
 * <p>
 * 站点违规服务类型
 * </p>
 *
 * @author cyh
 * @since 2021-07-17
 */
public interface SiteViolationService {

    /**
     * 创建站点违规信息
     *
     * @param siteId
     * @param vt
     * @param entity
     * @return
     */
    Violation createSiteViolationInfo(MessageEntity entity, Long siteId, ViolationType vt);

    /**
     * 创建站点违规站长信息
     *
     * @param vo
     * @return
     */
    Violation createSiteLeaderInfo(Violation vo);

    /**
     * 获取站长邮箱和电话信息
     *
     * @param siteLeaderId
     * @return
     */
    SiteAndRegionVO getSiteLeaderInfo(Long siteLeaderId);

    /**
     * 失效并变更原先站点违规失效的类型
     *
     * @param code
     * @param newType
     * @return
     */
    void invalidAndChangeInvalidType(String code, ViolationType newType);

    /**
     * 给质质高-发送站点服务禁忌审批提醒邮件
     *
     * @param violation
     * @return
     */
    void sendSiteForbiddenEmail(Violation violation);

    /**
     * 构建站点违规复训
     *
     * @param violation
     * @return
     */
    void createSiteRetrain(Violation violation);

    /**
     * 是否符合站点违规规则
     *
     * @param violation
     * @return
     */
    Boolean isSiteRetrainRule(Violation violation);

    /**
     * 根据站点id获取站点培训师信息
     *
     * @param siteId
     * @return
     */
    SiteAndRegionVO getTrainTeacherInfo(Long siteId);

}
