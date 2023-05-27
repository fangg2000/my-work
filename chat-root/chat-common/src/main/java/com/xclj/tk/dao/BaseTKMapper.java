package com.xclj.tk.dao;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 此为基本查询方法mapper增强
 * <pre>
 * 注意：需要操作sharingsphere的mapper不要继承此接口
 * </pre>
 * @author fangg
 * 2021年11月24日 下午2:13:10
 */
@tk.mybatis.mapper.annotation.RegisterMapper
public interface BaseTKMapper<T> extends Mapper<T>, MySqlMapper<T> {

}
