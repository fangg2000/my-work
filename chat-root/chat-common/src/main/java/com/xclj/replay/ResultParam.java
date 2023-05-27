package com.xclj.replay;

public enum ResultParam {

	SUCCESS(1, "操作成功"),
	FAIlED(-1, "操作失败"),
	PARAM_FAILED(-2, "参数有误"),
	FAILED_UNKNOW(-3, "操作异常，请联系系统管理员。"),
	/** -4 账号正在别处登录 */
	LOGIN_LOCATION_UNKNOW(-4, "系统检测到您的账号正在别处登录"),
	/** -5 登录验证失败 */
	LOGIN_CHECK_FAILED(-5, "登录验证失败"),
	LOGIN_TIMEOUT(101, "登录超时"),
	FAILED_WEBSOCKET(102, "websocket链接异常"),
	/** 104 黑名单用户 */
	BLACK_ONE(104, "您已被列入黑名单"),
	/** 103 登录状态异常(账号没有正常退出) */
	LOGIN_UNKNOW(103, "登录状态异常");
	
	private int code;
	private String msg;
	
	private ResultParam() {
	}
	
	private ResultParam(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
