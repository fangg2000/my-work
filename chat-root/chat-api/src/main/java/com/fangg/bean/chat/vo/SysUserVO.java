package com.fangg.bean.chat.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author fangg date:2021/12/17 15:56
 */
public class SysUserVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer userId;
	private String userCode;
	private String username;
	private String password;
	private String newPassword;
	private String salt;
	private String ticket;		// 用户登录key
	private Integer roleId;

	/**
	 * 是否禁用，0：否，1：禁用 默认：0
	 */
	private Integer status;

	/**
	 * 在线状态，0不在，1在线
	 */
	private Integer onlineStatus;
	private String mobile;
	private Date createTime;
	private Date updateTime;
	private Integer likeNum;		// 点赞数
	private Integer fansNum;		// 关注数/粉丝
	private Integer age;
	private Integer deleteFlag;
	private Integer sex;			// 性别
	private String userSign;		// 用户签名
	private String profilePicture;	// 用户头像
	private Integer userType;		// 用户类型，0客服，1用户
	private String companyCode;		// 企业编号
	private String country;			// 国家
	private String city;			// 城市
	private String province;		// 省份
	private Integer loginAgain;		// 初次登录标识，0第一次登录，1再次登录
	private String email;			// 邮箱（绑定信息后不能修改，解绑时会通过邮件通知）
	private Integer registType;		// 注册类型，0个人，1企业(企业定义为一种特别的用户，可以管理客服信息)
	
	
	private String content;			// 查询内容
	private Integer startAge;		// 开始年龄
	private Integer endAge;			// 结束年龄
	private Boolean likeFlag;		// 当前登录用户是否给查询用户点赞
	private Boolean focusFlag;		// 当前登录用户是否关注查询用户
	private String contactCode;		// 联系人编号
	private String loginIp;			// 登录IP
	private Integer loginType;		// 登录类型，0：WEB端，1：手机端
	private String loginIdentifier;	// 登录识别码(前端登录时生成，应该缓存在sessionStorage)
	private String checkIdentifier;	// 验证识别码
	private Integer outConfirm;		// 用户退出确认提示，0否，1是
	private Integer configId;
	
	
	
	private String aesKey;					// 前端登录时生成的AES密钥(接口数据加解密用)
	private String fingerPrint;				// 前端指纹ID
	
		
	private List<SysUserVO> userFocusList;	// 用户的关注用户列表
	
	
	

	public Integer getRegistType() {
		return registType;
	}

	public void setRegistType(Integer registType) {
		this.registType = registType;
	}

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

	public String getFingerPrint() {
		return fingerPrint;
	}

	public void setFingerPrint(String fingerPrint) {
		this.fingerPrint = fingerPrint;
	}

	public String getAesKey() {
		return aesKey;
	}

	public void setAesKey(String aesKey) {
		this.aesKey = aesKey;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public Integer getOutConfirm() {
		return outConfirm;
	}

	public void setOutConfirm(Integer outConfirm) {
		this.outConfirm = outConfirm;
	}

	public String getCheckIdentifier() {
		return checkIdentifier;
	}

	public void setCheckIdentifier(String checkIdentifier) {
		this.checkIdentifier = checkIdentifier;
	}

	public String getLoginIdentifier() {
		return loginIdentifier;
	}

	public void setLoginIdentifier(String loginIdentifier) {
		this.loginIdentifier = loginIdentifier;
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

	public List<SysUserVO> getUserFocusList() {
		return userFocusList;
	}

	public void setUserFocusList(List<SysUserVO> userFocusList) {
		this.userFocusList = userFocusList;
	}

	public String getContactCode() {
		return contactCode;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public Integer getFansNum() {
		return fansNum;
	}

	public void setFansNum(Integer fansNum) {
		this.fansNum = fansNum;
	}

	public Boolean getFocusFlag() {
		return focusFlag;
	}

	public void setFocusFlag(Boolean focusFlag) {
		this.focusFlag = focusFlag;
	}

	public Boolean getLikeFlag() {
		return likeFlag;
	}

	public void setLikeFlag(Boolean likeFlag) {
		this.likeFlag = likeFlag;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getStartAge() {
		return startAge;
	}

	public void setStartAge(Integer startAge) {
		this.startAge = startAge;
	}

	public Integer getEndAge() {
		return endAge;
	}

	public void setEndAge(Integer endAge) {
		this.endAge = endAge;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
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

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	/** 密码加盐 **/
	public String getCredentialsSalt() {
		if (this.salt != null) {
			return getUserCode() + salt;
		}
		return null;
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
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append(", userId=").append(userId);
		sb.append(", userCode=").append(userCode);
		sb.append(", username=").append(username);
		sb.append(", mobile=").append(mobile);
		sb.append(", age=").append(age);
		sb.append(", sex=").append(sex);
		sb.append(", createTime=").append(createTime);
		sb.append(", userSign=").append(userSign);
		sb.append(", profilePicture=").append(profilePicture);
		sb.append(", userType=").append(userType);
		sb.append("]");
		return sb.toString();
	}
	
	
}