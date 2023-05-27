package com.fangg.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.ChatGroup;
import com.fangg.bean.chat.vo.ChatGroupVO;
import com.xclj.tk.dao.BaseTKMapper;

/**
 * 
 * @author fangg
 * 2022年3月3日 下午3:34:57
 */
@Mapper
public interface ChatGroupMapper extends BaseTKMapper<ChatGroup> {
	
	/** 批量新增群信息 **/
	int insertChatGroupByBatch(List<ChatGroupVO> chatGroupVOList);
	
	/** 分页查询群列表 **/
	List<ChatGroup> selectChatGroupByPage(ChatGroupVO chatGroupVO);
	
	/** 查询多个群信息 **/
	List<ChatGroup> selectChatGroupByIn(List<String> codeList);
	
}