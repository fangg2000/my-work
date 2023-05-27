package com.fangg.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangg.annotation.Decrypt;
import com.fangg.bean.chat.query.ChatLog;
import com.fangg.bean.chat.query.PermissionFunc;
import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.bean.chat.vo.ChatLogVO;
import com.fangg.constant.RedisConstant;
import com.fangg.constant.TimeoutConstant;
import com.fangg.constant.TypeConstant;
import com.fangg.service.ChatLogService;
import com.fangg.service.CompanyInfoService;
import com.fangg.service.SysUserService;
import com.fangg.service.WebBaseService;
import com.fangg.websocket.ChatSocketService;
import com.xclj.common.redis.RedisClientTemplate;
import com.xclj.common.util.StringUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * 聊天
 * @author fangg
 * 2021年12月17日 下午12:10:43
 */
@Controller
public class ChatController {
	private static Logger logger = LoggerFactory.getLogger(ChatController.class);

	@Value("${aes.private.key}")
	private String aesPrivateKey;

	@Autowired
	RedisClientTemplate redisClientTemplate;
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	ChatLogService chatService;
	@Autowired
	SysUserService sysUserService;
	@Autowired
	CompanyInfoService companyInfoService;
	@Autowired
	WebBaseService webBaseService;
	@Autowired
	ChatSocketService chatSocketService;
	
	// 首次显示历史记录数量和查看数量
	private static final int PAGE_NUM = 5;

