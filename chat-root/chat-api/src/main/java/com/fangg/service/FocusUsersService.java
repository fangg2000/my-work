package com.fangg.service;

import java.util.List;

import com.fangg.bean.chat.query.FocusUsers;
import com.fangg.bean.chat.vo.FocusUsersVO;
import com.xclj.page.PageEntity;
import com.xclj.page.PageResult;
import com.xclj.replay.ResultEntity;

public interface FocusUsersService {

	/** 新增关注用户 **/
	ResultEntity addFocusUser(FocusUsersVO focusUsersVO);
	
	/** 排量新增关注用户 **/
	ResultEntity addFocusUsersByBatch(List<FocusUsersVO> focusUsersVOList);

	/** 分页查询关注用户列表 **/
	PageResult getFocusUsersListByPage(PageEntity pageEntity);

	/** 取消关注用户 **/
	ResultEntity cancelFocusUser(FocusUsersVO focusUsersVO);
	
	/** 查询关注用户列表 **/
	List<FocusUsers> getFocusUsersAll(FocusUsersVO focusUsersVO);
	
}