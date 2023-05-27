package com.fangg.constant;

/**
 * redis缓存KEY配置(注：key统一用"_"分隔)
 * @author fangg
 * 2022年1月19日 上午9:08:26
 */
public class RedisConstant {
	
	/** redis数据库db0 **/
	public final static int REDIS_DB0 = 0;
	/** redis数据库db1 **/
	public final static int REDIS_DB1 = 1;
	/** redis数据库db2 **/
	public final static int REDIS_DB2 = 2;
	/** redis数据库db3 **/
	public final static int REDIS_DB3 = 3;
	/** redis数据库db4 **/
	public final static int REDIS_DB4 = 4;
	/** redis数据库db5 **/
	public final static int REDIS_DB5 = 5;
	
	/**客服列表KEY**/
	public final static String CUSTOMER_SERVICE_KEY = "CUSTOMER_SERVICE_KEY";
	/**用户与用户聊天缓存Map列表KEY**/
	public final static String USER_CHAT_USER_KEY = "USER_CHAT_USER_KEY";
	/** 用户所有聊天门店缓存KEY**/
	public final static String USER_CHAT_CUSTOMER_KEY = "USER_CHAT_CUSTOMER_KEY";
	/** 省份城市缓存KEY**/
	public final static String PROVINCE_CITY_KEY = "PROVINCE_CITY_KEY";
	/** 用户点赞缓存KEY(field为"点赞Code_被点赞Code")**/
	public final static String GIVE_A_LIKE_KEY = "GIVE_A_LIKE_KEY";
	/** 用户点赞数量缓存KEY**/
	public final static String USER_LIKE_NUM_KEY = "USER_LIKE_NUM_KEY";
	/** 关注用户缓存KEY(field为"关注Code_被关注Code")**/
	public final static String FOCUS_USER_KEY = "FOCUS_USER_KEY";
	/** 用户关注数量缓存KEY**/
	public final static String USERS_FOCUS_NUM_KEY = "USERS_FOCUS_NUM_KEY";
	/** 用户上线缓存KEY**/
	public final static String USER_ONLINE_KEY = "USER_ONLINE_KEY";
	/** 新用户联系缓存KEY**/
	public final static String NEW_USER_CHAT_KEY = "NEW_USER_CHAT_KEY";
	/** 用户功能权限缓存KEY(field为"userCode")**/
	public final static String USER_PER_FUNC_KEY = "USER_PER_FUNC_KEY";
	/** 用户最近联系人缓存KEY(field为"userCode")**/
	public final static String USER_NEAR_CHAT_KEY = "USER_NEAR_CHAT_KEY";
	/** 用户密钥信息缓存KEY(field为"userCode")**/
	public final static String RSA_AES_KEY = "RSA_AES_KEY";
	/** 用户/群头像路径缓存KEY(field为"userCode/chat_group_code") **/
	public final static String UG_PP_KEY = "UG_PP_KEY";
	/** 黑名单IP缓存KEY **/
	public final static String BLACK_IP_KEY = "BLACK_IP_KEY";
	/** 黑名单用户缓存KEY **/
	public final static String BLACK_USER_KEY = "BLACK_USER_KEY";
	
	
	/** 群信息缓存KEY(field为"chat_group_code") **/
	public final static String CHAT_GROUP_KEY = "CHAT_GROUP_KEY";
	/** 群申请信息提醒缓存KEY(field为"userCode") **/
	public final static String CG_APPLY_REMIND_KEY = "CG_APPLY_REMIND_KEY";
	/** 用户群列表缓存KEY(field为"userCode") **/
	public final static String USER_CG_LIST_KEY = "USER_CG_LIST_KEY";
	/** 群用户列表缓存KEY(field为"chat_group_code") **/
	public final static String CG_USER_LIST_KEY = "CG_USER_LIST_KEY";
	
	
	public final static String BIND_KEY = "BIND_KEY";
	/** 日记默认分组ID缓存KEY(field为"userCode")**/
	public final static String DR_DEFAULT_KEY = "DR_DEFAULT_KEY";
	/** 日记ID缓存KEY(field为"userCode")**/
	public final static String DR_ID_BY_MONTH_KEY = "DR_ID_BY_MONTH_KEY";
	/** 日记浏览数、用户收藏数、评论点赞数缓存KEY(field为"recordId")**/
	public final static String DR_NUM_KEY = "DR_NUM_KEY";
	/** 用户日记收藏缓存KEY(field为"userCode")**/
	public final static String USER_DR_COLLECT_KEY = "USER_DR_COLLECT_KEY";
	
	/** 分表配置KEY **/
	public final static String SPLIT_TB_CONFIG_KEY = "SPLIT_TB_CONFIG_KEY";
	/** 功能权限KEY **/
	public final static String PERMISSION_FUNC_KEY = "PERMISSION_FUNC_KEY";
	
	// 会超时的缓存尽量放后面
	/** ticket前缀**/
	public final static String TICKET_PREFIX_KEY = "ZH_TK_";
	/** 用户与用户聊天缓存前缀**/
	public final static String U2U_PREFIX_KEY = "ZI_U2U_";
	/** 用户与客服聊天缓存前缀**/
	public final static String U2C_PREFIX_KEY = "ZJ_U2C_";
	/** 用户群聊天缓存前缀**/
	public final static String U2G_PREFIX_KEY = "ZK_U2G_";
	
}
