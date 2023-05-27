package com.fangg.service;

import java.util.List;

import com.fangg.bean.chat.query.ChatGroup;
import com.fangg.bean.chat.query.UserChatGroup;
import com.fangg.bean.chat.vo.ChatGroupVO;
import com.fangg.bean.chat.vo.UserChatGroupVO;
import com.xclj.replay.ResultEntity;
import com.xclj.tk.service.BaseService;

public interface ChatGroupService extends BaseService<ChatGroup> {

	/** 新增群信息 **/
	ResultEntity insertChatGroupInfo(ChatGroupVO chatGroupVO);
	
	/** 批量新增群信息 **/
	ResultEntity insertChatGroupByBatch(List<ChatGroupVO> chatGroupVOList);
	
	/** 入群申请 **/
	ResultEntity joinChatGroup(ChatGroupVO chatGroupVO);
	
	/** 退出群 **/
	ResultEntity leaveChatGroup(ChatGroupVO chatGroupVO);
	
	/** 用户群列表 **/
	List<UserChatGroup> getUserChatGroupList(UserChatGroupVO userChatGroupVO);
	
	/** 分页查询群列表 **/
	List<ChatGroup> getChatGroupListByPage(ChatGroupVO chatGroupVO);
	
	/** 查询群信息 **/
	ChatGroup getChatGroupInfo(ChatGroupVO chatGroupVO);
	
	/** 查询入群申请信息列表 **/
	ResultEntity getCGApplyList(ChatGroupVO chatGroupVO);
	
	/** 入群申请审核 **/
	ResultEntity checkCGApply(ChatGroupVO chatGroupVO);
	
	/** 群用户列表 **/
	List<UserChatGroup> getChatGroupUserList(UserChatGroupVO userChatGroupVO);
	
	/** 更新群消息状态 **/
	ResultEntity updateCGMsgStatus(ChatGroupVO chatGroupVO);
	
}