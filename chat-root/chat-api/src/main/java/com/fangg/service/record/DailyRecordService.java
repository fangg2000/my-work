package com.fangg.service.record;

import java.util.List;

import com.fangg.bean.chat.query.record.DailyRecord;
import com.fangg.bean.chat.vo.record.DailyRecordVO;
import com.xclj.replay.ResultEntity;

public interface DailyRecordService {

	/** 新增日记 **/
	ResultEntity insertDailyRecord(DailyRecordVO dailyRecordVO);
	
	/** 更新日记 **/
	ResultEntity updateByPrimaryKeySelective(DailyRecordVO dailyRecordVO);

	/** 删除日记 **/
	ResultEntity deleteDailyRecord(DailyRecordVO dailyRecordVO);
	
	/** 查询日记列表 **/
	List<DailyRecord> getDailyRecordList(DailyRecordVO dailyRecordVO);

	/** 通过分页查询日记列表 **/
	List<DailyRecord> getDailyRecordListByPage(DailyRecordVO dailyRecordVO);

	/** 通过in查询日记列表 **/
	List<DailyRecord> getDRListByIn(List<Long> idList);
	
	/** 查询缓存日记信息列表 **/
	ResultEntity getDRListByCache(DailyRecordVO dailyRecordVO);

	/** 查询日记信息 **/
	ResultEntity getDailyRecord(DailyRecordVO dailyRecordVO);

	/** 根据ID更新日记浏览数量、收藏数量、评论数量 **/
	ResultEntity updateDRNum(DailyRecordVO dailyRecordVO, String drNumType);
	
	/** 查询用户收藏日记列表 **/
	ResultEntity getCollectList(DailyRecordVO dailyRecordVO);
	
}