package com.fangg.bean.chat.to;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fangg.bean.chat.query.PermissionFunc;
import com.fangg.bean.chat.vo.SysUserVO;

/**
 * 用户配置信息TO
 * @author fangg
 * 2022年1月19日 上午9:40:23
 */
public class UserConfigTO implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 1545980058278773728L;
	private Integer userId;
	private String userCode;
	private String username;
	private String ticket;			// 用户登录key
	private Integer status;			// 是否禁用，0：否，1：禁用 默认：0
	private Integer onlineStatus;	// 在线状态，0不在，1在线
	private String mobile;
	private Date createTime;
	private Date updateTime;
	private String loginIp;			// 登录IP
	private Integer loginType;		// 登录类型，0：WEB端，1：手机端
	private Integer loginAgain;		// 初次登录标识，0第一次登录，1再次登录
	private String email;			// 邮箱（绑定信息后不能修改，解绑时会通过邮件通知）
	
	// 用户配置相关
	private Integer configId;
	private Integer showBall;		// 是否禁止自动弹出泡泡提示，0否，1是 默认：0
	private Integer ballShowSeconds;// 泡泡显示时长（秒），默认0无限 默认：0
	private Integer strangerContact;// 是否禁止陌生人联系，0否，1是 默认：0
	private Integer grade;			// 等级，0普通用户，1VIP用户，2MVP用户，3SUP用户
	private Integer ppAvtiveType;	// 来新消息时，头像动作类型，0晃动，1闪动
	private Integer outConfirm;		// 用户退出确认提示，0否，1是
	private Map<Integer, String> bindIp;	// 绑定IP或标签，JSON格式"{0:\\"192.168.0.1\\"}"，0为登录类型
	private Integer bindFlag;		// 当前登录端绑定标识

	private Integer likeNum;		// 点赞数
	private Integer fansNum;		// 关注数/粉丝
	private Integer age;
	private Integer deleteFlag;
	private Integer sex;			// 性别
	private String userSign;		// 用户签名
	private String profilePicture;	// 用户头像
	private Integer userType;		// 用户类型，0客服，1用户
	private String country;			// 国家
	private String city;			// 城市
	private String province;		// 省份
	private Boolean likeFlag;		// 当前登录用户是否给查询用户点赞
	private Boolean focusFlag;		// 当前登录用户是否关注查询用户
	private Boolean byFocusFlag;	// 查询用户是否关注当前登录用户(登录用户被关注)
	private String companyCode;		// 企业编号
	private String content;			// 内容
	private String chatId;
	private Long initTime;			// 聊天信息时间戳
	
	
	
	
