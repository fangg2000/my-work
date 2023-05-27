package com.fangg.config.sharingshpere;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态切换数据源主要依靠AbstractRoutingDataSource。创建一个AbstractRoutingDataSource的子类，
 * 重写determineCurrentLookupKey方法，用于决定使用哪一个数据源。
 * 这里主要用到AbstractRoutingDataSource的两个属性defaultTargetDataSource和targetDataSources。
 * defaultTargetDataSource默认目标数据源，targetDataSources（map类型）存放用来切换的数据源。
 * @author fangg
 * 2022年1月3日 下午3:51:01
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
		super.setDefaultTargetDataSource(defaultTargetDataSource);
		super.setTargetDataSources(targetDataSources);
		// afterPropertiesSet()方法调用时用来将targetDataSources的属性写入resolvedDataSources中的
		super.afterPropertiesSet();
	}

	/**
	 * 根据Key获取数据源的信息
	 *
	 * @return
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		return DynamicDataSourceContextHolder.getDataSourceType();
	}
	
}
