package com.fangg.bean.chat.entity;

import java.io.Serializable;

/**
 * 功能权限属性对象
 * 
 * @author fangg 2022年1月20日 下午4:46:03
 */
public class PermissionEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean permission; 	// 是否有权限
	private Integer limit; 			// 数量限制

	/**
	 * 是否有权限
	 */
	public Boolean getPermission() {
		return permission;
	}

	public void setPermission(Boolean permission) {
		this.permission = permission;
	}

	/**
	 * 数量限制
	 */
	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

}
