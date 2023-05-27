package com.fangg.bean.chat.query;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fangg date:2021/12/19 07:54
 */
@Table(name = "company_info")
public class CompanyInfo implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 6427681880018069228L;

	/** 
	 */
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "company_id")
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

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append(", companyId=").append(companyId);
		sb.append(", companyName=").append(companyName);
		sb.append(", companyCode=").append(companyCode);
		sb.append(", userId=").append(userId);
		sb.append(", status=").append(status);
		sb.append(", splitRows=").append(splitRows);
		sb.append(", createTime=").append(createTime);
		sb.append(", deleteFlag=").append(deleteFlag);
		sb.append(", tableTimeSplit=").append(tableTimeSplit);
		sb.append("]");
		return sb.toString();
	}
}