package com.fangg.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.ChatLog;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.ChatLogVO;
import com.fangg.bean.chat.vo.UserCompanyVO;
import com.fangg.config.sharingshpere.SnowFlakeUtils;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.dao.ChatLogMapper;
import com.fangg.dao.SysUserMapper;
import com.fangg.service.ChatLogService;
import com.fangg.service.CompanyInfoService;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.StringUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

@Service(value="chatLogService")
public class ChatLogServiceImpl implements ChatLogService {

    private static final Logger logger = LoggerFactory.getLogger(ChatLogServiceImpl.class);

	@Autowired
	RedisClientTemplate redisClientTemplate;
	
    @Autowired
    private ChatLogMapper chatLogMapper;
    @Autowired
    SysUserMapper sysUserMapper;
	@Autowired
	CompanyInfoService companyInfoService;
    

	/** 
	 * 新增聊天记录 
	 * **/
	public ResultEntity insertChatLog(ChatLogVO chatLogVO) {
		//logger.info("新增聊天记录入参：{}", JSONObject.toJSONString(chatLogVO));
    	ResultEntity resultEntity = new ResultEntity();
    	
    	try {
    		chatLogVO.setChatId(SnowFlakeUtils.getTableIdByRedis("chat_log", redisClientTemplate));
			int result = chatLogMapper.insertChatLog(chatLogVO);
			if (result > 0) {
				//logger.info("{}新增聊天记录成功", chatLogVO.getCompanyCode());
				
				// 如果是与客服聊天，则保存用户聊天所在门店
				if (TypeConstant.SYSTEM_CODE.equals(chatLogVO.getCompanyCode()) == false) {
					String resultStr = redisClientTemplate.getMapValue(RedisConstant.USER_CHAT_CUSTOMER_KEY, chatLogVO.getClient());
					JSONArray jsonArray = null;
					if (resultStr != null) {
						jsonArray = JSONArray.parseArray(resultStr);
						
						// 保证最近聊天的门店在列表最后(以防以后业务需要)
						if (jsonArray.contains(chatLogVO.getCompanyCode())) {
							if (jsonArray.lastIndexOf(chatLogVO.getCompanyCode()) == jsonArray.size() -1) {
								// 如果最后一个正是当前聊天门店，则不需要更新
								return resultEntity;
							}
							jsonArray.remove(chatLogVO.getCompanyCode());
						}
						jsonArray.add(chatLogVO.getCompanyCode());
					} else {
						jsonArray = new JSONArray();
						jsonArray.add(chatLogVO.getCompanyCode());
					}
					redisClientTemplate.setMapValue(RedisConstant.USER_CHAT_CUSTOMER_KEY, chatLogVO.getClient(), jsonArray.toJSONString());
				}
			} else {
				logger.warn("{}新增聊天记录失败", chatLogVO.getCompanyCode());
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} catch (Exception e) {
			logger.error("新增聊天记录异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "新增聊天记录异常，请联系系统管理员。");
		}
    	
    	return resultEntity;
	}

    /** 
     * 批量新增聊天日志
     * **/
    public ResultEntity insertChatLogByBatch(List<ChatLogVO> chatLogVOList) {
    	//logger.info("聊天日志批量新增入参数量：{}", chatLogVOList.size());
    	ResultEntity resultEntity = new ResultEntity();
    	
    	try {
    		if (chatLogVOList != null && chatLogVOList.size() > 0) {
    			for (ChatLogVO chatLogVO : chatLogVOList) {
					if (StringUtil.isEmpty(chatLogVO.getChatId())) {
			    		chatLogVO.setChatId(SnowFlakeUtils.getTableIdByRedis("chat_log", redisClientTemplate));
					}
				}
    			
    			int result = chatLogMapper.insertChatLogByBatch(chatLogVOList);
    			if (result > 0) {
    				//logger.info("聊天日志批量新增成功数量：{}", result);
    			} else {
    				logger.warn("聊天日志批量新增失败");
    				resultEntity.setResultBody(ResultParam.FAIlED);
    			}
			}
		} catch (Exception e) {
			logger.error("聊天日志批量新增异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "聊天日志批量新增异常，请联系系统管理员。");
		}
    	
    	return resultEntity;
    }

	/**
	 * 查看更多
	 */
	public List<ChatLog> getChatMoreList(ChatLogVO chatLogVO) {
		List<ChatLog> listChat = chatLogMapper.listChatMore(chatLogVO);
		if (listChat != null && listChat.size() > 0) {
			if (chatLogVO.getChatKey() != null) {
				int size = listChat.size();
				String [] temp = new String[size];
				for (int i = 0; i < size; i++) {
					temp[i] = JSONObject.toJSONString(listChat.get(i));
				}
				
				// 更新缓存列表
				redisClientTemplate.setListValueFrom0ByTimeout(chatLogVO.getChatKey(), temp, TimeoutConstant.CHAT_SESSION_LIST_TIMEOUT);
			}
			
			// 升序
			Collections.sort(listChat, new Comparator<ChatLog>() {
				@Override
				public int compare(ChatLog o1, ChatLog o2) {
					return o1.getChatId().compareTo(o2.getChatId());
				}
	        });
			
			return listChat;
		}
		return new ArrayList<>();
	}

	/**
	 * 更新状态
	 */
	public ResultEntity updateReadStatus(ChatLogVO chatLogVO) {
		//logger.info("聊天更新状态入参：{}", JSONObject.toJSONString(chatLogVO));
    	ResultEntity resultEntity = new ResultEntity();
		try {
			int result = chatLogMapper.updateReadStatus(chatLogVO);
			if (result > 0) {
				//logger.info("{}聊天状态更新成功，数量：{}", chatLogVO.getCompanyCode(), result);
			} else {
				//logger.warn("{}聊天状态更新失败", chatLogVO.getCompanyCode());
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} catch (Exception e) {
			logger.error("聊天状态更新异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "聊天状态更新异常，请联系系统管理员。");
		}
		return resultEntity;
	}

	/**
	 * 查询客服所属下未读的用户(可根据客服优先排序) 
	 */
	public List<UserCompanyTO> getU4CListByChatNotRead(ChatLogVO chatLogVO) {
		return chatLogMapper.selectUserListByChatNotRead(chatLogVO);
	}

	/**
	 * 查询用户未读的客服列表 
	 */
	public List<UserCompanyTO> getC4UListByChatNotRead(ChatLogVO chatLogVO) {
		// 多线程查询用户聊天门店客服列表(改为sharingspere分表查询)
		//unReadCustomer(chatLogVO, resultList);
		return chatLogMapper.selectCustomerListByChatNotRead(chatLogVO);
	}

	/**
	 * 查询未读的客服列表
	 */
	/*private void unReadCustomer(ChatLogVO chatLogVO, List<UserCompanyTO> resultList) {
		String resultStr = redisClientTemplate.getMapValue(RedisConstant.USER_CHAT_CUSTOMER_KEY, chatLogVO.getClient());
		if (resultStr != null) {
			JSONArray companyArr = JSONArray.parseArray(resultStr);
			//List<Callable<List<UserCompanyTO>>> ncList = new ArrayList<Callable<List<UserCompanyTO>>>();
            Callable<List<UserCompanyTO>> callableNC = null;
            ChatLogVO chatLogVONew = null;
            
            // 注入线程池
            ExecutorCompletionService<List<UserCompanyTO>> completionService = new ExecutorCompletionService<List<UserCompanyTO>>(taskExecutor);
            
            for (Object object : companyArr) {
				chatLogVONew = new ChatLogVO();
				chatLogVONew.setCompanyCode(object.toString());
				chatLogVONew.setClient(chatLogVO.getClient());
				callableNC = new CustomerNotReadCallable(chatLogMapper, chatLogVONew);
				completionService.submit(callableNC);
			}
            
            try {
            	List<UserCompanyTO> userCompanyList = null;
            	for (int i = 0,size=companyArr.size(); i < size; i++) {
					userCompanyList = completionService.take().get();
	            	if (userCompanyList != null) {
	            		resultList.addAll(userCompanyList);
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				logger.error("多线程查询用户聊天门店客服列表异常：", e);
			}
		}
	}*/

	/**
	 * 查询用户未读的新用户列表 
	 */
	public List<UserCompanyTO> getNewUserListByChatNotRead(ChatLogVO chatLogVO) {
		return chatLogMapper.selectNewUserListByChatNotRead(chatLogVO);
	}

	/**
	 * 查询用户未读的老用户列表 
	 */
	public List<UserCompanyTO> getOldUserListByChatNotRead(ChatLogVO chatLogVO) {
		chatLogVO.setServer(chatLogVO.getClient());
		return chatLogMapper.selectOldUserListByChatNotRead(chatLogVO);
	}

	/**
	 * 查询用户未读的关注用户列表 
	 */
	public List<UserCompanyTO> getFocusUserListByChatNotRead(ChatLogVO chatLogVO) {
		// 查询未读的用户列表(关注用户优先，因为聊天记录表7个和关注用户表3个，都是分表7*3=21，相当于调用数据库21次，这个以后可能会出大问题，需要分开处理)
		//chatLogMapper.selectFocusUserListByChatNotRead(chatLogVO);
		
		// 先查询未读的用户code
		List<UserCompanyTO> noReadList = chatLogMapper.selectChatLogListByChatNotRead(chatLogVO);
		
		// 判断关注用户
		
		
		// 查询关注用户信息
		
		return null;
	}

	/**
	 * 查询用户未读的(客服、关注用户、新用户、老用户)列表 
	 */
	public List<UserCompanyTO> getUserListByChatNotRead(ChatLogVO chatLogVO) {
		//logger.info("查询用户未读的(客服、关注用户、新用户、老用户)列表入参：{}", JSONObject.toJSONString(chatLogVO));

		/*
		List<UserCompanyTO> resultList = new ArrayList<>();
		// 用户未读的客服列表 
		checkNRUni(resultList, getC4UListByChatNotRead(chatLogVO));
		//logger.info("用户未读的客服列表 --{}", JSONArray.toJSONString(resultList));
		// 用户未读的新用户列表 
		checkNRUni(resultList, getNewUserListByChatNotRead(chatLogVO));
		//logger.info("用户未读的新用户列表 --{}", JSONArray.toJSONString(resultList));
		// 用户未读的老用户列表 
		checkNRUni(resultList, getOldUserListByChatNotRead(chatLogVO));
		//logger.info("用户未读的老用户列表 --{}", JSONArray.toJSONString(resultList));
		// 用户未读的关注用户列表 
		checkNRUni(resultList, getFocusUserListByChatNotRead(chatLogVO));
		SELECT t1.user_id,
		t1.user_code,
		t1.username,
		t1.profile_picture,
		t1.user_sign,
		t1.user_type as userType,
		*/

		// 先查询未读的用户code(客服、关注用户、新用户、老用户)
		List<UserCompanyTO> noReadList = chatLogMapper.selectChatLogListByChatNotRead(chatLogVO);
		
		if (noReadList != null && noReadList.size() > 0) {
			// 排除黑名单用户
			String blackStr = redisClientTemplate.getMapValue(RedisConstant.BLACK_USER_KEY, chatLogVO.getClient());
			if (StringUtils.isNotEmpty(blackStr)) {
				JSONArray bArray = JSONArray.parseArray(blackStr);
				List<UserCompanyTO> cacheList = new ArrayList<>();
				
				for (UserCompanyTO userCompanyTO : noReadList) {
					if (bArray.contains(userCompanyTO.getUserCode())) {
						cacheList.add(userCompanyTO);
					}
				}
				noReadList.removeAll(cacheList);
				
				if (noReadList.size() == 0) {
					return noReadList;
				}
			}
			
			// 取userCode集合（启动报 java.io.IOException: invalid constant type: 18, 因为使用了lambda表达式）
			List<String> codeList = noReadList.stream().map(UserCompanyTO::getUserCode).collect(Collectors.toList());
//			List<String> codeList = new ArrayList<>();
//			for (UserCompanyTO ucTO : noReadList) {
//				codeList.add(ucTO.getUserCode());
//			}
			
			List<UserConfigTO> userList = sysUserMapper.selectUserListByIn(codeList);
			
			for (UserCompanyTO ucTO : noReadList) {
				for (UserConfigTO userTO : userList) {
					if (ucTO.getUserCode().equals(userTO.getUserCode())) {
						ucTO.setUserId(userTO.getUserId());
						ucTO.setUsername(userTO.getUsername());
						ucTO.setCompanyCode(chectUCCode(userTO));
						ucTO.setProfilePicture(userTO.getProfilePicture());
						ucTO.setUserSign(userTO.getUserSign());
						ucTO.setUserType(userTO.getUserType());
						// 设置为可联系（如果对方禁止陌生人联系且对方没有关注用户，则无法再联系）
						ucTO.setStrangerContact(TypeConstant.STRANGER_CONTACT_0);
						// 是否显示泡泡球
						ucTO.setShowBall(userTO.getShowBall());
						// 泡泡球显示时长
						ucTO.setBallShowSeconds(userTO.getBallShowSeconds());
						break;
					}
				}
			}
		}
		
		logger.info("用户未读的用户列表 --{}", JSONArray.toJSONString(noReadList));
		return noReadList;
	}

	private String chectUCCode(UserConfigTO userConfig) {
		if (userConfig.getUserType() == TypeConstant.USER_TYPE_1) {
			return TypeConstant.SYSTEM_CODE;
		} 
		
		UserCompanyVO userCompanyVOIn = new UserCompanyVO();
		userCompanyVOIn.setUserCode(userConfig.getUserCode());
		UserCompanyTO userCompanyTO = companyInfoService.getCustomerServiceCompanyInfo(userCompanyVOIn);
		if (userCompanyTO != null) {
			return userCompanyTO.getCompanyCode();
		}
		
		return null;
	}

	/**
	 * 去掉重复用户判断
	 */
	/*private void checkNRUni(List<UserCompanyTO> resultList, List<UserCompanyTO> inList) {
		boolean flag = false;
		if (inList != null && inList.size() > 0) {
			for (UserCompanyTO ucTO : inList) {
				flag = false;
				for (UserCompanyTO reTo : resultList) {
					if (reTo.getUserCode().equals(ucTO.getUserCode())) {
						flag = true;
						break;
					}
				}
				if (flag == false) {
					resultList.add(ucTO);
				}
			}
		}
	}*/
    
}