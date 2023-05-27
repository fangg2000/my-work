package com.fangg.bean.chat.vo;

import java.io.Serializable;

/**
 * @author fangg date:2021/12/17 15:56
 */
public class UserCompanyVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer userId;

	/** 
	 */
	private String userCode;

	/** 
	 */
	private String username;

	/** 
	 */
	private Integer roleId;

	/**
	 * 是否禁用，0：否，1：禁用 默认：0
	 */
	private Integer userStatus;

	/**
	 * 在线状态，0不在，1在线
	 */
	private Integer onlineStatus;

	/** 
	 */
	private String mobile;

	private Integer companyId;

	/**
	 * 组织名称
	 */
	private String companyName;

	/** 
	 */
	private String companyCode;

	/**
	 * 禁用状态，0禁用，1可用 默认：0
	 */
	private Integer companyStatus;

	/**
	 * 聊天记录表上限行数，默认1千万
	 */
	private Long splitRows;

	/**
	 * 通过两个时间分隔一个新表
	 */
	private String tableTimeSplit;
	private String ticket;		// 用户登录key

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}

	public Integer getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(Integer onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

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

	public Integer getCompanyStatus() {
		return companyStatus;
	}

	public void setCompanyStatus(Integer companyStatus) {
		this.companyStatus = companyStatus;
	}

	public Long getSplitRows() {
		return splitRows;
	}

	public void setSplitRows(Long splitRows) {
		this.splitRows = splitRows;
	}

	public String getTableTimeSplit() {
		return tableTimeSplit;
	}

	public void setTableTimeSplit(String tableTimeSplit) {
		this.tableTimeSplit = tableTimeSplit;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

}