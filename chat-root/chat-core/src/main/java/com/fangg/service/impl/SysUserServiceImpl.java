package com.fangg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.query.PermissionFunc;
import com.fangg.bean.chat.query.SysUser;
import com.fangg.bean.chat.query.UserChatGroup;
import com.fangg.bean.chat.query.UserConfig;
import com.fangg.bean.chat.to.CityTO;
import com.fangg.bean.chat.to.ProvinceTO;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.SysUserVO;
import com.fangg.bean.chat.vo.UserCompanyVO;
import com.fangg.bean.chat.vo.UserConfigVO;
import com.fangg.config.PasswordHelper;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.dao.SysUserMapper;
import com.fangg.dao.UserChatGroupMapper;
import com.fangg.dao.UserConfigMapper;
import com.fangg.exception.ChatException;
import com.fangg.service.CompanyInfoService;
import com.fangg.service.SysUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.DateUtil;
import com.xclj.common.util.StringUtil;
import com.xclj.common.util.UuidUtil;
import com.xclj.page.PageEntity;
import com.xclj.page.PageResult;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;
import com.xclj.tk.service.impl.BaseServiceImpl;

@Service(value="sysUserService")
public class SysUserServiceImpl extends BaseServiceImpl<SysUser> implements SysUserService {

    private static final Logger logger = LoggerFactory.getLogger(SysUserServiceImpl.class);
    
    @Autowired
    PasswordHelper passwordHelper;
    @Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    UserConfigMapper userConfigMapper;
	@Autowired
	CompanyInfoService companyInfoService;
	@Autowired
	UserChatGroupMapper userChatGroupMapper;
    
    @Override
    public SysUser selectOne(SysUser sysUserIn) {
    	// 只能查询没有删除标识的用户
    	sysUserIn.setDeleteFlag(TypeConstant.DEL_FLAG_0);
    	return super.selectOne(sysUserIn);
    }
    
    @Override
    public List<SysUser> selectAll(SysUser sysUserIn) {
    	// 只能查询没有删除标识的用户
    	sysUserIn.setDeleteFlag(TypeConstant.DEL_FLAG_0);
    	return super.selectAll(sysUserIn);
    }

	/**
	 * 批量新增用户 
	 */
	//@Transactional(rollbackFor = ChatException.class)
	public ResultEntity insertUserByBatch(List<SysUserVO> sysUserVOList) {
		logger.info("批量新增用户入参数量：{}", sysUserVOList.size());
		ResultEntity resultEntity = new ResultEntity();
		try {
			if (sysUserVOList != null && sysUserVOList.size() > 0) {
				int result = sysUserMapper.insertUserByBatch(sysUserVOList);
				if (result > 0) {
					logger.info("批量新增用户成功数量：{}", result);
				} else {
					logger.warn("批量新增用户失败");
					resultEntity.setResultBody(ResultParam.FAIlED);
				}
			} else {
				logger.warn("批量新增用户失败，用户信息为空");
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} catch (Exception e) {
			logger.error("批量新增用户异常：", e);
			resultEntity.setResultBody(ResultParam.FAILED_UNKNOW.getCode(), "批量新增用户异常，请联系系统管理员。");
		}
		
		return resultEntity;
	}

	/**
	 * 新增用户
	 */
	public ResultEntity inserSysUser(SysUserVO sysUserVO) {
		// 加盐处理
		passwordHelper.encryptPassword(sysUserVO);
		List<SysUserVO> sysUserVOList = new ArrayList<SysUserVO>();
		sysUserVOList.add(sysUserVO);
		ResultEntity resultEntity = insertUserByBatch(sysUserVOList);
		if (resultEntity.getCode() == ResultParam.SUCCESS.getCode()) {
			// 缓存用户头像路径
			if (StringUtils.isNotEmpty(sysUserVO.getProfilePicture())) {
				redisClientTemplate.setMapValue(RedisConstant.UG_PP_KEY, sysUserVO.getUserCode(), sysUserVO.getProfilePicture());
			}
		}
		return resultEntity;
	}

	/**
	 * 获取登录用户信息 
	 */
	public UserConfigTO getLoginSysUserInfo(SysUserVO sysUserVO) {
		String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+sysUserVO.getTicket());
		if (userStr != null) {
			return JSONObject.parseObject(userStr, UserConfigTO.class);
		}
		return null;
	}

