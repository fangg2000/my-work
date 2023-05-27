package com.fangg.bean.chat.vo.record;

import java.io.Serializable;
import java.util.Date;

/** 
 * 评论VO
 * @author fangg
 * 2022年2月21日 上午10:38:40
 */
public class DiscussInfoVO implements Serializable {
    /** 
     * 串行版本ID
    */
    private static final long serialVersionUID = 4130909736202317721L;

    /** 
     * 评论ID
     */ 
    private Long discussId; 
    private Long recordId;
    private String userCode;
    private String username;
    private String content;

    /** 
     * 点赞数量  默认：0
     */ 
    private Integer agreeNum;

    /** 
     * 回复上级ID
     */ 
    private String parentId;
	private Date createTime;
    private String disId;		// 查询discussId时转字符串
    private String drId;		// 查询recordId时转字符串
	private String loginCode;	// 登录人用户编号

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

	public String getDisId() {
		return disId;
	}

	public void setDisId(String disId) {
		this.disId = disId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getDiscussId() {
		return discussId;
	}

	public void setDiscussId(Long discussId) {
		this.discussId = discussId;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getAgreeNum() {
		return agreeNum;
	}

	public void setAgreeNum(Integer agreeNum) {
		this.agreeNum = agreeNum;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}