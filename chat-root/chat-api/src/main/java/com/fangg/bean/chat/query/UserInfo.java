package com.fangg.bean.chat.query;

import java.io.Serializable;

import javax.persistence.Table;

/**
 * @author fangg date:2021/12/17 15:56
 */
@Table(name="tb_user")
public class UserInfo implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = -1390976413839411390L;

	/** 
	 */

	private Long userId;

	/** 
	 */
	private String userName;

	/** 
	 */
	private Integer age;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

}