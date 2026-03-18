package com.hk.simba.license.service.entity;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import java.io.Serializable;

/**
 * <p>
 * 违规类型处理方式
 * </p>
 *
 * @author chenm
 * @since 2022-04-13
 */
public class ViolationTypeProcessMode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方式id
     */
	@TableId(value="id", type= IdType.AUTO)
	private Long id;
    /**
     * 违规类型id
     */
	private Long violationTypeId;
    /**
     * 名称
     */
	private String name;
    /**
     * 创建人
     */
	private String createBy;
    /**
     * 创建时间
     */
	private Date createTime;
    /**
     * 更新人
     */
	private String modifyBy;
    /**
     * 更新时间
     */
	private Date modifyTime;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getViolationTypeId() {
		return violationTypeId;
	}

	public void setViolationTypeId(Long violationTypeId) {
		this.violationTypeId = violationTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "ViolationTypeProcessMode{" +
			", id=" + id +
			", violationTypeId=" + violationTypeId +
			", name=" + name +
			", createBy=" + createBy +
			", createTime=" + createTime +
			", modifyBy=" + modifyBy +
			", modifyTime=" + modifyTime +
			"}";
	}
}
