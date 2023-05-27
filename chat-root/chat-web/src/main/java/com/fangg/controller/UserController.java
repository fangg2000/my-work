package com.fangg.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.alibaba.fastjson.JSONObject;
import com.fangg.annotation.Decrypt;
import com.fangg.annotation.Encrypt;
import com.fangg.bean.chat.query.PermissionFunc;
import com.fangg.bean.chat.query.SysUser;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.ChatLogVO;
import com.fangg.bean.chat.vo.FocusUsersVO;
import com.fangg.bean.chat.vo.SysUserVO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.service.ChatLogService;
import com.fangg.service.CompanyInfoService;
import com.fangg.service.FocusUsersService;
import com.fangg.service.SysUserService;
import com.fangg.service.UserConfigService;
import com.fangg.service.WebBaseService;
import com.fangg.util.HttpUtil;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.AESUtil;
import com.xclj.common.util.StringUtil;
import com.xclj.page.PageEntity;
import com.xclj.page.PageResult;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * 用户信息
 * @author fangg
 * 2021年12月29日 下午6:07:09
 */
@Controller
public class UserController {
	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Value("${aes.private.key}")
	private String aesPrivateKey;
	@Value("${aes.ticket.key}")
	private String aesTicketKey;

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
	@Autowired
	FocusUsersService focusUsersService;
	@Autowired
	UserConfigService userConfigService;
	@Autowired
	WebBaseService webBaseService;

