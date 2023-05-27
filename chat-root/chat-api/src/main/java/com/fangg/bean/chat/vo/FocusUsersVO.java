/* https://github.com/orange1438 */
package com.fangg.bean.chat.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 关注用户表
 */
public class FocusUsersVO implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 6597272598965820222L;

	/**
	 * id
	 */
	private Long focusId;

	/**
	 * 关注用户ID
	 */
	private Integer userId;

	/**
	 * 被关注用户ID
	 */
	private String focusUserCode;

	/**
	 * 被关注用户名称
	 */
	private String focusUserName;

	/**
	 * 关注开始时间
	 */
	private Date createTime;
	private String userCode;
	private Integer configFlag;		// 查询用户配置信息标识，0否，1是

	public Integer getConfigFlag() {
		return configFlag;
	}

	public void setConfigFlag(Integer configFlag) {
		this.configFlag = configFlag;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public Long getFocusId() {
		return focusId;
	}

	public void setFocusId(Long focusId) {
		this.focusId = focusId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFocusUserCode() {
		return focusUserCode;
	}

	public void setFocusUserCode(String focusUserCode) {
		this.focusUserCode = focusUserCode;
	}

	public String getFocusUserName() {
		return focusUserName;
	}

	public void setFocusUserName(String focusUserName) {
		this.focusUserName = focusUserName;
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
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append(", focusId=").append(focusId);
		sb.append(", userId=").append(userId);
		sb.append(", focusUserCode=").append(focusUserCode);
		sb.append(", focusUserName=").append(focusUserName);
		sb.append(", createTime=").append(createTime);
		sb.append("]");
		return sb.toString();
	}
}