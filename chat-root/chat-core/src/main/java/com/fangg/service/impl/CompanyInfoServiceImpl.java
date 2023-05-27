package com.fangg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.CompanyInfo;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.vo.ChatLogVO;
import com.fangg.bean.chat.vo.CompanyInfoVO;
import com.fangg.bean.chat.vo.SysUserVO;
import com.fangg.bean.chat.vo.UserCompanyVO;
import com.fangg.config.PasswordHelper;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.dao.ChatLogMapper;
import com.fangg.dao.CompanyInfoMapper;
import com.fangg.dao.SysUserMapper;
import com.fangg.exception.ChatException;
import com.fangg.service.CompanyInfoService;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.StringUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;
import com.xclj.tk.service.impl.BaseServiceImpl;

@Service(value="companyInfoService")
public class CompanyInfoServiceImpl extends BaseServiceImpl<CompanyInfo> implements CompanyInfoService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyInfoServiceImpl.class);

	@Autowired
	RedisClientTemplate redisClientTemplate;
    @Autowired
    CompanyInfoMapper companyInfoMapper;
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    ChatLogMapper chatLogMapper;
    @Autowired
    PasswordHelper passwordHelper;

	/**
	 * 批量新增企业信息
	 */
	public ResultEntity insertCompanyInfoByBatch(List<CompanyInfoVO> companyInfoVOList) {
		logger.info("批量新增企业信息入参数量：{}", companyInfoVOList.size());
		ResultEntity resultEntity = new ResultEntity();
		try {
			if (companyInfoVOList != null && companyInfoVOList.size() > 0) {
				int result = companyInfoMapper.insertCompanyInfoByBatch(companyInfoVOList);
				if (result > 0) {
					logger.info("批量新增企业信息成功数量：{}", result);
				} else {
					logger.warn("批量新增企业信息失败");
					resultEntity.setResultBody(ResultParam.FAIlED);
				}
			} else {
				logger.warn("批量新增企业信息失败，信息不能为空");
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} catch (Exception e) {
			logger.error("批量新增企业信息异常：", e);
			resultEntity.setResultBody(ResultParam.FAILED_UNKNOW.getCode(), "批量新增企业信息异常，请联系系统管理员。");
		}
		
		return resultEntity;
	}

	/**
	 * 新增企业信息 
	 */
	public ResultEntity insertCompanyInfo(CompanyInfoVO companyInfoVO) {
		logger.info("新增企业信息入参：{}", JSONObject.toJSONString(companyInfoVO));
		companyInfoVO.setCreateTime(new Date());
		companyInfoVO.setDeleteFlag(TypeConstant.DEL_FLAG_0);
		companyInfoVO.setStatus(TypeConstant.STATUS_TYPE_0);
		List<CompanyInfoVO> companyInfoVOList = new ArrayList<>();
		ResultEntity resultEntity = insertCompanyInfoByBatch(companyInfoVOList);
		if (resultEntity.getCode() == ResultParam.SUCCESS.getCode()) {
			createNewChatTable(companyInfoVO);
		}
		return resultEntity;
	}
	
	/**
	 * 创建对应企业聊天记录表
	 */
	private void createNewChatTable(CompanyInfoVO companyInfoVO) {
		logger.info("判断【{}】数据行表是否存在", companyInfoVO.getCompanyName());
		boolean tableFlag = false;
		ChatLogVO chatLogVONEW = new ChatLogVO();
		chatLogVONEW.setCompanyCode(companyInfoVO.getCompanyCode());
		
		try {
            Integer count = chatLogMapper.checkTableExistsWithSchema(chatLogVONEW);
            if (count > 0) {
				tableFlag = true;
			}
        } catch (Exception e) {
            logger.warn("使用information_schema检测表失败：", e);
            Map<String, Object> resultMap = chatLogMapper.checkTableExistsWithShow(chatLogVONEW);
            
            if(resultMap != null && resultMap.isEmpty() == false) {
                tableFlag = true;
            }
        }
		
		if (tableFlag) {
			logger.info("当前【{}】数据行表已经存在", companyInfoVO.getCompanyName());
		} else {
			logger.info("当前【{}】数据行表不存在，需要创建", companyInfoVO.getCompanyName());
			
			int result = chatLogMapper.createNewTable(chatLogVONEW);
			if (result > 0) {
				logger.info("创建【{}】数据行表成功", companyInfoVO.getCompanyName());
			} else {
				logger.error("创建【{}】数据行表失败", companyInfoVO.getCompanyName());
			}
		}
	}

	/**
	 * 查询用户对应企业信息列表
	 */
	public List<UserCompanyTO> getUserCompanyInfoForList(CompanyInfoVO companyInfoVO) {
		return companyInfoMapper.selectUserCompanyInfoForList(companyInfoVO);
	}

	/**
	 * 新增企业用户信息
	 */
	@Transactional(rollbackFor = ChatException.class)
	public ResultEntity insertUserCompanyInfo(SysUserVO sysUserVO) {
		logger.info("新增企业用户信息入参：{}", JSONObject.toJSONString(sysUserVO));
		ResultEntity resultEntity = new ResultEntity();

		// 回滚不能有异常处理
		try {
			// 这里的用户编号即为企业编号
			/*sysUserVO.setUserCode("C"+UuidUtil.genUuid(9));
			sysUserVO.setDeleteFlag(TypeConstant.DEL_FLAG_0);
			sysUserVO.setCreateTime(new Date());
			sysUserVO.setStatus(TypeConstant.STATUS_TYPE_0);
			// 密码加密处理
			passwordHelper.encryptPassword(sysUserVO);
			// 用户类型：企业
			sysUserVO.setUserType(TypeConstant.USER_TYPE_4);
			
			List<SysUserVO> sysUserVOList = new ArrayList<>();
			sysUserVOList.add(sysUserVO);
			
			int result = sysUserMapper.insertUserByBatch(sysUserVOList);
			if (result > 0) {
				SysUser sysUserIn = new SysUser();
				sysUserIn.setUserCode(sysUserVO.getUserCode());
				sysUserIn.setUserType(TypeConstant.USER_TYPE_4);
				SysUser sysUserOut = sysUserMapper.selectOne(sysUserIn);
				if (sysUserOut != null) {
					// 新增企业信息
					CompanyInfoVO companyInfoNew = new CompanyInfoVO();
					
					UserCompanyVO userCompanyVO = new UserCompanyVO();
					userCompanyVO.setUserId(sysUserOut.getUserId());
					userCompanyVO.setCompanyCode(sysUserVO.getUserCode());
					userCompanyVO.setCompanyName(sysUserVO.getUsername());

					result = companyInfoMapper.insertUserCompanyInfo(userCompanyVO);
					if (result > 0) {
						logger.info("新增【{}】企业用户信息成功", userCompanyVO.getCompanyCode());
						resultEntity.setData(userCompanyVO.getCompanyCode());
						
						// 缓存客服所属信息
						redisClientTemplate.setMapValue(RedisConstant.CUSTOMER_SERVICE_KEY, userCompanyVO.getUserCode(), 
								JSONObject.toJSONString(userCompanyVO));
					} else {
						logger.warn("新增【{}】企业用户信息失败", userCompanyVO.getCompanyCode());
						resultEntity.setResultBody(ResultParam.FAIlED);
					}
				} else {
					logger.warn("新增【{}】企业用户信息失败，用户信息不存在", sysUserVO.getUserCode());
					resultEntity.setResultBody(ResultParam.FAIlED);
				}
			}*/
		} catch (Exception e) {
			logger.error("新增【{}】企业用户信息异常：", e);
			//resultEntity.setResultBody(ResultParam.FAIlED.getCode(), "新增客服企业外键表信息异常，请联系系统管理员。");
			throw new ChatException("新增企业用户信息异常，请联系系统管理员。");
		}
		
		return resultEntity;
	}

	/**
	 * 客服对应所属信息 
	 */
	public UserCompanyTO getCustomerServiceCompanyInfo(UserCompanyVO userCompanyVO) {
		UserCompanyTO userCompanyTOOut = null;
		try {
			String resultStr = redisClientTemplate.getMapValue(RedisConstant.CUSTOMER_SERVICE_KEY, userCompanyVO.getUserCode());
			if (StringUtil.isEmpty(resultStr)) {
				userCompanyTOOut = companyInfoMapper.selectUserCompanyInfo(userCompanyVO);
				if (userCompanyTOOut != null && StringUtil.isNotEmpty(userCompanyTOOut.getCompanyName())) {
					logger.info("获取客服对应所属信息并缓存");
					redisClientTemplate.setMapValue(RedisConstant.CUSTOMER_SERVICE_KEY, userCompanyVO.getUserCode(), 
							JSONObject.toJSONString(userCompanyTOOut));
				}
			} else {
				return JSONObject.parseObject(resultStr, UserCompanyTO.class);
			}
		} catch (Exception e) {
			logger.error("获取客服对应所属信息异常：", e);
		}
		
		return userCompanyTOOut;
	}

	/**
	 * 客服信息列表(与企业关联的)
	 */
	public List<UserCompanyTO> getCustomerServiceList(UserCompanyVO userCompanyVO) {
		return companyInfoMapper.selectCustomerServiceList(userCompanyVO);
	}

}