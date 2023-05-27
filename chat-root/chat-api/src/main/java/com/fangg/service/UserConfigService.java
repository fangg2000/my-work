package com.fangg.service;

import java.util.List;

import com.fangg.bean.chat.query.UserConfig;
import com.fangg.bean.chat.vo.UserConfigVO;
import com.xclj.replay.ResultEntity;
import com.xclj.tk.service.BaseService;

public interface UserConfigService extends BaseService<UserConfig> {

	/** 批量新增用户配置信息 **/
	ResultEntity insertUserConfigByBatch(List<UserConfigVO> userConfigVOList);
	
	/** 更新用户配置信息 **/
	ResultEntity updateUserConfig(UserConfigVO userConfigVO);
	
	/** 用户黑名单更新 **/
	ResultEntity updateBlackUserList(UserConfigVO userConfigVO);
	
	
}