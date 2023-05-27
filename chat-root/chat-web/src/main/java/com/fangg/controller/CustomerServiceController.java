package com.fangg.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.vo.UserCompanyVO;
import com.fangg.service.CompanyInfoService;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * 客服
 * @author fangg
 * 2022年3月16日 上午11:55:34
 */
@Controller
@RequestMapping("/cs")
public class CustomerServiceController {
	private static Logger logger = LoggerFactory.getLogger(CustomerServiceController.class);

	@Value("${aes.private.key}")
	private String aesPrivateKey;

//	@Autowired
//	RedisClientTemplate redisClientTemplate;
//	@Autowired
//	ThreadPoolTaskExecutor taskExecutor;
//	@Autowired
//	WebBaseService webBaseService;
	@Autowired
	CompanyInfoService companyInfoService;
//	@Autowired
//	SysUserService sysUserService;

	@RequestMapping(value="/getCustomerServiceList", method=RequestMethod.POST, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getCustomerServiceList(HttpServletRequest request, HttpServletResponse response, 
			@RequestBody UserCompanyVO userCompanyVO) {
//		logger.info("查询日记分组入参：dailyRecordVO={}", JSONObject.toJSONString(recordGroupVO));
		if (userCompanyVO == null) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "参数不能为空");
		}
		
		List<UserCompanyTO> csUserList = companyInfoService.getCustomerServiceList(userCompanyVO);
		if (csUserList == null) {
			csUserList = new ArrayList<>();
		}
		
		return new ResultEntity(csUserList);
	}
		
	
}
