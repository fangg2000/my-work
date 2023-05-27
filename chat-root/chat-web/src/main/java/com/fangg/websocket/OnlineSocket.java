package com.fangg.websocket;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnError;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.BeforeHandshake;
import org.yeauty.annotation.OnBinary;
import org.yeauty.annotation.OnClose;
import org.yeauty.annotation.OnEvent;
import org.yeauty.annotation.OnOpen;
import org.yeauty.annotation.PathVariable;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.Session;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.ChatLog;
import com.fangg.bean.chat.query.SysUser;
import com.fangg.bean.chat.query.UserChatGroup;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.SysUserVO;
import com.fangg.bean.chat.vo.UserCompanyVO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.service.CompanyInfoService;
import com.fangg.service.FocusUsersService;
import com.fangg.service.SysUserService;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.AESUtil;
import com.xclj.common.util.StringUtil;
import com.xclj.replay.ResultEntity;

import io.netty.handler.codec.http.HttpHeaders;

/**
 * 用户登录建立连接，关闭或退出网页关闭连接
 * @author fangg
 * 2022年1月11日 上午7:47:50
 */
@ServerEndpoint(path = "/chat/onlineSocket/{userCode}/{ticket}/{fingerPrint}", 
	host = "${netty.websocket.host}", 
	port = "${netty.websocket.port}", 
	readerIdleTimeSeconds = "30")
@Component
public class OnlineSocket {
	private static final Logger logger = LoggerFactory.getLogger(OnlineSocket.class);

	@Value("${rsa.private.key}")
	String rsaPrivateKey;
	@Value("${aes.ticket.key}")
	private String aesTicketKey;

	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ChatSocketService chatSocketService;
	@Autowired
	SysUserService sysUserService;
	@Autowired
	CompanyInfoService companyInfoService;
	@Autowired
	FocusUsersService focusUsersService;

