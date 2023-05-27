package com.fangg.websocket;

import java.io.IOException;

import javax.websocket.OnError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.BeforeHandshake;
import org.yeauty.annotation.OnBinary;
import org.yeauty.annotation.OnClose;
import org.yeauty.annotation.OnEvent;
import org.yeauty.annotation.OnOpen;
import org.yeauty.annotation.PathVariable;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.Session;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.service.CompanyInfoService;
import com.fangg.service.SysUserService;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.AESUtil;
import com.xclj.common.util.RSAUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

import io.netty.handler.codec.http.HttpHeaders;

/**
 * 在端点类上加上@ServerEndpoint、@Component注解，并在相应的方法上加上@OnOpen、@OnClose、@OnError、@OnMessage注解（不想关注某个事件可不添加对应的注解）：
 * 当ServerEndpointExporter类通过Spring配置进行声明并被使用，它将会去扫描带有@ServerEndpoint注解的类
 * 被注解的类将被注册成为一个WebSocket端点 所有的配置项都在这个注解的属性中 ( 如:@ServerEndpoint("/ws") )
 * readerIdleTimeSeconds
 * 与IdleStateHandler中的readerIdleTimeSeconds一致，并且当它不为0时，将在pipeline中添加IdleStateHandler,
 *
 * @author 四叶草 All right reserved
 * @version 1.0
 * @Copyright 2019
 * @Created 2019年12月5日
 */
@ServerEndpoint(path = "/chat/readSocket/{info}", 
	host = "${netty.websocket.host}", 
	port = "${netty.websocket.port}", 
	readerIdleTimeSeconds = "55")
@Component
public class ChatWebSocket {
	private static final Logger logger = LoggerFactory.getLogger(ChatWebSocket.class);

	@Value("${rsa.private.key}")
	String rsaPrivateKey;
	@Value("${aes.ticket.key}")
	private String aesTicketKey;

	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ChatSocketService chatSocketService;
	@Autowired
	SysUserService sysUserService;
	@Autowired
	CompanyInfoService companyInfoService;

