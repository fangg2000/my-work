package com.fangg.service.record;

import java.util.List;

import com.fangg.bean.chat.query.record.RecordGroup;
import com.fangg.bean.chat.vo.record.RecordGroupVO;
import com.xclj.replay.ResultEntity;
import com.xclj.tk.service.BaseService;

public interface RecordGroupService extends BaseService<RecordGroup> {

	/** 批量新增分组 **/
	ResultEntity insertRecordGroupByBatch(List<RecordGroupVO> listRecordGroupVO);
	
	/** 查询日记分组 **/
	List<RecordGroup> getRecordGroupList(RecordGroupVO recordGroupVO);
	
}