package com.fangg.service;

import java.util.List;

import com.fangg.bean.chat.query.BlackInfo;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.BlackInfoVO;
import com.xclj.replay.ResultEntity;
import com.xclj.tk.service.BaseService;

public interface BlackInfoService extends BaseService<BlackInfo> {

	/** 批量新增黑名单信息 **/
	ResultEntity insertBlackInfoByBatch(List<BlackInfoVO> blackInfoList);
	
	/** ip加入黑名单 **/
	void ipAdd2BlackInfo(String descript, UserConfigTO userConfig);
	
	/** 用户加入黑名单 **/
	void userAdd2BlackInfo(String descript, UserConfigTO userConfig);
}