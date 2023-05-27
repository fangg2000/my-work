package com.fangg.service;

import java.util.List;

import com.fangg.bean.chat.query.SysUser;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.SysUserVO;
import com.xclj.page.PageEntity;
import com.xclj.page.PageResult;
import com.xclj.replay.ResultEntity;
import com.xclj.tk.service.BaseService;

public interface SysUserService extends BaseService<SysUser> {

	/** 批量新增用户 **/
	ResultEntity insertUserByBatch(List<SysUserVO> sysUserVOList);
	/** 新增用户 **/
	ResultEntity inserSysUser(SysUserVO sysUserVO);
	/** 登录用户 **/
	ResultEntity userLogin(SysUserVO sysUserVO);
	/** 获取登录用户信息 **/
	UserConfigTO getLoginSysUserInfo(SysUserVO sysUserVO);
	/** 用户注册 **/
	ResultEntity userRegist(SysUserVO sysUserVO);
	/** 用户退出登录 **/
	ResultEntity loginOut(SysUserVO sysUserVO);
	
	/** 省份城市列表查询 **/
	ResultEntity getProvinceCityList();

	/** 分页查询用户列表 **/
	PageResult getUserListByPage(PageEntity pageEntity);
	
	/** 给用户点赞 **/
	ResultEntity giveALike(SysUserVO sysUserVO);

	/** 查询用户列表 **/
	ResultEntity getUserListByIn(SysUserVO sysUserVO);
	
	/** 更新用户信息 **/
	ResultEntity updateUserInfo(SysUserVO sysUserVO);
	
}