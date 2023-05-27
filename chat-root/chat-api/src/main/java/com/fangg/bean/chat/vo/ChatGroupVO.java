package com.fangg.bean.chat.vo;

import java.io.Serializable;
import java.util.Date;

/** 
 * 群信息
 * @author fangg
 * date:2022/03/03 15:27
 */
public class ChatGroupVO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 
     */ 
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

    /** 
     * 群等级，1到12级，默认1级  默认：1
     */ 
    private Integer groupGrade;
    private String groupApply;		// 入群申请
    private Integer groupLimit;		// 群数量大小
    private Integer groupType;		// 群所属分类
    private Integer applyCondition;	// 加入条件，0自由加入，1管理员审核，2拒绝加入
    private String profilePicture;	// 群头像
    private String userCode;		// 当前登录人编号
    private String applyCode;		// 申请加入的用户编号
    private String applyDescript;	// 申请说明，审核时用
    private Integer checkStatus;	// 审核状态，0拒绝，1通过
    private Integer msgStatus;		// 群消息状态，0屏蔽，1接收
    private String groupSign;		// 群签名
    private String username;		// 用户名

    /** 
     * 创建时间
     */ 
    private Date createTime;
    private Integer pageSize = 10;	// 每页数量

	public Integer getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(Integer msgStatus) {
		this.msgStatus = msgStatus;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGroupSign() {
		return groupSign;
	}

	public void setGroupSign(String groupSign) {
		this.groupSign = groupSign;
	}

	public Integer getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(Integer checkStatus) {
		this.checkStatus = checkStatus;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getApplyCode() {
		return applyCode;
	}

	public void setApplyCode(String applyCode) {
		this.applyCode = applyCode;
	}

	public Integer getApplyCondition() {
		return applyCondition;
	}

	public void setApplyCondition(Integer applyCondition) {
		this.applyCondition = applyCondition;
	}

	public String getApplyDescript() {
		return applyDescript;
	}

	public void setApplyDescript(String applyDescript) {
		this.applyDescript = applyDescript;
	}

	public String getGroupApply() {
		return groupApply;
	}

	public void setGroupApply(String groupApply) {
		this.groupApply = groupApply;
	}

	public Integer getGroupLimit() {
		return groupLimit;
	}

	public void setGroupLimit(Integer groupLimit) {
		this.groupLimit = groupLimit;
	}

	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public void setGroupGrade(Integer groupGrade) {
		this.groupGrade = groupGrade;
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
        sb.append(" [chatGroupId=").append(chatGroupId);
        sb.append(", chatGroupName=").append(chatGroupName);
        sb.append(", chatGroupCode=").append(chatGroupCode);
        sb.append(", groupMaster=").append(groupMaster);
        sb.append(", groupMember=").append(groupMember);
        sb.append(", groupGrade=").append(groupGrade);
        sb.append(", groupLimit=").append(groupLimit);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }

}