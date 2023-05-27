package com.fangg.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.PermissionFunc;
import com.fangg.bean.chat.query.SplitTbConfig;
import com.fangg.constant.RedisConstant;
import com.fangg.dao.PermissionFuncMapper;
import com.fangg.dao.SplitTbConfigMapper;
import com.xclj.common.redis.RedisClientTemplate;

/**
 * 项目启动后加载
 * @author fangg
 * 2022年1月4日 下午4:57:06
 */
@Component
public class ApplicationInitRunner implements ApplicationRunner{
	private static Logger logger = LoggerFactory.getLogger(ApplicationInitRunner.class);

	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	SplitTbConfigMapper splitTbConfigMapper;
	@Autowired
	PermissionFuncMapper permissionFuncMapper;

	public void run(ApplicationArguments arguments) throws Exception {
		logger.info("项目启动缓存");
		List<SplitTbConfig> listSplitTConfig = splitTbConfigMapper.selectAll();
		
		if (listSplitTConfig != null && listSplitTConfig.size() > 0) {
			
			Map<String, String> map = new HashMap<String, String>();
			for (SplitTbConfig splitTbConfig : listSplitTConfig) {
				map.put(splitTbConfig.getTableName(), JSONObject.toJSONString(splitTbConfig));
			}
			
			String result = redisClientTemplate.setMap(RedisConstant.SPLIT_TB_CONFIG_KEY, map);
			if (result != null) {
				logger.info("缓存--分表配置信息成功");
			} else {
				logger.info("缓存--分表配置信息失败");
			}
		} else {
			logger.info("分表配置信息为空");
		}
		
		//logger.info("项目启动缓存--功能权限信息");
		List<PermissionFunc> perList = permissionFuncMapper.selectAll();
		String resultStr = redisClientTemplate.setString(RedisConstant.PERMISSION_FUNC_KEY, JSONArray.toJSONString(perList));
		if (resultStr != null) {
			logger.info("缓存--功能权限信息成功");
		} else {
			logger.info("缓存--功能权限信息失败");
		}
		
	}

}
