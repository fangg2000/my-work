package com.xclj.page;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class PageEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	private int page = 1; 	// 接收初始化页码，默认第1页
	private int rows = 10; 	// 注：页面显示每页数量从此处取得，默认查询10行数据
	private int offset; 	// 初始行
	private Map<String, Object> params;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getOffset() {
		return (getPage()-1)*getRows();
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
		
		if (params.get("pageNum") != null) {
			this.page = Integer.valueOf(params.get("pageNum").toString());
		} else if (params.get("pageSize") != null) {
			this.rows = Integer.valueOf(params.get("pageSize").toString());
		}
	}

	public <T> T getParamEntity(Class<T> t) {
		return JSONObject.parseObject(JSONObject.toJSONString(getParams()), t);
	}

}
