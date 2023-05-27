package com.fangg.service.impl.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.record.DailyRecord;
import com.fangg.bean.chat.query.record.DiscussInfo;
import com.fangg.bean.chat.query.record.RecordGroup;
import com.fangg.bean.chat.vo.record.DailyRecordVO;
import com.fangg.bean.chat.vo.record.DiscussInfoVO;
import com.fangg.config.redis.RedisDbTemplate;
import com.fangg.config.sharingshpere.SnowFlakeUtils;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.dao.record.DailyRecordMapper;
import com.fangg.dao.record.DiscussInfoMapper;
import com.fangg.dao.record.RecordGroupMapper;
import com.fangg.service.record.DailyRecordService;
import com.fangg.util.DateUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

@Service(value="dailyRecordService")
public class DailyRecordServiceImpl implements DailyRecordService {

    private static final Logger logger = LoggerFactory.getLogger(DailyRecordServiceImpl.class);
    
    @Autowired
	RedisDbTemplate redisDbTemplate;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	
    @Autowired
    DailyRecordMapper dailyRecordMapper;
    @Autowired
    RecordGroupMapper recordGroupMapper;
    @Autowired
    DiscussInfoMapper discussInfoMapper;

	/**
	 * 新增日记 
	 */
	public ResultEntity insertDailyRecord(DailyRecordVO dailyRecordVO) {
		// 如果分组ID不存在，则新增默认分组
		if (dailyRecordVO.getRecordGroupId() == null || dailyRecordVO.getRecordGroupId() == 0) {
			// 取默认分组
			Object defaultStr = redisDbTemplate.getMapValue(RedisConstant.DR_DEFAULT_KEY, dailyRecordVO.getUserCode(), 
					RedisConstant.REDIS_DB1);
			if (defaultStr != null) {
				dailyRecordVO.setRecordGroupId(Integer.valueOf(String.valueOf(defaultStr)));
			} else {
				RecordGroup rgNew = new RecordGroup();
				rgNew.setGroupCode(TypeConstant.DEFAULT);
				rgNew.setGroupName("默认分组");
				rgNew.setUserCode(dailyRecordVO.getUserCode());
				int result = recordGroupMapper.insert(rgNew);
				if (result > 0) {
					rgNew.setGroupName(null);
					RecordGroup recordGroupOut = recordGroupMapper.selectOne(rgNew);
					if (recordGroupOut != null) {
						logger.info("【{}】新增日记默认分组成功", dailyRecordVO.getUserCode());
						dailyRecordVO.setRecordGroupId(recordGroupOut.getRecordGroupId());

						// 缓存日记默认分组(放到redis的第2个数据库)
						redisDbTemplate.setMapValue(RedisConstant.DR_DEFAULT_KEY, dailyRecordVO.getUserCode(), 
								String.valueOf(recordGroupOut.getRecordGroupId()), RedisConstant.REDIS_DB1);
					}
				}
			}
		}
		
		if (dailyRecordVO.getRecordGroupId() == null) {
			logger.warn("【{}】新增日记失败，没有分组", dailyRecordVO.getUserCode());
			return new ResultEntity(ResultParam.FAIlED);
		}
		
		// 添加ID
		dailyRecordVO.setRecordId(Long.parseLong(SnowFlakeUtils.getId().toString()));
		int result = dailyRecordMapper.insertDailyRecord(dailyRecordVO);
		if (result > 0) {
			logger.info("【{}】新增日记成功", dailyRecordVO.getUserCode());
			
			//日记id按月缓存
			cacheDRid(dailyRecordVO);
			
			String month = DateUtil.format(dailyRecordVO.getCreateTime(), "yyyy/MM");
			Map<String, DailyRecordVO> resultMap = new HashMap<>();
			dailyRecordVO.setContent(null);
			dailyRecordVO.setDrId(String.valueOf(dailyRecordVO.getRecordId()));
			resultMap.put(month, dailyRecordVO);
			
			return new ResultEntity(resultMap);
		}
		
		logger.warn("【{}】新增日记失败", dailyRecordVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 日记id按月缓存
	 */
	private void cacheDRid(DailyRecordVO dailyRecordVO) {
		taskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					/*DailyRecordVO dailyRecordVOIn = new DailyRecordVO();
					dailyRecordVOIn.setUserCode(dailyRecordVO.getUserCode());
					dailyRecordVOIn.setTitle(dailyRecordVO.getTitle());
					//dailyRecordVOIn.setCreateTime(dailyRecordVO.getCreateTime());
					List<DailyRecord> dailyReList = dailyRecordMapper.selectDailyRecordList(dailyRecordVOIn);
					
					if (dailyReList != null && dailyReList.size() > 0) {
						
					}*/
					
					List<DailyRecordVO> idList = null;
					Map<String, List<DailyRecordVO>> map = null;
					DailyRecordVO cacheDR = null;
					
					Object drStr = redisDbTemplate.getMapValue(RedisConstant.DR_ID_BY_MONTH_KEY, dailyRecordVO.getUserCode(), 
							RedisConstant.REDIS_DB1);
					String month = DateUtil.format(dailyRecordVO.getCreateTime(), "yyyy/MM");
					if (drStr != null) {
						map = JSONArray.parseObject(String.valueOf(drStr), HashMap.class);
						if (map.containsKey(month)) {
							idList = map.get(month);
						} else {
							idList = new ArrayList<>();
						}
					} else {
						map = new HashMap<>();
						idList = new ArrayList<>();
					}

					cacheDR = new DailyRecordVO();
					cacheDR.setRecordId(dailyRecordVO.getRecordId());
					cacheDR.setDrId(String.valueOf(dailyRecordVO.getRecordId()));
					cacheDR.setTitle(dailyRecordVO.getTitle());
					cacheDR.setCreateTime(dailyRecordVO.getCreateTime());
					cacheDR.setRecordStatus(dailyRecordVO.getRecordStatus());
					idList.add(cacheDR);
					map.put(month, idList);
					//logger.info("新增日记缓存信息--{}", JSONObject.toJSONString(map));
					
					// 按月份缓存用户日记
					redisDbTemplate.setMapValue(RedisConstant.DR_ID_BY_MONTH_KEY, dailyRecordVO.getUserCode(), 
							JSONObject.toJSONString(map), RedisConstant.REDIS_DB1);
				} catch (Exception e) {
					logger.error("【{}】新增日记缓存信息异常：", dailyRecordVO.getUserCode(), e);
				}
			}
		});
	}

	/**
	 * 更新日记 
	 */
	public ResultEntity updateByPrimaryKeySelective(DailyRecordVO dailyRecordVO) {
		int result = dailyRecordMapper.updateByPrimaryKeySelective(dailyRecordVO);
		if (result > 0) {
			logger.info("【{}】更新日记成功", dailyRecordVO.getUserCode());
			return new ResultEntity();
		}
		
		logger.warn("【{}】更新日记失败", dailyRecordVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 删除日记
	 */
	public ResultEntity deleteDailyRecord(DailyRecordVO dailyRecordVO) {
		int result = dailyRecordMapper.deleteDailyRecord(dailyRecordVO);
		if (result > 0) {
			logger.info("【{}】删除日记成功", dailyRecordVO.getUserCode());
			return new ResultEntity();
		}
		
		logger.warn("【{}】删除日记失败", dailyRecordVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 查询日记列表
	 */
	public List<DailyRecord> getDailyRecordList(DailyRecordVO dailyRecordVO) {
		// 默认查询默认分组
		if (dailyRecordVO.getRecordGroupId() == null) {
			// 取默认分组
			Object defaultStr = redisDbTemplate.getMapValue(RedisConstant.DR_DEFAULT_KEY, dailyRecordVO.getUserCode(), 
					RedisConstant.REDIS_DB1);
			if (defaultStr != null) {
				dailyRecordVO.setRecordGroupId(Integer.valueOf(String.valueOf(defaultStr)));
			} else {
				return new ArrayList<>();
			}
		}
		
		List<DailyRecord> dailyRecordList = dailyRecordMapper.selectDailyRecordList(dailyRecordVO);
		if (dailyRecordList != null) {
			return dailyRecordList;
		}
		
		return new ArrayList<>();
	}

	/**
	 * 通过分页查询日记列表
	 */
	public List<DailyRecord> getDailyRecordListByPage(DailyRecordVO dailyRecordVO) {
		List<DailyRecord> dailyRecordList = dailyRecordMapper.selectDailyRecordListByPage(dailyRecordVO);
		if (dailyRecordList != null) {
			// 升序
			Collections.sort(dailyRecordList, new Comparator<DailyRecord>() {
				@Override
				public int compare(DailyRecord o1, DailyRecord o2) {
					return o1.getRecordId().compareTo(o2.getRecordId());
				}
	        });
			
			return dailyRecordList;
		}
		return new ArrayList<>();
	}

	/**
	 * 通过in查询用户列表 
	 */
	public List<DailyRecord> getDRListByIn(List<Long> idList) {
		return dailyRecordMapper.selectDRListByIn(idList);
	}

	/**
	 * 查询缓存日记信息列表 (只能取公开的日记，好友可取公开和好友可见的日记，本人可取全部)
	 */
	public ResultEntity getDRListByCache(DailyRecordVO dailyRecordVO) {
		boolean focusFlag = false;
		boolean ownerFlag = dailyRecordVO.getUserCode().equals(dailyRecordVO.getLoginCode())?true:false;
		
		// 用户关注登录本人即为好友可见
		if (ownerFlag == false) {
			String field = dailyRecordVO.getUserCode()+"_"+dailyRecordVO.getLoginCode();
			Object focusStr = redisDbTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, field);
			focusFlag = focusStr!=null?true:false;
		}
		
		Map<String, List<JSONObject>> map = null;
		// 按月份降序
		TreeMap<String, List<JSONObject>> treeMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                    return o2.compareTo(o1);
            }
        });
		
		Object drStr = redisDbTemplate.getMapValue(RedisConstant.DR_ID_BY_MONTH_KEY, dailyRecordVO.getUserCode(), 
				RedisConstant.REDIS_DB1);
		if (drStr != null) {
			map = JSONArray.parseObject(String.valueOf(drStr), HashMap.class);
			// 按月排序数据
			putInTreeMap(focusFlag, ownerFlag, map, treeMap);
		} 
		// 读取数据库，如果没有数据则缓存当前月份为空
		else {
			List<DailyRecord> dailyRList = dailyRecordMapper.selectDailyRecordList(dailyRecordVO);
			String month = null;
			map = new HashMap<>();
			
			if (dailyRList != null && dailyRList.size() > 0) {
				List<JSONObject> cacheDRList = null;
				JSONObject drVO = null;
				
				// 按月分组
				for (DailyRecord dailyRecord : dailyRList) {
					dailyRecord.setContent(null);
					dailyRecord.setUpdateTime(null);
					dailyRecord.setDeleteFlag(null);
					dailyRecord.setCollectNum(null);
					dailyRecord.setDiscussNum(null);
					dailyRecord.setReviewNum(null);
					month = DateUtil.format(dailyRecord.getCreateTime(), "yyyy/MM");
					if (map.containsKey(month)) {
						cacheDRList = map.get(month);
						drVO = JSONObject.parseObject(JSONObject.toJSONString(dailyRecord));
						drVO.put("drId", String.valueOf(dailyRecord.getRecordId()));
						cacheDRList.add(drVO);
					} else {
						cacheDRList = new ArrayList<>();
						drVO = JSONObject.parseObject(JSONObject.toJSONString(dailyRecord));
						drVO.put("drId", String.valueOf(dailyRecord.getRecordId()));
						cacheDRList.add(drVO);
					}
					map.put(month, cacheDRList);
				}

				// 按月排序数据
				putInTreeMap(focusFlag, ownerFlag, map, treeMap);
			} else {
				month = DateUtil.format(new Date(), "yyyy/MM");
				treeMap.put(month, new ArrayList<>());
			}

			// 按月份缓存用户日记
			redisDbTemplate.setMapValue(RedisConstant.DR_ID_BY_MONTH_KEY, dailyRecordVO.getUserCode(), 
					JSONObject.toJSONString(map), RedisConstant.REDIS_DB1);
		}
		
		return new ResultEntity(treeMap);
	}

	/**
	 * 按月排序数据
	 */
	private void putInTreeMap(boolean focusFlag, boolean ownerFlag, Map<String, List<JSONObject>> map,
			TreeMap<String, List<JSONObject>> treeMap) {
		List<JSONObject> idList;
		List<JSONObject> cacheList;
		for (Entry<String, List<JSONObject>> entry : map.entrySet()) {
			// 本人日记列表
			if (ownerFlag) {
				treeMap.put(entry.getKey(), entry.getValue());
			} 
			// 公开、好友可见的日记列表
			else if (focusFlag) {
				idList = entry.getValue();
				cacheList = new ArrayList<>();
				for (JSONObject dailyRecord : idList) {
					if (dailyRecord.getIntValue("recordStatus") == TypeConstant.RECORD_STATUS_1 
							|| dailyRecord.getIntValue("recordStatus") == TypeConstant.RECORD_STATUS_2) {
						cacheList.add(dailyRecord);
					}
				}
				if (cacheList.size() > 0) {
					treeMap.put(entry.getKey(), cacheList);
				}
			} 
			// 公开日记
			else {
				idList = entry.getValue();
				cacheList = new ArrayList<>();
				for (JSONObject dailyRecord : idList) {
					if (dailyRecord.getIntValue("recordStatus") == TypeConstant.RECORD_STATUS_1) {
						cacheList.add(dailyRecord);
					}
				}
				if (cacheList.size() > 0) {
					treeMap.put(entry.getKey(), cacheList);
				}
			}
		}
	}

	/**
	 * 查询日记 
	 */
	public ResultEntity getDailyRecord(DailyRecordVO dailyRecordVO) {
		JSONObject resultJson = null;
		
		// 日记信息
		DailyRecord dailyRecord = dailyRecordMapper.selectDRByPrimaryKey(dailyRecordVO);
		
		if (dailyRecord != null) {
			// 评论列表
			resultJson = JSONObject.parseObject(JSONObject.toJSONString(dailyRecord));
			
			DiscussInfoVO discussInfoVOIn = new DiscussInfoVO();
			discussInfoVOIn.setRecordId(dailyRecordVO.getRecordId());
			List<DiscussInfo> disList = discussInfoMapper.selectDiscussInfoList(discussInfoVOIn);
			if (disList == null) {
				disList = new ArrayList<>();
			}

			resultJson.put("discussList", disList);
			
			return new ResultEntity(resultJson);
		}
		
		logger.warn("查询【{}】日记信息失败", dailyRecordVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 根据ID更新日记浏览数量、收藏数量、评论数量 
	 */
	public ResultEntity updateDRNum(DailyRecordVO dailyRecordVO, String drNumType) {
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		dailyRecordVO.setDrId(String.valueOf(dailyRecordVO.getRecordId()));
		
		// 判断用户是否已经对此评论进行点赞
		Object drStr = redisDbTemplate.getMapValue(RedisConstant.DR_NUM_KEY, dailyRecordVO.getDrId(), RedisConstant.REDIS_DB1);
		if (drStr != null) {
			jsonObject = JSONObject.parseObject(String.valueOf(drStr));
			
			// 浏览用户/收藏用户
			if (jsonObject.containsKey(drNumType)) {
				jsonArray = jsonObject.getJSONArray(drNumType);
				if (jsonArray.contains(dailyRecordVO.getLoginCode())) {
					logger.warn("根据ID更新【{}】日记{}失败，用户已经浏览过此日记", dailyRecordVO.getUserCode(), 
							TypeConstant.DR_REVIEW_USER.equals(drNumType)?"浏览用户数量":"收藏用户数量");
					
					// 如果是日记收藏，则取消收藏
					if (TypeConstant.DR_COLLECT_USER.equals(drNumType)) {
						dailyRecordVO.setCollectNum(-1);
						int result = dailyRecordMapper.updateDRNum(dailyRecordVO);
						if (result > 0) {
							jsonArray.remove(dailyRecordVO.getLoginCode());
							jsonObject.put(drNumType, jsonArray);
							
							// 缓存浏览用户/收藏用户
							redisDbTemplate.setMapValue(RedisConstant.DR_NUM_KEY, dailyRecordVO.getDrId(), 
									jsonObject.toJSONString(),
									RedisConstant.REDIS_DB1);
							
							// 从收藏缓存中删除
							Object collectStr = redisDbTemplate.getMapValue(RedisConstant.USER_DR_COLLECT_KEY, 
									dailyRecordVO.getLoginCode(), RedisConstant.REDIS_DB1);
							if (collectStr != null) {
								JSONArray collectArr = JSONArray.parseArray(String.valueOf(collectStr));
								DailyRecordVO dRecordVO = null;
								for (Object object : collectArr) {
									dRecordVO = JSONObject.parseObject(JSONObject.toJSONString(object), DailyRecordVO.class);
									if (dailyRecordVO.getDrId().equals(String.valueOf(dRecordVO.getRecordId()))) {
										collectArr.remove(object);
										// 缓存用户收藏日记
										redisDbTemplate.setMapValue(RedisConstant.USER_DR_COLLECT_KEY, 
												dailyRecordVO.getLoginCode(), collectArr.toJSONString(), RedisConstant.REDIS_DB1);
										break;
									}
								}
							}
							
							return new ResultEntity(2);
						}
					}
					
					return new ResultEntity(ResultParam.FAIlED.getCode());
				}
				
				jsonArray.add(dailyRecordVO.getLoginCode());
				jsonObject.put(drNumType, jsonArray);
			} else {
				jsonArray = new JSONArray();
				jsonArray.add(dailyRecordVO.getLoginCode());
				jsonObject.put(drNumType, jsonArray);
			}
		} else {
			jsonObject = new JSONObject();
			jsonArray = new JSONArray();
			jsonArray.add(dailyRecordVO.getLoginCode());
			jsonObject.put(drNumType, jsonArray);
		}
		
		int result = dailyRecordMapper.updateDRNum(dailyRecordVO);
		if (result > 0) {
//			logger.info("根据ID更新【{}】日记数量成功", dailyRecordVO.getUserCode());
			
			// 缓存浏览用户/收藏用户
			redisDbTemplate.setMapValue(RedisConstant.DR_NUM_KEY, dailyRecordVO.getDrId(), 
					jsonObject.toJSONString(),
					RedisConstant.REDIS_DB1);
			
			// 如果是日记收藏
			if (TypeConstant.DR_COLLECT_USER.equals(drNumType)) {
				Object collectStr = redisDbTemplate.getMapValue(RedisConstant.USER_DR_COLLECT_KEY, 
						dailyRecordVO.getLoginCode(), RedisConstant.REDIS_DB1);
				
				if (collectStr != null) {
					jsonArray = JSONArray.parseArray(String.valueOf(collectStr));
				} else {
					jsonArray = new JSONArray();
				}
				
				String loginCode = dailyRecordVO.getLoginCode();
				dailyRecordVO.setPageSize(null);
				dailyRecordVO.setLoginCode(null);
				dailyRecordVO.setCollectNum(null);
				dailyRecordVO.setReviewNum(null);
				jsonArray.add(dailyRecordVO);
				// 缓存用户收藏日记
				redisDbTemplate.setMapValue(RedisConstant.USER_DR_COLLECT_KEY, 
						loginCode, jsonArray.toJSONString(), RedisConstant.REDIS_DB1);
			}
			
			return new ResultEntity(ResultParam.SUCCESS.getCode());
		}

		logger.warn("根据ID更新【{}】日记{}失败", dailyRecordVO.getUserCode(), 
				TypeConstant.DR_REVIEW_USER.equals(drNumType)?"浏览用户数量":"收藏用户数量");
		return new ResultEntity(ResultParam.FAIlED.getCode());
	}

	/**
	 * 查询用户收藏日记列表 
	 */
	public ResultEntity getCollectList(DailyRecordVO dailyRecordVO) {
		JSONArray jsonArray = null;
		JSONObject resultJson = null;
		Object collectStr = redisDbTemplate.getMapValue(RedisConstant.USER_DR_COLLECT_KEY, 
				dailyRecordVO.getLoginCode(), RedisConstant.REDIS_DB1);
		
		if (collectStr != null) {
			jsonArray = JSONArray.parseArray(String.valueOf(collectStr));
		} else {
			jsonArray = new JSONArray();
		}
		
		resultJson = new JSONObject();
		resultJson.put("收藏日记列表", jsonArray);
		return new ResultEntity(resultJson);
	}

}