	/**
	 * 当有新的连接进入时
	 */
	@BeforeHandshake
	public void handshake(Session session, HttpHeaders headers, @PathVariable String userCode, @PathVariable String ticket, 
			@PathVariable String fingerPrint) {
		logger.info("【{}】开启onlineSocket连接", userCode);

		try {
			String ticketStr = AESUtil.decrypt(ticket.replaceAll("\\*", "/"), aesTicketKey);
			JSONObject ticketJson = JSONObject.parseObject(ticketStr);
			if (ticketJson == null || ticketJson.isEmpty()) {
				logger.warn("onlineSocket连接，ticket验证不通过--{}", ticket);
				if (session.isOpen()) {
					session.close();
				}
				return;
			}
			
			if (ticketJson.getString("f").equals(fingerPrint) == false) {
				logger.warn("onlineSocket连接，指纹验证不通过--{}", fingerPrint);
				if (session.isOpen()) {
					session.close();
				}
			}

			// 判断绑定信息（IP或标签）
			
		} catch (Exception e) {
			logger.error("onlineSocket连接验证异常：", fingerPrint, e.getMessage());
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * WebSocket连接完成时，对该方法进行回调 
	 */
	@OnOpen
    public void onOpen(Session session, HttpHeaders headers, @PathVariable String userCode, 
    		@PathVariable String ticket) throws IOException {
		logger.info("onlineSocket连接打开--userCode={},ticket={}", userCode, ticket);
		
		try {
			String ticketStr = AESUtil.decrypt(ticket.replaceAll("\\*", "/"), aesTicketKey);
			JSONObject ticketJson = JSONObject.parseObject(ticketStr);
			if (ticketJson == null || ticketJson.isEmpty()) {
				logger.warn("WebSocket连接打开之前执行，ticket验证不通过--{}", ticket);
				if (session.isOpen()) {
					session.close();
				}
				return;
			}
			
			// 真实ticket
			String real_ticket = ticketJson.getString("t");
			
			session.setAttribute("user_code_ticket", userCode+","+real_ticket);
			ResultEntity resultEntity = new ResultEntity();
			resultEntity.setMsg(String.format("【%s】用户连接成功了", userCode));
			resultEntity.setData(new ArrayList<>());
			session.sendText(JSONObject.toJSONString(resultEntity));
			
			SysUserVO sysUserVOIn = new SysUserVO();
			sysUserVOIn.setUserCode(userCode);
			// 这ticket是查询条件
			sysUserVOIn.setTicket(real_ticket);
			sysUserVOIn.setOnlineStatus(TypeConstant.ONLINE_STATUS_1);
			sysUserService.loginOut(sysUserVOIn);
		} catch (Exception e) {
			logger.error("更新【{}】用户在线状态异常：", userCode, e);
			if (session.isOpen()) {
				session.close();
			}
		}
    }

	/**
	 * 当有WebSocket连接关闭时，对该方法进行回调 注入参数的类型:Session
	 *
	 * @param session
	 * @throws IOException
	 */
	@OnClose
	public void onClose(Session session) throws Exception {
		String sessionStr = session.getAttribute("user_code_ticket");
		logger.warn("onlineSocket连接{}关闭", sessionStr);
		
		// 更新用户登录状态
		if (StringUtil.isNotEmpty(sessionStr)) {
			String [] temp = sessionStr.split(",");

			/*String ticketStr = AESUtil.decrypt(temp[1], aesTicketKey);
			JSONObject ticketJson = JSONObject.parseObject(ticketStr);
			if (ticketJson == null || ticketJson.isEmpty()) {
				logger.warn("【{}】onlineSocket连接关闭，ticket验证不通过--{}", temp[0], temp[1]);
			}
			
			// 真实ticket
			String real_ticket = ticketJson.getString("t");*/
			
			SysUserVO sysUserVOIn = new SysUserVO();
			sysUserVOIn.setUserCode(temp[0]);
			sysUserVOIn.setTicket(temp[1]);
			sysUserVOIn.setOnlineStatus(TypeConstant.ONLINE_STATUS_0);
			sysUserService.loginOut(sysUserVOIn);
		}
	}

	/**
	 * 当有WebSocket抛出异常时，对该方法进行回调 注入参数的类型:Session、Throwable
	 *
	 * @param session
	 * @param throwable
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		throwable.printStackTrace();
		
		//throw new ChatException(ResultParam.FAILED_WEBSOCKET);
	}

	/**
	 * 接收到字符串消息时，对该方法进行回调 注入参数的类型:Session、String
	 *
	 * @param session
	 * @param message
	 */
	@org.yeauty.annotation.OnMessage
	public void OnMessage(Session session, String message) {
//		logger.info("onlineSocket接收到消息：{}", message);
		try {
			String sessionStr = session.getAttribute("user_code_ticket");
			String [] temp = sessionStr.split(",");
			JSONObject jsonObject = JSONObject.parseObject(message);
			if (jsonObject != null && temp[0].equals(jsonObject.getString("userCode"))) {
				String userCode = temp[0];
				String ticket = temp[1];
				// 新用户列表
				List<UserConfigTO> listUC = newContactUserCheck(jsonObject, userCode, ticket);
				
				// 判断上线用户
				List<String> listOC = onlineUserCheck(jsonObject, userCode, ticket);
				
				// 判断下线用户
				List<String> listNC = notOnlineUserCheck(jsonObject, userCode, ticket);
				
				// 群信息
				List<Object> listUG = ucgCheck(jsonObject, userCode, ticket);
				
				ResultEntity resultEntity = new ResultEntity();
				JSONObject resultJson = new JSONObject();
				resultJson.put("listUC", listUC);
				resultJson.put("listOC", listOC);
				resultJson.put("listNC", listNC);
				resultJson.put("listUG", listUG);
				resultEntity.setData(resultJson);
				session.sendText(JSONArray.toJSONString(resultEntity));
			}
		} catch (Exception e) {
			logger.error("onlineSocket接收信息并推送数据异常：", e);
		}
	}

	/**
	 * 群新消息判断
	 */
	private List<Object> ucgCheck(JSONObject jsonObject, String userCode, String ticket) {
		List<Object> resultList = new ArrayList<>();
		// 获取用户群列表
		String ucgInfo = redisClientTemplate.getMapValue(RedisConstant.USER_CG_LIST_KEY, userCode);
		
		if (ucgInfo != null) {
			List<UserChatGroup> ucgList = null;
			ucgList = JSONArray.parseArray(ucgInfo, UserChatGroup.class);
			String infoStr = null;
			String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dMMy"));
			JSONObject logJson = null;
			ChatLog chatLog = null;
			
			// 用户开启聊天窗口的群
			int size = ucgList.size();
			JSONArray gcodeArr = jsonObject.getJSONArray("gcList");
			/*String [] codeTemp = new String[size];
			for (int i = 0; i < size; i++) {
				codeTemp[i] = ucgList.get(i).getUserCode();
			}
			// 群头像列表
			List<String> cgPpList = redisClientTemplate.getMapValue(RedisConstant.UG_PP_KEY, codeTemp);*/
			UserChatGroup ucGroup = null;
			//ChatGroup chatGroup = null;
			long nowTime = System.currentTimeMillis()/1000, initTime = 0;
			
			
			for (int i = 0; i < size; i++) {
				ucGroup = ucgList.get(i);
				if (TypeConstant.UCG_MSG_STATUS_1 == ucGroup.getMsgStatus() 
						&& gcodeArr.contains(ucGroup.getChatGroupCode()) == false) {
					infoStr = redisClientTemplate.getString(RedisConstant.U2G_PREFIX_KEY + ucGroup.getChatGroupCode() + "_" + datetime);
					if (infoStr != null) {
						//chatGroup = redisClientTemplate.getMapValue(RedisConstant.CHAT_GROUP_KEY, ucGroup.getChatGroupCode(), ChatGroup.class);
						chatLog = JSONObject.parseObject(infoStr, ChatLog.class);
						
						// 判断消息时间是否在30秒内（这个时间应该小于websocket调用时间）
						initTime = chatLog.getInitTime() / 1000;
						if ((nowTime - initTime) < 30) {
							logJson = new JSONObject();
							logJson.put("cc", chatLog.getServer());
							logJson.put("cl", chatLog.getClient());
							logJson.put("gn", ucGroup.getChatGroupName());
							logJson.put("os", TypeConstant.ONLINE_STATUS_1);
							logJson.put("co", chatLog.getContent());
							logJson.put("it", chatLog.getInitTime());
							logJson.put("ut", TypeConstant.USER_TYPE_2);
							
//							if (chatGroup != null) {
//								logJson.put("pp", chatGroup.getProfilePicture());
//								logJson.put("gs", chatGroup.getGroupSign());
//							}
							// 这里应该是用户的头像
							String pp = redisClientTemplate.getMapValue(RedisConstant.UG_PP_KEY, chatLog.getClient());
							logJson.put("pp", StringUtils.isNotEmpty(pp)?pp:"");
							resultList.add(logJson);
						}
					}
				}
			}
		}
		
		return resultList;
	}

	/**
	 * 上线判断
	 */
	private List<String> onlineUserCheck(JSONObject jsonObject, String userCode, String ticket) {
		List<String> result = new ArrayList<>();
		try {
			JSONObject codeJson = jsonObject.getJSONObject("mapCode");
			if (codeJson != null) {
				JSONArray arrCode = codeJson.getJSONArray("noList");
				if (arrCode != null && arrCode.isEmpty() == false) {
					String [] temp = arrCode.toArray(new String[]{});
					List<String> listStr = redisClientTemplate.getMapValue(RedisConstant.USER_ONLINE_KEY, temp);
					if (listStr != null) {
						for (int i = 0,size = listStr.size(); i < size; i++) {
							if (listStr.get(i) != null) {
								result.add(temp[i]);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("onlineSocket判断在线用户异常：", e);
		}
		return result;
	}
	
	/**
	 * 下线判断
	 */
	private List<String> notOnlineUserCheck(JSONObject jsonObject, String userCode, String ticket) {
		List<String> result = new ArrayList<>();
		try {
			JSONObject codeJson = jsonObject.getJSONObject("mapCode");
			if (codeJson != null) {
				JSONArray arrCode = codeJson.getJSONArray("onList");
				if (arrCode != null && arrCode.isEmpty() == false) {
					String [] temp = arrCode.toArray(new String[]{});
					List<String> listStr = redisClientTemplate.getMapValue(RedisConstant.USER_ONLINE_KEY, temp);
					if (listStr != null) {
						for (int i = 0,size = listStr.size(); i < size; i++) {
							if (listStr.get(i) == null) {
								result.add(temp[i]);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("onlineSocket判断下线用户异常：", e);
		}
		return result;
	}

	/**
	 * 判断新用户消息
	 */
	private List<UserConfigTO> newContactUserCheck(JSONObject jsonObject, String userCode, String ticket) {
		List<UserConfigTO> resultList = new ArrayList<>();
		UserConfigTO userConfig = null;
		
		// 新用户消息
		String newContactStr = redisClientTemplate.getMapValue(RedisConstant.NEW_USER_CHAT_KEY, userCode);
		if (newContactStr != null) {

			// 不为空表示有新用户联系消息
			List<String> listCon = JSONArray.parseArray(newContactStr, String.class);
			List<String> cacheList = null;
			
			String configStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+ticket);
			if (configStr != null) {
				userConfig = JSONObject.parseObject(configStr, UserConfigTO.class);
			}
			
			
			// 禁止陌生人消息
			if (userConfig == null) {
				// 清空缓存
				redisClientTemplate.delMapValue(RedisConstant.NEW_USER_CHAT_KEY, userCode);
				return resultList;
			} 
			
			// 判断黑名单(不接收在黑名单中的用户消息)
			String blackStr = redisClientTemplate.getMapValue(RedisConstant.BLACK_USER_KEY, userConfig.getUserCode());
			if (StringUtils.isNotEmpty(blackStr)) {
				JSONArray bArray = JSONArray.parseArray(blackStr);
				cacheList = new ArrayList<>();
				
				for (String code : listCon) {
					if (bArray.contains(code)) {
						cacheList.add(code);
					}
				}
				listCon.removeAll(cacheList);
				
				if (listCon.size() == 0) {
					// 清空缓存
					redisClientTemplate.delMapValue(RedisConstant.NEW_USER_CHAT_KEY, userCode);
					return resultList;
				}
			}
			
			// 如果是关注用户则提示
			if (userConfig.getStrangerContact() == TypeConstant.STRANGER_CONTACT_1) {
				// 判断是否为关注用户
				String [] fields = new String[listCon.size()];
				cacheList = new ArrayList<>();
				int num = 0;
				
				for (String code : listCon) {
//					String focusUserStr = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, userConfig.getUserCode()+"_"+code);
//					System.out.println("关注缓存--"+focusUserStr);
					fields[num] = userConfig.getUserCode()+"_"+code;
					num ++;
				}
				
				// String focusUserStr = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, fields);
				List<String> resultCode = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, fields);
				if (resultCode != null && resultCode.size() > 0) {
					for (int i = 0,size=resultCode.size(); i < size; i++) {
						if (resultCode.get(i) != null) {
							cacheList.add(listCon.get(i));
						}
					}
				}
				
				if (cacheList.size() > 0) {
					listCon = new ArrayList<>();
					listCon.addAll(cacheList);
				} else {
					// 清空缓存
					redisClientTemplate.delMapValue(RedisConstant.NEW_USER_CHAT_KEY, userCode);
					return resultList;
				}
			}
			
			int userType = jsonObject.getIntValue("userType");
			SysUser sysUser = null;
			String companyCode = null, chatKey = null;
			SysUser sysUserIn = new SysUser();
			UserConfigTO userCO = null;
			Map<String, String> chatMap = null;
			Map<String, String> scMap = null;
			String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dMMy"));
			
			for (String code : listCon) {
				sysUserIn.setUserCode(code);
				sysUser = sysUserService.selectOne(sysUserIn);
				if (sysUser != null) {
					scMap = new HashMap<>();
					companyCode = checkCompanyCode(sysUser, userType, userCode, scMap);
					if (companyCode != null) {
						chatMap = new HashMap<>();
						chatKey = chatSocketService.backChatKey(companyCode, scMap.get("server"), scMap.get("client"), 
								TypeConstant.CHAT_STARTER_1, chatMap);
						
						// 最新的用户消息
						String chatStr = redisClientTemplate.getString(chatKey + "_" + datetime);
						if (chatStr != null) {
							ChatLog chatLog = JSONObject.parseObject(chatStr, ChatLog.class);
							userCO = new UserConfigTO();
							userCO.setUserId(sysUser.getUserId());
							userCO.setUserCode(code);
							//userCO.setCompanyCode(chectUCCode(sysUser));
							userCO.setCompanyCode(chatLog.getCompanyCode());
							userCO.setUsername(sysUser.getUsername());
							userCO.setUserSign(sysUser.getUserSign());
							userCO.setOnlineStatus(sysUser.getOnlineStatus());
							userCO.setContent(chatLog.getContent());
							userCO.setUserType(sysUser.getUserType());
							userCO.setProfilePicture(sysUser.getProfilePicture());
							userCO.setInitTime(chatLog.getInitTime());
							//userCO.setChatId(chatLog.getChatId());
							// 头像动作类型(取的是当前操作人的配置，前端从用户信息中取)
							//userCO.setPpAvtiveType(userConfig.getPpAvtiveType());
							resultList.add(userCO);
						}
					}
				}
			}
			
			// 清空缓存
			redisClientTemplate.delMapValue(RedisConstant.NEW_USER_CHAT_KEY, userCode);
		}
		
		return resultList;
	}

	private String chectUCCode(SysUser sysUser) {
		if (sysUser.getUserType() == TypeConstant.USER_TYPE_1) {
			return TypeConstant.SYSTEM_CODE;
		} 
		
		UserCompanyVO userCompanyVOIn = new UserCompanyVO();
		userCompanyVOIn.setUserCode(sysUser.getUserCode());
		UserCompanyTO userCompanyTO = companyInfoService.getCustomerServiceCompanyInfo(userCompanyVOIn);
		if (userCompanyTO != null) {
			return userCompanyTO.getCompanyCode();
		}
		
		return null;
	}

	private String checkCompanyCode(SysUser sysUser, int userType, String sendOneCode, Map<String, String> scMap) {
		if (sysUser.getUserType() != null) {
			// 用户占多数，放前面判断
			if (TypeConstant.USER_TYPE_1 == userType) {
				if (sysUser.getUserType() == userType) {
					scMap.put("server", sysUser.getUserCode());
					scMap.put("client", sendOneCode);
					return TypeConstant.SYSTEM_CODE;
				} else if (TypeConstant.USER_TYPE_0 == sysUser.getUserType()) {
					UserCompanyVO userCompanyVOIn = new UserCompanyVO();
					userCompanyVOIn.setUserCode(sysUser.getUserCode());
					UserCompanyTO userCompanyTO = companyInfoService.getCustomerServiceCompanyInfo(userCompanyVOIn);
					if (userCompanyTO != null) {
						scMap.put("server", sysUser.getUserCode());
						scMap.put("client", sendOneCode);
						return userCompanyTO.getCompanyCode();
					}
				}
			} 
			// 如果当前登录人是客服
			else if (TypeConstant.USER_TYPE_0 == userType) {
				UserCompanyVO userCompanyVOIn = new UserCompanyVO();
				userCompanyVOIn.setUserCode(sendOneCode);
				UserCompanyTO userCompanyTO = companyInfoService.getCustomerServiceCompanyInfo(userCompanyVOIn);
				if (userCompanyTO != null) {
					scMap.put("server", sendOneCode);
					scMap.put("client", sysUser.getUserCode());
					return userCompanyTO.getCompanyCode();
				}
			}
		}
		return null;
	}

	/**
	 * 当接收到二进制消息时，对该方法进行回调 注入参数的类型:Session、byte[]
	 *
	 * @param session
	 * @param bytes
	 */
	@OnBinary
	public void onBinary(Session session, byte[] bytes) {
		/*for (byte b : bytes) {
			logger.debug("==========>>>>>>>>>>>{},", b);
		}*/
		session.sendBinary(bytes);
	}

	/**
	 * 当接收到Netty的事件时，对该方法进行回调 注入参数的类型:Session、Object
	 *
	 * @param session
	 * @param evt
	 */
	@OnEvent
	public void onEvent(Session session, Object evt) {
		logger.debug("==netty心跳事件===evt=>>>>{},来自===userId:{}", JSONObject.toJSONString(evt), session.channel().id());
		
	}

}
