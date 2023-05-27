package com.fangg.constant;

public class TypeConstant {
	
	/** 默认DEFAULT **/
	public final static String DEFAULT = "DEFAULT";
	
	/**系统编号**/
	public final static String SYSTEM_CODE = "SYSTEM";
	/** ticket名称 **/
	public final static String TICKET = "ticket";
	
	
	/**删除：0否**/
	public final static int DEL_FLAG_0 = 0;
	/**删除：1是**/
	public final static int DEL_FLAG_1 = 1;
	
	/**禁用状态：0否**/
	public final static int STATUS_TYPE_0 = 0;
	/**禁用状态：1是**/
	public final static int STATUS_TYPE_1 = 1;
	
	/**聊天记录状态：0未读**/
	public final static String CHAT_STATUS_0 = "0";
	/**聊天记录状态：1已读**/
	public final static String CHAT_STATUS_1 = "1";
	
	/**聊天发起者 **/
	public final static String CHAT_STARTER_1 = "1";
	/**聊天接收者 **/
	public final static String CHAT_STARTER_0 = "0";
	
	/**在线状态：0离线**/
	public final static int ONLINE_STATUS_0 = 0;
	/**在线状态：1在线**/
	public final static int ONLINE_STATUS_1 = 1;
	/**在线状态：2忙碌**/
	public final static int ONLINE_STATUS_2 = 2;
	
	/**登录类型：0WEB**/
	public final static int LOGIN_TYPE_0 = 0;
	/**登录类型：1手机**/
	public final static int LOGIN_TYPE_1 = 1;
	/**登录类型：2pad**/
	public final static int LOGIN_TYPE_2 = 2;
	
	/**身份：0客服 **/
	public final static int USER_TYPE_0 = 0;
	/**身份：1用户 **/
	public final static int USER_TYPE_1 = 1;
	/**身份：2群 **/
	public final static int USER_TYPE_2 = 2;
	/**身份：3房间(没人时删除) **/
	public final static int USER_TYPE_3 = 3;
	/**身份：4企业 **/
	public final static int USER_TYPE_4 = 4;
	
	/**点赞：0否**/
	public final static String GIVE_A_LIKE_0 = "0";
	/**点赞：1是**/
	public final static String GIVE_A_LIKE_1 = "1";
		
	/**禁止陌生人消息：0否 **/
	public final static int STRANGER_CONTACT_0 = 0;
	/**禁止陌生人消息：1是 **/
	public final static int STRANGER_CONTACT_1 = 1;
	
	/**查询配置消息：0否 **/
	public final static int CONFIG_FLAG_0 = 0;
	/**查询配置消息：1是 **/
	public final static int CONFIG_FLAG_1 = 1;
	
	// 等级越大权限越大
	/**用户等级：0普通用户 **/
	public final static int GRADE_0 = 0;
	/**用户等级：1VIP用户 **/
	public final static int GRADE_1 = 1;
	/**用户等级：2MVP用户 **/
	public final static int GRADE_2 = 2;
	/**用户等级：3SUP用户 **/
	public final static int GRADE_3 = 3;
	
	/** 加解密类型：AES **/
	public final static String AES = "AES";
	/** 加解密类型：RSA **/
	public final static String RSA = "RSA";
	
	/** 初次登录0 **/
	public final static int LOGIN_AGAIN_0 = 0;
	/** 再次登录1 **/
	public final static int LOGIN_AGAIN_1 = 1;
	
	/** 当前登录端绑定标识0：未绑定 **/
	public final static int LOGIN_BIND_0 = 0;
	/** 当前登录端绑定标识1：已绑定 **/
	public final static int LOGIN_BIND_1 = 1;
	/** 当前登录端绑定标识2：已绑定其他平台 **/
	public final static int LOGIN_BIND_2 = 2;
	
	/** 日记类型0：草稿 **/
	public final static int RECORD_STATUS_0 = 0;
	/** 日记类型1：公开 **/
	public final static int RECORD_STATUS_1 = 1;
	/** 日记类型2：好友可见 **/
	public final static int RECORD_STATUS_2 = 2;
	/** 日记类型3：私密 **/
	public final static int RECORD_STATUS_3 = 3;
	
	/** 日记浏览用户 **/
	public final static String DR_REVIEW_USER = "DR_REVIEW_USER";
	/** 日记收藏用户 **/
	public final static String DR_COLLECT_USER = "DR_COLLECT_USER";
	
	/** 用户注册类型：0个人 **/
	public final static int REGIST_TYPE_0 = 0;
	/** 用户注册类型：1企业 **/
	public final static int REGIST_TYPE_1 = 1;
	
	// 0自由加入，1管理员审核，2拒绝加入
	/** 入群申请条件：0自由加入 **/
	public final static int JOIN_CG_CONDITION_0 = 0;
	/** 入群申请条件：1管理员审核 **/
	public final static int JOIN_CG_CONDITION_1 = 1;
	/** 入群申请条件：2拒绝加入 **/
	public final static int JOIN_CG_CONDITION_2 = 2;
	
	/** 用户群消息状态：0屏蔽消息 **/
	public final static int UCG_MSG_STATUS_0 = 0;
	/** 用户群消息状态：1接收消息 **/
	public final static int UCG_MSG_STATUS_1 = 1;
	
	/** 群成员数量: 30(默认) **/
	public final static int CG_member_30 = 30;
	/** 群成员数量: 100 **/
	public final static int CG_member_100 = 100;
	/** 群成员数量: 500 **/
	public final static int CG_member_500 = 500;
	/** 群成员数量: 1000 **/
	public final static int CG_member_1000 = 1000;
	
	/** 入群审核状态: 0拒绝 **/
	public final static int CG_APPLY_CHECK_0 = 0;
	/** 入群审核状态: 1通过 **/
	public final static int CG_APPLY_CHECK_1 = 1;
	
	/** 群等级: 1 **/
	public final static int CG_GRADE_1 = 1;
	/** 群等级: 2 **/
	public final static int CG_GRADE_2 = 2;
	/** 群等级: 3 **/
	public final static int CG_GRADE_3 = 3;
	/** 群等级: 4 **/
	public final static int CG_GRADE_4 = 4;
	
	/** 黑名单记录状态: 0失效 **/
	public final static int BLACK_STATUS_0 = 0;
	/** 黑名单记录状态: 1有效 **/
	public final static int BLACK_STATUS_1 = 1;
	
	/** 所在群身份: 0普通成员 **/
	public final static int CG_IDENTITY_0 = 0;
	/** 所在群身份: 1长老 **/
	public final static int CG_IDENTITY_1 = 1;
	/** 所在群身份: 2副群 **/
	public final static int CG_IDENTITY_2 = 2;
	/** 所在群身份: 3群主 **/
	public final static int CG_IDENTITY_3 = 3;
	
}
