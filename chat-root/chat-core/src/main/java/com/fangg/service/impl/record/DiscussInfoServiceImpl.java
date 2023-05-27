package com.fangg.service.impl.record;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.record.DiscussInfo;
import com.fangg.bean.chat.vo.record.DiscussInfoVO;
import com.fangg.config.redis.RedisDbTemplate;
import com.fangg.config.sharingshpere.SnowFlakeUtils;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.dao.record.DiscussInfoMapper;
import com.fangg.service.record.DiscussInfoService;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

@Service(value="discussInfoService")
public class DiscussInfoServiceImpl implements DiscussInfoService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussInfoServiceImpl.class);
    
    @Autowired
	RedisDbTemplate redisDbTemplate;
    @Autowired
    DiscussInfoMapper discussInfoMapper;

	/**
	 * 新增评论
	 */
	public ResultEntity insertDiscussInfo(DiscussInfoVO discussInfoVO) {
		// 添加ID
		String id = SnowFlakeUtils.getId().toString();
		discussInfoVO.setDiscussId(Long.parseLong(id));
		int result = discussInfoMapper.insertDiscussInfo(discussInfoVO);
		if (result > 0) {
			discussInfoVO.setDisId(id);
			return new ResultEntity(discussInfoVO);
		}
		
		logger.warn("新增【{}】日记评论失败", discussInfoVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 通过ID删除评论(30秒内可回撤)
	 */
	public ResultEntity deleteByPrimaryKey(DiscussInfoVO discussInfoVO) {
		Date date = new Date();
		long seconds = (date.getTime()-discussInfoVO.getCreateTime().getTime())/1000;
		if (seconds <= TimeoutConstant.DISCUSS_BACK_TIMEOUT) {
			int result = discussInfoMapper.deleteByPrimaryKey(discussInfoVO);
			if (result > 0) {
				return new ResultEntity();
			}
		}
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 查询评论列表 
	 */
	public List<DiscussInfo> getDiscussInfoList(DiscussInfoVO discussInfoVO) {
		List<DiscussInfo> disList = discussInfoMapper.selectDiscussInfoList(discussInfoVO);
//		for (DiscussInfo discussInfo : disList) {
//			discussInfo.setDisId(String.valueOf(discussInfo.getDiscussId()));
//		}
		return disList;
	}

	/**
	 * 更新评论点赞数量(对日记的评论用户进行缓存判断)
	 */
	public ResultEntity updateDisNum(DiscussInfoVO discussInfoVO) {
		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		discussInfoVO.setDisId(String.valueOf(discussInfoVO.getDiscussId()));
		
		// 判断用户是否已经对此评论进行点赞
		Object agreeStr = redisDbTemplate.getMapValue(RedisConstant.DR_NUM_KEY, String.valueOf(discussInfoVO.getRecordId()), 
				RedisConstant.REDIS_DB1);
		if (agreeStr != null) {
			jsonObject = JSONObject.parseObject(String.valueOf(agreeStr));
			if (jsonObject.containsKey(discussInfoVO.getDisId())) {
				jsonArray = jsonObject.getJSONArray(String.valueOf(discussInfoVO.getDisId()));
				if (jsonArray != null) {
					if (jsonArray.contains(discussInfoVO.getLoginCode())) {
						// 可考虑加入黑名单
						logger.warn("用户【{}】存在恶意点赞操作", discussInfoVO.getLoginCode());
						return new ResultEntity(ResultParam.FAIlED.getCode());
					}
					jsonArray.add(discussInfoVO.getLoginCode());
				} else {
					jsonArray = new JSONArray();
					jsonArray.add(discussInfoVO.getLoginCode());
				}
				
				jsonObject.put(discussInfoVO.getDisId(), jsonArray);
			} else {
				jsonArray = new JSONArray();
				jsonArray.add(discussInfoVO.getLoginCode());
				jsonObject.put(discussInfoVO.getDisId(), jsonArray);
			}
		} else {
			jsonObject = new JSONObject();
			jsonArray = new JSONArray();
			jsonArray.add(discussInfoVO.getLoginCode());
			jsonObject.put(discussInfoVO.getDisId(), jsonArray);
		}
		
		int result = discussInfoMapper.updateDisNum(discussInfoVO);
		if (result > 0) {
			// 缓存用户评论点赞
			redisDbTemplate.setMapValue(RedisConstant.DR_NUM_KEY, String.valueOf(discussInfoVO.getRecordId()), 
					jsonObject.toJSONString(),
					RedisConstant.REDIS_DB1);
			return new ResultEntity(ResultParam.SUCCESS.getCode());
		}
		
		logger.warn("更新【{}】评论点赞数量失败", discussInfoVO.getLoginCode());
		return new ResultEntity(ResultParam.FAIlED.getCode());
	}
    
}