	@RequestMapping(value="/chat/getNotReadUserList", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity getNotReadUserList(HttpServletRequest request, HttpServletResponse response, String userCode, 
			String companyCode) {
		//logger.info("查询未读的用户列表入参:userCode={}, companyCode={}", userCode, companyCode);
		ResultEntity resultEntity = new ResultEntity();
		try {
			// 获取未读的用户列表
			ChatLogVO chatLogVOIn = new ChatLogVO();
			chatLogVOIn.setCompanyCode(companyCode);
			chatLogVOIn.setServer(userCode);
			chatLogVOIn.setType(String.valueOf(TypeConstant.USER_TYPE_1));
			List<UserCompanyTO> userList = chatService.getUserListByChatNotRead(chatLogVOIn);
			resultEntity.setData(userList);
		} catch (Exception e) {
			logger.error("查询未读的用户列表异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "查询未读的用户列表异常，请联系管理员。");
		}
		return resultEntity;
	}

	/**
	* 聊天记录保存
	*/
	@Decrypt
	@RequestMapping(value="/chat/write", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity write(HttpServletRequest request, HttpServletResponse response, @RequestBody ChatLogVO chatLogVO) throws Exception {
		// logger.info("{}写入内容：{}", bcCode, text);
		ResultEntity resultEntity = new ResultEntity();
		try {
			if (chatLogVO == null) {
				return new ResultEntity(ResultParam.PARAM_FAILED);
			} else if (StringUtil.isEmpty(chatLogVO.getCompanyCode())) {
				return new ResultEntity(ResultParam.PARAM_FAILED, "companyCode不能为空");
			} else if (StringUtil.isEmpty(chatLogVO.getServer())) {
				return new ResultEntity(ResultParam.PARAM_FAILED, "server不能为空");
			} else if (StringUtil.isEmpty(chatLogVO.getClient())) {
				return new ResultEntity(ResultParam.PARAM_FAILED, "client不能为空");
			} else if (StringUtil.isEmpty(chatLogVO.getContactCode())) {
				return new ResultEntity(ResultParam.PARAM_FAILED, "contactCode不能为空");
			}
			
			Map<String, String> chatMap = new HashMap<>();
			String chatKey = chatSocketService.backChatKey(chatLogVO.getCompanyCode(), chatLogVO.getServer(), 
					chatLogVO.getClient(), chatLogVO.getType(), chatMap);
			
			String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dMMy"));
			ChatLogVO chatTest = new ChatLogVO();
			chatTest.setCompanyCode(chatLogVO.getCompanyCode());
			chatTest.setServer(chatMap.get("server"));
			chatTest.setClient(chatMap.get("client"));
			chatTest.setContent(chatLogVO.getContent().replaceAll("uploaded\\/temp_", "uploaded/"));
			chatTest.setType(chatMap.get("type"));
			chatTest.setStatus(TypeConstant.CHAT_STATUS_0); // 默认“未读”
			chatTest.setCreateTime(new Timestamp(System.currentTimeMillis()));
			chatTest.setInitTime(System.currentTimeMillis());
			
			// 通知联系用户参数设置
			chatTest.setNewContactFlag(chatLogVO.isNewContactFlag());
			chatTest.setContactCode(chatLogVO.getContactCode());
			
			redisClientTemplate.setListValueByTimeout(chatKey, chatTest, TimeoutConstant.CHAT_SESSION_LIST_TIMEOUT);
			String result = redisClientTemplate.setStringByTimeout(chatKey + "_" + datetime,
					JSONObject.toJSONString(chatTest), TimeoutConstant.CHAT_SESSION_NEW_TIMEOUT);
			
			if (result != null) {
				// 保存
				String ticket = request.getAttribute("ticket").toString();
				//ticket = AESUtil.decrypt(ticket, aesPrivateKey);
				saveLog(chatTest, ticket, request.getServletContext().getRealPath(""));
			}
		} catch (Exception e) {
			logger.error("聊天记录保存异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "聊天记录保存异常，请联系管理员。");
		}
		return resultEntity;
	}

	/**
	 * 异步保存记录
	 */
	private void saveLog(final ChatLogVO chatLog, final String ticket, final String prefixPath) {
		taskExecutor.execute(new Runnable() {
			public void run() {
				// 接收者
				chatLog.setReceiver(chatLog.getContactCode());
				chatService.insertChatLog(chatLog);

				// 对方编号
				String otherCode = chatLog.getContactCode().equals(chatLog.getServer())?chatLog.getClient():chatLog.getServer();
				
				// 判断新用户联系
				// 需要考虑的情况是--对方不一定会接受和你聊天，所以只有在对方点击时再获取相关信息，这里做的是方便通知对方--“有新用户联系您”
				if (chatLog.isNewContactFlag() && StringUtil.isNotEmpty(chatLog.getContactCode())) {
					// 判断对方是否在线，不在线则不需要通知消息
					String onlineStr = redisClientTemplate.getMapValue(RedisConstant.USER_ONLINE_KEY, chatLog.getContactCode());
					if (onlineStr != null) {
						System.out.println("有新用户消息--"+chatLog.getContactCode());
						JSONArray userArr = null;
						// 这里可能出现一种很严重的情况--万一某个万人迷同时被N个新用户想要联系，这个列表会非常大……
						// (世界上也不知道有多少万人迷……离题了，速度回归代码)
						String userArrStr = redisClientTemplate.getMapValue(RedisConstant.NEW_USER_CHAT_KEY, chatLog.getContactCode());
						if (userArrStr != null) {
							userArr = JSONArray.parseArray(userArrStr);
							if (userArr.contains(otherCode) == false) {
								userArr.add(otherCode);

								// 如果是新用户，则添加到缓存中，为了能及时通知用户
								redisClientTemplate.setMapValue(RedisConstant.NEW_USER_CHAT_KEY, chatLog.getContactCode(), 
										userArr.toJSONString());
							}
						} else {
							userArr = new JSONArray();
							userArr.add(chatLog.getContactCode().equals(chatLog.getServer())?chatLog.getClient():chatLog.getServer());

							// 如果是新用户，则添加到缓存中，为了能及时通知用户
							redisClientTemplate.setMapValue(RedisConstant.NEW_USER_CHAT_KEY, chatLog.getContactCode(), 
									userArr.toJSONString());
						}
					}
					
					// 缓存到最近联系人(最新的放前面)
					nearContactUserCache(chatLog, ticket, otherCode);
				}
				
				// 发送图片处理
				checkTempImg(chatLog, otherCode);
			}

			private void checkTempImg(ChatLogVO chatLog, String otherCode) {
				String dirPath = prefixPath + "uploaded/temp_" + otherCode;
				String realPath = prefixPath + "uploaded/" + otherCode;
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
							if (chatLog.getContent().indexOf(file.getName()) != -1) {
								newFile = new File(realPath + "/" + file.getName());
								FileCopyUtils.copy(file, newFile);
							}
							file.delete();
						}
						
						// 删除缓存文件
						dirFile.delete();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private void nearContactUserCache(ChatLogVO chatLog, String ticket, String otherCode) {
				String nearStr = redisClientTemplate.getMapValue(RedisConstant.USER_NEAR_CHAT_KEY, otherCode);
				List<String> chatArr = null;
				
				if (nearStr != null) {
					chatArr = JSONArray.parseArray(nearStr, String.class);
					// 如果联系人已经存在，则删除并移到前面
					if (chatArr.contains(chatLog.getContactCode())) {
						if (chatArr.get(0).equals(chatLog.getContactCode()) == false) {
							chatArr.remove(chatLog.getContactCode());
							chatArr.add(0, chatLog.getContactCode());
						}
					} else {
						chatArr.add(0, chatLog.getContactCode());
					}
					
					// 根据用户等级限制联系人个数
//					PermissionFuncTO perTO = webBaseService.getUserFuncPermission(ticket);
					Map<String, PermissionFunc> fp4Server = webBaseService.getUserFuncPermission(ticket);
					PermissionFunc perFunc = null;
					if (fp4Server.containsKey("F0003") == false) {
						perFunc = fp4Server.get("F0002");
						if (fp4Server.containsKey("F0002") == false) {
							perFunc = fp4Server.get("F0004");
							if (perFunc != null && chatArr.size() > perFunc.getLimitNum()) {
								chatArr = chatArr.subList(0, perFunc.getLimitNum());
							} 
							// 如果取不到权限信息
							else if (fp4Server.isEmpty()) {
								chatArr = new ArrayList<>();
								chatArr.add(chatLog.getContactCode());
							}
						} else if (chatArr.size() > perFunc.getLimitNum()) {
							chatArr = chatArr.subList(0, perFunc.getLimitNum());
						}
					} else {
						perFunc = fp4Server.get("F0003");
						if (perFunc.getLimitNum() > -1 && chatArr.size() > perFunc.getLimitNum()) {
							chatArr = chatArr.subList(0, perFunc.getLimitNum());
						}
					}
				} else {
					chatArr = new ArrayList<>();
					chatArr.add(chatLog.getContactCode());
				}
				
				redisClientTemplate.setMapValue(RedisConstant.USER_NEAR_CHAT_KEY, otherCode, JSONArray.toJSONString(chatArr));
			}
		});
	}


	/**
	* 查看更多
	*/
	@RequestMapping(value="/chat/readMore", produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity readMore(HttpServletRequest request, HttpServletResponse response, @RequestBody ChatLogVO chatLogVO) throws Exception {
		 logger.info("查看更多入参：{}", JSONObject.toJSONString(chatLogVO));
		if (StringUtils.isEmpty(chatLogVO.getClient())) {
			return new ResultEntity(ResultParam.PARAM_FAILED, "client不能为空");
		}
		
		ResultEntity resultEntity = new ResultEntity();
		
		try {
			List<ChatLog> listChat = new ArrayList<>();
			ChatLogVO chatTestIn = new ChatLogVO();
			chatTestIn.setCompanyCode(chatLogVO.getCompanyCode());
			if (StringUtil.isNotEmpty(chatLogVO.getReceiver())) {
				chatTestIn.setReceiver(chatLogVO.getReceiver());
				chatTestIn.setClient(chatLogVO.getClient());
			} else {
				Map<String, String> chatMap = new HashMap<>();
				String chatKey = chatSocketService.backChatKey(chatLogVO.getCompanyCode(), chatLogVO.getServer(), 
						chatLogVO.getClient(), null, chatMap);
				chatTestIn.setServer(chatMap.get("server"));
				chatTestIn.setClient(chatMap.get("client"));
				chatTestIn.setChatKey(chatKey);
				
				// 判断缓存中数据
				if (chatLogVO.getInitTime() != null) {
					List<ChatLog> chatList = redisClientTemplate.getList(chatKey, ChatLog.class);
					if (chatList != null & chatList.size() > 0 && chatList.get(0).getInitTime() < chatLogVO.getInitTime()) {
						List<ChatLog> newList = new ArrayList<>();
						for (ChatLog chatLog : chatList) {
							if (chatLog.getInitTime() < chatLogVO.getInitTime()) {
								newList.add(chatLog);
							}
						}
						
						int size = newList.size();
						if (size > chatLogVO.getPageSize()) {
							resultEntity.setData(newList.subList(size-chatLogVO.getPageSize(), size-1));
						} else {
							resultEntity.setData(newList);
						}
						return resultEntity;
					}
				}
			}
			chatTestIn.setInitTime(chatLogVO.getInitTime());
			chatTestIn.setPageSize(chatLogVO.getPageSize());
			chatTestIn.setStatus(chatLogVO.getStatus());
			
			// 优化--如果缓存中存在，则先从中取
			listChat = chatService.getChatMoreList(chatTestIn);
			if (listChat == null) {
				listChat = new ArrayList<>();
			}
			resultEntity.setData(listChat);
		} catch (Exception e) {
			logger.error("查看更多聊天记录异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "查看更多聊天记录异常，请联系管理员。");
		}
		return resultEntity;
	}
	
	/**
	 * 上传图片
	 */
	@RequestMapping(value="/chat/img", method={RequestMethod.POST}, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity img(HttpServletRequest request, HttpServletResponse response, @RequestParam("lastImg") String lastImg, 
			@RequestParam("imgFile") MultipartFile imgFile) throws Exception {
//		logger.info("上一图片路径：{}", lastImg);
//		logger.info("图片路径：{}", imgFile.getOriginalFilename());
		ResultEntity resultEntity = new ResultEntity();
		try {
			//String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dMMy"));
			String resultPath = null;
			// 用户信息
			UserConfigTO userInfo = webBaseService.getUserInfoByCache(request);
			if (userInfo != null) {
				// 1. 接受上传的文件 @RequestParam("file") MultipartFile file
				// 2.根据时间戳创建新的文件名，这样即便是第二次上传相同名称的文件，也不会把第一次的文件覆盖了
				resultPath = "uploaded/" + userInfo.getUserCode() 
						+ "/pp_" + System.currentTimeMillis() + "."
						+ imgFile.getOriginalFilename().split("\\.")[1];
				// 3.通过req.getServletContext().getRealPath("")
				// 获取当前项目的真实路径，然后拼接前面的文件名
				String destFileName = request.getServletContext().getRealPath("") + resultPath;
				// 4.第一次运行的时候，这个文件所在的目录往往是不存在的，这里需要创建一下目录（创建到了webapp下uploaded文件夹下）
				File destFile = new File(destFileName);
				if (destFile.getParentFile().exists() == false) {
					destFile.getParentFile().mkdirs();
				}
				// 5.把浏览器上传的文件复制到希望的位置
				imgFile.transferTo(destFile);
				
				// 删除上一图片(合理的操作是保留用户的头像图片，所以要知道哪些头像是对应用户的，可以判断保留数量)
				/*if (lastImg != null) {
					File lastFile = new File(request.getServletContext().getRealPath("") + lastImg.substring(1));
					if (lastFile != null && lastFile.exists()) {
						lastFile.delete();
					}
				}*/
				
				resultEntity.setData("/" + resultPath);
			} else {
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} catch (FileNotFoundException e) {
			logger.error("上传图片异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "上传图片异常，请联系管理员。");
		} catch (IOException e) {
			logger.error("上传图片异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "上传图片异常，请联系管理员。");
		} catch (Exception e) {
			logger.error("上传图片异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "上传图片异常，请联系管理员。");
		}
		
		return resultEntity;
	}
	
	/**
	 * 发送图片缓存处理
	 */
	@RequestMapping(value="/chat/sendImgCache", method={RequestMethod.POST}, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity sendImgCache(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("imgFile") MultipartFile imgFile) throws Exception {
		logger.info("图片路径：{}", imgFile.getOriginalFilename());
		ResultEntity resultEntity = new ResultEntity();
		try {
			String resultPath = null;
			// 用户信息
			UserConfigTO userInfo = webBaseService.getUserInfoByCache(request);
			if (userInfo != null) {
				resultPath = "uploaded/temp_" + userInfo.getUserCode()
					+ "/" + System.currentTimeMillis() + "."
					+ imgFile.getOriginalFilename().split("\\.")[1];
				// 获取当前项目的真实路径，然后拼接前面的文件名
				String destFileName = request.getServletContext().getRealPath("") + resultPath;
				File destFile = new File(destFileName);
				if (destFile.getParentFile().exists() == false) {
					destFile.getParentFile().mkdirs();
				}
				// 文件复制到希望的位置
				imgFile.transferTo(destFile);
				
				resultEntity.setData("/" + resultPath);
			} else {
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} catch (FileNotFoundException e) {
			logger.error("发送图片缓存处理异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "发送图片缓存处理异常，请联系管理员。");
		} catch (IOException e) {
			logger.error("发送图片缓存处理异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "发送图片缓存处理异常，请联系管理员。");
		} catch (Exception e) {
			logger.error("发送图片缓存处理异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "发送图片缓存处理异常，请联系管理员。");
		}
		
		return resultEntity;
	}
	
	/**
	 * 发送多图片缓存处理
	 */
	@RequestMapping(value="/chat/moreImgCache", method={RequestMethod.POST}, produces={"text/html;charset=UTF-8;","application/json;"})
	@ResponseBody
	public ResultEntity moreImgCache(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("imgFiles") MultipartFile [] imgFiles) throws Exception {
//		logger.info("多图片数量：{}", imgFile.length);
		ResultEntity resultEntity = new ResultEntity();
		try {
			String resultPath = null;
			// 用户信息
			UserConfigTO userInfo = webBaseService.getUserInfoByCache(request);
			if (userInfo != null) {
				resultPath = "uploaded/temp_" + userInfo.getUserCode() + "/";
				List<String> pathList = new ArrayList<>();
				for (MultipartFile file : imgFiles) {
					String new_path = resultPath + System.currentTimeMillis() + "." + file.getOriginalFilename().split("\\.")[1];
					// 获取当前项目的真实路径，然后拼接前面的文件名
					String destFileName = request.getServletContext().getRealPath("") + new_path;
					File destFile = new File(destFileName);
					if (destFile.getParentFile().exists() == false) {
						destFile.getParentFile().mkdirs();
					}
					// 文件复制到希望的位置
					file.transferTo(destFile);
					pathList.add("/" + new_path);
				}
				
				resultEntity.setData(pathList);
			} else {
				resultEntity.setResultBody(ResultParam.FAIlED);
			}
		} catch (FileNotFoundException e) {
			logger.error("发送图片缓存处理异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "发送图片缓存处理异常，请联系管理员。");
		} catch (IOException e) {
			logger.error("发送图片缓存处理异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "发送图片缓存处理异常，请联系管理员。");
		} catch (Exception e) {
			logger.error("发送图片缓存处理异常：", e);
			return new ResultEntity(ResultParam.FAILED_UNKNOW, "发送图片缓存处理异常，请联系管理员。");
		}
		
		return resultEntity;
	}

}
