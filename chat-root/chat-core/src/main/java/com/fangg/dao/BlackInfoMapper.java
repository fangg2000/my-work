package com.fangg.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.BlackInfo;
import com.fangg.bean.chat.vo.BlackInfoVO;
import com.xclj.tk.dao.BaseTKMapper;

/**
 * 黑名单mapper
 * @author fangg
 * 2022年3月8日 上午10:40:18
 */
@Mapper
public interface BlackInfoMapper extends BaseTKMapper<BlackInfo> {
	
	/** 批量新增黑名单信息 **/
	int insertBlackInfoByBatch(List<BlackInfoVO> blackInfoList);
	
}