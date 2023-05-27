package com.fangg.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.ChatGroup;
import com.fangg.bean.chat.query.UserChatGroup;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.ChatGroupVO;
import com.fangg.bean.chat.vo.UserChatGroupVO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.service.ChatGroupService;
import com.fangg.service.WebBaseService;
import com.fangg.util.UuidUtil;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.StringUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * 聊天群
 * @author fangg
 * 2022年3月3日 下午5:29:26
 */
@Controller
@RequestMapping("/chat")
public class ChatGroupController {
	private static Logger logger = LoggerFactory.getLogger(ChatGroupController.class);

	@Value("${aes.private.key}")
	private String aesPrivateKey;

	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	WebBaseService webBaseService;
	@Autowired
	ChatGroupService chatGroupService;
	
	@RequestMapping(value="/getChatGroupList", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getChatGroupList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody ChatGroupVO chatGroupVO) {
		if (chatGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		}
//		logger.info("查询聊天群列表入参：{}", chatGroupVO.toString());
		
		try {
			List<ChatGroup> resultList = chatGroupService.getChatGroupListByPage(chatGroupVO);
			if (resultList != null && resultList.size() > 0) {
				JSONArray jsonArray = new JSONArray();
				JSONObject jsonObject = null;
				for (ChatGroup chatGroup : resultList) {
					jsonObject = JSONObject.parseObject(JSON.toJSONString(chatGroup));
					jsonObject.put("groupNum", JSONArray.parseArray(chatGroup.getGroupMember()).size());
					jsonObject.put("groupMember", null);
					jsonArray.add(jsonObject);
				}
				return new ResultEntity(jsonArray);
			} else {
				resultList = new ArrayList<>();
			}
			return new ResultEntity(resultList);
		} catch (Exception e) {
			logger.error("查询聊天群列表异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "查询聊天群列表异常，请联系系统管理员。");
		}
	}
	
	@RequestMapping(value="/getUCGList", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getUCGList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody UserChatGroupVO userChatGroupVO) {
//		logger.info("查询用户聊天群列表入参：{}", JSONObject.toJSONString(userChatGroupVO));
		if (userChatGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(userChatGroupVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			if (userConfig == null || userChatGroupVO.getUserCode().equals(userConfig.getUserCode()) == false) {
				logger.warn("查询用户【{}】聊天群列表失败，登录用户信息获取失败", userChatGroupVO.getUserCode());
				return new ResultEntity(ResultParam.FAIlED);
			}
			
			List<UserChatGroup> resultList = chatGroupService.getUserChatGroupList(userChatGroupVO);
			if (resultList == null) {
				resultList = new ArrayList<>();
			}
			return new ResultEntity(resultList);
		} catch (Exception e) {
			logger.error("查询用户聊天群列表异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "查询用户聊天群列表异常，请联系系统管理员。");
		}
	}
	
	@RequestMapping(value="/postChatGroupInfo", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity postChatGroupInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody ChatGroupVO chatGroupVO) {
		logger.info("新增聊天群信息入参：chatGroupVO={}", JSONObject.toJSONString(chatGroupVO));
		if (chatGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(chatGroupVO.getChatGroupName())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "chatGroupName不能为空");
		}
		
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			if (userConfig == null) {
				logger.warn("新增【{}】聊天群信息失败，登录用户信息获取失败", chatGroupVO.getChatGroupName());
				return new ResultEntity(ResultParam.FAIlED);
			} else if (userConfig.getUserCode().equals(chatGroupVO.getUserCode()) == false) {
				logger.warn("新增【{}】聊天群信息失败，用户信息不匹配", chatGroupVO.getChatGroupName());
				return new ResultEntity(ResultParam.FAIlED);
			}
			
			JSONArray masterArr = new JSONArray();
			masterArr.add(userConfig.getUserCode());
			chatGroupVO.setApplyCode(userConfig.getUserCode());
			chatGroupVO.setGroupMaster(masterArr.toJSONString());
			chatGroupVO.setGroupMember(masterArr.toJSONString());
			chatGroupVO.setChatGroupCode("G"+UuidUtil.genUuid_0(8));
			chatGroupVO.setCreateTime(new Date());
			chatGroupVO.setGroupLimit(TypeConstant.CG_member_30);
			chatGroupVO.setGroupGrade(TypeConstant.CG_GRADE_1);
			chatGroupVO.setUsername(userConfig.getUsername());
			
			return chatGroupService.insertChatGroupInfo(chatGroupVO);
		} catch (Exception e) {
			logger.error("更新用户配置信息异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "更新用户配置信息异常，请联系系统管理员。");
		}
	}
	
	@RequestMapping(value="/postChatGroupApply", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity postChatGroupApply(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody ChatGroupVO chatGroupVO) {
		logger.info("申请加入聊天群入参：chatGroupVO={}", JSONObject.toJSONString(chatGroupVO));
		if (chatGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(chatGroupVO.getChatGroupCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "chatGroupCode不能为空");
		}
		
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			if (userConfig == null) {
				logger.warn("申请加入聊天群失败，登录用户信息获取失败", chatGroupVO.getChatGroupName());
				return new ResultEntity(ResultParam.FAIlED);
			} else if (userConfig.getUserCode().equals(chatGroupVO.getUserCode()) == false) {
				logger.warn("申请加入【{}】聊天群失败，用户信息不匹配", chatGroupVO.getChatGroupName());
				return new ResultEntity(ResultParam.FAIlED);
			}
			
			// 从缓存中判断
			ChatGroup groupCache = redisClientTemplate.getMapValue(RedisConstant.CHAT_GROUP_KEY, chatGroupVO.getChatGroupCode(), ChatGroup.class);
			if (groupCache != null && TypeConstant.JOIN_CG_CONDITION_2 == groupCache.getApplyCondition()) {
				logger.warn("【{}】申请加入【{}】聊天群失败，该群拒绝新人加入", chatGroupVO.getUserCode(), chatGroupVO.getChatGroupName());
				// 可能是恶意操作
				return new ResultEntity(ResultParam.FAIlED);
			}
			
			chatGroupVO.setApplyCode(userConfig.getUserCode());
			chatGroupVO.setUsername(userConfig.getUsername());
			return chatGroupService.joinChatGroup(chatGroupVO);
		} catch (Exception e) {
			logger.error("申请加入聊天群异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "申请加入聊天群异常，请联系系统管理员。");
		}
	}
	
	@RequestMapping(value="/leaveChatGroup", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity leaveChatGroup(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody ChatGroupVO chatGroupVO) {
		logger.info("退出聊天群入参：chatGroupVO={}", JSONObject.toJSONString(chatGroupVO));
		if (chatGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(chatGroupVO.getChatGroupCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "chatGroupCode不能为空");
		}
		
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			if (userConfig == null) {
				logger.warn("申请加入聊天群失败，登录用户信息获取失败", chatGroupVO.getChatGroupName());
				return new ResultEntity(ResultParam.FAIlED);
			}
			
			chatGroupVO.setApplyCode(userConfig.getUserCode());
			return chatGroupService.leaveChatGroup(chatGroupVO);
		} catch (Exception e) {
			logger.error("退出聊天群异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "退出聊天群异常，请联系系统管理员。");
		}
	}
	
	@RequestMapping(value="/getUserCGApplyList", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getUserCGApplyList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody ChatGroupVO chatGroupVO) {
//		logger.info("获取用户入群申请信息入参：userCode={}", chatGroupVO.getUserCode());
		if (chatGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(chatGroupVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			if (userConfig == null) {
				logger.warn("获取【{}】用户入群申请信息失败，登录用户信息获取失败", chatGroupVO.getUserCode());
				return new ResultEntity(ResultParam.FAIlED);
			} else if (userConfig.getUserCode().equals(chatGroupVO.getUserCode()) == false) {
				logger.warn("获取【{}】用户入群申请信息失败，用户信息不匹配", chatGroupVO.getUserCode());
				return new ResultEntity(ResultParam.FAIlED);
			}
			
			//logger.warn("获取用户入群申请信息失败，没有群申请信息", chatGroupVO.getChatGroupName());
			return chatGroupService.getCGApplyList(chatGroupVO);
		} catch (Exception e) {
			logger.error("获取用户入群申请信息异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "获取用户入群申请信息异常，请联系系统管理员。");
		}
	}
	
	@RequestMapping(value="/postCGApplyCheck", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity postCGApplyCheck(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody ChatGroupVO chatGroupVO) {
//		logger.info("入群审核入参：userCode={}", chatGroupVO.getUserCode());
		if (chatGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(chatGroupVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		} else if (StringUtil.isEmpty(chatGroupVO.getApplyCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "applyCode不能为空");
		} else if (StringUtil.isEmpty(chatGroupVO.getChatGroupCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "chatGroupCode不能为空");
		} else if (chatGroupVO.getCheckStatus() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "checkStatus不能为空");
		}
		
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			if (userConfig == null) {
				logger.warn("入群审核失败，登录用户信息获取失败", chatGroupVO.getUserCode());
				return new ResultEntity(ResultParam.FAIlED);
			} else if (userConfig.getUserCode().equals(chatGroupVO.getUserCode()) == false) {
				logger.warn("入群审核失败，用户信息不匹配", chatGroupVO.getUserCode());
				return new ResultEntity(ResultParam.FAIlED);
			}
			
			// 判断用户是否为群管理员
			ChatGroup groupOut = chatGroupService.getChatGroupInfo(chatGroupVO);
			if (groupOut != null 
					&& StringUtils.isNotEmpty(groupOut.getGroupMaster()) 
					&& groupOut.getGroupMaster().indexOf(chatGroupVO.getUserCode()) != -1) {
				return chatGroupService.checkCGApply(chatGroupVO);
			}
			
			// 该操作人没有审核权限（很可能是恶意操作）
			webBaseService.userAdd2BlackInfo(request, String.format("操作人没有群【%s】审核权限", chatGroupVO.getChatGroupCode()), userConfig);
			return new ResultEntity(ResultParam.FAIlED);
		} catch (Exception e) {
			logger.error("入群审核异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "入群审核异常，请联系系统管理员。");
		}
	}
	
	@RequestMapping(value="/getCGUserList", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getCGUserList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody UserChatGroupVO userChatGroupVO) {
//		logger.info("查询群用户列表入参：userCode={}", chatGroupVO.getUserCode());
		if (userChatGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(userChatGroupVO.getChatGroupCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "chatGroupCode不能为空");
		}
		
		try {
			JSONObject jsonObject = new JSONObject();
			List<String> ppList = new ArrayList<>();
			
			//logger.warn("获取用户入群申请信息失败，没有群申请信息", chatGroupVO.getChatGroupName());
			List<UserChatGroup> groupList = chatGroupService.getChatGroupUserList(userChatGroupVO);
			if (groupList == null) {
				groupList = new ArrayList<>();
			} else {
				int size = groupList.size();
				String [] fields = new String[size];
				for (int i = 0; i < size; i++) {
					fields[i] = groupList.get(i).getUserCode();
				}
				
				// 用户头像列表
				if (fields.length > 0) {
					ppList = redisClientTemplate.getMapValue(RedisConstant.UG_PP_KEY, fields);
				}
			}
			
			
			jsonObject.put("cguList", groupList);
			jsonObject.put("ppList", ppList);
			return new ResultEntity(jsonObject);
		} catch (Exception e) {
			logger.error("查询群用户列表异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "查询群用户列表异常，请联系系统管理员。");
		}
	}
	
	@RequestMapping(value="/patchCGMsgStatus", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchCGMsgStatus(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody ChatGroupVO chatGroupVO) {
		logger.info("更新群用户消息状态入参：{}", JSONObject.toJSONString(chatGroupVO));
		if (chatGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(chatGroupVO.getChatGroupCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "chatGroupCode不能为空");
		} else if (StringUtil.isEmpty(chatGroupVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		} else if (chatGroupVO.getMsgStatus() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "msgStatus不能为空");
		}
		
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			if (userConfig == null) {
				logger.warn("更新【{}】群用户消息状态失败，登录用户信息获取失败", chatGroupVO.getChatGroupCode());
				return new ResultEntity(ResultParam.FAIlED);
			}
			
			return chatGroupService.updateCGMsgStatus(chatGroupVO);
		} catch (Exception e) {
			logger.error("更新群用户消息状态异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "更新群用户消息状态异常，请联系系统管理员。");
		}
	}

	
}
