package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;

import java.io.Serializable;

/**
 * <p>
 * 站点-大区映射表
 * </p>
 *
 * @author chenjh1
 * @since 2020-08-12
 */
public class RegionDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 城市名
     */
    private String cityName;
    /**
     * 站点名
     */
    private String siteName;
    /**
     * 站点id
     */
    private Long siteId;
    /**
     * 好慷自定义大区
     */
    private String hkRegion;
    /**
     * 经度
     */
    private Float lng;
    /**
     * 纬度
     */
    private Float lat;
    /**
     * 省份
     */
    private String province;
    /**
     * 站点类型：1保洁、2保姆
     */
    private Integer siteBelong;
    /**
     * 大区经理-名字
     */
    private String manager;
    /**
     * 大区经理-学号id
     */
    private String managerId;
    /**
     * 大区经理-邮箱
     */
    private String managerEmail;
    /**
     * 大区经理-手机号
     */
    private String managerPhone;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private String modifyBy;
    /**
     * 修改时间
     */
    private Date modifyTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getHkRegion() {
        return hkRegion;
    }

    public void setHkRegion(String hkRegion) {
        this.hkRegion = hkRegion;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getSiteBelong() {
        return siteBelong;
    }

    public void setSiteBelong(Integer siteBelong) {
        this.siteBelong = siteBelong;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "RegionDetail{" +
                ", id=" + id +
                ", cityName=" + cityName +
                ", siteName=" + siteName +
                ", siteId=" + siteId +
                ", hkRegion=" + hkRegion +
                ", lng=" + lng +
                ", lat=" + lat +
                ", province=" + province +
                ", siteBelong=" + siteBelong +
                ", manager=" + manager +
                ", managerId=" + managerId +
                ", managerEmail=" + managerEmail +
                ", managerPhone=" + managerPhone +
                ", createBy=" + createBy +
                ", createTime=" + createTime +
                ", modifyBy=" + modifyBy +
                ", modifyTime=" + modifyTime +
                "}";
    }
}
