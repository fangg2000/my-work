package com.fangg.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONArray;
import com.fangg.bean.chat.vo.record.DiscussInfoVO;
import com.fangg.config.redis.RedisDbTemplate;
import com.fangg.dao.ChatLogMapper;
import com.fangg.dao.FocusUsersMapper;
import com.fangg.dao.PermissionFuncMapper;
import com.fangg.dao.SysUserMapper;
import com.fangg.dao.record.DiscussInfoMapper;
import com.fangg.service.record.RecordGroupService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShardingsphereTest {
	private Logger logger = LoggerFactory.getLogger(ShardingsphereTest.class);
	
	@Autowired
	SysUserMapper sysUserMapper;
	@Autowired
	FocusUsersMapper focusUsersMapper;
	@Autowired
	ChatLogMapper chatLogMapper;
//	@Autowired
//	RedisClientTemplate redisClientTemplate;
	@Autowired
	PermissionFuncMapper permissionFuncMapper;
	@Autowired
	RedisDbTemplate redisDbTemplate;
	@Autowired
	RecordGroupService recordGroupService;
	@Autowired
	DiscussInfoMapper discussInfoMapper;
	
	@Test
	public void test() {
	    logger.info("这是--ShardingsphereTest");
	    
	    // 多库单表查询测试
//	    RecordGroup recordGroup = new RecordGroup();
//	    List<RecordGroup> recordGroupList = recordGroupService.selectAll(recordGroup);
//	    System.out.println(JSONArray.toJSONString(recordGroupList));
	    
	    DiscussInfoVO discussInfoVOIn = new DiscussInfoVO();
	    discussInfoVOIn.setRecordId(new Long("704056073715691520"));
	    System.out.println(JSONArray.toJSONString(discussInfoMapper.selectDiscussInfoList(discussInfoVOIn)));
	    
//	    System.out.println(redisDbTemplate.isValidConnect());
//	    System.out.println(redisDbTemplate.isValidConnect(1));
//	    System.out.println(redisDbTemplate.getString("hello"));
	    
//	    Map<String, Object> map = new HashMap<>();
//	    map.put("a", true);
//	    map.put("b", 1);
//	    redisDbTemplate.setMap("test", map, 1);
//	    System.out.println(redisDbTemplate.getMapValue("test", "b", 1));

//		List<PermissionFunc> perList = permissionFuncMapper.selectAll();
//		System.out.println(JSONObject.toJSONString(perList));
	    
	    /*ChatLogVO chatTestIn = new ChatLogVO();
		chatTestIn.setCompanyCode("S20211219");
		chatTestIn.setServer("S80d10cf7");
		chatTestIn.setClient("C2a7f5e28");
		List<ChatLog> listChat = chatLogMapper.listChatMore(chatTestIn);
		listChat.forEach(chatLog -> {
	    	System.out.println(JSONObject.toJSONString(chatLog));
	    });*/
	    
	    // 新增聊天记录
	    /*ChatLogVO chatLogNew = new ChatLogVO();
	    chatLogNew.setCompanyCode("S20211219");
	    chatLogNew.setServer("S80d10cf7");
	    chatLogNew.setClient("Cfccb060d");
	    chatLogNew.setType("1");
	    chatLogNew.setStatus("0");
	    
	    for (int i = 60; i < 75; i++) {
		    chatLogNew.setChatId(SnowFlakeUtils.getTableId("0,1,2,3,4"));
		    chatLogNew.setContent("测试数据插入--"+i);
		    chatLogNew.setCreateTime(new Date());
		    int result = chatLogMapper.insertChatLog(chatLogNew);
		    if (result > 0) {
				logger.info("新增记录成功");
			} else {
				logger.error("新增记录失败");
			}
		}*/
	    
//	    chatLogNew.setContent("测试数据插入--test4");
//	    chatLogNew.setCreateTime(new Date());
//	    int result = chatLogMapper.insertChatLog(chatLogNew);
//	    if (result > 0) {
//			logger.info("新增记录成功");
//		} else {
//			logger.error("新增记录失败");
//		}
	    
	    // 查询未读的聊天记录
	    /*ChatLogVO chatLogVO = new ChatLogVO();
	    chatLogVO.setUserId(85);
	    chatLogVO.setType("1");
	    chatLogVO.setServer("S80d10cf7");
	    chatLogVO.setClient("C2a7f5e28");
	    chatLogVO.setCompanyCode("S20211219");
//	    List<UserCompanyTO> listCL = chatLogMapper.selectUserListByChatNotRead(chatLogVO);
	    List<UserCompanyTO> listCL = chatLogMapper.selectCustomerListByChatNotRead(chatLogVO);
	    
//	    List<ChatLog> listCL = chatLogMapper.selectChatLogByBA("6854954683518156800000", "6854954697065758720002");
//	    List<ChatLog> listCL = chatLogMapper.selectChatLogByClient("C80759eeb");
	    
	    listCL.forEach(chatLog -> {
	    	System.out.println(JSONObject.toJSONString(chatLog));
	    });*/

	    /*List<SysUser> listSysUser = new ArrayList<>();
	    SysUser record = new SysUser();
	    record.setStatus(0);
	    listSysUser = sysUserMapper.select(record);
	    
	    List<FocusUsersVO> listFU = new ArrayList<>();
	    FocusUsersVO focusUsersVONew = null;
	    int i = 0;
	    for (SysUser sysUser : listSysUser) {
			i++;
			if (i < 10) {
				continue;
			}
			
	    	focusUsersVONew = new FocusUsersVO();
			focusUsersVONew.setFocusUserId(sysUser.getUserId());
			focusUsersVONew.setFocusUserName(sysUser.getUsername());
			focusUsersVONew.setCreateTime(new Date());
			focusUsersVONew.setUserId(28);
			listFU.add(focusUsersVONew);
			
			if (i > 15) {
				break;
			}
		}
	    
	    int result = focusUsersMapper.insertFocusUsersByBatch(listFU);
	    if (result > 0) {
			logger.info("批量新增用户关注成功");
		} else {
			logger.error("批量新增用户关注失败");
		}*/
	    
//	    List<UserInfo> listUserInfo = new ArrayList<>();
//	    UserInfo userInfoNew = null;
//	    for (int i = 0; i < 5; i++) {
//			userInfoNew = new UserInfo();
//			userInfoNew.setUserName("用户"+i);
//			userInfoNew.setAge(i+10);
//			listUserInfo.add(userInfoNew);
//		}
	    
//	    int result = userMapper.insertUserByBatch(listUserInfo);
//	    if (result > 0) {
//			logger.info("批量新增用户成功");
//		} else {
//			logger.error("批量新增用户失败");
//		}
	    
//	    List<UserInfoTO> listUser = userMapper.selectUserInfo();
//	    for (UserInfoTO userCompanyTO : listUser) {
//	    	System.out.println(JSONObject.toJSONString(userCompanyTO));
//		}
	    
//	    List<SysUser> listSysUser = new ArrayList<>();
//	    SysUser record = new SysUser();
//	    record.setStatus(1);
//	    listSysUser = sysUserMapper.select(record);
//	    for (SysUser sysUser : listSysUser) {
//	    	logger.info(JSONObject.toJSONString(sysUser));
//		}
	}

    private long getLongValue(final Comparable<?> value) {
        return Long.parseLong(value.toString());
    }

}
