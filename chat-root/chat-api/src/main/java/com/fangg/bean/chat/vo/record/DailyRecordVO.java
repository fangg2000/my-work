package com.fangg.bean.chat.vo.record;

import java.io.Serializable;
import java.util.Date;

/**
 * 日记
 * @author fangg 2022年2月20日 下午7:05:47
 */
public class DailyRecordVO implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 4338227355350035678L;

	private Long recordId;
	private String drId;	// 由于前端long类型只能取到16位，多于则转成0，所以这里将recordId用字符串保存

	/**
	 * 用户编号
	 */
	private String userCode;
	private String username;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 日记状态，0公开，1好友可见，2保密
	 */
	private Integer recordStatus;

	/**
	 * 日记分组（没分组则为默认分组）
	 */
	private Integer recordGroupId;
	private Date createTime;
	private Date updateTime;
	private Integer deleteFlag;

	/**
	 * 日记内容
	 */
	private String content;
	private Integer reviewNum;		// 浏览数
	private Integer collectNum;		// 收藏数
	private Integer discussNum;		// 评论数
	
	private Integer pageSize = 10;	// 每页查询数量	
	private String loginCode;		// 登录人用户编号

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDrId() {
		return drId;
	}

	public void setDrId(String drId) {
		this.drId = drId;
	}

	public String getLoginCode() {
		return loginCode;
	}

	public void setLoginCode(String loginCode) {
		this.loginCode = loginCode;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(Integer reviewNum) {
		this.reviewNum = reviewNum;
	}

	public Integer getCollectNum() {
		return collectNum;
	}

	public void setCollectNum(Integer collectNum) {
		this.collectNum = collectNum;
	}

	public Integer getDiscussNum() {
		return discussNum;
	}

	public void setDiscussNum(Integer discussNum) {
		this.discussNum = discussNum;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}

	public Integer getRecordGroupId() {
		return recordGroupId;
	}

	public void setRecordGroupId(Integer recordGroupId) {
		this.recordGroupId = recordGroupId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append(", recordId=").append(recordId);
		sb.append(", userCode=").append(userCode);
		sb.append(", title=").append(title);
		sb.append(", recordStatus=").append(recordStatus);
		sb.append(", recordGroupId=").append(recordGroupId);
		sb.append(", updateTime=").append(updateTime);
		sb.append(", deleteFlag=").append(deleteFlag);
		sb.append(", pageSize=").append(pageSize);
		sb.append("]");
		return sb.toString();
	}

}