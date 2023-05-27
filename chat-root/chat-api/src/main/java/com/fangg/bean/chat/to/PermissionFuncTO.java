package com.fangg.bean.chat.to;

import java.io.Serializable;

import com.fangg.annotation.PermiFuncAnnotation;
import com.fangg.bean.chat.entity.PermissionEntity;

/**
 * 用户功能权限(用boolean原始类型(不能使用封装类Boolean)判断是否有权限， 使用PermissionEntity对象表示有相关限制)
 * <pre>注：先判断属性不能为null</pre>
 * @author fangg 2022年1月20日 上午11:19:06
 */
public class PermissionFuncTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PermiFuncAnnotation(code = "F0001")
	private boolean findF0001 = false; 	// 查看用户的关注用户列表权限

	@PermiFuncAnnotation(code = "F0002", limit = true)
	private PermissionEntity findF0002; // 最近联系人15个

	@PermiFuncAnnotation(code = "F0003", limit = true)
	private PermissionEntity findF0003; // 最近联系人无限个

	@PermiFuncAnnotation(code = "F0004", limit = true)
	private PermissionEntity findF0004; // 最近联系人5个


	/** 查看用户的关注用户列表权限 */
	public boolean getFindF0001() {
		return findF0001;
	}

	public void setFindF0001(boolean findF0001) {
		this.findF0001 = findF0001;
	}

	/** 最近联系人15个 */
	public PermissionEntity getFindF0002() {
		return findF0002;
	}

	public void setFindF0002(PermissionEntity findF0002) {
		this.findF0002 = findF0002;
	}

	/** 最近联系人无限个 */
	public PermissionEntity getFindF0003() {
		return findF0003;
	}

	public void setFindF0003(PermissionEntity findF0003) {
		this.findF0003 = findF0003;
	}

	/** 最近联系人5个 */
	public PermissionEntity getFindF0004() {
		return findF0004;
	}

	public void setFindF0004(PermissionEntity findF0004) {
		this.findF0004 = findF0004;
	}

}
