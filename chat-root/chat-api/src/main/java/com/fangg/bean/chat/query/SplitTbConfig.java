package com.fangg.bean.chat.query;

import java.io.Serializable;

/** 
 * 动态分表配置实体类
 * @author fangg
 * date:2022/01/04 16:24
 */
public class SplitTbConfig implements Serializable {
    /** 
     * 串行版本ID
    */
    private static final long serialVersionUID = -3346586793127682004L;

    /** 
     */ 
    private Integer splitId;

    /** 
     * 数据库地址
     */ 
    private String dbIp;

    /** 
     * 数据库名
     */ 
    private String dbName;

    /** 
     * 分表虚名
     */ 
    private String tableName;

    /** 
     * 新增时操作表后缀，用“,”分隔(数量为2/3/5/6/7/9)
     */ 
    private String tableSuffix;

	public Integer getSplitId() {
		return splitId;
	}

	public void setSplitId(Integer splitId) {
		this.splitId = splitId;
	}

	public String getDbIp() {
		return dbIp;
	}

	public void setDbIp(String dbIp) {
		this.dbIp = dbIp;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableSuffix() {
		return tableSuffix;
	}

	public void setTableSuffix(String tableSuffix) {
		this.tableSuffix = tableSuffix;
	}

}