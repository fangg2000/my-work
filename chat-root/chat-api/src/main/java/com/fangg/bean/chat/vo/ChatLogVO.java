package com.fangg.bean.chat.vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author fangg date:2021/12/17 08:39
 */
public class ChatLogVO implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 7590315625720872053L;

	private String chatId;
	private String companyCode;
	private String server;
	private String client;
	private String receiver;	// 接收者，方便查询未读信息

	/**
	 * 类型，0：服务端，1：客户端 默认：0
	 */
	private String type;
	private String content;

	/**
	 * 0：未读，1：已读 默认：1
	 */
	private String status;
	private String chatKey;
	private Timestamp createTime;
	private Integer userId;
	private String contactCode;		// 联系人编号(消息接收者)
	private boolean newContactFlag;	// 新用户联系
	private Integer pageSize;		// 查询数量
	private Long initTime;			// 初始时间戳

	public String getChatKey() {
		return chatKey;
	}

	public void setChatKey(String chatKey) {
		this.chatKey = chatKey;
	}

	public Long getInitTime() {
		return initTime;
	}

	public void setInitTime(Long initTime) {
		this.initTime = initTime;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getContactCode() {
		return contactCode;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public boolean isNewContactFlag() {
		return newContactFlag;
	}

	public void setNewContactFlag(boolean newContactFlag) {
		this.newContactFlag = newContactFlag;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
}