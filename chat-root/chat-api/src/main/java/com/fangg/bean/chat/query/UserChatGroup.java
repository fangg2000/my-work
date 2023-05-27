package com.fangg.bean.chat.query;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户与聊天群关联表
 * 
 * @author fangg date:2022/03/04 11:51
 */
@Table(name = "user_cg_fk")
public class UserChatGroup implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 1196972706681615144L;


	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ucg_id")
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
	private Integer userType = 2; 	// 用户类型：2群
    private String profilePicture;	// 群头像
    private String groupSign;		// 群签名

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getGroupSign() {
		return groupSign;
	}

	public void setGroupSign(String groupSign) {
		this.groupSign = groupSign;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

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

}