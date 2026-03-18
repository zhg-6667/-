package com.hk.simba.license.service.open;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hk.base.util.DateUtils;
import com.hk.quark.base.dto.response.BaseResponse;
import com.hk.simba.license.api.ViolationPayRecordOpenService;
import com.hk.simba.license.api.enums.PayRecordStatusEnum;
import com.hk.simba.license.api.enums.StatusEnum;
import com.hk.simba.license.api.enums.ViolationPayStatusEnum;
import com.hk.simba.license.api.request.BaseRequest;
import com.hk.simba.license.api.vo.ViolationPayRecordVO;
import com.hk.simba.license.service.constant.enums.ResponseCodeEnum;
import com.hk.simba.license.service.entity.Violation;
import com.hk.simba.license.service.entity.ViolationPayRecord;
import com.hk.simba.license.service.service.ViolationPayRecordService;
import com.hk.simba.license.service.service.ViolationService;
import com.hk.simba.license.service.utils.CodeUtil;
import com.hk.simba.license.service.utils.R;
import com.hk.simba.license.service.utils.YearUtil;
import com.hk.tbs.api.PayService;
import com.hk.tbs.dto.constant.OrderItemType;
import com.hk.tbs.dto.constant.TradeOrderType;
import com.hk.tbs.dto.constant.TradeType;
import com.hk.tbs.dto.param.pay.ChargeRequest;
import com.hk.tbs.dto.param.pay.ChargeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description @author 羊皮
 * @since 2020-4-10 10:09:36
 */
@Slf4j
@DubboService
public class ViolationPayRecordOpenServiceImpl implements ViolationPayRecordOpenService {

    @Reference(version = "1.0")
    public PayService payService;
    @Autowired
    private ViolationService violationService;
    @Autowired
    private ViolationPayRecordService violationPayRecordService;
    /**
     * 支付单超时时间（分钟）
     */
    @Value("${violation.pay.expire.minute:1440}")
    private int expireMinute;
    /**
     * 支付平台 3-移动网站
     */
    private static final Integer PLATFORM = 3;
    private static final String REMARK = "违规缴款:%s";
    /**
     * 支付图标
     */
    @Value("${tbs.pay.icon.url}")
    private String iconUrl;
    /**
     * 超时未缴
     */
    @Value("${violation.timeout.pay}")
    private int timeoutPay;

