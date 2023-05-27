package com.fangg.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fangg.bean.chat.query.ChatLog;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.vo.ChatLogVO;

/**
 * @author fangg
 * date:2021/12/17 08:39
 */
@Mapper
public interface ChatLogMapper {
	
	/** 新增聊天记录 **/
	public int insertChatLog(ChatLogVO chatLogVO);
	
	/** 批量新增 **/
	public int insertChatLogByBatch(List<ChatLogVO> chatLogVOList);
	
	/** 查看更多 **/
	public List<ChatLog> listChatMore(ChatLogVO chatLogVO);
	
	/** 更新状态 **/
	int updateReadStatus(ChatLogVO chatLogVO);

	/** 通过行数判断表是否存在 **/
	int checkTableExistsWithSchema(ChatLogVO chatLogVO);
	
	/** 判断表是否存在 **/
	Map<String, Object> checkTableExistsWithShow(ChatLogVO chatLogVO);
	
	/** 根据企业编号创建新表 **/
	int createNewTable(ChatLogVO chatLogVO);
	
	/** 查询客服所属下未读的用户(可根据客服优先排序) **/
	List<UserCompanyTO> selectUserListByChatNotRead(ChatLogVO chatLogVO);
	
	/** 查询用户未读的客服信息列表 **/
	List<UserCompanyTO> selectCustomerListByChatNotRead(ChatLogVO chatLogVO);
	
	/** 查询用户未读的新用户列表 **/
	List<UserCompanyTO> selectNewUserListByChatNotRead(ChatLogVO chatLogVO);
	
	/** 查询用户未读的老用户列表 **/
	List<UserCompanyTO> selectOldUserListByChatNotRead(ChatLogVO chatLogVO);
	
	/** 查询用户未读的关注用户列表(此查询在数据量大以后可能出问题) **/
	@Deprecated
	List<UserCompanyTO> selectFocusUserListByChatNotRead(ChatLogVO chatLogVO);
	
	/** 查询用户未读的聊天记录列表 **/
	List<UserCompanyTO> selectChatLogListByChatNotRead(ChatLogVO chatLogVO);
	
	List<ChatLog> selectChatLogByBA(@Param("startId") String startId, @Param("endId") String endId);
	
}