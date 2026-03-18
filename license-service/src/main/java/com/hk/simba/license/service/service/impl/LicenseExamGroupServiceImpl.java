package com.hk.simba.license.service.service.impl;

import com.google.common.collect.Lists;
import com.hk.simba.license.service.constant.Constants;
import com.hk.simba.license.service.constant.enums.CommonStatusEnum;
import com.hk.simba.license.service.entity.LicenseExamGroup;
import com.hk.simba.license.service.mapper.LicenseExamGroupMapper;
import com.hk.simba.license.service.service.LicenseExamGroupService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 执照考试分组信息 服务实现类
 * </p>
 *
 * @author cyh
 * @since 2021-09-03
 */
@Service
public class LicenseExamGroupServiceImpl extends ServiceImpl<LicenseExamGroupMapper, LicenseExamGroup> implements LicenseExamGroupService {
    /**
     * 执行力中的执照地图考试,科目一
     */
    @Value("${executive.map.first.exam.id}")
    private String mapFirstExamId;

    /**
     * 执照科目一考试
     */
    @Value("${executive.license.first.exam.id}")
    private String licenseFirstExamId;

    /**
     * 执照科目四考试
     */
    @Value("${executive.license.fourth.exam.id}")
    private String licenseFourthExamId;


    @Override
    public LicenseExamGroup findValidGroupByStaffId(Long staffId) {
        return this.baseMapper.findValidGroupByStaffId(staffId);
    }

    @Override
    public LicenseExamGroup createOrUpdateLicenseExamGroup(Long staffId, String examId, String name) {
        LicenseExamGroup group = this.findValidGroupByStaffId(staffId);
        examId = examId.replace(Constants.COMMA, "");
        List<String> firstMapIds = Lists.newArrayList(mapFirstExamId.split(Constants.COMMA));
        List<String> firstExamIds = Lists.newArrayList(licenseFirstExamId.split(Constants.COMMA));
        List<String> fourthExamIds = Lists.newArrayList(licenseFourthExamId.split(Constants.COMMA));
        if (group == null) {
            Boolean isCreate = false;
            group = new LicenseExamGroup();
            group.setStaffId(staffId);
            group.setName(name);
            group.setStatus(CommonStatusEnum.VALID.getValue());
            group.setCreateBy(Constants.SYS);
            group.setCreateTime(new Date());
            if (firstMapIds.contains(examId) || firstExamIds.contains(examId)) {
                group.setFirstSubjectExamId(examId);
                isCreate = true;

            }
            if (fourthExamIds.contains(examId)) {
                group.setFourthSubjectExamId(examId);
                isCreate = true;
            }
            if (!isCreate) {
                return null;
            }
            this.baseMapper.insert(group);
        } else {
            group.setModifyBy(Constants.SYS);
            group.setModifyTime(new Date());
            if (firstMapIds.contains(examId) || firstExamIds.contains(examId)) {
                group.setFirstSubjectExamId(examId);
            }
            if (fourthExamIds.contains(examId)) {
                group.setFourthSubjectExamId(examId);
            }
            this.baseMapper.updateById(group);
        }
        return group;
    }

    @Override
    public void invalidLicenseExamGroup(LicenseExamGroup group) {
        if (group != null && group.getId() != null && group.getStatus().equals(CommonStatusEnum.VALID.getValue())) {
            group.setStatus(CommonStatusEnum.INVALID.getValue());
            group.setModifyTime(new Date());
            if (StringUtils.isBlank(group.getModifyBy())) {
                group.setModifyBy(Constants.SYS);
            }
            this.baseMapper.updateById(group);
        }
    }


}
