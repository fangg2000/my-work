package com.fangg.interceptor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.fangg.bean.chat.entity.PageRequest;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.exception.ChatException;
import com.fangg.service.BlackInfoService;
import com.fangg.util.HttpUtil;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.AESUtil;
import com.xclj.constant.BaseConstant;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * 页面调用拦截
 * @author fangg
 * 2021年12月22日 上午7:10:44
 */
@Component
public class ChatWebInterceptor implements HandlerInterceptor, ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(ChatWebInterceptor.class);

	@Value("${http-headers}")
	String httpHeaders;
	@Value("${http-request-exclude}")
	String excludePath;
	@Value("${aes.ticket.key}")
	String aesTicketKey;
	//记录用户调用接口
	Map<String, PageRequest> prMap = null;
	
	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	BlackInfoService blackInfoService;


	@Override
	public void run(ApplicationArguments arg0) throws Exception {	
		if (prMap == null) {
			prMap = new ConcurrentHashMap<String, PageRequest>();
			// 从缓存中加载黑名单信息
			Map<String, String> blackMap = redisClientTemplate.getMapValueAll(RedisConstant.BLACK_IP_KEY);
			PageRequest pageRequest = null;
			
			if (blackMap != null && blackMap.isEmpty() == false) {
				for (Entry<String, String> entry : blackMap.entrySet()) {
					pageRequest = new PageRequest();
					pageRequest.setBlackFlag(true);
					prMap.put(entry.getKey(), pageRequest);
				}
			}
			logger.info("--初始化黑名单信息完成");
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {

		//设置浏览器编码格式
		//response.setContentType("text/html;character=utf-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		//这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT, OPTIONS");
//        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", httpHeaders);
		
		String ticket = null;
		
		//logger.info("进入页面拦截器");
		try {		
			String ipAddr = HttpUtil.getIpAddress(request);
			
			// 判断用户调用接口频率
			checkPageRequest(request, ipAddr);
			
			// VUE(axios)跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
	        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
	            response.setStatus(org.springframework.http.HttpStatus.OK.value());
	            return false;
	        }
	        
	        // 接口排除判断
	        if (checkRequestPath(request)) {
				return true;
			}
	        
	        ticket = request.getHeader("ticket");
			if (ticket == null) {
				// logger.warn("ticket为空");
				throw new Exception("ticket为空");
			}
			//logger.info(ticket);

        	// ticket统一使用自定义的ticketKey进行AES解密(ticket会在登录或获取用户信息即刷新时改变，
			// 因为加密信息中有动态生成的aesKey，但这个aesKey并不安全)
            String ticketStr = AESUtil.decrypt(ticket, aesTicketKey);
            JSONObject ticketJson = JSONObject.parseObject(ticketStr);
			
            if (ticketJson != null) {        		
        		// 功能权限判断
        		
            	// ticket信息验证
    			return checkTicketInfo(request, response, ticketJson, ipAddr);
			} else {
				throw new ChatException(ResultParam.FAILED_UNKNOW, "ticket无效");
			}
		} catch (ChatException e) {
			logger.warn("ticket【{}】登录超时", ticket);
//			throw new ChatException(ResultParam.LOGIN_TIMEOUT);
			response.setContentType("application/json; charset=utf-8");
			response.getWriter().print(JSONObject.toJSONString(new ResultEntity(e.getResultParam(), e.getErrorMsg())));
			response.getWriter().flush();
			response.getWriter().close();
//			response.sendRedirect("/myChat/index.html");
		} catch (Exception e) {
			//logger.error("接口调用异常：", e);
			// ticket为空、ticket解密异常、其他
			response.setContentType("application/json; charset=utf-8");
			response.getWriter().print(JSONObject.toJSONString(new ResultEntity(ResultParam.FAILED_UNKNOW)));
			response.getWriter().flush();
			response.getWriter().close();
		}

		return false;
	}

	/**
	 * 接口调用不在拦截内
	 */
	private boolean checkRequestPath(HttpServletRequest request) {
		String [] temp = excludePath.split("\\|");
		for (String path : temp) {
			if (request.getServletPath().endsWith(path)) {
				return true;
			}
		}
		
		return false;
	}

	private void checkPageRequest(HttpServletRequest request, String ipAddr) throws Exception {
		PageRequest pageRequest = null;
		Date nowTime = new Date();
				
		if (prMap.containsKey(ipAddr)) {
			pageRequest = prMap.get(ipAddr);
			
			if (pageRequest.isBlackFlag()) {
				throw new ChatException(ResultParam.BLACK_ONE, "您已被系统拉入黑名单");
			}
			
			// 如果用户在1秒内调用接口超过10次，则认定为恶意操作并拉入黑名单
			if (pageRequest.getNumber() >= BaseConstant.REQUEST_NUM) {
				if ((nowTime.getTime() - pageRequest.getFirstTime().getTime()) < BaseConstant.MILLS_SECOND_1000) {
					// 先更新集合防止数据没有同步
					pageRequest.setBlackFlag(true);
					prMap.put(ipAddr, pageRequest);
					
					// 0-web/1-mobile/2-pad(不能通过ticket信息判断唯一，因为多平台上登录)
					int loginType = HttpUtil.fromMoblie(request);
					String ticket = request.getHeader("ticket");
					
					UserConfigTO userConfig = new UserConfigTO();
					userConfig.setLoginIp(ipAddr);
					userConfig.setLoginType(loginType);
					
					if (ticket != null) {
						String ticketStr = AESUtil.decrypt(ticket, aesTicketKey);
			            JSONObject ticketJson = JSONObject.parseObject(ticketStr);
			            String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+ticketJson.getString("t"));
			    		if (userStr != null) {
			    			JSONObject jsonObject = JSONObject.parseObject(userStr);
							userConfig.setUserCode(jsonObject.getString("userCode"));
							userConfig.setUsername(jsonObject.getString("username"));
							blackInfoService.userAdd2BlackInfo("用户恶意频繁调用接口", userConfig);
			    		} else {
							blackInfoService.ipAdd2BlackInfo("用户恶意频繁调用接口", userConfig);
						}
					} else {
						blackInfoService.ipAdd2BlackInfo("用户恶意频繁调用接口", userConfig);
					}
				} else {
					pageRequest.setNumber(1);
					pageRequest.setFirstTime(nowTime);
					prMap.put(ipAddr, pageRequest);
				}
			} else {
				pageRequest.setNumber(pageRequest.getNumber() + 1);
				prMap.put(ipAddr, pageRequest);
			}
		} else {
			pageRequest = new PageRequest();
			pageRequest.setNumber(1);
			pageRequest.setFirstTime(nowTime);
			prMap.put(ipAddr, pageRequest);
		}
	}

	/**
	 * ticket信息验证
	 * @throws Exception 
	 */
	private boolean checkTicketInfo(HttpServletRequest request, HttpServletResponse response, 
			JSONObject ticketJson, String ipAddr) throws Exception, ChatException {

//		 0-web/1-mobile/2-pad(不能通过ticket信息判断唯一，因为多平台上登录)
		int loginType = HttpUtil.fromMoblie(request);
//		if (ipAddr.equals(ticketJson.getString("i")) == false 
//				|| loginType != ticketJson.getIntValue("l")) {
//			throw new ChatException(ResultParam.LOGIN_UNKNOW, "登录状态异常");
//		}
		
		String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+ticketJson.getString("t"));
		if (userStr != null) {
			JSONObject jsonObject = JSONObject.parseObject(userStr);
			JSONObject ideJson = jsonObject.getJSONObject("loginIdentifierMap");
			if (ideJson.containsKey(String.valueOf(loginType)) == false) {
				logger.warn("【{}】登录状态异常", jsonObject.getString("username"));
				throw new ChatException(ResultParam.LOGIN_UNKNOW, "登录状态异常");
			}

			// 用户是否在黑名单中/IP是否在黑名单中
			List<String> blackList = redisClientTemplate.getMapValue(RedisConstant.BLACK_IP_KEY, jsonObject.getString("userCode"), ipAddr);
			if (blackList != null && blackList.size() > 0) {
				for (String bCode : blackList) {
					if (bCode != null) {
						throw new ChatException(ResultParam.BLACK_ONE);
					}
				}
			}
			
			// 这里的指纹判断通过取成功登录时保存的指纹，其他ticket指纹信息无效
			String [] temp = ideJson.getString(String.valueOf(loginType)).split("_");
			
			// 这里可以通过得到的用户信息判断IP、指纹、类型是否对应(可以在生产上开启判断)
			// 不能对aesKey判断，因为刷新时aesKey会改变
			if (ticketJson.getString("f").equals(temp[1]) == false) {
				logger.warn("【{}】指纹无效", jsonObject.getString("username"));
				throw new ChatException(ResultParam.FAILED_UNKNOW, "指纹无效");
			}
			
			// 更新登录用户最后时间
			// 缓存登录用户信息时间更新
			redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+ticketJson.getString("t"), 
					userStr, TimeoutConstant.LOGIN_TIMEOUT);
			// 方便直接从request中直接获取，不需要再做解密处理
			request.setAttribute("ticket", ticketJson.getString("t"));
			
			// 给表头加上aesKey(方便返回值加密，但需要加密否则前端可看到，如果有新的aesKey则需要覆盖)
			response.setHeader("Other", AESUtil.encrypt(jsonObject.getString("aesKey"), aesTicketKey));
			return true;
		} else {
			throw new ChatException(ResultParam.LOGIN_TIMEOUT, "登录超时");
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	/*private void updateUserLoginTime(String ticket, String userStr) {
		taskExecutor.execute(new Runnable() {
			public void run() {
				// 缓存登录用户信息
				redisClientTemplate.setStringByTimeout(RedisConstant.TICKET_PREFIX_KEY+ticket, 
						userStr, RedisConstant.LOGIN_TIMEOUT);
				
				// 因为已经通过socket更新登录用户上线离线状态，此处不需要再处理
//				SysUser sysUserUp = new SysUser();
//				sysUserUp.setUserId(sysUserOut.getUserId());
//				//sysUserUp.setTicket(sysUserOut.getTicket());
//				sysUserUp.setUpdateTime(new Date());
//				sysUserService.updateByPrimaryKey(sysUserUp);
			}
		});
	}*/	

}
