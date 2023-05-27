package com.fangg.websocket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.ChatLog;
import com.fangg.bean.chat.vo.ChatLogVO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.service.ChatLogService;
import com.fangg.service.CompanyInfoService;
import com.fangg.service.SysUserService;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.UuidUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * 聊天
 * @author fangg
 * 2021年12月17日 下午12:10:43
 */
@Service(value="chatSocketService")
public class ChatSocketService {
	private static Logger logger = LoggerFactory.getLogger(ChatSocketService.class);

	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	ChatLogService chatService;
	@Autowired
	SysUserService sysUserService;
	@Autowired
	CompanyInfoService companyInfoService;
	
	// 首次显示历史记录数量和查看数量
	private static final int PAGE_NUM = 5;

	public String backChatKey(String companyCode, String server, String client, String type, Map<String, String> chatMap) {
		String chatKey = null;

		// 为系统用户
		if (TypeConstant.SYSTEM_CODE.equals(companyCode)) {
			// 如果是用户与用户聊天，则在redis中保存两条双方共用一个的key(这个值最好在删除用户时一起删除，以节省内存，通过用户聊天记录判断)
			String resultStr = redisClientTemplate.getMapValue(RedisConstant.USER_CHAT_USER_KEY, server+ "_" +client);
			if (resultStr == null) {
				chatKey = RedisConstant.U2U_PREFIX_KEY + System.currentTimeMillis() + UuidUtil.genUuid(5);
				redisClientTemplate.setMapValue(RedisConstant.USER_CHAT_USER_KEY, server+ "_" +client, 
						chatKey+"/"+server+"/"+client+"/"+TypeConstant.CHAT_STARTER_1);
				redisClientTemplate.setMapValue(RedisConstant.USER_CHAT_USER_KEY, client+ "_" +server, 
						chatKey+"/"+server+"/"+client+"/"+TypeConstant.CHAT_STARTER_0);
				chatMap.put("server", server);
				chatMap.put("client", client);
				chatMap.put("type", TypeConstant.CHAT_STARTER_1);
			} else {
				String [] temp = resultStr.split("\\/");
				chatKey = temp[0];
				chatMap.put("server", temp[1]);
				chatMap.put("client", temp[2]);
				chatMap.put("type", temp[3]);
			}
		} 
		// 群
		else if (companyCode.equals(server)) {
			chatKey = RedisConstant.U2G_PREFIX_KEY + companyCode;
			chatMap.put("server", server);
			chatMap.put("client", client);
			chatMap.put("type", TypeConstant.CHAT_STARTER_1);
		}
		// 为客服
		else {
			chatKey = RedisConstant.U2C_PREFIX_KEY + companyCode + "_" + server + "_" + client;
			chatMap.put("server", server);
			chatMap.put("client", client);
			chatMap.put("type", type);
		}
		
		return chatKey;
	}

