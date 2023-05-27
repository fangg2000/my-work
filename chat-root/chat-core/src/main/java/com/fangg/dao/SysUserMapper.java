package com.fangg.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.fangg.bean.chat.query.SysUser;
import com.fangg.bean.chat.to.CityTO;
import com.fangg.bean.chat.to.ProvinceTO;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.SysUserVO;
import com.xclj.tk.dao.BaseTKMapper;

/**
 * @author fangg
 * date:2021/12/17 15:56
 */
@Mapper
public interface SysUserMapper extends BaseTKMapper<SysUser> {

	/** 批量新增用户 **/
	int insertUserByBatch(List<SysUserVO> sysUserVOList);
	
	/** 用户退出登录 **/
	int loginOut(SysUserVO sysUserVO);
	
	/** 省份列表 **/
	List<ProvinceTO> provinceList();
	
	/** 城市列表 **/
	List<CityTO> cityList();
	
	/** 分页查询用户列表 **/
	List<UserConfigTO> selectUserListByPage(SysUserVO sysUserVO);
	
	/** 查询用户列表 **/
	List<UserConfigTO> selectUserListByIn(List<String> listUserCode);
	
	/** 用户离线更新 **/
	int updateUserOnlineDown(List<String> listUserCode);
	
}