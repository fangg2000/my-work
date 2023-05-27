package com.fangg.bean.chat.query;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 黑名单
 * 
 * @author fangg 2022年3月8日 上午9:30:35
 */
@Table(name="black_info")
public class BlackInfo implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = -1888863935281370252L;

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="black_id")
	private Integer blackId;

	/**
	 * 黑名单编号，ip地址/手机标识/手机号等
	 */
	private String blackCode;

	/**
	 * 用户编号
	 */
	private String userCode;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 原因
	 */
	private String descript;

	/**
	 * 当前黑名单记录是否有效，0失效，1有效 默认：1
	 */
	private Integer status;

	/**
	 * ip
	 */
	private String ipAddr;
	private Date createTime;
	private Integer loginType;	// 操作端类型，0WEB，1手机，2PAD

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public Integer getBlackId() {
		return blackId;
	}

	public void setBlackId(Integer blackId) {
		this.blackId = blackId;
	}

	public String getBlackCode() {
		return blackCode;
	}

	public void setBlackCode(String blackCode) {
		this.blackCode = blackCode;
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

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}