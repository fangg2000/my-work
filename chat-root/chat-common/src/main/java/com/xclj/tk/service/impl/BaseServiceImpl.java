package com.xclj.tk.service.impl;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xclj.page.PageEntity;
import com.xclj.page.PageResult;
import com.xclj.tk.service.BaseService;

import tk.mybatis.mapper.common.Mapper;

/**
 * 基本方法封装(基本的CRUD操作和分页查询)
 * <pre>
 * 注：
 * 	1>如果实体类名称T与数据库表名不一致，则需要使用@Table(name="表名")标注
 * 	2>如果有主键，则需要使用@Id和@Column(name="字段名")标注。否则根据主键操作时异常
 * </pre>
 * @author fangg
 * 2021年11月24日 下午2:15:31
 */
//@Service(value = "baseService")
public class BaseServiceImpl<T> implements BaseService<T> {

	private Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

	@Autowired
	protected Mapper<T> mapper;

	public String getTName() {
		Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		return tClass.getSimpleName();
	}

	//@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int insert(T entity) {
		//logger.info("新增{}信息入参：{}", getTName(), JSONObject.toJSONString(entity));
		int result = 0;
		result = this.mapper.insertSelective(entity);
		if (result == 1) {
			//logger.info("新增{}信息成功", getTName());
		} else {
			logger.info("新增{}信息失败", getTName());
		}
		return result;
	}

	//@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteByPrimaryKey(Object key) {
		//logger.info("删除{}信息入参：key={}", getTName(), JSONObject.toJSONString(key));
		int result = 0;
		result = this.mapper.deleteByPrimaryKey(key);
		if (result == 1) {
			//logger.info("删除{}信息成功", getTName());
		} else {
			logger.info("删除{}信息失败", getTName());
		}
		return result;
	}

	//@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateByPrimaryKey(T entity) {
		//logger.info("更新{}信息入参：{}", getTName(), JSONObject.toJSONString(entity));
		int result = 0;
		result = this.mapper.updateByPrimaryKeySelective(entity);
		if (result == 1) {
			//logger.info("更新{}信息成功", getTName());
		} else {
			logger.info("更新{}信息失败", getTName());
		}
		return result;
	}

	public T selectByPrimaryKey(Object key) {
		//logger.info("根据主键查询{}信息入参：key={}", getTName(), JSONObject.toJSONString(key));
		try {
			return this.mapper.selectByPrimaryKey(key);
		} catch (Exception e) {
			logger.error("{}基本操作，根据主键查询数据异常：", getTName(), e);
		}
		return null;
	}

	/**
	 * 查询单个对象：如果多条记录则会抛出异常
	 * 
	 * @param entity
	 * @return
	 */
	public T selectOne(T entity) {
		try {
			return this.mapper.selectOne(entity);
		} catch (Exception e) {
			logger.error("{}查询数据异常，检查是否返回多个结果集", getTName(), e);
		}
		return null;
	}

	/**
	 * 带条件查询所有
	 * 
	 * @param entity
	 * @return
	 */
	public List<T> selectAll(T entity) {
		return this.mapper.select(entity);
	}

	public PageResult getListByPage(PageEntity pageEntity, Class<T> entity) {
		PageHelper.offsetPage(pageEntity.getOffset(), pageEntity.getRows());
		//PageHelper.startPage(pageEntity.getPage(), pageEntity.getRows());
		//Map<String, Object> params = pageEntity.getParams();
		
		T bookInfoVOIn = JSONObject.parseObject(JSONObject.toJSONString(pageEntity.getParams()), entity);
		List<T> bookInfoList = this.mapper.select(bookInfoVOIn);
		return new PageResult(new PageInfo<T>(bookInfoList));
	}
}
