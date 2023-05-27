package com.fangg.bean.chat.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户与聊天群关联表VO
 */
public class UserChatGroupVO implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 1196972706681615144L;

	private Integer ucgId;
	private String userCode;
	private String username;

	/**
	 * 群编号
	 */
	private String chatGroupCode;

	/**
	 * 群名称
	 */
	private String chatGroupName;

	/**
	 * 消息状态，0屏蔽消息，1接收 默认：1
	 */
	private Integer msgStatus;

	/**
	 * 加入群时间
	 */
	private Date createTime;
	private Integer identity; 		// 身份/职位，0普通成员，1长老，2副群，3群主

	public Integer getIdentity() {
		return identity;
	}

	public void setIdentity(Integer identity) {
		this.identity = identity;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getUcgId() {
		return ucgId;
	}

	public void setUcgId(Integer ucgId) {
		this.ucgId = ucgId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getChatGroupCode() {
		return chatGroupCode;
	}

	public void setChatGroupCode(String chatGroupCode) {
		this.chatGroupCode = chatGroupCode;
	}

	public String getChatGroupName() {
		return chatGroupName;
	}

	public void setChatGroupName(String chatGroupName) {
		this.chatGroupName = chatGroupName;
	}

	public Integer getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(Integer msgStatus) {
		this.msgStatus = msgStatus;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [ucgId=").append(ucgId);
		sb.append(", userCode=").append(userCode);
		sb.append(", chatGroupCode=").append(chatGroupCode);
		sb.append(", chatGroupName=").append(chatGroupName);
		sb.append(", msgStatus=").append(msgStatus);
		sb.append(", createTime=").append(createTime);
		sb.append("]");
		return sb.toString();
	}
}