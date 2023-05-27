package com.fangg.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.PermissionFunc;
import com.fangg.bean.chat.vo.PermissionFuncVO;
import com.xclj.tk.dao.BaseTKMapper;

/**
 * 功能权限mapper
 * @author fangg
 * 2022年1月20日 上午10:33:07
 */
@Mapper
public interface PermissionFuncMapper extends BaseTKMapper<PermissionFunc> {
	
	/** 批量新增功能权限信息 **/
	int insertPermissionFuncByBatch(List<PermissionFuncVO> permissionFuncVOList);
	
}