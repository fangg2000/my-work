package com.fangg.controller.record;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.record.DailyRecord;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.record.DailyRecordVO;
import com.fangg.bean.chat.vo.record.DiscussInfoVO;
import com.fangg.constant.TypeConstant;
import com.fangg.service.WebBaseService;
import com.fangg.service.record.DailyRecordService;
import com.fangg.service.record.DiscussInfoService;
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
public class DailyRecordController {
	private static Logger logger = LoggerFactory.getLogger(DailyRecordController.class);

	@Value("${aes.private.key}")
	private String aesPrivateKey;

	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	DailyRecordService dailyRecordService;
	@Autowired
	WebBaseService webBaseService;
	@Autowired
	DiscussInfoService discussInfoService;

	@RequestMapping(value="/getDailyRecordList", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getDailyRecordList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DailyRecordVO dailyRecordVO) {
//		logger.info("查询日记入参：dailyRecordVO={}", JSONObject.toJSONString(dailyRecordVO));
		if (dailyRecordVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(dailyRecordVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		List<DailyRecord> dailyRecordList = dailyRecordService.getDailyRecordListByPage(dailyRecordVO);
		return new ResultEntity(dailyRecordList);
	}
	
	@RequestMapping(value="/putDailyRecord", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity putDailyRecord(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DailyRecordVO dailyRecordVO) {
		if (dailyRecordVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(dailyRecordVO.getTitle())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "title不能为空");
		} else if (StringUtil.isEmpty(dailyRecordVO.getContent())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "content不能为空");
		} else if (dailyRecordVO.getRecordGroupId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordGroupId不能为空");
		} else if (StringUtil.isEmpty(dailyRecordVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
//		logger.info("【{}】新增日记入参：{}", dailyRecordVO.getUserCode(), dailyRecordVO.toString());
		
		dailyRecordVO.setCollectNum(0);
		dailyRecordVO.setReviewNum(0);
		dailyRecordVO.setDiscussNum(0);
		dailyRecordVO.setCreateTime(new Date());
		dailyRecordVO.setContent(dailyRecordVO.getContent().replaceAll("uploaded\\/temp_", "uploaded/"));
		
		ResultEntity resultEntity = dailyRecordService.insertDailyRecord(dailyRecordVO);
		if (resultEntity.getCode() == ResultParam.SUCCESS.getCode()) {
			logger.info("【{}】新增日记成功", dailyRecordVO.getUserCode(), dailyRecordVO.toString());
			checkTempImg(dailyRecordVO, request.getServletContext().getRealPath(""));
			return resultEntity;
		}
		
		logger.warn("【{}】新增日记失败", dailyRecordVO.getUserCode(), dailyRecordVO.toString());
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 日记图片处理
	 */
	private void checkTempImg(DailyRecordVO dailyRecordVO, String prefixPath) {
		String dirPath = prefixPath + "uploaded/temp_" + dailyRecordVO.getUserCode();
		String realPath = prefixPath + "uploaded/" + dailyRecordVO.getUserCode();
		try {
			File dirFile = new File(dirPath);
			if (dirFile.exists() && dirFile.isDirectory()) {
				File realDir = new File(realPath);
				if (realDir.exists() == false) {
					realDir.mkdir();
				}
				
				File [] listFiles = dirFile.listFiles();
				File newFile = null;
				for (File file : listFiles) {
					// 如果发送内容中图片存在
					if (dailyRecordVO.getContent().indexOf(file.getName()) != -1) {
						newFile = new File(realPath + "/" + file.getName());
						FileCopyUtils.copy(file, newFile);
					}
					file.delete();
				}
				
				// 删除缓存文件
				dirFile.delete();
			}
		} catch (IOException e) {
			logger.error("【{}】新增日记图片处理异常：", dailyRecordVO.getUserCode(), e);
		}
	}
	
	@RequestMapping(value="/patchDailyRecord", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchDailyRecord(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DailyRecordVO dailyRecordVO) {
		if (dailyRecordVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtil.isEmpty(dailyRecordVO.getTitle())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "title不能为空");
		} else if (StringUtil.isEmpty(dailyRecordVO.getContent())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "content不能为空");
		} else if (dailyRecordVO.getRecordGroupId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordGroupId不能为空");
		} else if (dailyRecordVO.getRecordId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordId不能为空");
		} else if (StringUtil.isEmpty(dailyRecordVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		logger.info("【{}】更新日记入参：{}", dailyRecordVO.getUserCode(), dailyRecordVO.toString());
				
		return dailyRecordService.updateByPrimaryKeySelective(dailyRecordVO);
	}
	
	@RequestMapping(value="/deleteDailyRecord", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity deleteDailyRecord(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DailyRecordVO dailyRecordVO) {
		if (dailyRecordVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (dailyRecordVO.getRecordId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordId不能为空");
		} 
		
		logger.info("【{}】删除日记入参：{}", dailyRecordVO.getUserCode(), dailyRecordVO.toString());
		// 物理删除
		if (dailyRecordVO.getDeleteFlag() != null && dailyRecordVO.getDeleteFlag() == TypeConstant.DEL_FLAG_1) {
			return dailyRecordService.deleteDailyRecord(dailyRecordVO);
		}
		
		// 逻辑删除
		DailyRecordVO dailyRecordVODel = new DailyRecordVO();
		dailyRecordVODel.setRecordId(dailyRecordVO.getRecordId());
		dailyRecordVODel.setDeleteFlag(TypeConstant.DEL_FLAG_1);
		return dailyRecordService.updateByPrimaryKeySelective(dailyRecordVO);
	}
	
	@RequestMapping(value="/getDRListForMonth", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getDRListForMonth(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DailyRecordVO dailyRecordVO) {
		if (dailyRecordVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtils.isEmpty(dailyRecordVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		} 
		
//		logger.info("查询【{}】缓存日记入参：{}", dailyRecordVO.getUserCode(), dailyRecordVO.toString());
		
		UserConfigTO userConfig = webBaseService.getUserInfoByCache(request); 
		if (userConfig != null) {
			dailyRecordVO.setLoginCode(userConfig.getUserCode());
			return dailyRecordService.getDRListByCache(dailyRecordVO);
		}
		
		logger.info("查询【{}】缓存日记失败", dailyRecordVO.getUserCode(), dailyRecordVO.toString());
		return new ResultEntity(ResultParam.FAIlED);
	}
	
	@RequestMapping(value="/getDailyRecordInfo", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getDailyRecordInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DailyRecordVO dailyRecordVO) {
		if (dailyRecordVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (dailyRecordVO.getRecordId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordId不能为空");
		} 
		
		logger.info("查询【{}】日记信息入参：{}", dailyRecordVO.getUserCode(), JSONObject.toJSONString(dailyRecordVO));
		return dailyRecordService.getDailyRecord(dailyRecordVO);
	}
	
	@RequestMapping(value="/putDiscussInfo", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity putDiscussInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DiscussInfoVO discussInfoVO) {
		if (discussInfoVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (discussInfoVO.getRecordId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordId不能为空");
		}  else if (StringUtils.isEmpty(discussInfoVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}  else if (StringUtils.isEmpty(discussInfoVO.getContent())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "content不能为空");
		} 
		
//		logger.info("【{}】新增日记评论信息入参：{}", discussInfoVO.getUserCode(), JSONObject.toJSONString(discussInfoVO));
		discussInfoVO.setAgreeNum(0);
		discussInfoVO.setCreateTime(new Date());
		return discussInfoService.insertDiscussInfo(discussInfoVO);
	}
	
	@RequestMapping(value="/patchDRReviewNum", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchDRReviewNum(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DailyRecordVO dailyRecordVO) {
		if (dailyRecordVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (dailyRecordVO.getRecordId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordId不能为空");
		} else if (StringUtils.isEmpty(dailyRecordVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
		if (userConfig != null && userConfig.getUserCode().equals(dailyRecordVO.getUserCode()) == false) {
			dailyRecordVO.setLoginCode(userConfig.getUserCode());
			dailyRecordVO.setReviewNum(1);
			dailyRecordVO.setCollectNum(null);
			return dailyRecordService.updateDRNum(dailyRecordVO, TypeConstant.DR_REVIEW_USER);
		}
		
		logger.warn("更新【{}】日记浏览数量失败，用户不能对自己日记的浏览数量更新", dailyRecordVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED.getCode());
	}
	
	@RequestMapping(value="/patchDRCollectNum", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchDRCollectNum(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DailyRecordVO dailyRecordVO) {
		if (dailyRecordVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (dailyRecordVO.getRecordId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordId不能为空");
		} else if (StringUtils.isEmpty(dailyRecordVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
		if (userConfig != null && userConfig.getUserCode().equals(dailyRecordVO.getUserCode()) == false) {
			dailyRecordVO.setLoginCode(userConfig.getUserCode());
			dailyRecordVO.setCollectNum(1);
			dailyRecordVO.setReviewNum(null);
			return dailyRecordService.updateDRNum(dailyRecordVO, TypeConstant.DR_COLLECT_USER);
		}
		
		logger.warn("更新【{}】日记收藏数量失败，用户不能对自己日记的收藏数量更新", dailyRecordVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED.getCode());
	}
	
	@RequestMapping(value="/patchDisNum", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity patchDisNum(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DiscussInfoVO discussInfoVO) {
		if (discussInfoVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (discussInfoVO.getRecordId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "recordId不能为空");
		} else if (discussInfoVO.getDiscussId() == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "discussId不能为空");
		}  else if (StringUtils.isEmpty(discussInfoVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
		if (userConfig != null && userConfig.getUserCode().equals(discussInfoVO.getUserCode()) == false) {
			discussInfoVO.setLoginCode(userConfig.getUserCode());
			// 只要不为null就行
			discussInfoVO.setAgreeNum(1);
			return discussInfoService.updateDisNum(discussInfoVO);
		}
		
		logger.warn("更新【{}】评论点赞数量失败，用户不能对自己的评论点赞数量更新", discussInfoVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED.getCode());
	}
	
	@RequestMapping(value="/getUserCollectList", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getUserCollectList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody DailyRecordVO dailyRecordVO) {
		if (dailyRecordVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		} else if (StringUtils.isEmpty(dailyRecordVO.getUserCode())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "userCode不能为空");
		}
		
		UserConfigTO userConfig = webBaseService.getUserInfoByCache(request);
		if (userConfig != null && userConfig.getUserCode().equals(dailyRecordVO.getUserCode())) {
			dailyRecordVO.setLoginCode(userConfig.getUserCode());
			return dailyRecordService.getCollectList(dailyRecordVO);
		}
		
		logger.warn("查询用户【{}】收藏日记列表失败，用户不能查询别人的收藏日记", dailyRecordVO.getUserCode());
		return new ResultEntity(ResultParam.FAIlED);
	}
	
	
}
