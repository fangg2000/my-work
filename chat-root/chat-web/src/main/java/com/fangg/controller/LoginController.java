package com.fangg.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.fangg.annotation.Decrypt;
import com.fangg.annotation.Encrypt;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.SysUserVO;
import com.fangg.bean.chat.vo.UserConfigVO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.service.ChatLogService;
import com.fangg.service.CompanyInfoService;
import com.fangg.service.SysUserService;
import com.fangg.service.UserConfigService;
import com.fangg.service.WebBaseService;
import com.fangg.util.HttpUtil;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.AESUtil;
import com.xclj.common.util.DateUtil;
import com.xclj.common.util.StringUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * 登录
 * @author fangg
 * 2021年12月24日 下午7:12:44
 */
@Controller
public class LoginController {
	private static Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Value("${aes.private.key}")
	private String aesPrivateKey;
	@Value("${rsa.public.key}")
	private String rsaPublicKey;
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
	WebBaseService webBaseService;
	@Autowired
	UserConfigService userConfigService;
	@Autowired
	CompanyInfoService companyInfoService;

	@RequestMapping("/")
	public String index(Model model, HttpServletResponse response) {
		//model.addAttribute("name", "test");
		return "success";
	}

	@Encrypt
	@Decrypt(value="RSA")
	@RequestMapping(value="/user/login", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity userLogin(HttpServletRequest request, HttpServletResponse response, @RequestBody SysUserVO sysUserVO) throws Exception {
		logger.info("用户登录入参：userCode={}", sysUserVO.getUserCode());
		logger.info("用户登录入参：{}", JSONObject.toJSONString(sysUserVO));
		
		if (StringUtil.isEmpty(sysUserVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getPassword())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "password不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getLoginIdentifier())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "loginIdentifier不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getFingerPrint())) {
			return new ResultEntity(ResultParam.LOGIN_UNKNOW);
		}
		
		String ipAddr = HttpUtil.getIpAddress(request);
		// 0-web/1-mobile/2-pad
		int loginType = HttpUtil.fromMoblie(request);
		logger.info("IP地址A：{}", ipAddr);
//		logger.info("IP地址B：{}", HttpUtil.getIpAddress(request));
//		logger.info("IP地址C：{}", request.getRemoteAddr());
//		logger.info("IP地址D：{}", request.getLocalAddr());
		
		// 用户登录 
		SysUserVO sysUserVOIn = new SysUserVO();
		sysUserVOIn.setUserCode(sysUserVO.getUserCode());
		sysUserVOIn.setPassword(sysUserVO.getPassword());
		sysUserVOIn.setUserType(sysUserVO.getUserType());
		sysUserVOIn.setLoginIp(ipAddr);
		sysUserVOIn.setLoginType(loginType);
		sysUserVOIn.setLoginIdentifier(sysUserVO.getLoginIdentifier());
		sysUserVOIn.setAesKey(sysUserVO.getAesKey());
		sysUserVOIn.setFingerPrint(sysUserVO.getFingerPrint());
		ResultEntity ucEntity = sysUserService.userLogin(sysUserVOIn);

		// 给表头加上aesKey(方便返回值加密，此处覆盖拦截器那里定义的other)
		response.setHeader("Other", AESUtil.encrypt(sysUserVO.getAesKey(), aesTicketKey));
		
		if (ucEntity.getCode() == ResultParam.SUCCESS.getCode()) {
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("userInfo", userConfig);
//			jsonObject.put("funcPermission", checkPermissionFunc(userConfig));
			JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(ucEntity.getData()));
			//UserConfigTO userConfig = JSONObject.parseObject(JSONObject.toJSONString(ucEntity), UserConfigTO.class);
			logger.info("用户【{}】登录成功", jsonObject.getString("username"));


			// loginIdentifier为登录时生成的UUID
			// 返回前端的loginIdentifier要经过RSA加密(前端查询用户时再加密失败，不要加密)
//			JSONObject ideJson = new JSONObject();
//			ideJson.put("id", jsonObject.getString("loginIdentifier"));
//			ideJson.put("ip", ipAddr);
//			ideJson.put("lt", loginType);
//			ideJson.put("fp", jsonObject.getString("fingerPrint"));
//			// 使用默认公钥加密（解密时需要使用默认私钥解密）
//			jsonObject.put("loginIdentifier", RSAUtil.encryptByBefault(ideJson.toJSONString()));
			
			// 返回前端的ticket要经过AES加密(这里附加一些验证信息)
			JSONObject ticketJson = new JSONObject();
			// 密文过长，这里使用简写
			ticketJson.put("t", jsonObject.getString("ticket"));
			ticketJson.put("f", sysUserVO.getFingerPrint());
			ticketJson.put("a", sysUserVO.getAesKey());
			jsonObject.put("ticket", AESUtil.encrypt(ticketJson.toJSONString(), aesTicketKey));
						
			// 删除不返回前端的属性
			jsonObject.remove("funcPermission4Server");
			jsonObject.remove("loginIdentifierMap");
			jsonObject.remove("checkIdentifier");
			jsonObject.remove("loginIp");
			jsonObject.remove("checkTimeout");
			jsonObject.remove("aesKey");
			jsonObject.remove("fingerPrint");
			jsonObject.remove("bindIp");

			ResultEntity resultEntity = new ResultEntity();
			resultEntity.setData(jsonObject);
			return resultEntity;
		} else if (ucEntity.getCode() == ResultParam.LOGIN_CHECK_FAILED.getCode()) {
			JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(ucEntity.getData()));
			if (jsonObject != null && jsonObject.containsKey("ticket")) {
				// 返回前端的ticket要经过AES加密(这里附加一些验证信息)
				JSONObject ticketJson = new JSONObject();
				// 密文过长，这里使用简写
				ticketJson.put("t", jsonObject.getString("ticket"));
				ticketJson.put("f", sysUserVO.getFingerPrint());
				ticketJson.put("a", sysUserVO.getAesKey());
				jsonObject.put("ticket", AESUtil.encrypt(ticketJson.toJSONString(), aesTicketKey));
				
				ucEntity.setData(jsonObject);
			}
		}
		
		return ucEntity;
	}
	
	@RequestMapping(value="/user/regist", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity regist(HttpServletRequest request, HttpServletResponse response, @RequestBody SysUserVO sysUserVO) {
		logger.info("用户注册入参：{}", sysUserVO.toString());
		
		if (StringUtil.isEmpty(sysUserVO.getUsername())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "username不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getPassword())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "password不能为空");
		}
		
		// 如果是个人注册
		if (TypeConstant.REGIST_TYPE_0 == sysUserVO.getRegistType()) {
			return sysUserService.userRegist(sysUserVO);
		} 
		// 如果是企业注册
		// 企业信息不应该和用户信息搞在一起，需要在另一个系统中管理企业信息和客服信息
//		else if (TypeConstant.REGIST_TYPE_1 == sysUserVO.getRegistType()) {
//			// username为企业名称
//			return companyInfoService.insertUserCompanyInfo(sysUserVO);
//		}
		
		return new ResultEntity(ResultParam.FAIlED);
	}
	
	@RequestMapping(value="/user/loginOut", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity loginOut(HttpServletRequest request, HttpServletResponse response, @RequestBody SysUserVO sysUserVO) throws Exception {
		logger.info("用户登出入参：{}", sysUserVO.toString());
		
		if (StringUtil.isEmpty(sysUserVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}

		String ticket = request.getAttribute("ticket").toString();
		sysUserVO.setTicket(ticket);
		sysUserVO.setOnlineStatus(TypeConstant.ONLINE_STATUS_0);
		
		// 用户下线更新
		ResultEntity resultEntity = sysUserService.loginOut(sysUserVO);
		if (resultEntity.getCode() == ResultParam.SUCCESS.getCode()) {
			// 删除ticket缓存
			redisClientTemplate.del(RedisConstant.TICKET_PREFIX_KEY + ticket);
			
			if (StringUtil.isNotEmpty(sysUserVO.getOutConfirm())) {
				UserConfigVO userConfigVOUp = new UserConfigVO();
				userConfigVOUp.setConfigId(sysUserVO.getConfigId());
				userConfigVOUp.setOutConfirm(sysUserVO.getOutConfirm());
				userConfigService.updateUserConfig(userConfigVOUp);
			}
		}
				
		return resultEntity;
	}


	@Encrypt()
	@Decrypt(value="RSA")
	@RequestMapping(value="/user/getLoginIdentifierStatus", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getLoginIdentifierStatus(HttpServletRequest request, HttpServletResponse response, @RequestBody SysUserVO sysUserVO) throws Exception {
		logger.info("登录识别码判断入参：ticket={}, loginIdentifier={}", request.getAttribute("ticket"), sysUserVO.getLoginIdentifier());
		
		if (StringUtil.isEmpty(sysUserVO.getLoginIdentifier())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "loginIdentifier不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getFingerPrint())) {
			return new ResultEntity(ResultParam.LOGIN_UNKNOW);
		}
		
		UserConfigTO userConfigTO = webBaseService.getUserInfoByCache(request);
		if (userConfigTO != null) {
			// 给表头加上aesKey(方便返回值加密)
			//response.setHeader("Other", AESUtil.encrypt(userConfigTO.getAesKey(), aesTicketKey));
			
			int loginType = HttpUtil.fromMoblie(request);
			Map<Integer, String> map = userConfigTO.getLoginIdentifierMap();
			if (map != null && map.containsKey(loginType) 
					&& map.get(loginType).equals(sysUserVO.getLoginIdentifier()+"_"+sysUserVO.getFingerPrint())) {
				return new ResultEntity();
			}
		}
		
		return new ResultEntity(ResultParam.FAIlED);
	}
	

	@Encrypt()
	@Decrypt(value="RSA")
	@RequestMapping(value="/user/putLoginCheck", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity putLoginCheck(HttpServletRequest request, HttpServletResponse response, @RequestBody SysUserVO sysUserVO) {
//		logger.info("登录验证入参：{}", sysUserVO.toString());
		
		if (StringUtil.isEmpty(sysUserVO.getLoginIdentifier())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "loginIdentifier不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getCheckIdentifier())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "checkIdentifier不能为空");
		} else if (StringUtil.isEmpty(sysUserVO.getFingerPrint())) {
			return new ResultEntity(ResultParam.LOGIN_UNKNOW);
		}
		
		try {
			Date date = new Date();
			ResultEntity resultEntity = new ResultEntity();
			UserConfigTO userConfigCache = webBaseService.getUserInfoByCache(request);
			if (userConfigCache != null) {				
				// 给表头加上aesKey(方便返回值加密)
				//response.setHeader("Other", AESUtil.encrypt(userConfigCache.getAesKey(), aesTicketKey));
				
				int loginType = HttpUtil.fromMoblie(request);
				Map<Integer, String> map = userConfigCache.getLoginIdentifierMap();
				if (map != null 
						&& userConfigCache.getCheckTimeout().after(date)
						&& checkXY(sysUserVO.getCheckIdentifier(), userConfigCache.getCheckIdentifier())) {
					map.put(loginType, sysUserVO.getLoginIdentifier()+"_"+sysUserVO.getFingerPrint());
					userConfigCache.setLoginIdentifierMap(map);

					redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+userConfigCache.getTicket(), 
							JSONObject.toJSONString(userConfigCache), TimeoutConstant.LOGIN_TIMEOUT);
					
					// 返回前端的ticket要经过AES加密(这里附加一些验证信息)
					JSONObject ticketJson = new JSONObject();
					// 密文过长，这里使用简写
					ticketJson.put("t", userConfigCache.getTicket());
					ticketJson.put("f", sysUserVO.getFingerPrint());
					ticketJson.put("a", userConfigCache.getAesKey());
					userConfigCache.setTicket(AESUtil.encrypt(ticketJson.toJSONString(), aesTicketKey));					
					
					userConfigCache.setFuncPermission4Server(null);
					userConfigCache.setLoginIdentifierMap(null);
					userConfigCache.setCheckIdentifier(null);
					userConfigCache.setLoginIp(null);
					userConfigCache.setCheckTimeout(null);
					userConfigCache.setAesKey(null);
					userConfigCache.setFingerPrint(null);
					userConfigCache.setBindIp(null);
					resultEntity.setData(userConfigCache);
					return resultEntity;
				}
			}
			
			resultEntity.setResultBody(ResultParam.LOGIN_CHECK_FAILED);
			return newIdentifier(request, resultEntity, userConfigCache);
		} catch (Exception e) {
			logger.error("登录验证异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "登录验证异常，请联系系统管理员。");
 		}
	}
	
	// 坐标误差在10之内
	private boolean checkXY(String identifierA, String identifierB) {
		String [] tempA = identifierA.split(",");
		String [] tempB = identifierB.split(",");
		if ((Integer.parseInt(tempA[0]) < Integer.parseInt(tempB[0])+10) 
				&& Integer.parseInt(tempA[0]) > Integer.parseInt(tempB[0])-10 
				&& Integer.parseInt(tempA[1]) < Integer.parseInt(tempB[1])+10 
				&& Integer.parseInt(tempA[1]) > Integer.parseInt(tempB[1])-10) {
			return true;
		}
		return false;
	}

	@RequestMapping(value="/user/getIdentifier", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getIdentifier(HttpServletRequest request, HttpServletResponse response, @RequestBody SysUserVO sysUserVO) throws Exception {
//		logger.info("获取验证识别码：{}", sysUserVO.toString());
		ResultEntity resultEntity = new ResultEntity();
		UserConfigTO userConfigCache = webBaseService.getUserInfoByCache(request);
		return newIdentifier(request, resultEntity, userConfigCache);
	}

	/**
	 * 更新验证识别码
	 * @throws Exception 
	 */
	private ResultEntity newIdentifier(HttpServletRequest request, ResultEntity resultEntity, UserConfigTO userConfigCache) throws Exception {
		// 刷新验证识别码(X,Y坐标)
		String identifier = StringUtil.getXY();
		Date date = DateUtil.getNextTimeWithSeconds(new Date(), TimeoutConstant.CHECK_TIMEOUT);
		JSONObject ideJson = new JSONObject();
		ideJson.put("checkIdentifier", identifier);
		ideJson.put("timeout", date);
		resultEntity.setData(ideJson);
		
		// N秒后超时
		if (userConfigCache != null) {
			userConfigCache.setCheckIdentifier(identifier);
			userConfigCache.setCheckTimeout(date);

			redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+userConfigCache.getTicket(), 
					JSONObject.toJSONString(userConfigCache), TimeoutConstant.LOGIN_TIMEOUT);
		}
		
		return resultEntity;
	}

}
