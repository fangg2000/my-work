package com.fangg.bean.chat.query.record;

import java.io.Serializable;
import java.util.Date;

/**
 * 日记
 * 
 * @author fangg 2022年2月20日 下午7:05:47
 */
public class DailyRecord implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 4338227355350035678L;

	private Long recordId;

	/**
	 * 用户编号
	 */
	private String userCode;

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

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
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
	
}