	/**
	 * 当有新的连接进入时
	 *
	 * @param token
	 *            用户网页的http的token 用户id+前缀 用户id
	 * @param session
	 * @param headers
	 * @param req
	 *            通过 通过@RequestParam实现请求中query的获取参数
	 * @param reqMap
	 * @param @PathVariable支持RESTful风格中获取参数
	 * @param pathMap
	 * @throws Exception 
	 * @BeforeHandshake 注解，可在握手之前对连接进行关闭 在@BeforeHandshake事件中可设置子协议
	 *                  去掉配置端点类上的 @Component 更新Netty版本到 4.1.44.Final
	 *                  当有新的连接进入时，对该方法进行回调 注入参数的类型:Session、HttpHeaders...
	 */
	@BeforeHandshake
	public void handshake(Session session, HttpHeaders headers, @PathVariable String info) {
//		logger.info("WebSocket连接打开之前执行--{}", info);
		
		try {

			// 为防止恶意开启链接，在开启链接前进行判断（ticket和指纹）
			String [] temp = info.replaceAll("\\*", "/").split("&");
			if (temp.length != 3) {
				logger.warn("WebSocket连接打开之前执行，参数验证不通过--{}", info);
				if (session.isOpen()) {
					session.close();
				}
				return;
			}
						
			String ticketStr = AESUtil.decrypt(temp[0], aesTicketKey);
			JSONObject ticketJson = JSONObject.parseObject(ticketStr);
			if (ticketJson == null || ticketJson.isEmpty()) {
				logger.warn("WebSocket连接打开之前执行，ticket验证不通过--{}", temp[0]);
				if (session.isOpen()) {
					session.close();
				}
				return;
			}
			
			if (ticketJson.getString("f").equals(temp[1]) == false) {
				logger.warn("WebSocket连接打开之前执行，指纹验证不通过--{}", temp[1]);
				if (session.isOpen()) {
					session.close();
				}
			}
			
			// 这里待添加了绑定IP再判断
			/*String userStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY + ticketJson.getString("t"));
			JSONObject userJson = JSONObject.parseObject(userStr);
			String ip = HttpUtil.getWebsocketIp(session.remoteAddress());
			if (StringUtils.isEmpty(ip) || ip.equals(userJson.getString("bindIp")) == false) {
				if (session.isOpen()) {
					session.close();
				}
			}*/
		} catch (Exception e) {
			logger.warn("WebSocket连接打开之前执行，参数验证不通过", e);
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * 当有新的WebSocket连接完成时，对该方法进行回调 , ParameterMap
	 * parameterMap注入参数的类型:Session、HttpHeaders、ParameterMap
	 *
	 * @param session
	 * @param headers
	 * @throws Exception 
	 */
	@OnOpen
    public void onOpen(Session session, HttpHeaders headers, @PathVariable String info) throws Exception {		
//		logger.info("WebSocket连接打开--info={}", info);
		
		String [] temp = info.replaceAll("\\*", "/").split("&");
		
		// 握手时前端要通过RSA加密，这里解密
		String paramStr = RSAUtil.decrypt(temp[2], rsaPrivateKey);
		JSONObject paramsJson = JSONObject.parseObject(paramStr);
		if (paramsJson == null || paramsJson.isEmpty()) {
			logger.warn("WebSocket连接打开--握手解密失败");
			if (session.isOpen()) {
				session.close();
			}
			return;
		}
		
		String companyCode = paramsJson.getString("cc");
		String server = paramsJson.getString("s");
		String client = paramsJson.getString("c");
		String type = paramsJson.getString("t");
		String status = paramsJson.getString("st");
		logger.info("WebSocket连接打开--companyCode={},server={},client={},type={},status={}", 
				companyCode, server, client, type, status);
		
		ResultEntity resultEntity = new ResultEntity();
		session.setAttribute("server_client", server+"_"+client);

		try {
			resultEntity = chatSocketService.read(companyCode, server, client, type, status);
			if (resultEntity.getCode() == ResultParam.SUCCESS.getCode()) {
				JSONObject resultJson = new JSONObject();
				resultJson.put("chatList", resultEntity.getData());
				resultJson.put("contactCode", "");
				resultEntity.setData(resultJson);
				session.sendText(JSONArray.toJSONString(resultEntity));
			}
			// 后台主动更新状态为“已读”，此处应该由前端发送信息更新状态
			//status  = TypeConstant.CHAT_STATUS_1;
			
//			resultEntity.setData("Hello Netty!次数："+num);
//		    session.sendText(JSONObject.toJSONString(resultEntity));
//		    num = num + 1;
			
			// 为防止线程太多内存耗尽（OOM），最好不要开启新线程
			/*new Thread(new Runnable() {
				
				@Override
				public void run() {
					// 在链接关闭前，每1秒往前端推送一次数据
					while (session.isActive()) {
						try {
							Thread.sleep(3000);
							ResultEntity resultEntity = chatSocketService.read(companyCode, server, client, type, status);
							session.sendText(JSONArray.toJSONString(resultEntity.getData()));
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();*/
		} catch (InterruptedException e) {
			logger.error("WebSocket推送数据异常：", e);
		} catch (Exception e) {
			logger.error("WebSocket推送数据异常：", e);
		}
    }

	/**
	 * 当有WebSocket连接关闭时，对该方法进行回调 注入参数的类型:Session
	 *
	 * @param session
	 * @throws IOException
	 */
	@OnClose
	public void onClose(Session session) throws IOException {
		logger.warn("WebSocket连接{}关闭", JSONObject.toJSONString(session.getAttribute("server_client")));
	}

	/**
	 * 当有WebSocket抛出异常时，对该方法进行回调 注入参数的类型:Session、Throwable
	 *
	 * @param session
	 * @param throwable
	 */
	@OnError
	public void onError(Session session, Throwable throwable) {
		throwable.printStackTrace();
		
		//throw new ChatException(ResultParam.FAILED_WEBSOCKET);
	}

	/**
	 * 接收到字符串消息时，对该方法进行回调 注入参数的类型:Session、String
	 *
	 * @param session
	 * @param message
	 */
	@org.yeauty.annotation.OnMessage
	public void OnMessage(Session session, String message) {
		//logger.info("WebSocket接收到消息：{}", message);
        
		try {
			// 握手成功后这里进来的消息不再加密
			/*JSONObject jsonObject = JSONObject.parseObject(message);
			String ticket = jsonObject.getString("ticket");
			String info = jsonObject.getString("info");
									
			// 消息在前端要通过AES加密，这里解密
			String ticketStr = AESUtil.decrypt(ticket, aesTicketKey);
			JSONObject ticketJson = JSONObject.parseObject(ticketStr);
			if (ticketJson == null || ticketJson.isEmpty()) {
				logger.warn("WebSocket接收消息--ticket解密失败");
				return;
			}
			
//			ticketStr = redisClientTemplate.getString(RedisConstant.TICKET_PREFIX_KEY+ticketJson.getString("t"));
//			UserConfigTO userConfig = JSONObject.parseObject(ticketStr, UserConfigTO.class);
			
			String paramStr = AESUtil.decrypt(info, ticketJson.getString("a"));
			JSONObject paramsJson = JSONObject.parseObject(paramStr);
			if (paramsJson == null || paramsJson.isEmpty()) {
				logger.warn("WebSocket接收消息--信息解密失败");
				return;
			}*/
			
			JSONObject paramsJson = JSONObject.parseObject(message);
			String companyCode = paramsJson.getString("companyCode");
			String server = paramsJson.getString("server");
			String client = paramsJson.getString("client");
			String type = paramsJson.getString("type");
			String status = paramsJson.getString("status");
			String contactCode = paramsJson.getString("contactCode");

//			String ticket = paramsJson.getString("ticket");
//			String ticketStr = AESUtil.decrypt(ticket, aesTicketKey);
//			JSONObject ticketJson = JSONObject.parseObject(ticketStr);
			
			if (session.getAttribute("server_client").equals(server+"_"+client) 
					|| session.getAttribute("server_client").equals(client+"_"+server)) {
				// 这里主要作用是更新消息状态为“已读”
				ResultEntity resultEntity = chatSocketService.read(companyCode, server, client, type, status);
				if (resultEntity.getCode() == ResultParam.SUCCESS.getCode()) {
					JSONObject resultJson = new JSONObject();
					resultJson.put("chatList", resultEntity.getData());
					resultJson.put("contactCode", contactCode);
					resultEntity.setData(resultJson);
					// 对返回的信息进行AES加密（如果要加强安全性，可以通过RSA加密）
					// 前端解密有点问题，暂时不加密
					//session.sendText(AESUtil.encrypt(JSONArray.toJSONString(resultEntity), ticketJson.getString("a")));
					session.sendText(JSONArray.toJSONString(resultEntity));
				}
			}
		} catch (Exception e) {
			logger.error("WebSocket接收信息并推送数据异常：{}", e.getMessage());
		}
	}

	/**
	 * 当接收到二进制消息时，对该方法进行回调 注入参数的类型:Session、byte[]
	 *
	 * @param session
	 * @param bytes
	 */
	@OnBinary
	public void onBinary(Session session, byte[] bytes) {
		for (byte b : bytes) {
			logger.debug("==========>>>>>>>>>>>{},", b);
		}
		session.sendBinary(bytes);
	}

	/**
	 * 当接收到Netty的事件时，对该方法进行回调 注入参数的类型:Session、Object
	 *
	 * @param session
	 * @param evt
	 */
	@OnEvent
	public void onEvent(Session session, Object evt) {
		logger.debug("==netty心跳事件===evt=>>>>{},来自===userId:{}", JSONObject.toJSONString(evt), session.channel().id());
		
	}

}
