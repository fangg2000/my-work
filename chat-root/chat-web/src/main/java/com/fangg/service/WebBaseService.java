package com.fangg.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.BlackInfo;
import com.fangg.bean.chat.query.PermissionFunc;
import com.fangg.bean.chat.query.SysUser;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.util.HttpUtil;
import com.xclj.common.redis.RedisClientTemplate;

/**
 * web端通用service(方便当前服务器直接获取用户权限)
 * @author fangg
 * 2022年1月20日 下午1:30:45
 */
@Component(value="permissionService")
public class WebBaseService {

	@Value("${aes.private.key}")
	private String aesPrivateKey;
	
	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	BlackInfoService blackInfoService;
	
	/**
	 * 更新用户信息缓存 
	 */
	public String updateUserInfoCache(String ticket, Object userInfo) {
		return redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+ticket, 
				JSONObject.toJSONString(userInfo), TimeoutConstant.LOGIN_TIMEOUT);
	}
	
	/**
	 * 从缓存中查询用户信息(包括部分配置信息、功能权限信息)
	 */
	public UserConfigTO getUserInfoByCache(String ticket) {
		String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+ticket);
		if (userStr != null) {
			return JSONObject.parseObject(userStr, UserConfigTO.class);
		}
		
		return null;
	}
	
	/**
	 * 从缓存中查询用户信息(包括部分配置信息、功能权限信息)
	 */
	public UserConfigTO getUserInfoByCache(HttpServletRequest request) {
		String ticket = request.getAttribute("ticket").toString();

    	// ticket统一使用AES默认 key进行解密
        try {
			String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY + ticket);
			if (userStr != null) {
				return JSONObject.parseObject(userStr, UserConfigTO.class);
			}
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	/**
	 * 从缓存中查询用户信息
	 */
	public SysUser getUserInfo(String ticket) {
		String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+ticket);
		if (userStr != null) {
			return JSONObject.parseObject(userStr, SysUser.class);
		}
		
		return null;
	}
	
	/**
	 * 从缓存中查询用户信息
	 */
	public SysUser getUserInfo(HttpServletRequest request) {
		String ticket = request.getAttribute("ticket").toString();

    	// ticket统一使用AES默认 key进行解密
        try {
			String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY + ticket);
			if (userStr != null) {
				return JSONObject.parseObject(userStr, SysUser.class);
			}
		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	/**
	 * 用户功能权限
	 */
	public Map<String, PermissionFunc> getUserFuncPermission(String ticket) {
		UserConfigTO userConfig = getUserInfoByCache(ticket);
		if (userConfig != null) {
			return userConfig.getFuncPermission4Server();
		}
		
		return new HashMap<>();
	}
	
	/**
	 * ip加入黑名单
	 */
	public void ipAdd2BlackInfo(HttpServletRequest request, String descript, UserConfigTO userConfig) {
		String ipAddr = HttpUtil.getIpAddress(request);
		// 0-web/1-mobile/2-pad
		int loginType = HttpUtil.fromMoblie(request);
		
		BlackInfo blackInfo = new BlackInfo();
		blackInfo.setBlackCode(ipAddr);
		blackInfo.setCreateTime(new Date());
		blackInfo.setDescript(StringUtils.isEmpty(descript)?"非法操作":descript);
		blackInfo.setIpAddr(ipAddr);
		blackInfo.setStatus(TypeConstant.BLACK_STATUS_1);
		blackInfo.setLoginType(loginType);
		
		if (userConfig == null || StringUtils.isEmpty(userConfig.getUserCode())) {
			userConfig = getUserInfoByCache(request);
			if (userConfig != null) {
				blackInfo.setUserCode(userConfig.getUserCode());
				blackInfo.setUsername(userConfig.getUsername());
			}
		} else {
			blackInfo.setUserCode(userConfig.getUserCode());
			blackInfo.setUsername(userConfig.getUsername());
		}
		int result = blackInfoService.insert(blackInfo);
		if (result > 0) {
			// 缓存IP
			redisClientTemplate.setMapValue(RedisConstant.BLACK_IP_KEY, ipAddr, StringUtils.isEmpty(descript)?"非法操作":descript);
		}
	}
	
	/**
	 * 用户加入黑名单
	 */
	public void userAdd2BlackInfo(HttpServletRequest request, String descript, UserConfigTO userConfig) {
		if (userConfig == null) {
			userConfig = getUserInfoByCache(request);
		}
		
		if (userConfig != null) {
			String ipAddr = HttpUtil.getIpAddress(request);
			// 0-web/1-mobile/2-pad
			int loginType = HttpUtil.fromMoblie(request);
			
			BlackInfo blackInfo = new BlackInfo();
			blackInfo.setBlackCode(userConfig.getUserCode());
			blackInfo.setCreateTime(new Date());
			blackInfo.setDescript(StringUtils.isEmpty(descript)?"非法操作":descript);
			blackInfo.setIpAddr(ipAddr);
			blackInfo.setStatus(TypeConstant.BLACK_STATUS_1);
			blackInfo.setLoginType(loginType);
			blackInfo.setUserCode(userConfig.getUserCode());
			blackInfo.setUsername(userConfig.getUsername());

			int result = blackInfoService.insert(blackInfo);
			if (result > 0) {
				// 缓存用户编号
				redisClientTemplate.setMapValue(RedisConstant.BLACK_IP_KEY, userConfig.getUserCode(), StringUtils.isEmpty(descript)?"非法操作":descript);
			}
		}
	}
	
}
