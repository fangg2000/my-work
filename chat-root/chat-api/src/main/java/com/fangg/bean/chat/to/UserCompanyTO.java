package com.fangg.bean.chat.to;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fangg date:2021/12/17 15:56
 */
public class UserCompanyTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer userId;
	private String userCode;
	private String username;
	private Integer roleId;

	/**
	 * 是否禁用，0：否，1：禁用 默认：0
	 */
	private Integer userStatus;

	/**
	 * 在线状态，0不在，1在线
	 */
	private Integer onlineStatus;
	private String mobile;
	private Integer companyId;

	/**
	 * 组织名称
	 */
	private String companyName;
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
	private String chatId;
	private int num;				// 未读数量
	private String content;			// 最新未读内容
	private String ticket;			// 用户登录key
	private String profilePicture;	// 用户头像
	private String userSign;		// 用户签名
	private Integer userType;		// 用户类型，0客服，1用户
	private Date createTime;
	private Date updateTime;
	private Long initTime;			// 初始时间戳
	

	private Integer configId;
	private Integer showBall;		// 是否禁止自动弹出泡泡提示，0否，1是 默认：0
	private Integer ballShowSeconds;// 泡泡显示时长（秒），默认0无限 默认：0
	private Integer strangerContact;// 是否禁止陌生人联系，0否，1是 默认：0
	private Integer grade;			// 等级，0普通用户，1VIP用户，2MVP用户，3SUP用户
	private Integer ppAvtiveType;	// 来新消息时，头像动作类型，0晃动，1闪动
	private Integer outConfirm;		// 用户退出确认提示，0否，1是

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public Integer getShowBall() {
		return showBall;
	}

	public void setShowBall(Integer showBall) {
		this.showBall = showBall;
	}

	public Integer getBallShowSeconds() {
		return ballShowSeconds;
	}

	public void setBallShowSeconds(Integer ballShowSeconds) {
		this.ballShowSeconds = ballShowSeconds;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getPpAvtiveType() {
		return ppAvtiveType;
	}

	public void setPpAvtiveType(Integer ppAvtiveType) {
		this.ppAvtiveType = ppAvtiveType;
	}

	public Integer getOutConfirm() {
		return outConfirm;
	}

	public void setOutConfirm(Integer outConfirm) {
		this.outConfirm = outConfirm;
	}

	public Long getInitTime() {
		return initTime;
	}

	public void setInitTime(Long initTime) {
		this.initTime = initTime;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getStrangerContact() {
		return strangerContact;
	}

	public void setStrangerContact(Integer strangerContact) {
		this.strangerContact = strangerContact;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getUserSign() {
		return userSign;
	}

	public void setUserSign(String userSign) {
		this.userSign = userSign;
	}

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

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

}