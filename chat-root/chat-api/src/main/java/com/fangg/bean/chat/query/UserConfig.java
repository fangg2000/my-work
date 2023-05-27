package com.fangg.bean.chat.query;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 用户配置信息表
 * 
 * @author fangg 2022年1月17日 下午3:26:10
 */
@Table(name="user_config")
public class UserConfig implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 1545980058278773728L;

	/**
	 * ID
	 */
	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="config_id")
	private Integer configId;

	/**
	 * 用户编号
	 */
	private String userCode;

	/**
	 * 是否自动弹出泡泡提示，0否，1是 默认：1
	 */
	private Integer showBall;

	/**
	 * 泡泡显示时长（秒），默认0无限 默认：0
	 */
	private Integer ballShowSeconds;

	/**
	 * 是否允许陌生人联系，0否，1是 默认：1
	 */
	private Integer strangerContact;
	private Integer grade;				// 等级，0普通用户，1VIP用户，2MVP用户，3SUP用户
	private Integer ppAvtiveType;		// 来新消息时，头像动作类型，0晃动，1闪动
	private Integer outConfirm;			// 用户退出确认提示，0否，1是
	private String bindIp;				// 绑定IP或标签，JSON格式"{0:\\"192.168.0.1\\"}"，0为登录类型
	private String blackList;			// 黑名单用户
    private Date overTime;				// 过期时间

	public Date getOverTime() {
		return overTime;
	}

	public void setOverTime(Date overTime) {
		this.overTime = overTime;
	}

	public String getBlackList() {
		return blackList;
	}

	public void setBlackList(String blackList) {
		this.blackList = blackList;
	}

	public String getBindIp() {
		return bindIp;
	}

	public void setBindIp(String bindIp) {
		this.bindIp = bindIp;
	}

	public Integer getOutConfirm() {
		return outConfirm;
	}

	public void setOutConfirm(Integer outConfirm) {
		this.outConfirm = outConfirm;
	}

	public Integer getPpAvtiveType() {
		return ppAvtiveType;
	}

	public void setPpAvtiveType(Integer ppAvtiveType) {
		this.ppAvtiveType = ppAvtiveType;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
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