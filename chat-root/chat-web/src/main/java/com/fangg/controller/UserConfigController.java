package com.fangg.controller;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.UserConfig;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.UserConfigVO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.constant.TypeConstant;
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
 * 用户配置
 * @author fangg
 * 2022年1月18日 上午9:37:24
 */
@Controller
@RequestMapping("/config")
public class UserConfigController {
	private static Logger logger = LoggerFactory.getLogger(UserConfigController.class);

	@Value("${aes.private.key}")
	private String aesPrivateKey;
	@Value("${aes.unbind.key}")
	private String aesUnbindKey;
	@Value("${spring.mail.properties.from}")
	private String mailFrom;

	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
//	@Autowired
//	SysUserService sysUserService;
//	@Autowired
//	CompanyInfoService companyInfoService;
//	@Autowired
//	FocusUsersService focusUsersService;
	@Autowired
	UserConfigService userConfigService;
	@Autowired
	WebBaseService webBaseService;
	@Autowired
	JavaMailSender jms;

	@RequestMapping(value="/getUserConfigInfo", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getUserConfigInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody UserConfigVO userConfigVO) {
		logger.info("查询用户配置信息入参：userConfigVO={}", JSONObject.toJSONString(userConfigVO));
		if (userConfigVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(userConfigVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
				
		ResultEntity resultEntity = new ResultEntity();
		UserConfig userConfigIn = new UserConfig();
		userConfigIn.setUserCode(userConfigVO.getUserCode());
		UserConfig userConfigOut = userConfigService.selectOne(userConfigIn);
		if (userConfigOut != null) {
			JSONObject configJson = JSONObject.parseObject(JSONObject.toJSONString(userConfigOut));
			if (StringUtil.isNotEmpty(userConfigOut.getBindIp())) {
				int loginType = HttpUtil.fromMoblie(request);
				//JSONObject bindJson = JSONObject.parseObject(userConfigOut.getBindIp());
				Map<Integer, String> bindMap = JSONObject.parseObject(userConfigOut.getBindIp(), HashMap.class);
				if (bindMap.containsKey(loginType)) {
					configJson.put("bindIpFlag", true);
					configJson.put("bindBrowerFlag", bindMap.get(loginType).indexOf("_")!=-1?true:false);
				} else {
					configJson.put("bindIpFlag", false);
					configJson.put("bindBrowerFlag", false);
				}
			} else {
				configJson.put("bindIpFlag", false);
				configJson.put("bindBrowerFlag", false);
			}
			configJson.remove("bindIp");
			resultEntity.setData(configJson);
		} else {
			resultEntity.setResultBody(ResultParam.FAIlED);
		}
		return resultEntity;
	}
	
	@RequestMapping(value="/patchUserConfigInfo", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchUserConfigInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody UserConfigVO userConfigVO) {
		logger.info("更新用户配置信息入参：userConfigVO={}", JSONObject.toJSONString(userConfigVO));
		if (userConfigVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(userConfigVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		try {
			if ((userConfigVO.getBindIpFlag() != null && userConfigVO.getBindIpFlag()) 
					|| (userConfigVO.getBindBrowerFlag() != null && userConfigVO.getBindBrowerFlag())) {
				UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
				// 客户端类型
				//int loginType = HttpUtil.fromMoblie(request);
				String bindStr = checkBindIp(request, userConfigVO, userConfig);
				if (bindStr == null) {
					return new ResultEntity(ResultParam.FAIlED, "信息已经绑定，如要更改请先手机验证");
				}
				userConfigVO.setBindIp(bindStr);
			}
			
			String ticket = request.getAttribute("ticket").toString();
			userConfigVO.setTicket(ticket);
			return userConfigService.updateUserConfig(userConfigVO);
		} catch (Exception e) {
			logger.error("更新用户配置信息异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "更新用户配置信息异常，请联系系统管理员。");
		}
	}

	private String checkBindIp(HttpServletRequest request, UserConfigVO userConfigVO, UserConfigTO userConfig) {
		Map<Integer, String> bindMap = userConfig.getBindIp();
		// 客户端类型
		int loginType = HttpUtil.fromMoblie(request);
		String ipAddr = HttpUtil.getIpAddress(request);
		
		if (bindMap != null) {
			if (TypeConstant.LOGIN_TYPE_0 == loginType) {
				if (bindMap.containsKey(loginType) == false) {
					if (userConfigVO.getBindBrowerFlag()) {
						// 保存登录时的ip和指纹
						bindMap.put(loginType, ipAddr+"_"+userConfig.getLoginIdentifierMap().get(loginType).split("_")[1]);
					} else {
						bindMap.put(loginType, ipAddr);
					}
				} else if (userConfigVO.getBindBrowerFlag() && bindMap.get(loginType).contains("_") == false) {
					// 保存登录时的指纹
					bindMap.put(loginType, bindMap.get(loginType)+"_"+userConfig.getLoginIdentifierMap().get(loginType).split("_")[1]);
				} else {
					logger.warn(String.format("【{}】%s信息已经绑定", 
							TypeConstant.LOGIN_TYPE_0==loginType?"WEB端":(TypeConstant.LOGIN_TYPE_1==loginType?"手机端":"Pad端")), 
							userConfig.getUsername());
					return null;
				}
			} else if (TypeConstant.LOGIN_TYPE_1 == loginType && bindMap.containsKey(loginType) == false) {
				bindMap.put(loginType, userConfigVO.getBindIp());
			} else if (TypeConstant.LOGIN_TYPE_2 == loginType && bindMap.containsKey(loginType) == false) {
				bindMap.put(loginType, userConfigVO.getBindIp());
			} else {
				logger.warn(String.format("【{}】%s信息已经绑定", 
						TypeConstant.LOGIN_TYPE_0==loginType?"WEB端":(TypeConstant.LOGIN_TYPE_1==loginType?"手机端":"Pad端")), 
						userConfig.getUsername());
				return null;
			}
		} else {
			bindMap = new HashMap<>();
			if (TypeConstant.LOGIN_TYPE_0 == loginType) {
				if (userConfigVO.getBindBrowerFlag()) {
					// 保存登录时的ip和指纹
					bindMap.put(loginType, ipAddr+"_"+userConfig.getLoginIdentifierMap().get(loginType).split("_")[1]);
				} else {
					bindMap.put(loginType, ipAddr);
				}
			} else if (TypeConstant.LOGIN_TYPE_1 == loginType) {
				// 手机唯一设备标识符IMEI
				bindMap.put(loginType, userConfigVO.getBindIp());
			} else if (TypeConstant.LOGIN_TYPE_2 == loginType) {
				// Pad唯一设备标识符IMEI
				bindMap.put(loginType, userConfigVO.getBindIp());
			}
		}
		
		return JSONObject.toJSONString(bindMap);
	}
	
	/**
	 * 更新绑定信息(绑定后只能通过手机验证更改)
	 */
	/*@RequestMapping(value="/user/patchBindInfo", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchBindInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody UserConfigVO userConfigVO) {
		logger.info("【{}】更新绑定信息", userConfigVO.getUserCode());
		
		if (StringUtils.isEmpty(userConfigVO.getBindIp())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "bindIp不能为空");
		} else if (userConfigVO.getConfigId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "configId不能为空");
		}
				
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			// 客户端类型
			int loginType = HttpUtil.fromMoblie(request);
			String bindStr = checkBindIp(request, userConfigVO, userConfig);
			if (bindStr == null) {
				return new ResultEntity(ResultParam.FAIlED, "信息已经绑定，如要更改请先手机验证");
			}
			
			UserConfig userConfigUp = new UserConfig();
			userConfigUp.setBindIp(bindStr);
			userConfigUp.setConfigId(userConfigVO.getConfigId());
			int result = userConfigService.updateByPrimaryKey(userConfigUp);
			if (result > 0) {
				logger.info(String.format("【{}】绑定%s信息成功", 
						TypeConstant.LOGIN_TYPE_0==loginType?"WEB端":(TypeConstant.LOGIN_TYPE_1==loginType?"手机端":"Pad端")), 
						userConfig.getUsername());
			} else {
				logger.warn(String.format("【{}】绑定%s信息失败", 
						TypeConstant.LOGIN_TYPE_0==loginType?"WEB端":(TypeConstant.LOGIN_TYPE_1==loginType?"手机端":"Pad端")), 
						userConfig.getUsername());
			}
		} catch (Exception e) {
			logger.error("【{}】绑定信息异常：", userConfigVO.getUserCode(), e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW);
		}

		return new ResultEntity();
	}*/
	
	/**
	 * 通过邮件解绑信息
	 */
	@RequestMapping(value="/unbindByMail", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity unbindByMail(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody UserConfigVO userConfigVO) {
		logger.info("通过邮件解绑信息入参：userConfigVO={}", JSONObject.toJSONString(userConfigVO));
		if (userConfigVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(userConfigVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		String ticket = request.getAttribute("ticket").toString();
		
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(ticket);
			if (userConfig != null && StringUtils.isNotEmpty(userConfig.getEmail())) {
				final String email = userConfig.getEmail();
				final String userCode = userConfigVO.getUserCode();
				final int id = userConfig.getConfigId();
				final int loginType = HttpUtil.fromMoblie(request);
				String path = request.getContextPath();
				final String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;
				
				// 判断是否绑定
				Map<Integer, String> bindMap = userConfig.getBindIp();
				if (bindMap == null || bindMap.containsKey(loginType) == false) {
					String ipAddr = HttpUtil.getIpAddress(request);
					logger.warn("【{}】用户正在做非法解绑信息操作，IP={}", userCode, ipAddr);
					
					// 不接受非法操作(直接退出登录，可以添加判断多做几次则把IP拉进系统拉黑单，因为可能不是用户 本人在操作)
					return new ResultEntity(ResultParam.LOGIN_UNKNOW);
				}

				taskExecutor.execute(new Runnable() {
					public void run() {
						try {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("d", id);
							jsonObject.put("l", loginType);
							jsonObject.put("t", ticket);
							jsonObject.put("o", DateUtil.getNextTimeWithSeconds(new Date(), TimeoutConstant.UNBIND_TIMEOUT).getTime());
							String encryptStr = AESUtil.encrypt(jsonObject.toJSONString(), aesUnbindKey);
							String urlStr = URLEncoder.encode(encryptStr, "UTF-8");
							
							// 发送邮件
							SimpleMailMessage message = new SimpleMailMessage();
							message.setFrom(mailFrom); //发送者邮箱地址
							message.setTo(email); //收件人邮箱地址
							message.setSubject("FUN_CHAT--解绑信息");
							message.setText(String.format("复制链接地址（%s/config/unbindCheck?text=%s）到浏览器打开进行解绑验证", basePath, urlStr));
							jms.send(message);
							logger.info("【{}】解绑验证信息邮件发送成功", userCode);
						} catch (MailException e) {
							logger.error("【{}】解绑信息邮件发送异常：", userCode, e);
						} catch (Exception e) {
							logger.error("【{}】解绑信息邮件发送异常：", userCode, e);
						}
					}
				});
			}
			return new ResultEntity();
		} catch (Exception e) {
			logger.error("通过邮件解绑信息异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "通过邮件解绑信息异常，请联系系统管理员。");
		}
	}
	
	/**
	 * 邮件解绑验证
	 */
	@RequestMapping(value="/unbindCheck")
	@ResponseBody
	public ResultEntity unbindCheck(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("text") String text) {
		logger.info("邮件解绑验证信息入参：text={}", text);
		if (StringUtils.isEmpty(text)) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "信息不能为空");
		}
		
		try {
			//String urlStr = URLDecoder.decode(text, "UTF-8");
			String decryptStr = AESUtil.decrypt(text, aesUnbindKey);
			JSONObject jsonObject = JSONObject.parseObject(decryptStr);
			if (jsonObject == null 
					|| jsonObject.containsKey("d") == false 
					|| jsonObject.containsKey("t") == false 
					|| jsonObject.containsKey("l") == false 
					|| jsonObject.containsKey("o") == false) {
				return new ResultEntity(ResultParam.FAIlED, "链接无效");
			}
			
			Date lastDate = new Date(jsonObject.getLongValue("o"));
			Date nowDate = new Date();
			if (lastDate.before(nowDate)) {
				return new ResultEntity(ResultParam.FAIlED, "验证超时");
			}
			
			int configId = jsonObject.getIntValue("d");
			int loginType = jsonObject.getIntValue("l");
			String ticket = jsonObject.getString("t");
			
			UserConfig configOut = userConfigService.selectByPrimaryKey(configId);
			if (configOut != null && StringUtils.isNotEmpty(configOut.getBindIp())) {
				Map<Integer, String> bindMap = JSONObject.parseObject(configOut.getBindIp(), HashMap.class);
				if (bindMap != null && bindMap.containsKey(loginType)) {
					bindMap.remove(loginType);
					
					UserConfig configUp = new UserConfig();
					configUp.setConfigId(configId);
					configUp.setBindIp(JSONObject.toJSONString(bindMap));
					int result = userConfigService.updateByPrimaryKey(configUp);
					if (result > 0) {
						logger.info("【{}】通过邮件解绑信息成功", configOut.getUserCode());
						
						// 更新用户缓存
						UserConfigTO configCache = webBaseService.getUserInfoByCache(ticket);
						if (configCache != null) {
							configCache.setBindIp(bindMap);
							redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+ticket, 
									JSONObject.toJSONString(configCache), TimeoutConstant.LOGIN_TIMEOUT);
						}
						
						return new ResultEntity(ResultParam.SUCCESS, "解绑成功");
					} 
				}
			}
			
			return new ResultEntity(ResultParam.FAIlED, "解绑失败");
		} catch (Exception e) {
			logger.error("邮件解绑验证异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "邮件解绑验证异常，请联系系统管理员。");
		}
	}

	@RequestMapping(value="/patchUserBlackList", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchUserBlackList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody UserConfigVO userConfigVO) {
		logger.info("更新用户黑名单入参：userConfigVO={}", JSONObject.toJSONString(userConfigVO));
		if (userConfigVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(userConfigVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		} else if (StringUtil.isEmpty(userConfigVO.getBlackCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "blackCode不能为空");
		} else if (userConfigVO.getBlackType() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "blackType不能为空");
		}
		
		try {
			UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
			if (userConfig != null && userConfig.getUserCode().equals(userConfigVO.getUserCode())) {
				return userConfigService.updateBlackUserList(userConfigVO);
			}

			logger.warn("更新用户黑名单失败，用户【{}】不存在", userConfigVO.getUserCode());
			return new ResultEntity(ResultParam.FAIlED);
		} catch (Exception e) {
			logger.error("更新用户黑名单异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "更新用户黑名单异常，请联系系统管理员。");
		}
	}
	
}
