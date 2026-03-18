package com.hk.simba.license.service.open;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Maps;
import com.hk.base.util.DateUtils;
import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.quark.base.util.ReplaceUtils;
import com.hk.simba.license.api.RetrainOpenService;
import com.hk.simba.license.api.request.PageRequest;
import com.hk.simba.license.api.request.retrain.RetrainQueryRequest;
import com.hk.simba.license.api.request.retrain.RetrainRequest;
import com.hk.simba.license.api.vo.EventAttachment;
import com.hk.simba.license.api.vo.RetrainVO;
import com.hk.simba.license.api.vo.comm.PageResult;
import com.hk.simba.license.service.constant.MessageConstant;
import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.simba.license.service.constant.enums.RetrainStatusEnum;
import com.hk.simba.license.service.entity.Retrain;
import com.hk.simba.license.service.service.MailService;
import com.hk.simba.license.service.service.RetrainService;
import com.hk.simba.license.service.utils.BeanCopyUtil;
import com.hk.simba.license.service.utils.R;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author cyh
 * @date 2020/8/3/17:03
 */
@Slf4j
@DubboService
public class RetrainOpenServiceImpl implements RetrainOpenService {

    @Autowired
    private RetrainService retrainService;


    @Autowired
    private MailService mailService;


    @Value("${quality.email}")
    private String qualityEmail;

    @Value("${retrain.time.out.day}")
    private int retrainTimeOutDay;

    @Override
    public BaseResponse<PageResult<RetrainVO>> list(PageRequest page, RetrainQueryRequest request) {
        Page<Retrain> retrainPage = new Page<>(page.getPageNo(), page.getPageSize());
        List<RetrainVO> voList = this.retrainService.getPageList(retrainPage, request);
        if (CollectionUtils.isEmpty(voList)) {
            return R.result(ResponseCodeEnum.SUCCESS, new PageResult<>());
        }

        for (RetrainVO vo : voList) {
            vo.setPhone(ReplaceUtils.replacePhone(vo.getPhone()));
            vo.setIdCard(ReplaceUtils.replaceIdCard(vo.getIdCard()));
            this.countRetrainTimeOut(vo);
        }
        PageResult<RetrainVO> pageResult = new PageResult<>();
        pageResult.setResults(voList);
        pageResult.setCount(retrainPage.getTotal());
        pageResult.setPageNo(retrainPage.getCurrent());
        pageResult.setPageSize(retrainPage.getSize());
        pageResult.setTotalPage(retrainPage.getPages());
        return R.result(ResponseCodeEnum.SUCCESS, pageResult);
    }

    @Override
    public BaseResponse findRetrainById(Long id) {
        if (null == id) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        Retrain retrain = this.retrainService.selectById(id);
        if (null == retrain) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        RetrainVO vo = new RetrainVO();
        BeanCopyUtil.copyProperties(retrain, vo);
        this.countRetrainTimeOut(vo);
        return R.result(ResponseCodeEnum.SUCCESS, vo);
    }

    @Override
    public BaseResponse updateRetrain(RetrainRequest request) {
        if (null == request.getId() || StringUtils.isBlank(request.getOperator()) || request.getStatus() == null) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        RetrainStatusEnum statusEnum = RetrainStatusEnum.getEnumByValue(request.getStatus());
        if (statusEnum == null) {
            return R.result(ResponseCodeEnum.ERROR_PARAM);
        }
        Retrain retrain = this.retrainService.selectById(request.getId());
        if (null == retrain) {
            return R.result(ResponseCodeEnum.ERROR_NONE_RECORD);
        }
        Integer oldStatus = retrain.getStatus();
        BeanCopyUtil.copyProperties(request, retrain);
        retrain.setModifyBy(request.getOperator());
        retrain.setModifyTime(new Date());
        retrain.setAnnex(this.saveAnnex(request.getImageAnnex(), request.getOtherAnnex()));
        //待复训，记录更新时间(只记录第一次变更时间)
        if (!oldStatus.equals(request.getStatus()) && oldStatus.equals(RetrainStatusEnum.WAIT_RETRAIN.getValue()) && retrain.getStatusChangeTime() == null) {
            retrain.setStatusChangeTime(new Date());
        }
        this.retrainService.updateById(retrain);
        if (!oldStatus.equals(RetrainStatusEnum.FAIL.getValue())
                && request.getStatus().equals(RetrainStatusEnum.FAIL.getValue())) {
            this.sendRetrainEmailToQuality(retrain.getName(), retrain.getStaffId());
        }
        return R.result(ResponseCodeEnum.SUCCESS);
    }


    /**
     * 存储附件信息
     *
     * @param imageAnnex 图片附件
     * @param otherAnnex 其他附件
     */
    private String saveAnnex(List<EventAttachment> imageAnnex, List<EventAttachment> otherAnnex) {

        List<EventAttachment> attachmentList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(imageAnnex)) {
            for (EventAttachment other : imageAnnex) {
                attachmentList.add(other);
            }
        }
        if (!CollectionUtils.isEmpty(otherAnnex)) {
            for (EventAttachment other : otherAnnex) {
                attachmentList.add(other);
            }
        }
        if (!CollectionUtils.isEmpty(attachmentList)) {
            return JSON.toJSONString(attachmentList);
        }
        return null;
    }

    /**
     * 发送[复训未通过]邮件至质质高
     */
    private void sendRetrainEmailToQuality(String staffName, Long staffId) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("staffName", staffName);
        params.put("staffId", staffId);
        try {
            mailService.sendTemplateMail(qualityEmail, MessageConstant.RETRAIN_RESULT_SUBJECT, MessageConstant.RETRAIN_RESULT_TEMPLATE, params);
        } catch (TemplateException e) {
            log.error("[复训结果提醒]模板内容解析失败:{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("[复训结果提醒]模板加载失败:{}", e.getMessage(), e);
        } catch (MessagingException e) {
            log.error("[复训结果提醒]邮件信息发送失败:{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("[复训结果提醒]邮件发送失败:{}", e.getMessage(), e);
        }

    }

    /**
     * 计算复训超时时间
     */
    private void countRetrainTimeOut(RetrainVO vo) {
        if (vo.getStatus().equals(RetrainStatusEnum.WAIT_RETRAIN.getValue())) {
            int day = DateUtils.daysBetween(vo.getCreateTime(), new Date());
            if (day > retrainTimeOutDay) {
                vo.setRetrainTimeOut(day - retrainTimeOutDay);
            }
        } else {
            if (vo.getStatusChangeTime() != null) {
                int day = DateUtils.daysBetween(vo.getCreateTime(), vo.getStatusChangeTime());
                if (day > retrainTimeOutDay) {
                    vo.setRetrainTimeOut(day - retrainTimeOutDay);
                }
            }
        }
    }
}
