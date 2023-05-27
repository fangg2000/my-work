package com.fangg.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.FocusUsers;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.FocusUsersVO;

/**
 * 关注用户
 */
@Mapper
public interface FocusUsersMapper {
	
	/** 新增关注用户 **/
	int insertFocusUser(FocusUsersVO focusUsersVO);
	/** 排量新增关注用户 **/
	int insertFocusUsersByBatch(List<FocusUsersVO> focusUsersVOList);
	/** 分页关注用户列表 **/
	List<UserConfigTO> selectFocusUserListByPage(FocusUsersVO focusUsersVO);
	/** 查询关注用户列表 **/
	List<FocusUsers> selectUserFocusAll(FocusUsersVO focusUsersVO);
	/** 用户关注数量 **/
	int selectUserFocusNum(FocusUsersVO focusUsersVO);
	/** 取消关注用户 **/
	int cancelFocusUser(FocusUsersVO focusUsersVO);
	
}