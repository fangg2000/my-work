package com.fangg.config.sharingshpere.autosplit;

import org.springframework.stereotype.Repository;

/**
 * 动态分表配置
 *
 */
@Repository
public class DynamicTablesProperties {
	String[] names; // 需要动态分表的逻辑表明

	String actualTableName; // 创建的真是表明

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public String getActualTableName() {
		return actualTableName;
	}

	public void setActualTableName(String actualTableName) {
		this.actualTableName = actualTableName;
	}
}
