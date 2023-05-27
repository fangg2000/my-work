package com.fangg.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.UserConfig;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.SysUserVO;
import com.fangg.bean.chat.vo.UserConfigVO;
import com.xclj.tk.dao.BaseTKMapper;

/**
 * 用户配置信息Mapper
 * @author fangg
 * 2022年1月17日 下午3:38:25
 */
@Mapper
public interface UserConfigMapper extends BaseTKMapper<UserConfig> {
	
	/** 批量新增用户配置信息 **/
	int insertUserConfigByBatch(List<UserConfigVO> userConfigVOList);
	
	/** 登录时获取用户相关配置 **/
	UserConfigTO selectUserConfigInfoForLogin(SysUserVO sysUserVO);
	
	/** 新增用户默认配置 **/
	int insertUserConfigByDefault(UserConfigVO userConfigVO);
	
	/** 更新用户默认配置 **/
	int updateUserConfigInfo(UserConfigVO userConfigVO);
	
}