package com.fangg.controller.record;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.record.RecordGroup;
import com.fangg.bean.chat.vo.record.RecordGroupVO;
import com.fangg.service.record.DailyRecordService;
import com.fangg.service.record.RecordGroupService;
import com.fangg.util.UuidUtil;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.StringUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * 日记
 * @author fangg
 * 2022年2月22日 上午6:29:12
 */
@Controller
@RequestMapping("/daily")
public class RecordGroupController {
	private static Logger logger = LoggerFactory.getLogger(RecordGroupController.class);

	@Value("${aes.private.key}")
	private String aesPrivateKey;

	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	DailyRecordService dailyRecordService;
	@Autowired
	RecordGroupService recordGroupService;

	@RequestMapping(value="/getRecordGroupList", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getRecordGroupList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody RecordGroupVO recordGroupVO) {
//		logger.info("查询日记分组入参：dailyRecordVO={}", JSONObject.toJSONString(recordGroupVO));
		if (recordGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(recordGroupVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		List<RecordGroup> recordGroupList = recordGroupService.getRecordGroupList(recordGroupVO);
		return new ResultEntity(recordGroupList);
	}
	
	@RequestMapping(value="/putRecordGroup", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity putRecordGroup(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody RecordGroupVO recordGroupVO) {
		if (recordGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(recordGroupVO.getGroupName())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "groupName不能为空");
		} else if (StringUtil.isEmpty(recordGroupVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		logger.info("【{}】新增日记分组入参：{}", recordGroupVO.getUserCode(), JSONObject.toJSONString(recordGroupVO));
		
		recordGroupVO.setGroupCode("RG"+UuidUtil.genUuid_0(6));
		
		RecordGroup recordGroupNew = JSONObject.parseObject(JSONObject.toJSONString(recordGroupVO), RecordGroup.class);
		int result = recordGroupService.insert(recordGroupNew);
		if (result > 0 ) {
			logger.info("【{}】新增日记分组成功", recordGroupVO.getUserCode());
			recordGroupNew.setGroupName(null);
			return new ResultEntity(recordGroupService.selectOne(recordGroupNew));
		}
		
		logger.warn("【{}】新增日记分组失败", recordGroupVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED);
	}
	
	@RequestMapping(value="/deleteRecordGroup", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity deleteRecordGroup(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody RecordGroupVO recordGroupVO) {
		if (recordGroupVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (recordGroupVO.getRecordGroupId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordGroupId不能为空");
		} 

		logger.info("【{}】删除日记分组入参：{}", recordGroupVO.getUserCode(), JSONObject.toJSONString(recordGroupVO));
		
		int result = recordGroupService.deleteByPrimaryKey(recordGroupVO.getRecordGroupId());
		if (result > 0 ) {
			logger.info("【{}】删除日记分组成功", recordGroupVO.getUserCode());
			return new ResultEntity();
		}
		
		logger.warn("【{}】删除日记分组失败", recordGroupVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED);
	}
	
	
}
