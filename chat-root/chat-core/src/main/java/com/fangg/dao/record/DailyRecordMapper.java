package com.fangg.dao.record;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.record.DailyRecord;
import com.fangg.bean.chat.vo.record.DailyRecordVO;

/**
 * 日记mapper
 * @author fangg
 * 2022年2月20日 下午7:10:12
 */
@Mapper
public interface DailyRecordMapper {
	
	/** 新增日记 **/
	int insertDailyRecord(DailyRecordVO dailyRecordVO);
	
	/** 更新日记 **/
	int updateByPrimaryKeySelective(DailyRecordVO dailyRecordVO);
	
	/** 删除日记 **/
	int deleteDailyRecord(DailyRecordVO dailyRecordVO);
	
	/** 查询日记列表 **/
	List<DailyRecord> selectDailyRecordList(DailyRecordVO dailyRecordVO);
	
	/** 通过分页查询日记列表 **/
	List<DailyRecord> selectDailyRecordListByPage(DailyRecordVO dailyRecordVO);

	/** 查询日记列表 **/
	List<DailyRecord> selectDRListByIn(List<Long> idList);
	
	/** 根据ID查询日记 **/
	DailyRecord selectDRByPrimaryKey(DailyRecordVO dailyRecordVO);
	
	/** 根据ID更新日记浏览数量、收藏数量、评论数量 **/
	int updateDRNum(DailyRecordVO dailyRecordVO);

}