    @Override
    @Transactional
    public BaseResponse createPayRecord(BaseRequest<ViolationPayRecordVO> request) {
        log.debug("[违规罚款交易创建:{}]", JSON.toJSONString(request));
        ViolationPayRecordVO vo = request.getData();
        if (null == vo || StringUtils.isBlank(vo.getCreateBy()) || null == vo.getViolationId()) {
            return R.result(ResponseCodeEnum.ERROR_NULL_PARAM);
        }
        Violation v = violationService.selectById(vo.getViolationId());
        if (null == v || StatusEnum.INVALID.getValue() == v.getStatus() ||
                StatusEnum.HAND_INVALID.getValue() == v.getStatus() ||
                StatusEnum.APPEAL_INVALID.getValue() == v.getStatus() ||
                !PayRecordStatusEnum.NO_PAY.getValue().equals(v.getPayStatus())) {
            return R.result(ResponseCodeEnum.ERROR_DATA_NOT_EXISTS);
        }
        if (v.getPayStatus() == ViolationPayStatusEnum.TIMEOUT_PAY.getValue()) {
            return R.result(ResponseCodeEnum.ERROR_TIMEOUT_PAY);
        }
        if (DateUtils.addDays(v.getCreateTime(), timeoutPay).compareTo(YearUtil.getCurrentDayZeroTime()) < 0) {
            v.setPayStatus(ViolationPayStatusEnum.TIMEOUT_PAY.getValue());
            violationService.updateById(v);
            return R.result(ResponseCodeEnum.ERROR_TIMEOUT_PAY);
        }
        ViolationPayRecord ent = new ViolationPayRecord();
        ent.setUserId(v.getUserId());
        ent.setViolationId(v.getId());
        BeanUtils.copyProperties(request.getData(), ent);

        EntityWrapper<ViolationPayRecord> ew = new EntityWrapper<>();
        ew.eq("violation_id", v.getId());
        List<ViolationPayRecord> vprs = violationPayRecordService.selectList(ew);
        if (!CollectionUtils.isEmpty(vprs)) {
            Date now = DateUtils.getCurrentDateTime();
            ViolationPayRecord noPayRecord = null;
            for (ViolationPayRecord vpr : vprs) {
                if (vpr.getExpiretTime().before(now)) {
                    vpr.setStatus(PayRecordStatusEnum.OVERTIME.getValue());
                    violationPayRecordService.updateById(vpr);
                } else if (PayRecordStatusEnum.NO_PAY.getValue().equals(vpr.getStatus())) {
                    noPayRecord = vpr;
                }
            }
            if (null != noPayRecord) {
                return R.result(ResponseCodeEnum.SUCCESS, noPayRecord.getPayUrl());
            }
        }
        String tradeOrderCode = String.format("QS%s-%d-%s", v.getCode(), v.getStaffId(), CodeUtil.generateCode());
        ent.setTradeOrderCode(tradeOrderCode);

        Date now = new Date();
        Date expireDate = DateUtils.add(now, Calendar.MINUTE, expireMinute);
        ChargeRequest req = build(v, expireDate, tradeOrderCode);
        ent.setExpiretTime(req.getExpireTime());
        ChargeResponse cr = payService.charge(req);
        if (!cr.isSuccess()) {
            log.error("违规罚款交易创建失败{}", cr.toString());
            return R.result(ResponseCodeEnum.ERROR_CREATE_PAY);
        }
//      旧收银台：https://pay.hkdev.cn/cashier?tradeOrderType=19&tradeOrderCode=QS1443597-201909-202206211122440671&platform=3  ->  https://pay.hkdev.cn/cashier?tradeOrderType=19&tradeOrderCode=QS1443597-201909-202206211122440671&platform=3&license=true&iconUrl=https://images.homeking365.com/license.png&paySuccessUrl=https://activity.hkdev.cn/page/2020-fe-license.html?payStatus=0&failUrl=https://activity.hkdev.cn/page/2020-fe-license.html?payStatus=1
//      新收银台：https://pay.hkdev.cn/cashier/p/37b7a4d1a9eaCM2z -> https://pay.hkdev.cn/cashier/p/37b7a4d1a9eaCM2z?license=true&iconUrl=https://images.homeking365.com/license.png&paySuccessUrl=https://activity.hkdev.cn/page/2020-fe-license.html?payStatus=0&failUrl=https://activity.hkdev.cn/page/2020-fe-license.html?payStatus=1
        String url = cr.getTradeDto().getCashierUrl();
        String myIconUrl = new String(iconUrl);
        if (!url.contains("tradeOrderCode")) {
            myIconUrl = myIconUrl.replaceFirst("&", "?");
        }
        url = String.format("%s%s", url, myIconUrl);
        ent.setPayUrl(url);
        ent.setStatus(PayRecordStatusEnum.NO_PAY.getValue());
        ent.setTotalAmount(v.getTotalAmount());
        ent.setCreateTime(now);
        violationPayRecordService.insert(ent);
        v.setTradeOrderCode(tradeOrderCode);
        violationService.updateById(v);
        //cr.getTradeDto().getTrade().getId()
        return R.result(ResponseCodeEnum.SUCCESS, url);
    }

    public static ChargeRequest build(Violation v, Date expireDate, String tradeOrderCode) {
        ChargeRequest chargeRequest = new ChargeRequest();
        chargeRequest.setExpireTime(expireDate);
        chargeRequest.setUserId(v.getUserId());
        chargeRequest.setTradeOrderCode(tradeOrderCode);
        chargeRequest.setTotalAmount(v.getTotalAmount());
        chargeRequest.setTradeSummary(String.format(REMARK, v.getCode()));

        chargeRequest.setTradeType(TradeType.BILL);
        chargeRequest.setPlatform(PLATFORM);
        chargeRequest.setUseBalance(Boolean.FALSE);
        chargeRequest.setSpecifyCardNo(Boolean.FALSE);
        chargeRequest.setTradeOrderType(TradeOrderType.LICENSE_BILL);

        ChargeRequest.OrderItem orderItem = new ChargeRequest.OrderItem();
        orderItem.setCode(v.getCode());
        orderItem.setOrderCode(v.getCode());
        orderItem.setPrice(v.getTotalAmount());
        orderItem.setPayAmount(v.getTotalAmount());
        orderItem.setTotalAmount(v.getTotalAmount());
        orderItem.setOriginalPrice(v.getTotalAmount());
        orderItem.setDetail(String.format(REMARK, v.getCode()));
        orderItem.setItemCode(String.valueOf(v.getId()));
        orderItem.setItemName(String.format("%s-%s", v.getType(), v.getDetail()));

        orderItem.setQuantity(1);
        orderItem.setType(OrderItemType.LICENSE_BILL);
        orderItem.setFreight(BigDecimal.ZERO);
        orderItem.setDiscountAmount(BigDecimal.ZERO);

        chargeRequest.getOrderItemList().add(orderItem);
        chargeRequest.setTradeDetail(String.format(REMARK, v.getCode()));
        return chargeRequest;
    }
}
