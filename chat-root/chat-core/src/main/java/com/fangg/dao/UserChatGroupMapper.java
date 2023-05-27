package com.fangg.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.UserChatGroup;
import com.fangg.bean.chat.vo.UserChatGroupVO;
import com.xclj.tk.dao.BaseTKMapper;

/**
 * 用户与聊天群关联表mapper
 */
@Mapper
public interface UserChatGroupMapper extends BaseTKMapper<UserChatGroup> {
	
	/** 批量新增用户群关联信息 **/
	int insertUCGByBatch(List<UserChatGroupVO> ucgList);
	
	/** 删除用户群关联信息 **/
	int deleteUCG(UserChatGroupVO userChatGroupVO);
	
	/** 更新群用户消息状态 **/
	int updateCGMsgStatus(UserChatGroupVO userChatGroupVO);

}