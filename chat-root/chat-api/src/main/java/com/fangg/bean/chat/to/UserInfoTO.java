package com.fangg.bean.chat.to;

import java.io.Serializable;

/**
 * @author fangg date:2021/12/17 15:56
 */
public class UserInfoTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long userId;

	/** 
	 */
	private String userCode;

	/** 
	 */
	private String userName;

	/**
	 * 组织名称
	 */
	private String companyName;
	private String companyCode;

	private int age; // 未读数量
	private int num; // 未读数量
	private String descript; // 最新未读内容

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

}