package com.fangg.service.record;

import java.util.List;

import com.fangg.bean.chat.query.record.DiscussInfo;
import com.fangg.bean.chat.vo.record.DiscussInfoVO;
import com.xclj.replay.ResultEntity;

public interface DiscussInfoService {

	/** 新增评论 **/
	ResultEntity insertDiscussInfo(DiscussInfoVO discussInfoVO);
	
	/** 通过ID删除评论 **/
	ResultEntity deleteByPrimaryKey(DiscussInfoVO discussInfoVO);
	
	/** 查询评论列表 **/
	List<DiscussInfo> getDiscussInfoList(DiscussInfoVO discussInfoVO);

	/** 更新评论点赞数量 **/
	ResultEntity updateDisNum(DiscussInfoVO discussInfoVO);
	
}