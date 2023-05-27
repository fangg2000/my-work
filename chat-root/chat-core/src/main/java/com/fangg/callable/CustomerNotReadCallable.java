package com.fangg.callable;

import java.util.List;
import java.util.concurrent.Callable;

import com.fangg.bean.chat.to.UserCompanyTO;
import com.fangg.bean.chat.vo.ChatLogVO;
import com.fangg.dao.ChatLogMapper;

/**
 * 用户查询门店未读聊天记录
 * @author fangg
 * 2021年12月30日 下午5:15:23
 */
public class CustomerNotReadCallable implements Callable<List<UserCompanyTO>> {

	private ChatLogMapper chatLogMapper;
	private ChatLogVO chatLogVO;
	
	public CustomerNotReadCallable(ChatLogMapper chatLogMapper, ChatLogVO chatLogVO) {
		this.chatLogMapper = chatLogMapper;
		this.chatLogVO = chatLogVO;
	}

	@Override
	public List<UserCompanyTO> call() throws Exception {
		System.out.println(Thread.currentThread().getName());
		return chatLogMapper.selectCustomerListByChatNotRead(chatLogVO);
	}
	
}
