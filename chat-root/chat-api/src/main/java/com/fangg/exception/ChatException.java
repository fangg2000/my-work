package com.fangg.exception;

import com.xclj.replay.ResultParam;

/**
 * 自定义异常类
 * @author fangg
 * 2021年12月20日 下午7:50:39
 */
public class ChatException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorMsg;
	private int errorCode;
	private ResultParam resultParam;
	
	public ChatException(String message) {
		this.errorCode = ResultParam.FAILED_UNKNOW.getCode();
		this.errorMsg = message;
	}
	
	public ChatException(int errorCode, String message) {
		this.errorCode = errorCode;
		this.errorMsg = message;
	}
	
	public ChatException(ResultParam resultParam) {
		this.resultParam = resultParam;
		this.errorCode = resultParam.getCode();
		this.errorMsg = resultParam.getMsg();
	}
	
	public ChatException(ResultParam resultParam, String message) {
		this.resultParam = resultParam;
		this.errorCode = resultParam.getCode();
		this.errorMsg = message;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public ResultParam getResultParam() {
		return resultParam;
	}

	public void setResultParam(ResultParam resultParam) {
		this.resultParam = resultParam;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getMessage() {
		return errorMsg;
	}
	
}
