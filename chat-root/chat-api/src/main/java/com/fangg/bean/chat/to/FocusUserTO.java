/* https://github.com/orange1438 */
package com.fangg.bean.chat.to;

import java.io.Serializable;
import java.util.Date;

/** 
 * 关注用户表 
 */
public class FocusUserTO implements Serializable {
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
    private Integer focusUserId;

    /** 
     * 被关注用户名称
     */ 
    private String focusUserName;

    /** 
     * 关注开始时间
     */ 
    private Date createTime;
    private String userCode;
    private String username;
    private String profilePicture;
    private String userSign;
    private String content;

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


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
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


	public Integer getFocusUserId() {
		return focusUserId;
	}


	public void setFocusUserId(Integer focusUserId) {
		this.focusUserId = focusUserId;
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
	
}