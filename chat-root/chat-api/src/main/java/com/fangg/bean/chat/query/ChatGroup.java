package com.fangg.bean.chat.query;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

/** 
 * @author fangg
 * date:2022/03/03 15:27
 */
public class ChatGroup implements Serializable {
    /** 
     * 串行版本ID
    */
    private static final long serialVersionUID = -3200115257619532322L;

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="chat_group_id")
    private Integer chatGroupId;

    /** 
     * 群名称
     */ 
    private String chatGroupName;

    /** 
     * 群编号
     */ 
    private String chatGroupCode;

    /** 
     * 群主编号
     */ 
    private String groupMaster;

    /** 
     * 群成员
     */ 
    private String groupMember;
    private Integer groupLimit;

    /** 
     * 群等级，1到12级，默认1级  默认：1
     */ 
    private Integer groupGrade;
    private String groupApply;		// 入群申请
    private Integer applyCondition;	// 加入条件，0自由加入，1管理员审核，2拒绝加入
    private Integer groupType;		// 群所属分类
    private String profilePicture;	// 群头像
    private String groupSign;		// 群签名

    /** 
     * 创建时间
     */ 
    private Date createTime;

	public String getGroupSign() {
		return groupSign;
	}

	public void setGroupSign(String groupSign) {
		this.groupSign = groupSign;
	}

	public Integer getGroupLimit() {
		return groupLimit;
	}

	public void setGroupLimit(Integer groupLimit) {
		this.groupLimit = groupLimit;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public Integer getApplyCondition() {
		return applyCondition;
	}

	public void setApplyCondition(Integer applyCondition) {
		this.applyCondition = applyCondition;
	}

	public Integer getChatGroupId() {
		return chatGroupId;
	}

	public void setChatGroupId(Integer chatGroupId) {
		this.chatGroupId = chatGroupId;
	}

	public String getChatGroupName() {
		return chatGroupName;
	}

	public void setChatGroupName(String chatGroupName) {
		this.chatGroupName = chatGroupName;
	}

	public String getChatGroupCode() {
		return chatGroupCode;
	}

	public void setChatGroupCode(String chatGroupCode) {
		this.chatGroupCode = chatGroupCode;
	}

	public String getGroupMaster() {
		return groupMaster;
	}

	public void setGroupMaster(String groupMaster) {
		this.groupMaster = groupMaster;
	}

	public String getGroupMember() {
		return groupMember;
	}

	public void setGroupMember(String groupMember) {
		this.groupMember = groupMember;
	}

	public Integer getGroupGrade() {
		return groupGrade;
	}

	public void setGroupGrade(Integer groupGrade) {
		this.groupGrade = groupGrade;
	}

	public String getGroupApply() {
		return groupApply;
	}

	public void setGroupApply(String groupApply) {
		this.groupApply = groupApply;
	}

	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}