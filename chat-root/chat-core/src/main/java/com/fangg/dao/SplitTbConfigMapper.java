package com.fangg.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.SplitTbConfig;
import com.xclj.tk.dao.BaseTKMapper;

/**
 * 分表配置dao
 */
@Mapper
public interface SplitTbConfigMapper extends BaseTKMapper<SplitTbConfig>{
	
	int insertSplitTbConfigByBatch(List<SplitTbConfig> splitTbConfigList);
	
}