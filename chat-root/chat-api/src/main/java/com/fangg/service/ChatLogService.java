package com.fangg.service;

import java.util.List;

import com.fangg.bean.chat.query.ChatLog;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.vo.ChatLogVO;
import com.xclj.replay.ResultEntity;

public interface ChatLogService {

	/** 新增聊天记录 **/
	ResultEntity insertChatLog(ChatLogVO chatLogVO);
   
	/** 批量新增 **/
    ResultEntity insertChatLogByBatch(List<ChatLogVO> chatLogVOList);
    
    /** 查看更多 **/
    List<ChatLog> getChatMoreList(ChatLogVO chatLogVO);
    
    /** 更新状态 **/
    ResultEntity updateReadStatus(ChatLogVO chatLogVO);

	/** 查询客服所属下未读的用户(可根据客服优先排序) **/
	List<UserCompanyTO> getU4CListByChatNotRead(ChatLogVO chatLogVO);
	
	/** 查询用户未读的客服列表 **/
	List<UserCompanyTO> getC4UListByChatNotRead(ChatLogVO chatLogVO);
	
	/** 查询用户未读的关注用户列表 **/
	List<UserCompanyTO> getFocusUserListByChatNotRead(ChatLogVO chatLogVO);

	/** 查询用户未读的新用户列表 **/
	List<UserCompanyTO> getNewUserListByChatNotRead(ChatLogVO chatLogVO);
	
	/** 查询用户未读的老用户列表 **/
	List<UserCompanyTO> getOldUserListByChatNotRead(ChatLogVO chatLogVO);
	
	/** 查询用户未读的(客服、关注用户、新用户、老用户)列表 **/
	List<UserCompanyTO> getUserListByChatNotRead(ChatLogVO chatLogVO);
    
}