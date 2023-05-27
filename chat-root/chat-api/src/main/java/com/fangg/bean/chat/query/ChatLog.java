package com.fangg.bean.chat.query;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fangg date:2021/12/17 08:39
 */
// 因为使用了sharingshpere中间件，用tk mybatis时sharingshpere会自动转换为各分表的表名
// 此处只能是表的前缀(而chat_log表在数据库中并不存在，只是为了sharingshpere转换用)
// 否则基础方法无法使用
@Table(name="chat_log")
public class ChatLog implements Serializable {
	/**
	 * 串行版本ID
	 */
	private static final long serialVersionUID = 7590315625720872053L;

	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="chat_id")
	private String chatId;
	private String companyCode;
	private String server;
	private String client;
	private String receiver;	// 接收者，方便查询未读信息
	private String type;		// 两方的初始类型，0接收方，1发送方（之后不再更改，此属性无法判断谁是消息接收方）
	private String content;

	/**
	 * 0：未读，1：已读 默认：1
	 */
	private String status;
	private Long initTime;			// 初始时间戳
	private Timestamp createTime;

	/**
	 * 更新时间
	 */
	private Timestamp updateTime;
	private int updateStarterCount;		// 对话发起方更新次数
	private int updateReceptorCount;	// 对话接收方更新次数

	public Long getInitTime() {
		return initTime;
	}

	public void setInitTime(Long initTime) {
		this.initTime = initTime;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
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

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public int getUpdateStarterCount() {
		return updateStarterCount;
	}

	public void setUpdateStarterCount(int updateStarterCount) {
		this.updateStarterCount = updateStarterCount;
	}

	public int getUpdateReceptorCount() {
		return updateReceptorCount;
	}

	public void setUpdateReceptorCount(int updateReceptorCount) {
		this.updateReceptorCount = updateReceptorCount;
	}

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		sb.append("Hash = ").append(hashCode());
		sb.append(", serialVersionUID=").append(serialVersionUID);
		sb.append(", chatId=").append(chatId);
		sb.append(", companyCode=").append(companyCode);
		sb.append(", server=").append(server);
		sb.append(", client=").append(client);
		sb.append(", type=").append(type);
		sb.append(", content=").append(content);
		sb.append(", status=").append(status);
		sb.append(", createTime=").append(createTime);
		sb.append(", updateTime=").append(updateTime);
		sb.append("]");
		return sb.toString();
	}
}