	/**
	 * 用户登录
	 */
	public ResultEntity userLogin(SysUserVO sysUserVO) {
		SysUser sysUserIn = new SysUser();
		sysUserIn.setUserCode(sysUserVO.getUserCode());
		sysUserIn.setDeleteFlag(TypeConstant.DEL_FLAG_0);	// 没有删除
		sysUserIn.setStatus(TypeConstant.STATUS_TYPE_0);	// 没有禁用
		//sysUserIn.setUserType(sysUserVO.getUserType());		// 用户类型（1用户/0客服）
		SysUser sysUserOut = sysUserMapper.selectOne(sysUserIn);
		//UserConfigTO userConfig = userConfigMapper.selectUserConfigInfoForLogin(sysUserVO);
		if (sysUserOut != null) {
			if (sysUserOut.getUserType() != sysUserVO.getUserType()) {
				return new ResultEntity(ResultParam.FAIlED, "账号类型不匹配");
			}
			
			sysUserVO.setSalt(sysUserOut.getSalt());
			passwordHelper.decryptPassword(sysUserVO);
			
			// 判断加密密码相同
			if (sysUserVO.getPassword().equals(sysUserOut.getPassword())) {
				// 判断上一次登录IP()
				if (StringUtils.isNotEmpty(sysUserOut.getLoginIp())) {
					if (sysUserIn.getLoginType() != sysUserOut.getLoginType() 
							|| sysUserIn.getLoginIp() != sysUserOut.getLoginIp()) {
						// 给用户手机发送通知
						
					}
				}

				// 查询配置信息(不放在一起查询是为了新增配置属性时少些改动)
				UserConfig userConfigIn = new UserConfig();
				userConfigIn.setUserCode(sysUserVO.getUserCode());
				UserConfig userConfigOut = userConfigMapper.selectOne(userConfigIn);

				// 判断账号过期时间
				if (userConfigOut.getOverTime() != null) {
					Date now_date = new Date();
					if (now_date.after(userConfigOut.getOverTime())) {
						logger.warn("【{}】账号过期，无法登录", sysUserVO.getUserCode());
						return new ResultEntity(ResultParam.FAIlED, "账号失效");
					}
				}
				
				// 判断绑定信息
				ResultEntity bindEntity = checkBindInfo(sysUserVO, sysUserOut, userConfigOut);
				if (bindEntity != null) {
					return bindEntity;
				}
				
				// 判断是否存在ticket，如果IP也相同则直接返回
				ResultEntity loginEntity = checkLoginState(sysUserVO, sysUserOut);
				if (loginEntity != null) {
					return loginEntity;
				}
				
				sysUserOut.setSalt(null);
				sysUserOut.setPassword(null);
				sysUserOut.setTicket(UuidUtil.genUuid_0(12));
				sysUserOut.setOnlineStatus(TypeConstant.ONLINE_STATUS_1);
				
				SysUser sysUserUp = new SysUser();
				sysUserUp.setUserId(sysUserOut.getUserId());
				sysUserUp.setTicket(sysUserOut.getTicket());
				sysUserUp.setOnlineStatus(TypeConstant.ONLINE_STATUS_1);
				// 登录IP
				sysUserUp.setLoginIp(sysUserVO.getLoginIp());
				// 登录设备类型
				sysUserUp.setLoginType(sysUserVO.getLoginType());
				// 初次登录判断
				if (sysUserOut.getLoginAgain() == null) {
					sysUserUp.setLoginAgain(TypeConstant.LOGIN_AGAIN_0);
					sysUserOut.setLoginAgain(TypeConstant.LOGIN_AGAIN_0);
				} else if (TypeConstant.LOGIN_AGAIN_0 == sysUserOut.getLoginAgain()) {
					sysUserUp.setLoginAgain(TypeConstant.LOGIN_AGAIN_1);
					sysUserOut.setLoginAgain(TypeConstant.LOGIN_AGAIN_1);
				}
				
				int result = sysUserMapper.updateByPrimaryKeySelective(sysUserUp);
				if (result > 0) {
					UserConfigTO userConfig = new UserConfigTO();
					BeanUtils.copyProperties(sysUserOut, userConfig);
					
					if (userConfigOut != null) {
						BeanUtils.copyProperties(userConfigOut, userConfig, new String[]{"userCode", "bindIp"});
						if (StringUtils.isNotEmpty(userConfigOut.getBindIp())) {
							Map<Integer, String> bindMap = JSONObject.parseObject(userConfigOut.getBindIp(), HashMap.class);
							userConfig.setBindIp(bindMap);
						}
						
						// 黑名单（需要在前端用户列表右键时标识出来）
						userConfig.setBlackUserList(JSONArray.parseArray(userConfigOut.getBlackList(), String.class));
					}
					
					// 判断功能权限
					checkPermissionFunc(userConfig);
					
					// 登录识别码
					// 不同类型设备登录（手机，Pad，WEB）
					Map<Integer, String> loginIdeMap = new HashMap<>();
					loginIdeMap.put(sysUserVO.getLoginType(), sysUserVO.getLoginIdentifier()+"_"+sysUserVO.getFingerPrint());
					userConfig.setLoginIdentifierMap(loginIdeMap);

					// 判断用户的companyCode(客服为所属企业，用户为SYSTEM)
					String companyCode = TypeConstant.SYSTEM_CODE;
					if (sysUserOut.getUserType() == TypeConstant.USER_TYPE_0) {
						UserCompanyVO userCompanyVOIn = new UserCompanyVO();
						userCompanyVOIn.setUserCode(sysUserVO.getUserCode());
						UserCompanyTO userCompanyTO = companyInfoService.getCustomerServiceCompanyInfo(userCompanyVOIn);
						if (userCompanyTO != null) {
							companyCode = userCompanyTO.getCompanyCode();
						}
					}
					userConfig.setCompanyCode(companyCode);
					// 前端登录时生成的aes密钥
					userConfig.setAesKey(sysUserVO.getAesKey());
					// 前端登录时的指纹ID(已经在loginIdeMap中)
					//userConfig.setFingerPrint(sysUserVO.getFingerPrint());
					
					// 缓存登录用户信息
					redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+userConfig.getTicket(), 
							JSONObject.toJSONString(userConfig), TimeoutConstant.LOGIN_TIMEOUT);
					// 用户上线缓存
					redisClientTemplate.setMapValue(RedisConstant.USER_ONLINE_KEY, sysUserVO.getUserCode(), 
							String.valueOf(TypeConstant.ONLINE_STATUS_1));
					
					// 不返回前端
					userConfig.setFuncPermission4Server(null);
					ResultEntity resultEntity = new ResultEntity();
					resultEntity.setData(userConfig);
					return resultEntity;
				}
			}
		}
		
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 登录状态判断
	 */
	private ResultEntity checkLoginState(SysUserVO sysUserVO, SysUser sysUserOut) {
		if (StringUtil.isNotEmpty(sysUserOut.getTicket())) {
			SysUserVO sysUserVOIn = new SysUserVO();
			sysUserVOIn.setTicket(sysUserOut.getTicket());
			UserConfigTO userConfigCache = getLoginSysUserInfo(sysUserVOIn);
			if (userConfigCache != null) {
				// 更新aesKey
				userConfigCache.setAesKey(sysUserVO.getAesKey());
				
				// 不同类型设备登录（手机，Pad，WEB）
				Map<Integer, String> loginIdeMap = userConfigCache.getLoginIdentifierMap();
				
				// 如果登录IP相同，并且保存的登录状态为--在线
				if (sysUserVO.getLoginIp().equals(sysUserOut.getLoginIp())) {
					if (TypeConstant.ONLINE_STATUS_1 == sysUserOut.getOnlineStatus()) {
						// 同一类型已经登录在线时
						if (loginIdeMap.containsKey(sysUserVO.getLoginType())) {
							//return new ResultEntity(ResultParam.FAIlED, "重复登录");
							return loginByCheck(sysUserOut, userConfigCache);
						} else {
							return loginWithSameTicket(sysUserVO, sysUserOut, userConfigCache, loginIdeMap);
						}
					} 
					// 上次登录没有正常退出登录（可能打开了第二个窗口进行登录，也可能直接关闭的窗口），需要用户验证
					else if (TypeConstant.ONLINE_STATUS_0 == sysUserOut.getOnlineStatus() 
							&& loginIdeMap.containsKey(sysUserVO.getLoginType())) {
						//更新缓存用户信息状态--验证状态
						//userConfigCache.setCheckLoginFlag(true);
						
						return loginByCheck(sysUserOut, userConfigCache);
					}
				} 
				else if (TypeConstant.ONLINE_STATUS_1 == sysUserOut.getOnlineStatus()) {
					if (loginIdeMap.containsKey(sysUserVO.getLoginType())) {
						return new ResultEntity(ResultParam.LOGIN_LOCATION_UNKNOW);
					} 
					// 这里判断可能有问题，最好能使用百度地图API定位地址是否相同
					else {
						return loginWithSameTicket(sysUserVO, sysUserOut, userConfigCache, loginIdeMap);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 绑定信息判断
	 */
	private ResultEntity checkBindInfo(SysUserVO sysUserVO, SysUser sysUserOut, UserConfig userConfigOut) {
		if (userConfigOut != null && userConfigOut.getBindIp() != null) {
			Map<Integer, String> bindMap = JSONObject.parseObject(userConfigOut.getBindIp(), HashMap.class);
			if (bindMap.containsKey(sysUserVO.getLoginType())) {
				if (TypeConstant.LOGIN_TYPE_0 == sysUserVO.getLoginType()) {
					String web = bindMap.get(sysUserVO.getLoginType());
					if (web.indexOf("_") != -1) {
						if (web.startsWith(sysUserVO.getLoginIp()) == false 
								|| web.endsWith(sysUserVO.getFingerPrint()) == false) {
							logger.warn("用户【{}】登录失败，与绑定信息不匹配--ip={}, loginType={}", sysUserVO.getUserCode(), 
									sysUserVO.getLoginIp(), sysUserVO.getLoginType());
							// 如果账号不在线，则发短信通知
							if (TypeConstant.ONLINE_STATUS_0 == sysUserOut.getOnlineStatus()) {
								logger.warn("您的账号【{}】正在别处登录，请确认是本人操作……", sysUserOut.getUserCode());
							}
							return new ResultEntity(ResultParam.FAIlED, "与绑定信息不匹配");
						}
					} else if (web.equals(sysUserVO.getLoginIp()) == false) {
						logger.warn("用户【{}】登录失败，与绑定信息不匹配--ip={}, loginType={}", sysUserVO.getUserCode(), 
								sysUserVO.getLoginIp(), sysUserVO.getLoginType());
						// 如果账号不在线，则发短信通知
						if (TypeConstant.ONLINE_STATUS_0 == sysUserOut.getOnlineStatus()) {
							logger.warn("您的账号【{}】正在别处登录，请确认是本人操作……", sysUserOut.getUserCode());
						}
						return new ResultEntity(ResultParam.FAIlED, "与绑定信息不匹配");
					}
				} else if (TypeConstant.LOGIN_TYPE_1 == sysUserVO.getLoginType()
						|| TypeConstant.LOGIN_TYPE_2 == sysUserVO.getLoginType()) {
					if (bindMap.get(sysUserVO.getLoginType()).equals(sysUserVO.getFingerPrint()) == false) {
						logger.warn("用户【{}】登录失败，绑定信息不匹配--IMIE={},ip={},loginType={}", sysUserVO.getUserCode(), 
								sysUserVO.getFingerPrint(), sysUserVO.getLoginIp(), sysUserVO.getLoginType());
						// 如果账号不在线，则发短信通知
						if (TypeConstant.ONLINE_STATUS_0 == sysUserOut.getOnlineStatus()) {
							logger.warn("您的账号【{}】正在别处登录，请确认是本人操作……", sysUserOut.getUserCode());
						}
						return new ResultEntity(ResultParam.FAIlED, "与绑定信息不匹配");
					}
				}
			}
		}
		return null;
	}

	/**
	 * 通过验证再次登录(否则服务器宕机时可能无法及时登录)
	 */
	private ResultEntity loginByCheck(SysUser sysUserOut, UserConfigTO userConfigCache) {
		// 验证识别码(X,Y坐标)
		String xy = StringUtil.getXY();
		Date date = DateUtil.getNextTimeWithSeconds(new Date(), TimeoutConstant.CHECK_TIMEOUT);
		JSONObject ideJson = new JSONObject();
		ideJson.put("checkIdentifier", xy);
		ideJson.put("timeout", date);
		ideJson.put("ticket", sysUserOut.getTicket());
		
		userConfigCache.setCheckIdentifier(xy);
		userConfigCache.setCheckTimeout(date);
		
		// 更新用户缓存信息 
		redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+sysUserOut.getTicket(), 
				JSONObject.toJSONString(userConfigCache), TimeoutConstant.LOGIN_TIMEOUT);
		ResultEntity resultEntity = new ResultEntity(ResultParam.LOGIN_CHECK_FAILED);
		resultEntity.setData(ideJson);
		return resultEntity;
	}

	/**
	 * 使用相同的ticket登录
	 */
	private ResultEntity loginWithSameTicket(SysUserVO sysUserVO, SysUser sysUserOut, UserConfigTO userConfigCache,
			Map<Integer, String> loginIdeMap) {
		ResultEntity resultEntity = new ResultEntity();
		loginIdeMap.put(sysUserVO.getLoginType(), sysUserVO.getLoginIdentifier()+"_"+sysUserVO.getFingerPrint());
		userConfigCache.setLoginIdentifierMap(loginIdeMap);
		resultEntity.setData(userConfigCache);
		
		SysUser sysUserUp = new SysUser();
		sysUserUp.setUserId(sysUserOut.getUserId());
		// 登录IP
		sysUserUp.setLoginIp(sysUserVO.getLoginIp());
		// 登录设备类型
		sysUserUp.setLoginType(sysUserVO.getLoginType());
		// 最后登录状态
		int result = sysUserMapper.updateByPrimaryKeySelective(sysUserUp);
		if (result > 0) {

			// 更新用户缓存信息 
			redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+sysUserOut.getTicket(), 
					JSONObject.toJSONString(userConfigCache), TimeoutConstant.LOGIN_TIMEOUT);
			return resultEntity;
		} else {
			return new ResultEntity(ResultParam.FAIlED);
		}
	}

	/**
	 * 处理用户功能权限
	 */
	private List<Map<String, Object>> checkPermissionFunc(UserConfigTO userConfig) {
		/*List<Map<String, Object>> outside = new ArrayList<>();
		String perStr = redisClientTemplate.getString(RedisConstant.PERMISSION_FUNC_KEY);
		if (perStr != null) {
			List<PermissionFunc> listPer = JSONArray.parseArray(perStr, PermissionFunc.class);
			Map<String, Object> map = null;
			PermissionFuncTO perInside = new PermissionFuncTO();
			Map<String, PermissionFunc> paramMap = new HashMap<>();
			
			for (PermissionFunc per : listPer) {
				if (userConfig.getGrade() >= per.getGrade()) {
					map = new HashMap<>();
					map.put("descript", per.getFunctionName());
					map.put("code", per.getFunctionCode());
					map.put("limit", per.getLimitNum());
					outside.add(map);
					
					paramMap.put(per.getFunctionCode(), per);
				}
			}

			// 判断用户权限
			checkPermissionFunc(paramMap, perInside, userConfig.getUserCode());
			
//			perTO.setListWebPermission(result);
			userConfig.setFuncPermission(outside);
			userConfig.setFuncPermissionInside(perInside);
			
			// 用户权限缓存(放在ticket中一起)
//			redisClientTemplate.setMapValue(RedisConstant.USER_PER_FUNC_KEY, userConfig.getUserCode(), 
//					JSONObject.toJSONString(perTO));
		}*/
		
		List<Map<String, Object>> outside = new ArrayList<>();
		Map<String, PermissionFunc> fp4Server = new HashMap<>();
		
		String perStr = redisClientTemplate.getString(RedisConstant.PERMISSION_FUNC_KEY);
		if (perStr != null) {
			List<PermissionFunc> listPer = JSONArray.parseArray(perStr, PermissionFunc.class);
			Map<String, Object> map = null;
			
			for (PermissionFunc per : listPer) {
				if (userConfig.getGrade() >= per.getGrade()) {
					// web端功能权限
					map = new HashMap<>();
					map.put("descript", per.getFunctionName());
					map.put("code", per.getFunctionCode());
					map.put("limit", per.getLimitNum());
					outside.add(map);
					
					// 服务端功能权限
					fp4Server.put(per.getFunctionCode(), per);
				}
			}
			
			// 判断用户权限
			
//			perTO.setListWebPermission(result);
			userConfig.setFp4Web(outside);
			userConfig.setFuncPermission4Server(fp4Server);
			
			// 用户权限缓存(放在ticket中一起)
//			redisClientTemplate.setMapValue(RedisConstant.USER_PER_FUNC_KEY, userConfig.getUserCode(), 
//					JSONObject.toJSONString(perTO));
		}
		return outside;
	}

	/**
	 * 通过类反射机制判断用户权限（缓存用）
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	/*@Deprecated
	private void checkPermissionFunc(Map<String, PermissionFunc> paramMap, PermissionFuncTO perTO, 
			String userCode) {
		try {
			Field [] fields = perTO.getClass().getDeclaredFields();
			PermiFuncAnnotation perAnnotation = null;
			PermissionEntity perEntity = null;
			for (Field field : fields) {
				perAnnotation = field.getAnnotation(PermiFuncAnnotation.class);
				if (perAnnotation == null) {
					continue;
				}
				field.setAccessible(true);
				if (paramMap.containsKey(perAnnotation.code())) {
					// limit=true表示有数量限制
					if (perAnnotation.limit()) {
						perEntity = new PermissionEntity();
						perEntity.setPermission(true);
						perEntity.setLimit(paramMap.get(perAnnotation.code()).getLimitNum());
						field.set(perTO, perEntity);
					} else {
						field.setBoolean(perTO, true);
					}
				}
				// 没有权限的属性值为NULL
			}
		} catch (Exception e) {
			logger.error("通过类反射机制判断【{}】用户权限异常：", userCode, e);
		}
	}*/

	/**
	 * 用户注册
	 */
	@Transactional(rollbackFor = ChatException.class)
	public ResultEntity userRegist(SysUserVO sysUserVO) {
		logger.info("用户注册入参：{}", sysUserVO.toString());
		ResultEntity resultEntity = new ResultEntity();
		sysUserVO.setUserCode("U"+UuidUtil.genUuid(8));
		sysUserVO.setDeleteFlag(TypeConstant.DEL_FLAG_0);
		sysUserVO.setCreateTime(new Date());
		sysUserVO.setOnlineStatus(TypeConstant.ONLINE_STATUS_1);
		sysUserVO.setStatus(TypeConstant.STATUS_TYPE_0);
		sysUserVO.setUserType(TypeConstant.USER_TYPE_1);
		
		// 密码加密处理
		passwordHelper.encryptPassword(sysUserVO);
		
		List<SysUserVO> sysUserVOList = new ArrayList<>();
		sysUserVOList.add(sysUserVO);
		int result = sysUserMapper.insertUserByBatch(sysUserVOList);
		if (result > 0) {
			// 新增用户默认配置
			UserConfigVO userConfigVONew = new UserConfigVO();
			userConfigVONew.setUserCode(sysUserVO.getUserCode());
			userConfigMapper.insertUserConfigByDefault(userConfigVONew);
			
			resultEntity.setData(sysUserVO.getUserCode());
			
			// 缓存用户头像路径
			if (StringUtils.isNotEmpty(sysUserVO.getProfilePicture())) {
				redisClientTemplate.setMapValue(RedisConstant.UG_PP_KEY, sysUserVO.getUserCode(), sysUserVO.getProfilePicture());
			}
		} else {
			resultEntity.setResultBody(ResultParam.FAIlED);
		}
		return resultEntity;
	}

	public ResultEntity loginOut(SysUserVO sysUserVO) {
		int result = sysUserMapper.loginOut(sysUserVO);
		if (result > 0) {
			logger.info(String.format("用户%s成功", (sysUserVO.getOnlineStatus()==TypeConstant.ONLINE_STATUS_0?"离线":"上线")));
			if (sysUserVO.getOnlineStatus() == TypeConstant.ONLINE_STATUS_1) {
				// 用户上线缓存
				redisClientTemplate.setMapValue(RedisConstant.USER_ONLINE_KEY, sysUserVO.getUserCode(), String.valueOf(TypeConstant.ONLINE_STATUS_1));
			} else {
				// 用户上线缓存删除
				redisClientTemplate.delMapValue(RedisConstant.USER_ONLINE_KEY, sysUserVO.getUserCode());
				// 新用户通知缓存删除
				redisClientTemplate.delMapValue(RedisConstant.NEW_USER_CHAT_KEY, sysUserVO.getUserCode());
			}
		} else {
			logger.warn(String.format("用户%s失败", (sysUserVO.getOnlineStatus()==TypeConstant.ONLINE_STATUS_0?"离线":"上线")));
			return new ResultEntity(ResultParam.FAIlED);
		}
		return new ResultEntity();
	}

	/**
	 * 省份城市列表查询 
	 */
	public ResultEntity getProvinceCityList() {
		ResultEntity resultEntity = new ResultEntity();
		String resultStr = redisClientTemplate.getString(RedisConstant.PROVINCE_CITY_KEY);
		if (resultStr != null) {
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
			resultEntity.setData(jsonObject);
		} else {
			List<ProvinceTO> provinceList = sysUserMapper.provinceList();
			List<CityTO> cityList = sysUserMapper.cityList();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("province", provinceList);
			jsonObject.put("city", cityList);
			
			// 缓存在redis中
			redisClientTemplate.setString(RedisConstant.PROVINCE_CITY_KEY, jsonObject.toJSONString());
			resultEntity.setData(jsonObject);
		}
		
		return resultEntity;
	}
	
	/**
	 * 分页查询用户列表
	 */
	public PageResult getUserListByPage(PageEntity pageEntity) {
		PageHelper.offsetPage(pageEntity.getOffset(), pageEntity.getRows());
		//PageHelper.startPage(pageEntity.getPage(), pageEntity.getRows());
		//Map<String, Object> params = pageEntity.getParams();
		
		SysUserVO sysUserVOIn = JSONObject.parseObject(JSONObject.toJSONString(pageEntity.getParams()), SysUserVO.class);
		List<UserConfigTO> bookInfoList = sysUserMapper.selectUserListByPage(sysUserVOIn);
		return new PageResult(new PageInfo<UserConfigTO>(bookInfoList));
	}

	/**
	 * 给用户点赞 (同一个用户只能点赞一次)
	 */
	public ResultEntity giveALike(SysUserVO sysUserVO) {
		
		// 获取登录用户信息 
		UserConfigTO sysUser = getLoginSysUserInfo(sysUserVO);
		if (sysUser != null) {
			//logger.info("【{}】给用户【{}】点赞", sysUser.getUsername(), sysUserVO.getUserCode());
			
			String field = sysUser.getUserCode()+"_"+sysUserVO.getUserCode();
			String resultStr = redisClientTemplate.getMapValue(RedisConstant.GIVE_A_LIKE_KEY, field);
			
			if (sysUserVO.getLikeNum() == 1 && resultStr == null) {
				return updateLikeNum(sysUserVO, sysUser, field, true);
			} else if (sysUserVO.getLikeNum() == 0 && resultStr != null) {
				return updateLikeNum(sysUserVO, sysUser, field, false);
			}
			
			return new ResultEntity();
		}
		return new ResultEntity(ResultParam.FAIlED);
	}

	/**
	 * 点赞更新
	 */
	private ResultEntity updateLikeNum(SysUserVO sysUserVO, UserConfigTO sysUser, String field, boolean isLike) {
		SysUser sysUserIn = new SysUser();
		sysUserIn.setUserCode(sysUserVO.getUserCode());
		SysUser userLike = sysUserMapper.selectOne(sysUserIn);
		if (userLike != null) {
			if (userLike.getLikeNum()==null?(sysUserVO.getLikeNum() > 0):(isLike?true:userLike.getLikeNum() > 0)) {
				SysUser sysUserUp = new SysUser();
				sysUserUp.setLikeNum(userLike.getLikeNum()==null?(isLike?1:0):(userLike.getLikeNum()+(isLike?1:-1)));
				sysUserUp.setUserId(userLike.getUserId());
				int result = sysUserMapper.updateByPrimaryKeySelective(sysUserUp);
				if (result > 0) {
					//logger.info("【{}】用户点赞更新成功", sysUserVO.getUserCode());
					// 缓存用户点赞
					if (isLike) {
						redisClientTemplate.setMapValue(RedisConstant.GIVE_A_LIKE_KEY, field, "");
					} else {
						redisClientTemplate.delMapValue(RedisConstant.GIVE_A_LIKE_KEY, field);
					}
					
					// 用户点赞数量(精确数量意义不大)
					String likeNumStr = redisClientTemplate.getMapValue(RedisConstant.USER_LIKE_NUM_KEY, sysUserVO.getUserCode());
					int likeNum = 1;
					if (likeNumStr != null) {
						likeNum = isLike?(likeNum + Integer.parseInt(likeNumStr)):(Integer.parseInt(likeNumStr) - likeNum);
					}
					redisClientTemplate.setMapValue(RedisConstant.USER_LIKE_NUM_KEY, sysUserVO.getUserCode(), String.valueOf(likeNum));
				} else {
					logger.warn("【{}】用户点赞更新失败", sysUserVO.getUserCode());
					return new ResultEntity(ResultParam.FAIlED);
				}
			}
		} else {
			logger.warn("【{}】用户点赞更新失败，用户【{}】不存在", sysUser.getUsername(), sysUserVO.getUserCode());
			return new ResultEntity(ResultParam.FAIlED);
		}

		return new ResultEntity();
	}

	/**
	 * 查询用户列表 (对最近联系人进行分页获取)
	 */
	public ResultEntity getUserListByIn(SysUserVO sysUserVO) {
		List<UserConfigTO> listUser = new ArrayList<>();
		ResultEntity resultEntity = new ResultEntity();
		int pageSize = 5;

		Date date = new Date();
		// 已经下线用户（没有退出）
		List<String> onlineDownList = new ArrayList<>();

		String nearStr = redisClientTemplate.getMapValue(RedisConstant.USER_NEAR_CHAT_KEY, sysUserVO.getUserCode());
		if (nearStr != null) {
			List<String> listCode = JSONArray.parseArray(nearStr, String.class);
			int size = listCode.size();
			if (size < pageSize) {
				listUser = sysUserMapper.selectUserListByIn(listCode);
			} else {
				// 如果最后一个编号不为空时，以下一个编号开始取5个
				if (StringUtil.isNotEmpty(sysUserVO.getContactCode())) {
					int index = listCode.lastIndexOf(sysUserVO.getContactCode());
					listUser = sysUserMapper.selectUserListByIn(listCode.subList(index, ((index+pageSize)<size?(index+pageSize):(size-1))));
				} else {
					listUser = sysUserMapper.selectUserListByIn(listCode.subList(0, pageSize));
				}
			}
		}
		
		// 查询是否被对方关注
		int size = listUser.size();
		if (size > 0) {
			String [] userCodes = new String[size];
			String userCode = sysUserVO.getUserCode();
			UserConfigTO userConfig = null;
			UserCompanyTO userCompany = null;
			UserCompanyVO userCompanyVO = null;
			
			for (int i = 0; i < size; i++) {
				userConfig = listUser.get(i);
				userCodes[i] = userConfig.getUserCode()+"_"+userCode;
				
				// 大于登录超时时间没有操作的--记为离线
				if (TypeConstant.ONLINE_STATUS_1 == userConfig.getOnlineStatus() 
						&& (date.getTime() - userConfig.getUpdateTime().getTime())/1000 > TimeoutConstant.LOGIN_TIMEOUT) {
					onlineDownList.add(userConfig.getUserCode());
					// 改为下线
					userConfig.setOnlineStatus(TypeConstant.ONLINE_STATUS_0);
				}
				
				// 如果是客服，则查询企业编号
				if (userConfig.getUserType() == TypeConstant.USER_TYPE_0) {
					userCompanyVO = new UserCompanyVO();
					userCompanyVO.setUserCode(userConfig.getUserCode());
					userCompany = companyInfoService.getCustomerServiceCompanyInfo(userCompanyVO);
					if (userCompany != null) {
						userConfig.setCompanyCode(userCompany.getCompanyCode());
					}
				}
			}
			
			// 关注用户
			List<String> listStr = redisClientTemplate.getMapValue(RedisConstant.FOCUS_USER_KEY, userCodes);
			if (listStr != null) {
				for (int i = 0; i < size; i++) {
					listUser.get(i).setByFocusFlag(listStr.get(i)!=null?true:false);
				}
			}
		}
		
		if (onlineDownList.size() > 0) {
			onlineDown(onlineDownList);
		}
		
		resultEntity.setData(listUser);
		return resultEntity;
	}

	/**
	 * 用户已经下线处理
	 */
	private void onlineDown(List<String> onlineDownList) {
		taskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				sysUserMapper.updateUserOnlineDown(onlineDownList);
				// 下线缓存处理
				int size = onlineDownList.size();
				String [] temp = new String[size];
				for (int i = 0; i < size; i++) {
					temp[i] = onlineDownList.get(i);
				}
				redisClientTemplate.delMapValue(RedisConstant.USER_ONLINE_KEY, temp);
			}
		});
	}

