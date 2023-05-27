package com.fangg.dao.record;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.record.RecordGroup;
import com.fangg.bean.chat.vo.record.RecordGroupVO;
import com.xclj.tk.dao.BaseTKMapper;

/**
 * 
 * @author fangg
 * 2022年2月21日 上午10:16:01
 */
@Mapper
public interface RecordGroupMapper extends BaseTKMapper<RecordGroup> {
	
	/** 批量新增分组 **/
	int insertRecordGroupByBatch(List<RecordGroupVO> listRecordGroupVO);
	
}