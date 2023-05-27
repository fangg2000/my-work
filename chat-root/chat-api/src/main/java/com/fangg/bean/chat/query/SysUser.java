package com.fangg.bean.chat.query;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fangg date:2021/12/17 15:56
 */
@Table(name="sys_user")
public class SysUser implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = -1390976413839411390L;

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="user_id")
	private Integer userId;

	/** 
	 */
	private String userCode;

	/** 
	 */
	private String username;

	/** 
	 */
	private String password;

	private String salt;
	private String ticket;		// 用户登录key

	/** 
	 */
	private Integer roleId;

	/**
	 * 是否禁用，0：否，1：禁用 默认：0
	 */
	private Integer status;

	/**
	 * 在线状态，0不在，1在线
	 */
	private Integer onlineStatus;

	/** 
	 */
	private String mobile;

	/** 
	 */
	private Date createTime;

	/** 
	 */
	private Date updateTime;

	/** 
	 */
	private Integer likeNum;		// 点赞数
	private Integer age;
	private Integer deleteFlag;
	private Integer sex;			// 性别
	private String userSign;		// 用户签名
	private String profilePicture;	// 用户头像
	private Integer userType;		// 用户类型，0客服，1用户
	private String country;			// 国家
	private String city;			// 城市
	private String province;		// 省份
	private String loginIp;			// 登录IP
	private Integer loginType;		// 登录类型，0：WEB端，1：手机端
	private Integer loginAgain;		// 初次登录标识，0第一次登录，1再次登录
	private String email;			// 邮箱（绑定信息后不能修改，解绑时会通过邮件通知）

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getLoginAgain() {
		return loginAgain;
	}

	public void setLoginAgain(Integer loginAgain) {
		this.loginAgain = loginAgain;
	}

	public Integer getLoginType() {
		return loginType;
	}

	public void setLoginType(Integer loginType) {
		this.loginType = loginType;
	}

	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public Integer getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(Integer likeNum) {
		this.likeNum = likeNum;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(Integer onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
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

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getUserSign() {
		return userSign;
	}

	public void setUserSign(String userSign) {
		this.userSign = userSign;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append(", userId=").append(userId);
		sb.append(", userCode=").append(userCode);
		sb.append(", username=").append(username);
		sb.append(", mobile=").append(mobile);
		sb.append(", sex=").append(sex);
		sb.append(", createTime=").append(createTime);
		sb.append(", userSign=").append(userSign);
		sb.append(", profilePicture=").append(profilePicture);
		sb.append(", userType=").append(userType);
		sb.append("]");
		return sb.toString();
	}
}