package com.fangg.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fangg.bean.chat.query.BlackInfo;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.BlackInfoVO;
import com.fangg.config.redis.RedisDbTemplate;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.dao.BlackInfoMapper;
import com.fangg.service.BlackInfoService;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;
import com.xclj.tk.service.impl.BaseServiceImpl;

@Service(value="blackInfoService")
public class BlackInfoServiceImpl extends BaseServiceImpl<BlackInfo> implements BlackInfoService {
    private static final Logger logger = LoggerFactory.getLogger(BlackInfoServiceImpl.class);

    @Autowired
	RedisDbTemplate redisDbTemplate;
    
    @Autowired
    BlackInfoMapper blackInfoMapper;

	/**
	 * 批量新增黑名单信息
	 */
	public ResultEntity insertBlackInfoByBatch(List<BlackInfoVO> blackInfoList) {
		int result = blackInfoMapper.insertBlackInfoByBatch(blackInfoList);
		if (result > 0) {
			logger.info("批量新增黑名单信息成功数量：{}", result);
		}
		
		logger.warn("批量新增黑名单信息失败");
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * ip加入黑名单
	 */
	public void ipAdd2BlackInfo(String descript, UserConfigTO userConfig) {
		String ipAddr = userConfig.getLoginIp();
		// 0-web/1-mobile/2-pad
		int loginType = userConfig.getLoginType();
		
		BlackInfo blackInfo = new BlackInfo();
		blackInfo.setBlackCode(ipAddr);
		blackInfo.setCreateTime(new Date());
		blackInfo.setDescript(StringUtils.isEmpty(descript)?"非法操作":descript);
		blackInfo.setIpAddr(ipAddr);
		blackInfo.setStatus(TypeConstant.BLACK_STATUS_1);
		blackInfo.setLoginType(loginType);
		blackInfo.setUserCode(userConfig.getUserCode());
		blackInfo.setUsername(userConfig.getUsername());
		
		int result = blackInfoMapper.insert(blackInfo);
		if (result > 0) {
			// 缓存IP
			redisDbTemplate.setMapValue(RedisConstant.BLACK_IP_KEY, ipAddr, StringUtils.isEmpty(descript)?"非法操作":descript);
		}
	}
	
	/**
	 * 用户加入黑名单
	 */
	public void userAdd2BlackInfo(String descript, UserConfigTO userConfig) {
		String ipAddr = userConfig.getLoginIp();
		// 0-web/1-mobile/2-pad
		int loginType = userConfig.getLoginType();
		
		BlackInfo blackInfo = new BlackInfo();
		blackInfo.setBlackCode(userConfig.getUserCode());
		blackInfo.setCreateTime(new Date());
		blackInfo.setDescript(StringUtils.isEmpty(descript)?"非法操作":descript);
		blackInfo.setIpAddr(ipAddr);
		blackInfo.setStatus(TypeConstant.BLACK_STATUS_1);
		blackInfo.setLoginType(loginType);
		blackInfo.setUserCode(userConfig.getUserCode());
		blackInfo.setUsername(userConfig.getUsername());

		int result = blackInfoMapper.insert(blackInfo);
		if (result > 0) {
			// 缓存用户编号
			redisDbTemplate.setMapValue(RedisConstant.BLACK_IP_KEY, userConfig.getUserCode(), StringUtils.isEmpty(descript)?"非法操作":descript);
		}
	}

}