//	private PermissionFuncTO funcPermissionInside;	// 服务端功能权限-不返回前端
	private Map<String, PermissionFunc> funcPermission4Server;		// 服务端功能权限-不返回前端
	private Boolean checkLoginFlag;					// 登录验证(为true时登录的时候需要验证)
	private String checkIdentifier;					// 验证识别码(前端的返回结果匹配才算验证通过)
	private Date checkTimeout;						// 验证超时（返回开始算时间，得到结果为结束时间，防恶意登录）
	private String aesKey;							// 前端登录时生成的密钥(接口数据加解密用)
	private String fingerPrint;						// 前端指纹ID
	
	
	private List<SysUserVO> userFocusList;					// 用户的关注用户列表
	private List<Map<String, Object>> fp4Web; 				// 返回前端的功能权限列表
	private Map<Integer, String> loginIdentifierMap;		// 登录识别码(不同类型前端登录时生成，应该缓存在sessionStorage)
	private List<String> blackUserList;						// 黑名单列表


	public List<String> getBlackUserList() {
		return blackUserList;
	}

	public void setBlackUserList(List<String> blackUserList) {
		this.blackUserList = blackUserList;
	}

	public Long getInitTime() {
		return initTime;
	}

	public void setInitTime(Long initTime) {
		this.initTime = initTime;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getBindFlag() {
		return bindFlag;
	}

	public void setBindFlag(Integer bindFlag) {
		this.bindFlag = bindFlag;
	}

	public Integer getLoginAgain() {
		return loginAgain;
	}

	public void setLoginAgain(Integer loginAgain) {
		this.loginAgain = loginAgain;
	}

	public Map<Integer, String> getBindIp() {
		return bindIp;
	}

	public void setBindIp(Map<Integer, String> bindIp) {
		this.bindIp = bindIp;
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

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public Integer getOutConfirm() {
		return outConfirm;
	}

	public void setOutConfirm(Integer outConfirm) {
		this.outConfirm = outConfirm;
	}

	public Date getCheckTimeout() {
		return checkTimeout;
	}

	public void setCheckTimeout(Date checkTimeout) {
		this.checkTimeout = checkTimeout;
	}

	public String getCheckIdentifier() {
		return checkIdentifier;
	}

	public void setCheckIdentifier(String checkIdentifier) {
		this.checkIdentifier = checkIdentifier;
	}

	public Map<Integer, String> getLoginIdentifierMap() {
		return loginIdentifierMap;
	}

	public void setLoginIdentifierMap(Map<Integer, String> loginIdentifierMap) {
		this.loginIdentifierMap = loginIdentifierMap;
	}

	public Boolean getCheckLoginFlag() {
		return checkLoginFlag;
	}

	public void setCheckLoginFlag(Boolean checkLoginFlag) {
		this.checkLoginFlag = checkLoginFlag;
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

	public Integer getPpAvtiveType() {
		return ppAvtiveType;
	}

	public void setPpAvtiveType(Integer ppAvtiveType) {
		this.ppAvtiveType = ppAvtiveType;
	}

	/**  
	 * 服务端功能权限-不返回前端
	 * <code>（通过对应的权限CODE能判断是否有权限）</code>
	 * */
	public Map<String, PermissionFunc> getFuncPermission4Server() {
		return funcPermission4Server;
	}

	public void setFuncPermission4Server(Map<String, PermissionFunc> funcPermission4Server) {
		this.funcPermission4Server = funcPermission4Server;
	}
	

	/** 返回前端的功能权限列表 */	
	public List<Map<String, Object>> getFp4Web() {
		return fp4Web;
	}

	public void setFp4Web(List<Map<String, Object>> fp4Web) {
		this.fp4Web = fp4Web;
	}
	
	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Boolean getByFocusFlag() {
		return byFocusFlag;
	}

	public void setByFocusFlag(Boolean byFocusFlag) {
		this.byFocusFlag = byFocusFlag;
	}

	public Integer getFansNum() {
		return fansNum;
	}

	public void setFansNum(Integer fansNum) {
		this.fansNum = fansNum;
	}

	public Boolean getLikeFlag() {
		return likeFlag;
	}

	public void setLikeFlag(Boolean likeFlag) {
		this.likeFlag = likeFlag;
	}

	public Boolean getFocusFlag() {
		return focusFlag;
	}

	public void setFocusFlag(Boolean focusFlag) {
		this.focusFlag = focusFlag;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<SysUserVO> getUserFocusList() {
		return userFocusList;
	}

	public void setUserFocusList(List<SysUserVO> userFocusList) {
		this.userFocusList = userFocusList;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
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

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
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

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
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

	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public Integer getShowBall() {
		return showBall;
	}

	public void setShowBall(Integer showBall) {
		this.showBall = showBall;
	}

	public Integer getBallShowSeconds() {
		return ballShowSeconds;
	}

	public void setBallShowSeconds(Integer ballShowSeconds) {
		this.ballShowSeconds = ballShowSeconds;
	}

	public Integer getStrangerContact() {
		return strangerContact;
	}

	public void setStrangerContact(Integer strangerContact) {
		this.strangerContact = strangerContact;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [configId=").append(configId);
		sb.append(", userCode=").append(userCode);
		sb.append(", showBall=").append(showBall);
		sb.append(", ballShowSeconds=").append(ballShowSeconds);
		sb.append(", strangerContact=").append(strangerContact);
		sb.append("]");
		return sb.toString();
	}
}