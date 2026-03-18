package com.hk.simba.license.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hk.simba.license.api.enums.PayRecordStatusEnum;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.request.violation.ViolationQueryRequest;
import com.hk.simba.license.api.vo.ViolationVO;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.mapper.ViolationMapper;
import com.hk.simba.license.service.service.MailService;
import com.hk.simba.license.service.service.ViolationService;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 违规信息 服务实现类
 * </p>
 *
 * @author lancw
 * @since 2020-04-09
 */
@Slf4j
@Service
public class ViolationServiceImpl extends ServiceImpl<ViolationMapper, Violation> implements ViolationService {
    @Autowired
    private ViolationMapper violationMapper;

    @Value("${quality.email}")
    private String qualityEmail;

    @Autowired
    private MailService mailService;

    @Override
    public List<ViolationVO> queryPage(Page<Violation> page, ViolationQueryRequest query) {
        return violationMapper.pageViolationList(page, query);
    }

    @Override
    public List<Violation> findListByCodeAndStaffId(String code, Long staffId) {
        EntityWrapper<Violation> violationWrapper = new EntityWrapper();
        violationWrapper.eq("code", code).eq("staff_id", staffId).orderBy("create_time", false);
        return this.selectList(violationWrapper);
    }

    @Override
    public List<Violation> pageTimoutPayList(Page<Violation> page, int timeoutPay) {
        return violationMapper.pageTimoutPayList(page, timeoutPay);
    }

    @Override
    public Page<Violation> findValidAndNoPayViolation(int pageNo, int pageSize) {
        Page<Violation> page = new Page<>(pageNo, pageSize);
        Violation violation = new Violation();
        violation.setStatus(StatusEnum.VALID.getValue());
        violation.setPayStatus(PayRecordStatusEnum.NO_PAY.getValue());
        Wrapper<Violation> wrapper = new EntityWrapper<>(violation);
        page = this.selectPage(page, wrapper);
        return page;
    }

    @Override
    public List<Violation> findListByCodeAndStatus(String code, Integer status) {
        EntityWrapper<Violation> violationWrapper = new EntityWrapper();
        violationWrapper.eq("code", code).eq("status", status).orderBy("create_time", false);
        return this.selectList(violationWrapper);
    }

    @Override
    public List<Violation> findListByCode(String code) {
        EntityWrapper<Violation> violationWrapper = new EntityWrapper();
        violationWrapper.eq("code", code).orderBy("create_time", false);
        return this.selectList(violationWrapper);
    }

    @Override
    public List<String> findAllDepartment() {
        return this.baseMapper.findAllDepartment();
    }

    @Override
    public List<Violation> pageByCreateTimeAndDeDuctType(Page<Violation> page, Date startTime, Date endTime, Integer deDuctType) {
        return this.baseMapper.pageByCreateTimeAndDeDuctType(page, startTime, endTime, deDuctType);
    }

    @Override
    public List<Violation> findListByCondition(ViolationQueryRequest request) {
        return this.baseMapper.findListByCondition(request);
    }

    @Override
    public void sendStaffForbiddenEmail(Violation violation, String responsibilityDeptName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("code", violation.getCode());
        params.put("type", violation.getType());
        params.put("detail", violation.getDetail());
        params.put("department", responsibilityDeptName);
        try {
            mailService.sendTemplateMail(qualityEmail, MessageConstant.STAFF_FORBIDDEN_SUBJECT, MessageConstant.STAFF_FORBIDDEN_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[服务禁忌违规审批提醒]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[服务禁忌违规审批提醒]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[服务禁忌违规审批提醒]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[服务禁忌违规审批提醒]邮件发送失败:{}", e.getMessage(), e);
        }
    }
}
