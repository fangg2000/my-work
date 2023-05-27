package com.fangg.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.UserConfig;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.UserConfigVO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.dao.UserConfigMapper;
import com.fangg.service.UserConfigService;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;
import com.xclj.tk.service.impl.BaseServiceImpl;

@Service(value="userConfigService")
public class UserConfigServiceImpl extends BaseServiceImpl<UserConfig> implements UserConfigService {
    private static final Logger logger = LoggerFactory.getLogger(UserConfigServiceImpl.class);

    @Autowired
	RedisClientTemplate redisClientTemplate;
    @Autowired
    private UserConfigMapper userConfigMapper;

	/**
	 * 批量新增用户配置信息 
	 */
	public ResultEntity insertUserConfigByBatch(List<UserConfigVO> userConfigVOList) {
		logger.info("批量新增用户配置信息入参：{}", userConfigVOList.size());
		ResultEntity resultEntity = new ResultEntity();
		int result = userConfigMapper.insertUserConfigByBatch(userConfigVOList);
		if (result > 0) {
			logger.info("批量新增用户配置信息成功--{}", result);
		} else {
			logger.warn("批量新增用户配置信息失败", result);
			resultEntity.setResultBody(ResultParam.FAIlED);
		}
		return resultEntity;
	}

	/**
	 * 更新用户配置信息
	 */
	public ResultEntity updateUserConfig(UserConfigVO userConfigVO) {
		String configStr = JSONObject.toJSONString(userConfigVO);
		logger.info("更新用户配置信息入参：{}", configStr);
		ResultEntity resultEntity = new ResultEntity();
		if (userConfigVO.getConfigId() != null) {
			UserConfig userConfigUp = JSONObject.parseObject(configStr, UserConfig.class);
			userConfigUp.setUserCode(null);
			int result = userConfigMapper.updateByPrimaryKeySelective(userConfigUp);
			if (result > 0) {
				logger.info("更新【{}】用户配置信息成功", userConfigVO.getUserCode());
				
				// 更新缓存中的配置
				String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+userConfigVO.getTicket());
				if (userStr != null) {
					UserConfigTO userConfig = JSONObject.parseObject(userStr, UserConfigTO.class);
//					logger.info("覆盖前：userConfigUp={}, userConfig={}", JSONObject.toJSONString(userConfigUp), 
//							JSONObject.toJSONString(userConfig));
					
					// 绑定信息
					if (StringUtils.isNotEmpty(userConfigVO.getBindIp())) {
						Map<Integer, String> bindMap = JSONObject.parseObject(userConfigVO.getBindIp(), HashMap.class);
						userConfig.setBindIp(bindMap);
					}
					
					// 覆盖对象属性
					BeanUtils.copyProperties(userConfigUp, userConfig, new String[]{"userCode", "bindIp"});
					redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+userConfigVO.getTicket(), 
							JSONObject.toJSONString(userConfig), TimeoutConstant.LOGIN_TIMEOUT);
					//logger.info("覆盖后：userConfig={}", JSONObject.toJSONString(userConfig));
				}
			} else {
				logger.warn("更新【{}】用户配置信息失败", userConfigVO.getUserCode());
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} else {
			logger.warn("更新【{}】用户配置信息失败", userConfigVO.getUserCode());
			resultEntity.setResultBody(ResultParam.FAIlED);
		}
		
		return resultEntity;
	}

	/**
	 * 用户黑名单更新 
	 */
	public ResultEntity updateBlackUserList(UserConfigVO userConfigVO) {
		List<String> blackList = null;
		String blackStr = redisClientTemplate.getMapValue(RedisConstant.BLACK_USER_KEY, userConfigVO.getUserCode());
		
		// 加入黑名单
		if (userConfigVO.getBlackType() == TypeConstant.BLACK_STATUS_1) {
			if (StringUtils.isNotEmpty(blackStr)) {
				blackList = JSONArray.parseArray(blackStr, String.class);
			} else if (redisClientTemplate.isValidConnect()) {
				blackList = new ArrayList<>();
			} else {
				UserConfig userConfigIn = new UserConfig();
				userConfigIn.setUserCode(userConfigVO.getUserCode());
				UserConfig userConfigOut = userConfigMapper.selectOne(userConfigIn);
				if (userConfigOut != null && StringUtils.isNotEmpty(userConfigOut.getBlackList())) {
					blackList = JSONArray.parseArray(userConfigOut.getBlackList(), String.class);
				} else {
					blackList = new ArrayList<>();
				}
			}
			
			if (blackList.contains(userConfigVO.getBlackCode()) == false) {
				blackList.add(userConfigVO.getBlackCode());
				userConfigVO.setBlackList(JSONArray.toJSONString(blackList));
				int result = userConfigMapper.updateUserConfigInfo(userConfigVO);
				if (result > 0) {
					logger.info("用户【{}】加入黑名单成功", userConfigVO.getBlackCode());
					// 缓存黑名单用户
					redisClientTemplate.setMapValue(RedisConstant.BLACK_USER_KEY, userConfigVO.getUserCode(), userConfigVO.getBlackList());
					return new ResultEntity();
				} else {
					logger.warn("用户【{}】加入黑名单失败，更新失败", userConfigVO.getBlackCode());
				}
			} else {
				logger.warn("用户【{}】加入黑名单失败，已经在列表中", userConfigVO.getBlackCode());
			}
		} 
		// 解除黑名单
		else if (userConfigVO.getBlackType() == TypeConstant.BLACK_STATUS_0 
				&& StringUtils.isNotEmpty(blackStr)) {
			blackList = JSONArray.parseArray(blackStr, String.class);
			if (blackList.contains(userConfigVO.getBlackCode())) {
				blackList.remove(userConfigVO.getBlackCode());
				userConfigVO.setBlackList(JSONArray.toJSONString(blackList));
				int result = userConfigMapper.updateUserConfigInfo(userConfigVO);
				if (result > 0) {
					logger.info("用户【{}】解除黑名单成功", userConfigVO.getBlackCode());
					// 更新缓存黑名单用户
					redisClientTemplate.setMapValue(RedisConstant.BLACK_USER_KEY, userConfigVO.getUserCode(), userConfigVO.getBlackList());
					return new ResultEntity();
				} else {
					logger.warn("用户【{}】解除黑名单失败，更新失败", userConfigVO.getBlackCode());
				}
			} else {
				logger.warn("用户【{}】解除黑名单失败，用户不在列表中", userConfigVO.getBlackCode());
			}
		}
		
		return new ResultEntity(ResultParam.FAIlED);
	}

}