	/**
	 * 更新用户信息 
	 */
	public ResultEntity updateUserInfo(SysUserVO sysUserVO) {
		ResultEntity resultEntity = new ResultEntity();
		
		SysUser sysUserIn = new SysUser();
		sysUserIn.setUserCode(sysUserVO.getUserCode());
		sysUserIn.setDeleteFlag(TypeConstant.DEL_FLAG_0);	// 没有删除
		sysUserIn.setStatus(TypeConstant.STATUS_TYPE_0);	// 没有禁用
		//sysUserIn.setUserType(sysUserVO.getUserType());		// 用户类型（1用户/0客服）
		SysUser sysUserOut = sysUserMapper.selectOne(sysUserIn);
		//UserConfigTO userConfig = userConfigMapper.selectUserConfigInfoForLogin(sysUserVO);
		if (sysUserOut != null) {
			sysUserVO.setSalt(sysUserOut.getSalt());
			passwordHelper.decryptPassword(sysUserVO);
			// 判断加密密码相同
			if (sysUserVO.getPassword().equals(sysUserOut.getPassword())) {
				// 如果新密码不为空
				if (StringUtils.isNotEmpty(sysUserVO.getNewPassword())) {
					sysUserVO.setPassword(sysUserVO.getNewPassword());
					
					// 加盐处理
					passwordHelper.encryptPassword(sysUserVO);
				}
				
				SysUser sysUserUp = new SysUser();
				sysUserUp.setUserId(sysUserVO.getUserId());
				sysUserUp.setUsername(sysUserVO.getUsername());
				if (StringUtils.isNotEmpty(sysUserVO.getNewPassword())) {
					sysUserUp.setPassword(sysUserVO.getPassword());
					sysUserUp.setSalt(sysUserVO.getSalt());
				}
				sysUserUp.setUserSign(sysUserVO.getUserSign());
				sysUserUp.setProfilePicture(sysUserVO.getProfilePicture());
				sysUserUp.setSex(sysUserVO.getSex());
				sysUserUp.setAge(sysUserVO.getAge());
				sysUserUp.setProvince(sysUserVO.getProvince());
				sysUserUp.setCity(sysUserVO.getCity());
				sysUserUp.setMobile(sysUserVO.getMobile());
				int result = sysUserMapper.updateByPrimaryKeySelective(sysUserUp);
				if (result > 0) {
					logger.info("更新【{}】用户信息成功", sysUserVO.getUserCode());
					// 更新用户缓存信息
					String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+sysUserVO.getTicket());
					if (userStr != null) {
						UserConfigTO userConfig = JSONObject.parseObject(userStr, UserConfigTO.class);
						BeanUtils.copyProperties(sysUserVO, userConfig, new String[]{"userCode", "ticket", "password", "salt", "aesKey", "bindIp"});

						redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+sysUserVO.getTicket(), 
								JSONObject.toJSONString(userConfig), TimeoutConstant.LOGIN_TIMEOUT);
					}

					// 缓存用户头像路径
					if (StringUtils.isNotEmpty(sysUserVO.getProfilePicture())) {
						redisClientTemplate.setMapValue(RedisConstant.UG_PP_KEY, sysUserVO.getUserCode(), sysUserVO.getProfilePicture());
					}
					
					// 如果用户名更新，则需要更新用户与群关联表中的用户名
					if (sysUserOut.getUsername().equals(sysUserVO.getUsername()) == false) {
						updateUCG(sysUserVO);
					}
					
					return new ResultEntity();
				} else {
					logger.warn("更新【{}】用户信息失败", sysUserVO.getUserCode());
					resultEntity.setResultBody(ResultParam.FAIlED);
				}
			} else {
				logger.warn("更新【{}】用户信息失败，密码验证失败", sysUserVO.getUserCode());
				resultEntity.setResultBody(ResultParam.FAIlED);
				resultEntity.setMsg("密码验证失败");
			}
		} else {
			logger.warn("更新【{}】用户信息失败，用户不存在", sysUserVO.getUserCode());
			resultEntity.setResultBody(ResultParam.FAIlED);
			resultEntity.setMsg("用户不存在");
		}
		return resultEntity;
	}

	/**
	 * 更新用户与群关联表中的用户名
	 */
	private void updateUCG(SysUserVO sysUserVO) {
		UserChatGroup ucgIn = new UserChatGroup();
		ucgIn.setUserCode(sysUserVO.getUserCode());
		List<UserChatGroup> ucgList = userChatGroupMapper.select(ucgIn);
		if (ucgList != null && ucgList.size() > 0) {
			UserChatGroup ucgUp = null;
			for (UserChatGroup userChatGroup : ucgList) {
				ucgUp = new UserChatGroup();
				ucgUp.setUcgId(userChatGroup.getUcgId());
				ucgUp.setUsername(sysUserVO.getUsername());
				userChatGroupMapper.updateByPrimaryKeySelective(ucgUp);
			}
			
			// 更新缓存
			ucgList = null;
			Object ucgInfo = redisClientTemplate.getMapValue(RedisConstant.USER_CG_LIST_KEY, sysUserVO.getUserCode());
			if (ucgInfo != null) {
				ucgList = JSONArray.parseArray(String.valueOf(ucgInfo), UserChatGroup.class);
				for (UserChatGroup userChatGroup : ucgList) {
					userChatGroup.setUsername(sysUserVO.getUsername());
				}
				redisClientTemplate.setMapValue(RedisConstant.USER_CG_LIST_KEY, sysUserVO.getUserCode(), JSONArray.toJSONString(ucgList));
			}
		}
	}
	
}