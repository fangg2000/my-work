//package com.fangg.config.sharingshpere.autosplit;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//@Repository
////@PropertySource({ "classpath:config/db.properties" })
//public class DatabaseTableInit {
//	
////	@Value(value = "${db.dirverClass}")
////	private String driver;
////
////	@Value(value = "${db.url}")
////	private String url;
////
////	@Value(value = "${db.username}")
////	private String userName;
////
////	@Value(value = "${db.password}")
////	private String password;
//	
//	@Autowired
//	private ActiveConfig activeConfig;
//	
//	@Autowired
//	private DynamicTablesProperties dynamicTables;
//
//	public void init() throws SQLException, ClassNotFoundException {
//		// 连接数据库
//		Class.forName("com.mysql.cj.jdbc.Driver");
//		// 测试url中是否包含useSSL字段，没有则添加设该字段且禁用
////		if (url.indexOf("?") == -1) {
////			url = url + "?useSSL=false";
////		} else if (url.indexOf("useSSL=false") == -1 || url.indexOf("useSSL=true") == -1) {
////			url = url + "&useSSL=false";
////		}
//		
//		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db0?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&autoReconnect=true", 
//				"root", "");
//		Statement stat = conn.createStatement();
//
//		// 要创建数据表的名称
//		String tableName = "wms_message";
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
//		String tableSuffix = sdf.format(new Date());
//		String actualTableName = tableName + "_" + tableSuffix;
//
//		// 获取数据库表名
//		ResultSet rs = conn.getMetaData().getTables(null, null, actualTableName, null);
//
//		// 判断表是否存在，如果存在则什么都不做，否则创建表
//		if (rs.next()) {
//			return;
//		} else {
//			// 先判断是否纯在表名，有则先删除表在创建表
//
//			// 创建信息表
//			stat.executeUpdate("CREATE TABLE " + actualTableName + "(\n" + "  `id` bigint(20) NOT NULL,\n"
//					+ "  `message` varchar(255) DEFAULT NULL COMMENT '信息',\n"
//					+ "  `create_time` datetime NOT NULL COMMENT '创建事件',\n"
//					+ "  `type_id` int(11) DEFAULT NULL COMMENT '类型id',\n"
//					+ "  `detail_id` bigint(20) DEFAULT NULL COMMENT '详情id',\n" + "  PRIMARY KEY (`id`) USING BTREE\n"
//					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;");
//
//			// 修改配置文件
//			try {
//				dynamicTables.setNames(new String[] { tableName });
//				dynamicTables.setActualTableName(actualTableName);
//				activeConfig.configInit();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		// 释放资源
//		stat.close();
//		conn.close();
//	}
//
//}