	/**
	* 读取记录
	*/
	public ResultEntity read(String companyCode, String server, 
			String client, String type, String status) throws Exception {
		// logger.info("{}写入内容：{}", bcCode, text);
		ResultEntity resultEntity = new ResultEntity();
		try {
			// logger.info("{}读取内容", bcCode);
			String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dMMy"));
			List<ChatLog> listChat = new ArrayList<>();
			ChatLog chatTest = null;
			boolean statusFlag = false;
			Map<String, String> chatMap = new HashMap<>();
			String chatKey = backChatKey(companyCode, server, client, type, chatMap);
			server = chatMap.get("server");
			client = chatMap.get("client");
			type = chatMap.get("type");
			
			// "未读"处理
			if (TypeConstant.CHAT_STATUS_0.equals(status)) {
				listChat = redisClientTemplate.getList(chatKey, ChatLog.class);
				if (listChat != null && listChat.size() > 0) {
					int size = listChat.size();
					resultEntity.setData(size >= PAGE_NUM ? listChat.subList(size - PAGE_NUM, size) : listChat);
					return resultEntity;
				} else {
					ChatLogVO chatTestIn = new ChatLogVO();
					chatTestIn.setCompanyCode(companyCode);
					chatTestIn.setServer(server);
					chatTestIn.setClient(client);
					listChat = chatService.getChatMoreList(chatTestIn);
					if (listChat != null && listChat.size() > 0) {
						saveValueToRedis(companyCode, server, client, type, status, datetime, listChat, chatKey);
						// 如果聊天记录不为空，则返回聊天记录列表
						int size = listChat.size();
						resultEntity.setData(size > PAGE_NUM ? listChat.subList(size - PAGE_NUM, size) : listChat);
						return resultEntity;
					} else {
						listChat = new ArrayList<>();
						chatTest = new ChatLog();
						chatTest.setContent("");
						chatTest.setInitTime(System.currentTimeMillis());
					}
				}
			} else {
				chatTest = redisClientTemplate.get(chatKey + "_" + datetime, ChatLog.class);
				if (chatTest == null) {
					//logger.info("取列表最后一个");
					String result = null;
					result = redisClientTemplate.getLastListObject(chatKey);
					
					if (result == null) {
						// 另起线程查询数据并缓存
						saveValueToRedis(companyCode, server, client, type, status, datetime, null, chatKey);
						listChat = new ArrayList<>();
						chatTest = new ChatLog();
						chatTest.setContent("");
						chatTest.setInitTime(System.currentTimeMillis());
						statusFlag = true;
					} else {
						chatTest = JSONObject.parseObject(result, ChatLog.class);
						// 缓存最后一条聊天记录
						redisClientTemplate.setStringByTimeout(chatKey + "_" + datetime,
								JSONObject.toJSONString(chatTest), TimeoutConstant.CHAT_SESSION_NEW_TIMEOUT);
					}
				} 
			}
			
			if (chatTest != null) {
				if (TypeConstant.CHAT_STATUS_1.equals(chatTest.getStatus())) {
					if (TypeConstant.CHAT_STARTER_1.equals(type) && chatTest.getUpdateStarterCount() > 0) {
						statusFlag = true;
					} else if (TypeConstant.CHAT_STARTER_0.equals(type) && chatTest.getUpdateReceptorCount() > 0) {
						statusFlag = true;
					}
				}
			}

			// 更新最新记录未读状态为“已读”
			if (statusFlag == false) {
				checkStatus(chatKey, type, status, datetime, chatTest);
			}
			
			// 不返回前端字段
			JSONObject chatJson = JSONObject.parseObject(JSONObject.toJSONString(chatTest));
			chatJson.remove("updateReceptorCount");
			chatJson.remove("updateStarterCount");
			chatJson.remove("companyCode");
			chatJson.remove("createTime");
			chatJson.remove("chatId");
			JSONArray chatArr = new JSONArray();
			chatArr.add(chatJson);
			resultEntity.setData(chatArr);
		} catch (Exception e) {
			logger.error("读取记录异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "读取记录异常，请联系管理员。");
		}
		return resultEntity;
	}

	/**
	 * 更新最新记录未读状态为“已读”
	 */
	private void checkStatus(String chatKey, String type, String status, String datetime, ChatLog chatLog) {
		try {
			taskExecutor.execute(new Runnable() {
				public void run() {
					ChatLog chatTest = null;
					if (chatLog == null || chatLog.getCompanyCode() == null) {
						chatTest = redisClientTemplate.get(chatKey + "_" + datetime, ChatLog.class);
					} else {
						chatTest = chatLog;
					}
					
					// 更新对方信息为“已读”
					if (chatTest != null && type.equals(chatTest.getType()) == false) {
						chatTest.setStatus(TypeConstant.CHAT_STATUS_1); // “已读”
						
						if (TypeConstant.CHAT_STARTER_1.equals(type)) {
							chatTest.setUpdateStarterCount(chatTest.getUpdateStarterCount() + 1);	// 表示当前操作人已经读取对方信息
							chatTest.setType(TypeConstant.CHAT_STARTER_0);
						} else if (TypeConstant.CHAT_STARTER_0.equals(type)) {
							chatTest.setUpdateReceptorCount(chatTest.getUpdateReceptorCount() + 1);
							chatTest.setType(TypeConstant.CHAT_STARTER_1);
						}

						// 缓存最后一条聊天记录
						redisClientTemplate.setStringByTimeout(chatKey + "_" + datetime,
								JSONObject.toJSONString(chatTest), TimeoutConstant.CHAT_SESSION_NEW_TIMEOUT);
						String result = redisClientTemplate.getLastListObject(chatKey);
						if (result != null) {
							ChatLog chatTestL = JSONObject.parseObject(result, ChatLog.class);
							if (chatTestL.getInitTime().equals(chatTest.getInitTime())) {
								redisClientTemplate.setListValueByIndexAndTimeout(chatKey, chatTest, -1, TimeoutConstant.CHAT_SESSION_LIST_TIMEOUT);
							}
						}

						// 更新对方为已读(也许没有要更新记录，所以不能以此结果作为判断)
						ChatLogVO chatLogVOIn = JSONObject.parseObject(JSONObject.toJSONString(chatTest), ChatLogVO.class);
						chatService.updateReadStatus(chatLogVOIn);
					}
				}
			});
		} catch (Exception e) {
			logger.error("多线程更新最新记录未读状态为“已读”异常：", e);
		}
	}

	/**
	 * 查询数据并缓存
	 */
	private void saveValueToRedis(String companyCode, String server, String client, String type, 
			String status, String datetime, List<ChatLog> listChatIn, String chatKey) {
		try {
			taskExecutor.execute(new Runnable() {
				public void run() {
					//String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dMMy"));
					logger.info("取数据库列表");
					ChatLogVO chatTestIn = new ChatLogVO();
					chatTestIn.setCompanyCode(companyCode);
					chatTestIn.setServer(server);
					chatTestIn.setClient(client);
					List<ChatLog> listChat = listChatIn;
					
					if (listChat == null) {
						listChat = chatService.getChatMoreList(chatTestIn);
					}
					if (listChat != null && listChat.size() > 0) {
						int size = listChat.size();
						// 缓存最后一条聊天记录
						redisClientTemplate.setStringByTimeout(chatKey + "_" + datetime,
								JSONObject.toJSONString(listChat.get(size - 1)), TimeoutConstant.CHAT_SESSION_NEW_TIMEOUT);
						// 缓存聊天列表
						if (!redisClientTemplate.exists(chatKey)) {
							redisClientTemplate.setListByTimeout(chatKey,
									(size > PAGE_NUM ? listChat.subList(size - PAGE_NUM, size) : listChat), TimeoutConstant.CHAT_SESSION_LIST_TIMEOUT);
						}
					} 
					// 如果数据库中没有相关聊天记录，则缓存一个空数据，以防不断的查询数据库
					else {
						ChatLog chatTest = new ChatLog();
						chatTest.setCompanyCode(companyCode);
						chatTest.setServer(server);
						chatTest.setClient(client);
						chatTest.setContent("");
						chatTest.setStatus(status);
						chatTest.setInitTime(System.currentTimeMillis());
						if (TypeConstant.CHAT_STARTER_1.equals(type)) {
							chatTest.setUpdateStarterCount(chatTest.getUpdateStarterCount() + 1);	// 表示当前操作人已经读取对方信息
							chatTest.setType(TypeConstant.CHAT_STARTER_0);
						} else if (TypeConstant.CHAT_STARTER_0.equals(type)) {
							chatTest.setUpdateReceptorCount(chatTest.getUpdateReceptorCount() + 1);
							chatTest.setType(TypeConstant.CHAT_STARTER_1);
						}
						// 缓存最后一条聊天记录
						redisClientTemplate.setStringByTimeout(chatKey + "_" + datetime,
								JSONObject.toJSONString(chatTest), TimeoutConstant.CHAT_SESSION_NEW_TIMEOUT);
					}
					
					if (TypeConstant.CHAT_STATUS_0.equals(status)) {
						// 更新对方状态为“已读”
						ChatLogVO chatTestUp = new ChatLogVO();
						chatTestUp.setCompanyCode(companyCode);
						chatTestUp.setServer(server);
						chatTestUp.setClient(client);
						chatTestUp.setType(TypeConstant.CHAT_STARTER_0.equals(type) ? TypeConstant.CHAT_STARTER_1: TypeConstant.CHAT_STARTER_0);
						chatService.updateReadStatus(chatTestUp);
					}
				}
			});
		} catch (Exception e) {
			logger.error("多线程保存日志异常：", e);
		}
	}


}
