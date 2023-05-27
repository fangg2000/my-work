package com.xclj.page;

import java.io.Serializable;
import java.util.List;

import com.github.pagehelper.PageInfo;

/**
 * 页面返回对象（针对easyui）
 * @author fangg
 * @time 2018年6月17日
 */
public class PageResult implements Serializable{

	private static final long serialVersionUID = 1L;
	private long total; // 记录总数
	private List rows; // 注：页面数量大小从此处取得
	
	public PageResult() {
		// TODO Auto-generated constructor stub
	}
	
	public PageResult(PageInfo pageInfo) {
		setRows(pageInfo.getList());
		this.total = pageInfo.getTotal();
	}
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

}
