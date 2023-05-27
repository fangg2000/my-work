package com.xclj.replay;

import java.io.Serializable;

public class ResultEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int code;
	private Object data;
	private String msg;
	
	public ResultEntity() {
		this.code = ResultParam.SUCCESS.getCode();
		this.msg = ResultParam.SUCCESS.getMsg();
	}
	
	public ResultEntity(ResultParam resultParam) {
		this.code = resultParam.getCode();
		this.msg = resultParam.getMsg();
	}
	
	public ResultEntity(Object data) {
		this.data = data;
		this.code = ResultParam.SUCCESS.getCode();
		this.msg = ResultParam.SUCCESS.getMsg();
	}
	
	public ResultEntity(ResultParam resultParam, String msg) {
		this.code = resultParam.getCode();
		this.msg = msg;
	}
	
	public ResultEntity setResultBody(ResultParam resultParam) {
		this.code = resultParam.getCode();
		this.msg = resultParam.getMsg();
		return this;
	}
	
	public ResultEntity setResultBody(int code, String msg) {
		this.code = code;
		this.msg = msg;
		return this;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