	@Encrypt()
	@Decrypt(value="RSA")
	@RequestMapping(value="/user/getUserInfo", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getUserInfo(HttpServletRequest request, HttpServletResponse response, @RequestBody SysUserVO sysUserVO) throws Exception {
		logger.info("查询用户信息入参：ticket={}, loginIdentifier={}", request.getAttribute("ticket"), sysUserVO.getLoginIdentifier());
		logger.info("查询用户信息入参：{}", JSONObject.toJSONString(sysUserVO));
		
		if (StringUtil.isEmpty(sysUserVO.getFingerPrint())) {
			return new ResultEntity(ResultParam.LOGIN_UNKNOW);
		}
				
		ResultEntity resultEntity = new ResultEntity();
		String ticket = request.getAttribute("ticket").toString();

		//String ipAddr = HttpUtil.getIpAddress(request);
		// 0-web/1-mobile/2-pad
		int loginType = HttpUtil.fromMoblie(request);

		/*String ideStr = RSAUtil.decryptByDefault(sysUserVO.getLoginIdentifier());
		JSONObject ideJson = JSONObject.parseObject(ideStr);
		if (ideJson == null) {
			logger.warn("{}查询用户信息，loginIdentifier解密失败--{}", ticket, sysUserVO.getLoginIdentifier());
			return new ResultEntity(ResultParam.LOGIN_UNKNOW);
		}*/

		try {
			// 给表头加上aesKey(方便返回值加密，此处覆盖拦截器那里定义的other)
			response.setHeader("Other", AESUtil.encrypt(sysUserVO.getAesKey(), aesTicketKey));
			
			String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+ticket);
			if (userStr != null) {
				JSONObject jsonObject = JSONObject.parseObject(userStr);
				
				// 不同类型设备登录（手机，Pad，WEB）
				JSONObject ideJson = jsonObject.getJSONObject("loginIdentifierMap");
				
				if (ideJson.containsKey(String.valueOf(loginType)) == false 
						|| ideJson.getString(String.valueOf(loginType)).equals(sysUserVO.getLoginIdentifier()+"_"+sysUserVO.getFingerPrint()) == false) {
					logger.warn("查询用户信息，登录异常");
					return new ResultEntity(ResultParam.LOGIN_UNKNOW);
				}
				
				// 前端刷新页面aes key已经发生变化，则需要更新缓存
				jsonObject.put("aesKey", sysUserVO.getAesKey());
				
				// 更新登录用户最后时间
				// 缓存登录用户信息时间更新
				redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+ticket, 
						jsonObject.toJSONString(), TimeoutConstant.LOGIN_TIMEOUT);

				// 返回前端的ticket要经过AES加密(这里附加一些验证信息)
				JSONObject ticketJson = new JSONObject();
				// 密文过长，这里使用简写
				ticketJson.put("t", ticket);
				ticketJson.put("f", sysUserVO.getFingerPrint());
				ticketJson.put("a", jsonObject.getString("aesKey"));
				jsonObject.put("ticket", AESUtil.encrypt(ticketJson.toJSONString(), aesTicketKey));

				//jsonObject.put("funcPermissionInside", null);
				jsonObject.remove("funcPermission4Server");
				jsonObject.remove("loginIdentifierMap");
				jsonObject.remove("checkIdentifier");
				jsonObject.remove("loginIp");
				jsonObject.remove("checkTimeout");
				jsonObject.remove("aesKey");
				jsonObject.remove("fingerPrint");
				jsonObject.remove("bindIp");
				resultEntity.setData(jsonObject);
			} else {
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return resultEntity;
	}

	/**
	 * 查询用户未读信息
	 */
	@RequestMapping(value="/user/getNotReadUserList", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getNotReadUserList(HttpServletRequest request, HttpServletResponse response, @RequestBody SysUserVO sysUserVO) {
		//logger.info("查询未读的用户列表入参:userCode={}, companyCode={}", userCode, companyCode);
		
		if (sysUserVO == null || StringUtil.isEmpty(sysUserVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		ResultEntity resultEntity = new ResultEntity();
		
		try {
			String resultStr = redisClientTemplate.getMapValue(RedisConstant.CUSTOMER_SERVICE_KEY, sysUserVO.getUserCode());
			
			// 判断是否为客服
			if (resultStr != null) {
				UserCompanyTO userCompanyTOOut = JSONObject.parseObject(resultStr, UserCompanyTO.class);
				
				// 获取未读的用户列表
				ChatLogVO chatLogVOIn = new ChatLogVO();
				chatLogVOIn.setCompanyCode(userCompanyTOOut.getCompanyCode());
				chatLogVOIn.setServer(sysUserVO.getUserCode());
				chatLogVOIn.setType(String.valueOf(TypeConstant.USER_TYPE_1));
				List<UserCompanyTO> userList = chatService.getU4CListByChatNotRead(chatLogVOIn);
				resultEntity.setData(userList);
			} else {
				// 查询未读的客服列表、关注用户列表、推荐用户列表
				ChatLogVO chatLogVOIn = new ChatLogVO();
				chatLogVOIn.setUserId(sysUserVO.getUserId());
				chatLogVOIn.setClient(sysUserVO.getUserCode());
				// 查询用户未读的(客服、关注用户、新用户、老用户)列表 
				List<UserCompanyTO> userList = chatService.getUserListByChatNotRead(chatLogVOIn);
				resultEntity.setData(userList);
			}
		} catch (Exception e) {
			logger.error("查询未读的用户列表异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "查询未读的用户列表异常，请联系管理员。");
		}
		return resultEntity;
	}
	
	/**
	 * 判断通话用户发送类型(0为接收者，1为发送者，默认客服为接收者)
	 */
	@RequestMapping(value="/user/getSendType", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getSendType(HttpServletRequest request, HttpServletResponse response, @RequestBody ChatLogVO chatLogVO) {
		//logger.info("判断通话用户发送类型入参:userCode={}, companyCode={}", userCode, companyCode);
		
		if (chatLogVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(chatLogVO.getServer())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "server不能为空");
		} else if (StringUtil.isEmpty(chatLogVO.getClient())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "client不能为空");
		}
		
		ResultEntity resultEntity = new ResultEntity();
		
		try {
			// 如果都是用户
			String resultStr = redisClientTemplate.getMapValue(RedisConstant.USER_CHAT_USER_KEY, 
					chatLogVO.getServer()+ "_" +chatLogVO.getClient());
			if (resultStr != null) {
				String [] temp = resultStr.split("\\/");
				resultEntity.setData(temp[3]);
			} else {
				resultEntity.setResultBody(ResultParam.FAIlED);
			}			
		} catch (Exception e) {
			logger.error("判断通话用户发送类型异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "判断通话用户发送类型异常，请联系管理员。");
		}
		return resultEntity;
	}
	

	/**
	 * 取省份城市列表
	 */
	@RequestMapping(value="/user/getProCityList", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getProCityList(HttpServletRequest request, HttpServletResponse response) {
		ResultEntity resultEntity = new ResultEntity();
		String resultStr = redisClientTemplate.getString(RedisConstant.PROVINCE_CITY_KEY);
		if (resultStr != null) {
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
			resultEntity.setData(jsonObject);
		} else {
			return sysUserService.getProvinceCityList();
		}
		
		return resultEntity;
	}
	
	/**
	 * 分页查询用户列表
	 */
	@RequestMapping(value="/user/getUserListByPage", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getUserListByPage(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody Map<String, Object> params) {
		ResultEntity resultEntity = new ResultEntity();
		PageEntity pageEntity = new PageEntity();
		pageEntity.setParams(params);
		PageResult pageResult = sysUserService.getUserListByPage(pageEntity);
		
		List<UserConfigTO> listUser = pageResult.getRows();
		if (listUser != null) {
			int size = listUser.size();
			if (size > 0) {
				String [] userCodes = new String[size];
				
				for (int i = 0; i < size; i++) {
					userCodes[i] = listUser.get(i).getUserCode();
				}
				
				// 关注数量/粉丝数量
				List<String> listStr = redisClientTemplate.getMapValue(RedisConstant.USERS_FOCUS_NUM_KEY, userCodes);
				if (listStr != null) {
					for (int i = 0; i < size; i++) {
						listUser.get(i).setFansNum(listStr.get(i)!=null?(Integer.valueOf(listStr.get(i))):0);
					}
				}
			}
		}
		
		resultEntity.setData(pageResult);
		return resultEntity;
	}
	
	/**
	 * 用户点赞更新
	 * @throws Exception 
	 */
	@RequestMapping(value="/user/patchGiveALike", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchGiveALike(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody SysUserVO sysUserVO) throws Exception {
		if (StringUtil.isEmpty(sysUserVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		} else if (sysUserVO.getLikeNum() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "likeNum不能为空");
		}

		String ticket = request.getAttribute("ticket").toString();
		sysUserVO.setTicket(ticket);
		return sysUserService.giveALike(sysUserVO);
	}
	
	/**
	 * 关注用户更新
	 * @throws Exception 
	 */
	@RequestMapping(value="/user/patchFocusUser", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity putFocusUser(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody SysUserVO sysUserVO) throws Exception {
		if (StringUtil.isEmpty(sysUserVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		} else if (sysUserVO.getFocusFlag() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "focusFlag不能为空");
		}

		String ticket = request.getAttribute("ticket").toString();
		sysUserVO.setTicket(ticket);
		
		String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+sysUserVO.getTicket());
		if (userStr != null) {
			SysUser sysUser = JSONObject.parseObject(userStr, SysUser.class);
			FocusUsersVO focusUsersVOIn = new FocusUsersVO();
			focusUsersVOIn.setUserId(sysUser.getUserId());
			focusUsersVOIn.setUserCode(sysUser.getUserCode());
			focusUsersVOIn.setFocusUserCode(sysUserVO.getUserCode());
			
			if (sysUserVO.getFocusFlag()) {
				// 关注用户
				focusUsersVOIn.setFocusUserName(sysUserVO.getUsername());
				return focusUsersService.addFocusUser(focusUsersVOIn);
			} else {
				// 取消关注用户
				return focusUsersService.cancelFocusUser(focusUsersVOIn);
			}
		} else {
			logger.warn("当前操作用户【{}】不存在", sysUserVO.getTicket());
		}
		
		return new ResultEntity(ResultParam.FAIlED);
	}
	
	
	/**
	 * 查看用户详细信息
	 * @throws Exception 
	 */
	@RequestMapping(value="/user/getUserDetailInfo", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getUserDetailInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody SysUserVO sysUserVO) throws Exception {
		if (StringUtil.isEmpty(sysUserVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		} else if (sysUserVO.getUserId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userId不能为空");
		}
		
		ResultEntity resultEntity = new ResultEntity();
		

		String ticket = request.getAttribute("ticket").toString();
		sysUserVO.setTicket(ticket);
		
		// 当前登录用户
		UserConfigTO sysUser = webBaseService.getUserInfoByCache(request);
		if (sysUser != null) {
			UserConfigTO userBack = new UserConfigTO();		
			userBack.setUserCode(sysUserVO.getUserCode());
			
			// 是否点赞
			String field = sysUser.getUserCode()+"_"+sysUserVO.getUserCode();
			String likeStr = redisClientTemplate.getMapValue(RedisConstant.GIVE_A_LIKE_KEY, field);
			userBack.setLikeFlag(likeStr!=null?true:false);
			
			// 点赞数量
			likeStr = redisClientTemplate.getMapValue(RedisConstant.USER_LIKE_NUM_KEY, sysUserVO.getUserCode());
			userBack.setLikeNum(likeStr!=null?Integer.valueOf(likeStr):0);
			
			// 关注数量/粉丝数量
			String focusNumStr = redisClientTemplate.getMapValue(RedisConstant.USERS_FOCUS_NUM_KEY, sysUserVO.getUserCode());
			userBack.setFansNum(focusNumStr!=null?(Integer.valueOf(focusNumStr)):0);
			
			// 当前操作用户是否关注
			String focusStr = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, field);
			userBack.setFocusFlag(focusStr!=null?true:false);
			
			// 用户是否关注当前操作用户(以此来判断能否联系对方，当对方禁止陌生人联系时)
			focusStr = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, (sysUserVO.getUserCode()+"_"+sysUser.getUserCode()));
			userBack.setByFocusFlag(focusStr!=null?true:false);
			
			// 查询用户配置信息(优化--频繁调用数据库不好，直接在查询用户列表时返回)
//			UserConfig userConfigIn = new UserConfig();
//			userConfigIn.setUserCode(sysUserVO.getUserCode());
//			UserConfig userConfigOut = userConfigService.selectOne(userConfigIn);
//			userBack.setStrangerContact(userConfigOut!=null?userConfigOut.getStrangerContact():TypeConstant.STRANGER_CONTACT_0);
			
			// 如果是当前操作用户查看自己的关注用户列表
			if (sysUserVO.getUserCode().equals(sysUser.getUserCode())) {
				findUserFocusUsersList(sysUserVO, sysUser, userBack);
				
				// 绑定信息判断
				int loginType = HttpUtil.fromMoblie(request);
				Map<Integer, String> bindMap = sysUser.getBindIp();
				if (bindMap != null && bindMap.size() > 0) {
					userBack.setBindFlag(TypeConstant.LOGIN_BIND_2);
					if (bindMap.containsKey(loginType)) {
						userBack.setBindFlag(TypeConstant.LOGIN_BIND_1);
					}
				} else {
					userBack.setBindFlag(TypeConstant.LOGIN_BIND_0);
				}
			} else {

				// 至少VIP用户才能查看用户的关注用户列表(减少调用数据库)
//				PermissionFuncTO perFuncTO = webBaseService.getUserFuncPermission(sysUserVO.getTicket());
//				PermissionFuncTO perFuncTO = sysUser.getFuncPermissionInside();
				Map<String, PermissionFunc> fp4Server = sysUser.getFuncPermission4Server();
				if (fp4Server.containsKey("F0001")) {
					findUserFocusUsersList(sysUserVO, sysUser, userBack);
				} else {
					userBack.setUserFocusList(new ArrayList<SysUserVO>());
				}
			}
			
			resultEntity.setData(userBack);
		} else {
			logger.warn("当前操作用户【{}】不存在", sysUserVO.getTicket());
			resultEntity.setResultBody(ResultParam.FAIlED);
		}
		
		return resultEntity;
	}

	/**
	 * 查看用户的关注用户列表
	 */
	private void findUserFocusUsersList(SysUserVO sysUserVO, UserConfigTO sysUser, UserConfigTO userBack) {
		// 用户的关注用户列表(取5个用户)
		PageEntity pageEntity = new PageEntity();
		// 这里需要权限判断
		pageEntity.setRows(sysUserVO.getUserCode().equals(sysUser.getUserCode())?Integer.MAX_VALUE:5);
		pageEntity.setRows(5);
//				SysUser sysUserIn = new SysUser();
//				sysUserIn.setUserCode(sysUserVO.getUserCode());
//				SysUser sysUserOut = sysUserService.selectOne(sysUserIn);
		Map<String, Object> params = new HashMap<>();
		params.put("userId", sysUserVO.getUserId());
		pageEntity.setParams(params);
		PageResult pageResult = focusUsersService.getFocusUsersListByPage(pageEntity);
		if (pageResult != null && pageResult.getRows().size() > 0) {
			// 如果取的是当前登录用户的信息，则加上点赞数量和关注数量
			if (sysUserVO.getUserCode().equals(sysUser.getUserCode())) {
				List<UserConfigTO> listUser = pageResult.getRows();
				if (listUser != null) {
					int size = listUser.size();
					if (size > 0) {
						String [] userCodes = new String[size];
						
						for (int i = 0; i < size; i++) {
							userCodes[i] = listUser.get(i).getUserCode();
						}
						
						// 关注数量/粉丝数量
						List<String> listStr = redisClientTemplate.getMapValue(RedisConstant.USERS_FOCUS_NUM_KEY, userCodes);
						if (listStr != null) {
							for (int i = 0; i < size; i++) {
								listUser.get(i).setFansNum(listStr.get(i)!=null?(Integer.valueOf(listStr.get(i))):0);
							}
						}
					}
				}
			}
			
			userBack.setUserFocusList(pageResult.getRows());
		} else {
			userBack.setUserFocusList(new ArrayList<SysUserVO>());
		}
	}
	
	/**
	 * 查询关注用户列表
	 */
	@RequestMapping(value="/user/getFocusUserList", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getFocusUserList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody Map<String, Object> params) {
		ResultEntity resultEntity = new ResultEntity();
		
		try {
			PageEntity pageEntity = new PageEntity();
			// 查询用户必要的配置信息(不是全部)
			params.put("configFlag", TypeConstant.CONFIG_FLAG_1);
			pageEntity.setParams(params);
			PageResult pageResult = focusUsersService.getFocusUsersListByPage(pageEntity);
			
			// 查询是否被对方关注
			List<UserConfigTO> listUser = pageResult.getRows();
			if (listUser != null) {
				int size = listUser.size();
				if (size > 0) {
					String [] userCodes = new String[size];
					String userCode = params.get("userCode").toString();
					
					for (int i = 0; i < size; i++) {
						userCodes[i] = listUser.get(i).getUserCode()+"_"+userCode;
					}
					
					// 关注数量/粉丝数量
					List<String> listStr = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, userCodes);
					if (listStr != null) {
						for (int i = 0; i < size; i++) {
							listUser.get(i).setByFocusFlag(listStr.get(i)!=null?true:false);
						}
					}
				}
			}
			
			resultEntity.setData(pageResult);
		} catch (Exception e) {
			logger.error("查询用户关注列表异常：", e);
			resultEntity.setResultBody(ResultParam.FAILED_UNKNOW);
		}
		
		return resultEntity;
	}
	
	/**
	 * 查询最近联系人列表
	 */
	@RequestMapping(value="/user/getNearContactUserList", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getNearContactUserList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody SysUserVO sysUserVO) {
		if (sysUserVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		ResultEntity resultEntity = new ResultEntity();
		
		try {
			return sysUserService.getUserListByIn(sysUserVO);
		} catch (Exception e) {
			logger.error("查询最近联系人列表异常：", e);
			resultEntity.setResultBody(ResultParam.FAILED_UNKNOW);
		}
		
		return resultEntity;
	}
	
	/**
	 * 更新用户信息
	 */
	@Decrypt
	@RequestMapping(value="/user/patchUserInfo", method={RequestMethod.POST}, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchUserInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody SysUserVO sysUserVO) {
		UserConfigTO userInfo = webBaseService.getUserInfoByCache(request);
		logger.info("{}更新用户信息：{}", userInfo.getUsername(), sysUserVO.toString());
		
		if (StringUtil.isEmpty(sysUserVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getPassword())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "password不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getUsername())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "username不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getFingerPrint())) {
			return new ResultEntity(ResultParam.FAILED_UNKNOW);
		}
		
		ResultEntity resultEntity = new ResultEntity();
		
		try {
			String ticket = request.getAttribute("ticket").toString();
			
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(ticket);
			int loginType = HttpUtil.fromMoblie(request);
			String ipAddr = HttpUtil.getIpAddress(request);

			// 绑定信息判断
			Map<Integer, String> bindMap = userConfig.getBindIp();
			if (bindMap != null && bindMap.size() > 0) {
				
				// 已经绑定(web/手机/pad)则不能修改邮箱（用来验证解绑）
				sysUserVO.setEmail(null);
				
				if (bindMap.containsKey(loginType)) {
					String bindInfo = bindMap.get(loginType);
					
					// WEB端--判断IP、指纹
					if (TypeConstant.LOGIN_TYPE_0 == loginType) {
						if (bindInfo.contains("_")
								?(bindInfo.startsWith(ipAddr) && bindInfo.endsWith(sysUserVO.getFingerPrint()))
										:bindInfo.equals(ipAddr)) {
							logger.info("【{}】更新用户信息，WEB端绑定信息验证通过", userInfo.getUsername());
						} else {
							logger.warn("【{}】更新用户信息，WEB端绑定信息验证失败", userInfo.getUsername());
							return new ResultEntity(ResultParam.FAIlED, "绑定信息不匹配");
						}
					} 
					// 手机端
					else if (TypeConstant.LOGIN_TYPE_1 == loginType) {
						
					} 
					// Pad端
					else if (TypeConstant.LOGIN_TYPE_2 == loginType) {
						
					}
				}
			}
			
			Map<Integer, String> ideMap = userConfig.getLoginIdentifierMap();
			if (ideMap != null && ideMap.containsKey(loginType) && ideMap.get(loginType).endsWith(sysUserVO.getFingerPrint())) {
				sysUserVO.setTicket(ticket);
				return sysUserService.updateUserInfo(sysUserVO);
			}

			logger.warn("{}更新用户信息，指纹验证失败（{}）", userInfo.getUsername(), ideMap.get(loginType)+"不包含"+sysUserVO.getFingerPrint());
			resultEntity.setResultBody(ResultParam.FAIlED);
		} catch (Exception e) {
			logger.error("更新用户信息异常：", e);
			resultEntity.setResultBody(ResultParam.FAILED_UNKNOW);
		}
		
		return resultEntity;
	}
	
	/**
	 * 更新用户签名
	 */
	@RequestMapping(value="/user/patchUserSign", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchUserSign(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody SysUserVO sysUserVO) {
		UserConfigTO userInfo = webBaseService.getUserInfoByCache(request);
		logger.info("{}更新用户签名：{}", userInfo.getUsername(), sysUserVO.toString());
		
		if (sysUserVO.getUserId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userId不能为空");
		}
		
		ResultEntity resultEntity = new ResultEntity();
		
		try {
			SysUser sysUserUp = new SysUser();
			sysUserUp.setUserId(sysUserVO.getUserId());
			sysUserUp.setUserSign(StringUtil.isEmpty(sysUserVO.getUserSign())?"":sysUserVO.getUserSign());
			int result = sysUserService.updateByPrimaryKey(sysUserUp);
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			if (result > 0) {
				logger.warn("【{}】更新用户签名成功", userConfig.getUserCode());
				userConfig.setUserSign(sysUserVO.getUserSign());
				
				// 更新缓存
				webBaseService.updateUserInfoCache(request.getHeader(TypeConstant.TICKET), userConfig);
			} else {
				logger.warn("【{}】更新用户签名失败", userConfig.getUserCode());
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} catch (Exception e) {
			logger.error("更新用户签名异常：", e);
			resultEntity.setResultBody(ResultParam.FAILED_UNKNOW);
		}
		
		return resultEntity;
	}
	
}
