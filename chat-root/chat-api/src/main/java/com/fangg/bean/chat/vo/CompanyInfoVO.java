package com.fangg.bean.chat.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fangg date:2021/12/19 07:54
 */
public class CompanyInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer companyId;

	/**
	 * 组织名称
	 */
	private String companyName;

	/** 
	 */
	private String companyCode;

	/**
	 * 用户ID
	 */
	private Integer userId;

	/**
	 * 禁用状态，0禁用，1可用 默认：0
	 */
	private Integer status;

	/**
	 * 聊天记录表上限行数，默认1千万
	 */
	private Long splitRows;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 是否删除，0否，1是 默认：0
	 */
	private Integer deleteFlag;

	/**
	 * 通过两个时间分隔一个新表
	 */
	private String tableTimeSplit;

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getSplitRows() {
		return splitRows;
	}

	public void setSplitRows(Long splitRows) {
		this.splitRows = splitRows;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getTableTimeSplit() {
		return tableTimeSplit;
	}

	public void setTableTimeSplit(String tableTimeSplit) {
		this.tableTimeSplit = tableTimeSplit;
	}
	
}