package com.fangg.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.FocusUsers;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.FocusUsersVO;
import com.fangg.constant.RedisConstant;
import com.fangg.dao.FocusUsersMapper;
import com.fangg.service.FocusUsersService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.page.PageEntity;
import com.xclj.page.PageResult;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

@Service(value="focusUsersService")
public class FocusUsersServiceImpl implements FocusUsersService {
    private static final Logger logger = LoggerFactory.getLogger(FocusUsersServiceImpl.class);

    @Autowired
	RedisClientTemplate redisClientTemplate;
    @Autowired
    FocusUsersMapper focusUsersMapper;

	/**
	 * 新增关注用户 
	 */
	public ResultEntity addFocusUser(FocusUsersVO focusUsersVO) {
		ResultEntity resultEntity = new ResultEntity();
		String field = focusUsersVO.getUserCode()+"_"+focusUsersVO.getFocusUserCode();
		String focusUserStr = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, field);
		if (focusUserStr == null) {
			focusUsersVO.setCreateTime(new Date());
			int result = focusUsersMapper.insertFocusUser(focusUsersVO);
			if (result > 0) {
				logger.info("关注用户【{}】成功", focusUsersVO.getFocusUserName());
				//String focusStr = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, field);
				// 存在即关注，汪关注则删除
				redisClientTemplate.setMapValue(RedisConstant.FOCUS_USER_KEY, field, "");
				// 用户关注数量
				String focusNumStr = redisClientTemplate.getMapValue(RedisConstant.USERS_FOCUS_NUM_KEY, focusUsersVO.getFocusUserCode());
				int focusNum = 1;
				if (focusNumStr != null) {
					focusNum = focusNum + Integer.parseInt(focusNumStr);
				}
				redisClientTemplate.setMapValue(RedisConstant.USERS_FOCUS_NUM_KEY, focusUsersVO.getFocusUserCode(), String.valueOf(focusNum));
			} else {
				logger.warn("关注用户【{}】失败", focusUsersVO.getFocusUserName());
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		}
//		else {
//			logger.warn("已经关注用户【{}】", focusUsersVO.getFocusUserName());
//		}
		return resultEntity;
	}

	/**
	 * 排量新增关注用户 
	 */
	public ResultEntity addFocusUsersByBatch(List<FocusUsersVO> focusUsersVOList) {
		ResultEntity resultEntity = new ResultEntity();
		int result = focusUsersMapper.insertFocusUsersByBatch(focusUsersVOList);
		if (result > 0) {
			logger.info("批量关注用户成功");
		} else {
			logger.warn("批量关注用户失败");
			resultEntity.setResultBody(ResultParam.FAIlED);
		}
		return resultEntity;
	}

	/**
	 * 分页查询关注用户列表 
	 */
	public PageResult getFocusUsersListByPage(PageEntity pageEntity) {
		PageHelper.offsetPage(pageEntity.getOffset(), pageEntity.getRows());
		//PageHelper.startPage(pageEntity.getPage(), pageEntity.getRows());
		//Map<String, Object> params = pageEntity.getParams();
		
		FocusUsersVO focusUsersVOIn = JSONObject.parseObject(JSONObject.toJSONString(pageEntity.getParams()), FocusUsersVO.class);
		List<UserConfigTO> bookInfoList = focusUsersMapper.selectFocusUserListByPage(focusUsersVOIn);
		return new PageResult(new PageInfo<UserConfigTO>(bookInfoList));
	}

	/**
	 * 取消关注用户
	 */
	public ResultEntity cancelFocusUser(FocusUsersVO focusUsersVO) {
		ResultEntity resultEntity = new ResultEntity();
		
		String field = focusUsersVO.getUserCode()+"_"+focusUsersVO.getFocusUserCode();
		String focusUserStr = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, field);
		if (focusUserStr != null) {
			int result = focusUsersMapper.cancelFocusUser(focusUsersVO);
			if (result > 0) {
				logger.info("取消关注用户成功");
				
				// 清空缓存
				redisClientTemplate.delMapValue(RedisConstant.FOCUS_USER_KEY, field);
				// 用户关注数量减1
				/*String focusNumStr = redisClientTemplate.getMapValue(RedisConstant.USERS_FOCUS_NUM_KEY, focusUsersVO.getFocusUserCode());
				int focusNum = 1;
				if (focusNumStr != null) {
					focusNum = Integer.parseInt(focusNumStr) - focusNum;
					if (focusNum < 0) {
						focusNum = 0;
					}
					redisClientTemplate.setMapValue(RedisConstant.USERS_FOCUS_NUM_KEY, focusUsersVO.getFocusUserCode(), String.valueOf(focusNum));
				}*/
				// 此处理在以后用户数量太多后可以注释并替换成上面的(因为每次查询数据库获取一个精确数量已经意义不大)
				int focusNum = focusUsersMapper.selectUserFocusNum(focusUsersVO);
				redisClientTemplate.setMapValue(RedisConstant.USERS_FOCUS_NUM_KEY, focusUsersVO.getFocusUserCode(), String.valueOf(focusNum));
			} else {
				logger.warn("取消关注用户失败");
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		}
		
		return resultEntity;
	}

	/**
	 * 查询关注用户列表 
	 */
	public List<FocusUsers> getFocusUsersAll(FocusUsersVO focusUsersVO) {
		return focusUsersMapper.selectUserFocusAll(focusUsersVO);
	}

}
