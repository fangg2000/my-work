package com.fangg.service.impl.record;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.record.RecordGroup;
import com.fangg.bean.chat.vo.record.RecordGroupVO;
import com.fangg.config.redis.RedisDbTemplate;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.dao.record.RecordGroupMapper;
import com.fangg.service.record.RecordGroupService;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;
import com.xclj.tk.service.impl.BaseServiceImpl;

@Service(value="recordGroupService")
public class RecordGroupServiceImpl extends BaseServiceImpl<RecordGroup> implements RecordGroupService {

    private static final Logger logger = LoggerFactory.getLogger(RecordGroupServiceImpl.class);

	@Autowired
	RedisDbTemplate redisDbTemplate;
	@Autowired
	RecordGroupMapper recordGroupMapper;

	/**
	 * 批量新增分组 
	 */
	public ResultEntity insertRecordGroupByBatch(List<RecordGroupVO> listRecordGroupVO) {
		logger.info("批量新增分组入参数量：{}", listRecordGroupVO.size());
		int result = recordGroupMapper.insertRecordGroupByBatch(listRecordGroupVO);
		if (result > 0) {
			logger.info("批量新增分组新增成功--数量：{}", result);
			return new ResultEntity();
		} else {
			logger.warn("批量新增分组新增失败--数量：{}", result);
		}
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 查询日记分组 
	 */
	public List<RecordGroup> getRecordGroupList(RecordGroupVO recordGroupVO) {
		RecordGroup recordGroupIn = JSONObject.parseObject(JSONObject.toJSONString(recordGroupVO), RecordGroup.class);
		List<RecordGroup> reList = recordGroupMapper.select(recordGroupIn);
		if (reList != null && reList.size() > 0) {
			return reList;
		}
		
		// 新增日记默认分组
		RecordGroup rgNew = new RecordGroup();
		rgNew.setGroupCode(TypeConstant.DEFAULT);
		rgNew.setGroupName("默认分组");
		rgNew.setUserCode(recordGroupVO.getUserCode());
		int result = recordGroupMapper.insert(rgNew);
		if (result > 0) {
			logger.info("【{}】新增日记默认分组成功", recordGroupVO.getUserCode());
			
			rgNew.setGroupName(null);			
			List<RecordGroup> rgList = recordGroupMapper.select(rgNew);
			
			// 缓存日记默认分组(放到redis的第2个数据库)
			redisDbTemplate.setMapValue(RedisConstant.DR_DEFAULT_KEY, recordGroupVO.getUserCode(), 
					String.valueOf(rgList.get(0).getRecordGroupId()), RedisConstant.REDIS_DB1);
			
			return rgList;
		}
		
		return new ArrayList<>();
	}
    
}