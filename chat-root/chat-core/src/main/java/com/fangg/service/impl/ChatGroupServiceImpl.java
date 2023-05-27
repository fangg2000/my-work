package com.fangg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.ChatGroup;
import com.fangg.bean.chat.query.UserChatGroup;
import com.fangg.bean.chat.vo.ChatGroupVO;
import com.fangg.bean.chat.vo.UserChatGroupVO;
import com.fangg.config.redis.RedisDbTemplate;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.dao.ChatGroupMapper;
import com.fangg.dao.UserChatGroupMapper;
import com.fangg.exception.ChatException;
import com.fangg.service.ChatGroupService;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;
import com.xclj.tk.service.impl.BaseServiceImpl;

@Service(value="chatGroupService")
public class ChatGroupServiceImpl extends BaseServiceImpl<ChatGroup> implements ChatGroupService {

    private static final Logger logger = LoggerFactory.getLogger(ChatGroupServiceImpl.class);

    @Autowired
	RedisDbTemplate redisDbTemplate;
    
    @Autowired
    ChatGroupMapper chatGroupMapper;
    @Autowired
    UserChatGroupMapper userChatGroupMapper;

	/**
	 * 批量新增群信息 
	 */
	public ResultEntity insertChatGroupByBatch(List<ChatGroupVO> chatGroupVOList) {
		int result = chatGroupMapper.insertChatGroupByBatch(chatGroupVOList);
		if (result > 0 ) {
			logger.info("批量新增群信息成功，数量：{}", result);
			return new ResultEntity();
		}

		logger.info("批量新增群信息失败");
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 新增群信息 
	 */
	@Transactional(rollbackFor = ChatException.class)
	public ResultEntity insertChatGroupInfo(ChatGroupVO chatGroupVO) {
		logger.info("新增群信息入参：{}", JSONObject.toJSONString(chatGroupVO));
		
		List<ChatGroupVO> chatGroupVOList = new ArrayList<>();
		chatGroupVOList.add(chatGroupVO);
		
		ResultEntity resultEntity = insertChatGroupByBatch(chatGroupVOList);
		if (resultEntity.getCode() == ResultParam.SUCCESS.getCode()) {
			// 用户与群关联信息新增
			ucgInfoNew(chatGroupVO, true);
			
			// 缓存群信息
			ChatGroup groupIn = new ChatGroup();
			groupIn.setChatGroupCode(chatGroupVO.getChatGroupCode());
			ChatGroup groupOut = chatGroupMapper.selectOne(groupIn);
			if (groupOut != null) {
				redisDbTemplate.setMapValue(RedisConstant.CHAT_GROUP_KEY, groupOut.getChatGroupCode(), 
						JSONObject.toJSONString(groupOut));
			}

			// 缓存群头像路径
			if (StringUtils.isNotEmpty(chatGroupVO.getProfilePicture())) {
				redisDbTemplate.setMapValue(RedisConstant.UG_PP_KEY, chatGroupVO.getChatGroupCode(), chatGroupVO.getProfilePicture());
			}
			
			resultEntity.setData(chatGroupVO.getChatGroupCode());
		}
		
		return resultEntity;
	}

	/**
	 * 用户与群关联信息新增
	 */
	private void ucgInfoNew(ChatGroupVO chatGroupVO, boolean masterFlag) {
		UserChatGroup ucgNew = new UserChatGroup();
		ucgNew.setUserCode(chatGroupVO.getApplyCode());
		ucgNew.setChatGroupCode(chatGroupVO.getChatGroupCode());
		ucgNew.setChatGroupName(chatGroupVO.getChatGroupName());
		ucgNew.setMsgStatus(TypeConstant.UCG_MSG_STATUS_1);
		ucgNew.setCreateTime(new Date());
		ucgNew.setUsername(chatGroupVO.getUsername());
		ucgNew.setIdentity(masterFlag?TypeConstant.CG_IDENTITY_3:TypeConstant.CG_IDENTITY_0);
		ucgNew.setUserType(null);
		
		int result = userChatGroupMapper.insertSelective(ucgNew);
		if (result > 0) {
			logger.info("用户【{}】与群【{}】关联信息新增成功", ucgNew.getUserCode(), ucgNew.getChatGroupName());
			
			List<UserChatGroup> ucgList = null;
			Object ucgInfo = redisDbTemplate.getMapValue(RedisConstant.USER_CG_LIST_KEY, 
					chatGroupVO.getApplyCode());
			if (ucgInfo != null) {
				ucgList = JSONArray.parseArray(String.valueOf(ucgInfo), UserChatGroup.class);
			} else {
				ucgList = new ArrayList<>();
			}
			
			ucgList.add(ucgNew);
			
			// 缓存用户群信息
			redisDbTemplate.setMapValue(RedisConstant.USER_CG_LIST_KEY, ucgNew.getUserCode(), JSONArray.toJSONString(ucgList));
			
			// 缓存群用户信息
			List<UserChatGroup> resultList = null;
			Object groupStr = redisDbTemplate.getMapValue(RedisConstant.CG_USER_LIST_KEY, chatGroupVO.getChatGroupCode());
			if (groupStr != null) {
				resultList = JSONArray.parseArray(String.valueOf(groupStr), UserChatGroup.class);
			} else {
				resultList = new ArrayList<>();
			}
			resultList.add(ucgNew);
			redisDbTemplate.setMapValue(RedisConstant.CG_USER_LIST_KEY, chatGroupVO.getChatGroupCode(), JSONArray.toJSONString(resultList));
		} else {
			throw new ChatException("新增用户与群关联信息失败");
		}
	}

	/**
	 * 入群申请 
	 */
	public ResultEntity joinChatGroup(ChatGroupVO chatGroupVO) {
		if (StringUtils.isEmpty(chatGroupVO.getChatGroupCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数验证失败");
		}
		
		ChatGroup chatGroupOut = null;

		// 从缓存中判断
		ChatGroup groupCache = redisDbTemplate.getMapValue(RedisConstant.CHAT_GROUP_KEY, chatGroupVO.getChatGroupCode(), ChatGroup.class);
		
		if (groupCache != null) {
			chatGroupOut = groupCache;
		} else {
			// 判断申请条件
			ChatGroup chatGroupIn = new ChatGroup();
			chatGroupIn.setChatGroupCode(chatGroupVO.getChatGroupCode());
			chatGroupOut = chatGroupMapper.selectOne(chatGroupIn);
		}
		
		if (chatGroupOut == null || TypeConstant.JOIN_CG_CONDITION_2 == chatGroupOut.getApplyCondition()) {
			logger.warn("【{}】申请入群【{}】失败，该群拒绝新人加入", chatGroupVO.getApplyCode(), chatGroupVO.getChatGroupCode());
			// 可能是恶意操作
			return new ResultEntity(ResultParam.FAIlED);
		}
		
		// 自由加入
		if (TypeConstant.JOIN_CG_CONDITION_0 == chatGroupOut.getApplyCondition()) {
			List<String> memberArr = JSONArray.parseArray(chatGroupOut.getGroupMember(), String.class);
			if (memberArr != null) {
				if (memberArr.contains(chatGroupVO.getApplyCode()) == false) {
					memberArr.add(chatGroupVO.getApplyCode());
				} else {
					logger.warn("【{}】申请入群【{}】失败，用户已经存在", chatGroupVO.getApplyCode(), chatGroupVO.getChatGroupCode());
					return new ResultEntity(ResultParam.FAIlED);
				}
			} else {
				memberArr = new ArrayList<>();
				memberArr.add(chatGroupVO.getApplyCode());
			}
			
			if (memberArr.size() <= chatGroupOut.getGroupLimit()) {
				ChatGroup chatGroupUp = new ChatGroup();
				chatGroupUp.setChatGroupId(chatGroupOut.getChatGroupId());
				chatGroupUp.setGroupMember(JSONArray.toJSONString(memberArr));
				int result = chatGroupMapper.updateByPrimaryKeySelective(chatGroupUp);
				if (result > 0) {
					// 新增用户与群关联信息
					chatGroupVO.setChatGroupName(chatGroupOut.getChatGroupName());
					ucgInfoNew(chatGroupVO, false);
					
					chatGroupOut.setGroupMember(chatGroupUp.getGroupMember());
					redisDbTemplate.setMapValue(RedisConstant.CHAT_GROUP_KEY, chatGroupVO.getChatGroupCode(), 
							JSONObject.toJSONString(chatGroupOut));
				}
				
				chatGroupOut.setGroupApply(null);
				return new ResultEntity(chatGroupOut);
			}

			logger.warn("【{}】申请入群【{}】失败，人员已满", chatGroupVO.getApplyCode(), chatGroupVO.getChatGroupCode());
		}
		// 审核
		else if (TypeConstant.JOIN_CG_CONDITION_1 == chatGroupOut.getApplyCondition()) {
			// 保存申请信息
			JSONObject applyJson = new JSONObject();
			applyJson.put("gc", chatGroupOut.getChatGroupCode());
			applyJson.put("uc", chatGroupVO.getApplyCode());
			applyJson.put("de", chatGroupVO.getApplyDescript());
			applyJson.put("un", chatGroupVO.getUsername());
			
			JSONArray applyArr = JSONArray.parseArray(chatGroupOut.getGroupApply());
			if (applyArr != null) {
				JSONObject aiJson = null;
				for (int i = 0,size=applyArr.size(); i < size; i++) {
					aiJson = applyArr.getJSONObject(i);
					if (aiJson.getString("uc").equals(chatGroupVO.getApplyCode())) {
						logger.warn("【{}】申请入群【{}】失败，无需重复申请", chatGroupVO.getApplyCode(), chatGroupVO.getChatGroupCode());
						return new ResultEntity(ResultParam.FAIlED, "无需重复申请");
					}
				}
			} else {
				applyArr = new JSONArray();
			}
			
			applyArr.add(applyJson);
			
			// 缓存提醒信息(群管理员都会收到申请信息提醒)
			ChatGroup chatGroupUp = new ChatGroup();
			chatGroupUp.setChatGroupId(chatGroupOut.getChatGroupId());
			chatGroupUp.setGroupApply(applyArr.toJSONString());
			int result = chatGroupMapper.updateByPrimaryKeySelective(chatGroupUp);
			if (result > 0) {
				List<String> masterList = JSONArray.parseArray(chatGroupOut.getGroupMaster(), String.class);
				Map<String, Object> map = new HashMap<>();
				List<Object> codeList = new ArrayList<>();
				for (String userCode : masterList) {
					map.put(userCode, applyArr.toJSONString());
					codeList.add(userCode);
				}
				
				// 如果管理员有别的群申请信息
				List<Object> objectList = redisDbTemplate.getMapValue(RedisConstant.CG_APPLY_REMIND_KEY, codeList);
				if (objectList != null && objectList.size() > 0) {
					JSONArray cArray = null;
					for (int i = 0,size=objectList.size(); i < size; i++) {
						if (objectList.get(i) != null) {
							cArray = JSONArray.parseArray(String.valueOf(objectList.get(i)));
							cArray.addAll(applyArr);
							redisDbTemplate.setMapValue(RedisConstant.CG_APPLY_REMIND_KEY, String.valueOf(codeList.get(i)), cArray.toJSONString());
							map.remove(String.valueOf(codeList.get(i)));
						}
					}
				}
				
				if (map.isEmpty() == false) {
					redisDbTemplate.setMap(RedisConstant.CG_APPLY_REMIND_KEY, map);
				}
				
				// 缓存群信息
				chatGroupOut.setGroupApply(applyArr.toJSONString());
				redisDbTemplate.setMapValue(RedisConstant.CHAT_GROUP_KEY, chatGroupVO.getChatGroupCode(), 
						JSONObject.toJSONString(chatGroupOut));
				return new ResultEntity();
			}
			
			logger.warn("【{}】申请入群【{}】失败，保存申请信息失败", chatGroupVO.getApplyCode(), chatGroupVO.getChatGroupCode());
		}
		// 拒绝人员加入
		else if (TypeConstant.JOIN_CG_CONDITION_2 == chatGroupOut.getApplyCondition()) {
			// 应该在前端已经判断，这里可以理解为恶意操作
			logger.warn("【{}】申请入群【{}】失败，拒绝人员加入", chatGroupVO.getApplyCode(), chatGroupVO.getChatGroupCode());
		}
		
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 退出群
	 */
	public ResultEntity leaveChatGroup(ChatGroupVO chatGroupVO) {
		logger.warn("【{}】退出群【{}】", chatGroupVO.getApplyCode(), chatGroupVO.getChatGroupCode());
		
		ChatGroup groupCache = redisDbTemplate.getMapValue(RedisConstant.CHAT_GROUP_KEY, chatGroupVO.getChatGroupCode(), ChatGroup.class);
		if (groupCache != null) {
			List<String> memberList = JSONArray.parseArray(groupCache.getGroupMember(), String.class);
			if (memberList.contains(chatGroupVO.getApplyCode())) {
				memberList.remove(chatGroupVO.getApplyCode());
				groupCache.setGroupMember(JSONArray.toJSONString(memberList));
				
			}
			
			List<String> masterList = JSONArray.parseArray(groupCache.getGroupMaster(), String.class);
			if (masterList.contains(chatGroupVO.getApplyCode())) {
				masterList.remove(chatGroupVO.getApplyCode());
				groupCache.setGroupMaster(JSONArray.toJSONString(masterList));
			}
			
			ChatGroup chatGroupUp = new ChatGroup();
			chatGroupUp.setChatGroupId(groupCache.getChatGroupId());
			chatGroupUp.setGroupMaster(groupCache.getGroupMaster());
			chatGroupUp.setGroupMember(groupCache.getGroupMember());
			int result = chatGroupMapper.updateByPrimaryKeySelective(chatGroupUp);
			if (result > 0) {
				// 从聊天群中删除用户
				redisDbTemplate.setMapValue(RedisConstant.CHAT_GROUP_KEY, chatGroupVO.getChatGroupCode(), 
						JSONObject.toJSONString(groupCache));
				
				// 用户群关联中删除用户
				List<UserChatGroup> ucgList = null;
				Object ucgInfo = redisDbTemplate.getMapValue(RedisConstant.USER_CG_LIST_KEY, chatGroupVO.getApplyCode());
				if (ucgInfo != null) {
					ucgList = JSONArray.parseArray(String.valueOf(ucgInfo), UserChatGroup.class);
					UserChatGroup userChatGroup = null;
					for (int i = 0,size=ucgList.size(); i < size; i++) {
						userChatGroup = ucgList.get(i);
						if (userChatGroup.getChatGroupCode().equals(chatGroupVO.getChatGroupCode()) 
								&& userChatGroup.getUserCode().equals(chatGroupVO.getApplyCode())) {
							
							UserChatGroupVO ucgDel = new UserChatGroupVO();
							ucgDel.setUserCode(userChatGroup.getUserCode());
							ucgDel.setChatGroupCode(userChatGroup.getChatGroupCode());
							result = userChatGroupMapper.deleteUCG(ucgDel);
							if (result > 0) {
								ucgList.remove(i);
								
								// 从缓存用户群信息中删除
								redisDbTemplate.setMapValue(RedisConstant.USER_CG_LIST_KEY, chatGroupVO.getApplyCode(), 
										JSONArray.toJSONString(ucgList));
							}
							
							break;
						}
					}
				}
				
				// 从群用户列表缓存中删除
				List<UserChatGroup> ucGroupList = null;
				Object groupStr = redisDbTemplate.getMapValue(RedisConstant.CG_USER_LIST_KEY, chatGroupVO.getChatGroupCode());
				if (groupStr != null) {
					ucGroupList = JSONArray.parseArray(String.valueOf(groupStr), UserChatGroup.class);
					for (int i = 0,size=ucGroupList.size(); i < size; i++) {
						if (ucGroupList.get(i).getUserCode().equals(chatGroupVO.getApplyCode())) {
							ucGroupList.remove(i);
							redisDbTemplate.setMapValue(RedisConstant.CG_USER_LIST_KEY, chatGroupVO.getChatGroupCode(), JSONArray.toJSONString(ucGroupList));
							break;
						}
					}
				}
				
				return new ResultEntity();
			}
		}
		
		logger.warn("【{}】退出群【{}】失败", chatGroupVO.getApplyCode(), chatGroupVO.getChatGroupCode());
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 用户群列表 
	 */
	public List<UserChatGroup> getUserChatGroupList(UserChatGroupVO userChatGroupVO) {
		List<UserChatGroup> resultList = new ArrayList<>();
		Object groupStr = redisDbTemplate.getMapValue(RedisConstant.USER_CG_LIST_KEY, userChatGroupVO.getUserCode());
		List<ChatGroup> cgList = null;
				
		if (groupStr != null) {
			resultList = JSONArray.parseArray(String.valueOf(groupStr), UserChatGroup.class);
			
			List<Object> codeList = new ArrayList<>();
			for (UserChatGroup userChatGroup : resultList) {
				codeList.add(userChatGroup.getChatGroupCode());
			}
			
			// 从缓存中取群信息
			cgList = redisDbTemplate.getMapValue(RedisConstant.CHAT_GROUP_KEY, codeList, ChatGroup.class);
		} 
		// 如果redis连接失败，则查询数据库
		else if (redisDbTemplate.isValidConnect() == false) {
			UserChatGroup groupIn = new UserChatGroup();
			groupIn.setUserCode(userChatGroupVO.getUserCode());
			resultList = userChatGroupMapper.select(groupIn);
			
			// 缓存用户与群列表
			//redisDbTemplate.setMapValue(RedisConstant.USER_CG_LIST_KEY, userChatGroupVO.getUserCode(), JSONArray.toJSONString(resultList));
			
			// 从数据库中取群信息
			List<String> codeList = new ArrayList<>();
			if (resultList != null && resultList.size() > 0) {
				for (UserChatGroup userChatGroup : resultList) {
					codeList.add(userChatGroup.getChatGroupCode());
				}
				cgList = chatGroupMapper.selectChatGroupByIn(codeList);
			}
		}
		
		if (cgList != null && cgList.size() > 0) {
			for (ChatGroup chatGroup : cgList) {
				if (chatGroup != null) {
					for (UserChatGroup userChatGroup : resultList) {
						if (userChatGroup.getChatGroupCode().equals(chatGroup.getChatGroupCode())) {
							userChatGroup.setGroupSign(chatGroup.getGroupSign());
							userChatGroup.setProfilePicture(chatGroup.getProfilePicture());
							break;
						}
					}
				}
			}
		}
		
		return resultList;
	}

	/**
	 * 群列表 
	 */
	public List<ChatGroup> getChatGroupListByPage(ChatGroupVO chatGroupVO) {
		List<ChatGroup> resultList = chatGroupMapper.selectChatGroupByPage(chatGroupVO);
		return resultList;
	}

	/**
	 * 查询群信息
	 */
	public ChatGroup getChatGroupInfo(ChatGroupVO chatGroupVO) {
		ChatGroup groupCache = redisDbTemplate.getMapValue(RedisConstant.CHAT_GROUP_KEY, chatGroupVO.getChatGroupCode(), ChatGroup.class);
		if (groupCache != null) {
			return groupCache;
		} else if (redisDbTemplate.isValidConnect() == false) {
			ChatGroup chatGroupIn = new ChatGroup();
			chatGroupIn.setChatGroupCode(chatGroupVO.getChatGroupCode());
			return chatGroupMapper.selectOne(chatGroupIn);
		}
		
		return null;
	}

	/**
	 * 查询入群申请信息列表 
	 */
	public ResultEntity getCGApplyList(ChatGroupVO chatGroupVO) {
		// 群申请信息(前提是你为某群的管理员)
		Object applyObj = redisDbTemplate.getMapValue(RedisConstant.CG_APPLY_REMIND_KEY, chatGroupVO.getUserCode());
		if (applyObj != null) {
			// 申请信息列表
			JSONArray resultArr = new JSONArray();
			JSONArray applyArr = JSONArray.parseArray(String.valueOf(applyObj));
			JSONObject applyJson = null;
			int size = applyArr.size();
			List<Object> fieldList = new ArrayList<>();
			
			for (int i = 0; i < size; i++) {
				applyJson = JSONObject.parseObject(String.valueOf(applyArr.get(i)));
				fieldList.add(applyJson.getString("gc"));
			}
			
			List<ChatGroup> cgList = redisDbTemplate.getMapValue(RedisConstant.CHAT_GROUP_KEY, fieldList, ChatGroup.class);
			boolean hideFlag = false;
			
			// 判断信息是否已经审核
			if (cgList != null && cgList.size() > 0) {
				for (int i = 0; i < size; i++) {
					applyJson = JSONObject.parseObject(String.valueOf(applyArr.get(i)));
					hideFlag = false;
					
					// 待申请信息里面还有用户的申请信息
					for (ChatGroup chatGroup : cgList) {
						if (chatGroup != null 
								&& chatGroup.getChatGroupCode().equals(applyJson.getString("gc")) 
								&& StringUtils.isNotEmpty(chatGroup.getGroupApply()) 
								&& chatGroup.getGroupApply().indexOf(applyJson.getString("uc")) != -1) {
							hideFlag = true;
							applyJson.put("gi", chatGroup.getChatGroupId());
							applyJson.put("gn", chatGroup.getChatGroupName());
							break;
						}
					}
					
					if (hideFlag) {
						resultArr.add(applyJson);
					}
				}
			}
			
			// 缓存没有审核的申请信息(否则那些已经被审核过的信息无法删除)
			if (resultArr.size() > 0) {
				if (resultArr.size() != applyArr.size()) {
					redisDbTemplate.setMapValue(RedisConstant.CG_APPLY_REMIND_KEY, chatGroupVO.getUserCode(), JSONArray.toJSONString(resultArr));
				}
			} else {
				// 删除缓存
				redisDbTemplate.delMapValue(RedisConstant.CG_APPLY_REMIND_KEY, chatGroupVO.getUserCode());
			}
			
			return new ResultEntity(resultArr);
		} 
		// redis连接失败时
		/*else if (redisDbTemplate.isValidConnect() == false) {
			
		}*/
		
		return new ResultEntity();
	}

	/**
	 * 入群申请审核 
	 */
	public ResultEntity checkCGApply(ChatGroupVO chatGroupVO) {
		// 审核通过
		if (TypeConstant.CG_APPLY_CHECK_1 == chatGroupVO.getCheckStatus()) {
			// 群申请信息列表，删除申请信息
			Object applyObj = redisDbTemplate.getMapValue(RedisConstant.CG_APPLY_REMIND_KEY, chatGroupVO.getUserCode());
			if (applyObj != null) {
				// 申请信息列表
				JSONArray resultArr = new JSONArray();
				JSONArray applyArr = JSONArray.parseArray(String.valueOf(applyObj));
				JSONObject applyJson = null;
				int size = applyArr.size();
				
				for (int i = 0; i < size; i++) {
					applyJson = JSONObject.parseObject(String.valueOf(applyArr.get(i)));
					if (chatGroupVO.getChatGroupCode().equals(applyJson.getString("gc"))) {
						// 从群信息中删除对应申请信息
						delCGApplyInfo(applyJson.getString("uc"), applyJson.getString("gc"), applyJson.getString("un"), true);
					} else {
						resultArr.add(applyJson);
					}
				}
				
				// 缓存其他没有审核的结果
				if (resultArr.size() > 0) {
					redisDbTemplate.setMapValue(RedisConstant.CG_APPLY_REMIND_KEY, chatGroupVO.getUserCode(), JSONArray.toJSONString(resultArr));
				} else {
					// 删除缓存
					redisDbTemplate.delMapValue(RedisConstant.CG_APPLY_REMIND_KEY, chatGroupVO.getUserCode());
				}
			} else {
				logger.warn("【{}】入群申请审核失败，申请信息获取失败", chatGroupVO.getUserCode());
				return new ResultEntity(ResultParam.FAIlED);
			}
		} 
		// 审核拒绝时
		else {
			// 从群信息中删除对应申请信息
			delCGApplyInfo(chatGroupVO.getUserCode(), chatGroupVO.getChatGroupCode(), chatGroupVO.getUsername(), false);
		}
		
		return new ResultEntity();
	}

	/**
	 * 从群信息中删除对应申请信息
	 */
	private void delCGApplyInfo(String userCode, String chatGroupCode, String username, boolean status) {
		ChatGroup chatGroup = redisDbTemplate.getMapValue(RedisConstant.CHAT_GROUP_KEY, chatGroupCode, ChatGroup.class);
		if (chatGroup != null 
				&& StringUtils.isNotEmpty(chatGroup.getGroupApply()) 
				&& chatGroup.getGroupApply().indexOf(userCode) != -1) {
			JSONArray applyArr = JSONArray.parseArray(chatGroup.getGroupApply());
			JSONArray resultArr = new JSONArray();
			JSONObject applyJson = null;
			int size = applyArr.size();
			
			for (int i = 0; i < size; i++) {
				applyJson = JSONObject.parseObject(String.valueOf(applyArr.get(i)));
				if (userCode.equals(applyJson.getString("uc")) == false) {
					// 从群信息中删除对应申请信息
					resultArr.add(applyJson);
				}
			}
			
			// 缓存其他没有审核的结果
			ChatGroup chatGroupUp = new ChatGroup();
			chatGroupUp.setChatGroupId(chatGroup.getChatGroupId());
			
			if (resultArr.size() > 0) {
				chatGroup.setGroupApply(resultArr.toJSONString());
				chatGroupUp.setGroupApply(resultArr.toJSONString());
			} else {
				chatGroup.setGroupApply("");
				chatGroupUp.setGroupApply("");
			}
			
			JSONArray memberArr = JSONArray.parseArray(chatGroup.getGroupMember());
			memberArr.add(userCode);
			chatGroupUp.setGroupMember(memberArr.toJSONString());
			
			int result = chatGroupMapper.updateByPrimaryKeySelective(chatGroupUp);
			if (result > 0) {
				// 审核通过时，新增用户与群关联信息
				if (status) {
					ChatGroupVO chatGroupVONew = new ChatGroupVO();
					chatGroupVONew.setApplyCode(userCode);
					chatGroupVONew.setChatGroupCode(chatGroupCode);
					chatGroupVONew.setChatGroupName(chatGroup.getChatGroupName());
					chatGroupVONew.setUsername(username);
					ucgInfoNew(chatGroupVONew, false);
				}
				
				// 缓存群结果
				redisDbTemplate.setMapValue(RedisConstant.CHAT_GROUP_KEY, chatGroupCode, JSONObject.toJSONString(chatGroup));
			} 
		}
	}

	/**
	 * 群用户列表 
	 */
	public List<UserChatGroup> getChatGroupUserList(UserChatGroupVO userChatGroupVO) {
		List<UserChatGroup> resultList = new ArrayList<>();
		Object groupStr = redisDbTemplate.getMapValue(RedisConstant.CG_USER_LIST_KEY, userChatGroupVO.getChatGroupCode());
		if (groupStr != null) {
			resultList = JSONArray.parseArray(String.valueOf(groupStr), UserChatGroup.class);
		} 
		// 如果redis连接失败，则查询数据库
		else if (redisDbTemplate.isValidConnect() == false) {
			UserChatGroup groupIn = new UserChatGroup();
			groupIn.setChatGroupCode(userChatGroupVO.getChatGroupCode());
			resultList = userChatGroupMapper.select(groupIn);
			
			// 缓存用户与群列表
			//redisDbTemplate.setMapValue(RedisConstant.CG_USER_LIST_KEY, userChatGroupVO.getChatGroupCode(), JSONArray.toJSONString(resultList));
		}
		
		return resultList;
	}

	/**
	 * 更新群消息状态 (禁止/接收群消息)
	 */
	public ResultEntity updateCGMsgStatus(ChatGroupVO chatGroupVO) {
		List<UserChatGroup> resultList = null;
		Object groupStr = redisDbTemplate.getMapValue(RedisConstant.USER_CG_LIST_KEY, chatGroupVO.getUserCode());
		if (groupStr != null) {
			resultList = JSONArray.parseArray(String.valueOf(groupStr), UserChatGroup.class);
			UserChatGroupVO userChatGroupUp = null;
			for (UserChatGroup userChatGroup : resultList) {
				if (userChatGroup.getChatGroupCode().equals(chatGroupVO.getChatGroupCode())) {
					userChatGroupUp = new UserChatGroupVO();
					userChatGroupUp.setUserCode(chatGroupVO.getUserCode());
					userChatGroupUp.setChatGroupCode(chatGroupVO.getChatGroupCode());
					userChatGroupUp.setChatGroupName(userChatGroup.getChatGroupName());
					
					if (chatGroupVO.getMsgStatus() == TypeConstant.UCG_MSG_STATUS_0) {
						userChatGroup.setMsgStatus(TypeConstant.UCG_MSG_STATUS_0);
						userChatGroupUp.setMsgStatus(TypeConstant.UCG_MSG_STATUS_0);
					} else if (chatGroupVO.getMsgStatus() == TypeConstant.UCG_MSG_STATUS_1) {
						userChatGroup.setMsgStatus(TypeConstant.UCG_MSG_STATUS_1);
						userChatGroupUp.setMsgStatus(TypeConstant.UCG_MSG_STATUS_1);
					}
					break;
				}
			}
			
			// 更新群用户信息
			int result = userChatGroupMapper.updateCGMsgStatus(userChatGroupUp);
			if (result > 0) {
				logger.info("【{}】{}【{}】群消息成功", chatGroupVO.getUserCode(), 
						(chatGroupVO.getMsgStatus() == TypeConstant.UCG_MSG_STATUS_0?"禁止":"接收"), 
						userChatGroupUp.getChatGroupName());
				
				// 缓存群用户信息
				redisDbTemplate.setMapValue(RedisConstant.USER_CG_LIST_KEY, chatGroupVO.getUserCode(), 
						JSONArray.toJSONString(resultList));
				return new ResultEntity();
			} else {
				logger.warn("【{}】禁止/接收群消息更新失败", chatGroupVO.getUserCode());
			}
		} else {
			logger.warn("【{}】禁止/接收群消息失败，redis获取群用户信息失败", chatGroupVO.getUserCode());
		}
		
		return new ResultEntity(ResultParam.FAIlED);
	}
	
}