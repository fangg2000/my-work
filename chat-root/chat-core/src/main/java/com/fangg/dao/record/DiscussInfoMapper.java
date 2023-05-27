package com.fangg.dao.record;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.record.DiscussInfo;
import com.fangg.bean.chat.vo.record.DiscussInfoVO;

/**
 * 评论mapper
 * @author fangg
 * 2022年2月21日 上午10:42:34
 */
@Mapper
public interface DiscussInfoMapper  {
	
	/** 新增评论 **/
	int insertDiscussInfo(DiscussInfoVO discussInfoVO);
	
	/** 通过ID删除评论 **/
	int deleteByPrimaryKey(DiscussInfoVO discussInfoVO);
	
	/** 查询评论列表 **/
	List<DiscussInfo> selectDiscussInfoList(DiscussInfoVO discussInfoVO);
	
	/** 更新评论点赞数量 **/
	int updateDisNum(DiscussInfoVO discussInfoVO);
	
}