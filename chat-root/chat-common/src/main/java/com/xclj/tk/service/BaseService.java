package com.xclj.tk.service;

import java.util.List;

import com.xclj.page.PageEntity;
import com.xclj.page.PageResult;

public interface BaseService<T> {
	
	public int insert(T entity);

	public int deleteByPrimaryKey(Object key);

	/** 根据ID只更新不为NULL的字段 **/
	public int updateByPrimaryKey(T entity);

	public T selectByPrimaryKey(Object key);

	/**
	 * 查询单个对象：如果多条记录则会抛出异常
	 */
	public T selectOne(T entity);

	/**
	 * 带条件查询所有
	 */
	public List<T> selectAll(T entity);
	
	/**
	 * 分页查询
	 */
	public PageResult getListByPage(PageEntity pageEntity, Class<T> entity);

}
