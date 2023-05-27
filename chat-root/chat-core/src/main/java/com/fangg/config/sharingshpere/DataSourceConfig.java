package com.fangg.config.sharingshpere;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.keygen.KeyGenerateStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
	
	private DataSource dataSource;
 
//    @Value("${spring.shardingsphere.datasource.db0.type}")
//    private String url0;
//    @Value("${datasource0.username}")
//    private String username0;
//    @Value("${datasource0.password}")
//    private String password0;
//    @Value("${datasource0.driver-class-name}")
//    private String driverClassName0;
// 
//    @Value(("${spring.datasource.druid.filters}"))
//    private String filters;
 
    public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/*@Bean(name="dataSource")
    public DataSource dataSource() {
		setDataSource(myDataSource(4));
        return this.dataSource;
    }
    
    @Bean(name="newDataSource")
    public DataSource newDataSource() {
    	return myDataSource(8);
    }*/
    
    /*@Bean
    //@ConfigurationProperties("spring.datasource.druid.master")
    public DataSource dataSource() {
        return myDataSource(4);
    }

    @Bean
    //@ConfigurationProperties("spring.datasource.druid.slave")
    //@ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource() {
        return myDataSource(8);
    }
    
    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dataSource(DataSource dataSource, DataSource newDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.MASTER.name(), dataSource);
        targetDataSources.put(DataSourceType.SLAVE.name(), newDataSource);
        return new DynamicDataSource(dataSource, targetDataSources);
    }*/
    
    public static DataSource myDataSource(int num) {
    	try {
    		
    		/* 数据源配置 */
    		HikariDataSource writeDataSource0 = new HikariDataSource();
    		writeDataSource0.setDriverClassName("com.mysql.cj.jdbc.Driver");
    		writeDataSource0.setJdbcUrl("jdbc:mysql://localhost:3306/db0?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8&autoReconnect=true");
    		writeDataSource0.setUsername("root");
    		writeDataSource0.setPassword("");
    		
//        	HikariDataSource writeDataSource1 = new HikariDataSource();
    		// ...忽略其他数据库配置项
    		
//        	HikariDataSource read0OfwriteDataSource0 = new HikariDataSource();
    		// ...忽略其他数据库配置项
    		
//        	HikariDataSource read1OfwriteDataSource0 = new HikariDataSource();
    		// ...忽略其他数据库配置项
    		
//        	HikariDataSource read0OfwriteDataSource1 = new HikariDataSource();
    		// ...忽略其他数据库配置项
    		
//        	HikariDataSource read1OfwriteDataSource1 = new HikariDataSource();
    		// ...忽略其他数据库配置项
    		
    		Map<String, DataSource> datasourceMaps = new HashMap<>(6);
    		
    		datasourceMaps.put("db0", writeDataSource0);
//        	datasourceMaps.put("write_ds0_read0", read0OfwriteDataSource0);
//        	datasourceMaps.put("write_ds0_read1", read1OfwriteDataSource0);
    		
//        	datasourceMaps.put("write_ds1", writeDataSource1);
//        	datasourceMaps.put("write_ds1_read0", read0OfwriteDataSource1);
//        	datasourceMaps.put("write_ds1_read1", read1OfwriteDataSource1);
    		
    		
    		// 分片规则配置 
    		// 表达式 ds_${0..1} 枚举值表示的是主从配置的逻辑数据源名称列表
    		ShardingTableRuleConfiguration tOrderRuleConfiguration = new ShardingTableRuleConfiguration("chat_log", String.format("db0.chat_log_$->{0..%s}", num));
    		tOrderRuleConfiguration.setKeyGenerateStrategy(new KeyGenerateStrategyConfiguration("chat_id", "snowflake"));
    		tOrderRuleConfiguration.setTableShardingStrategy(new StandardShardingStrategyConfiguration("chat_id", "tOrderInlineShardingAlgorithm"));
    		Properties tOrderShardingInlineProps = new Properties();
    		tOrderShardingInlineProps.setProperty("algorithm-expression", "chat_log_$->{chat_id % 9}");
    		
    		ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
    		shardingRuleConfiguration.getTables().add(tOrderRuleConfiguration);
    		shardingRuleConfiguration.getShardingAlgorithms().putIfAbsent("tOrderInlineShardingAlgorithm", 
    				new ShardingSphereAlgorithmConfiguration("INLINE", tOrderShardingInlineProps));
    		
//        	ShardingTableRuleConfiguration tOrderItemRuleConfiguration = new ShardingTableRuleConfiguration("t_order_item", "ds_${0..1}.t_order_item_${[0, 1]}");
//        	tOrderItemRuleConfiguration.setKeyGenerateStrategy(new KeyGenerateStrategyConfiguration("order_item_id", "snowflake"));
//        	tOrderRuleConfiguration.setTableShardingStrategy(new StandardShardingStrategyConfiguration("order_item_id", "tOrderItemInlineShardingAlgorithm"));
//        	Properties tOrderItemShardingInlineProps = new Properties();
//        	tOrderItemShardingInlineProps.setProperty("algorithm-expression", "t_order_item_${order_item_id % 2}");
//        	ruleConfiguration.getShardingAlgorithms().putIfAbsent("tOrderItemInlineShardingAlgorithm", new ShardingSphereAlgorithmConfiguration("INLINE",tOrderItemShardingInlineProps));
    		
//        	shardingRuleConfiguration.getTables().add(tOrderItemRuleConfiguration);
//        	shardingRuleConfiguration.getBindingTableGroups().add("t_order, t_order_item");
//        	shardingRuleConfiguration.getBroadcastTables().add("t_bank");
    		// 默认分库策略
//        	shardingRuleConfiguration.setDefaultDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("user_id", "default_db_strategy_inline"));
//        	Properties defaultDatabaseStrategyInlineProps = new Properties();
//        	defaultDatabaseStrategyInlineProps.setProperty("algorithm-expression", "ds_${user_id % 2}");
//        	shardingRuleConfiguration.getShardingAlgorithms().put("default_db_strategy_inline", new ShardingSphereAlgorithmConfiguration("INLINE", defaultDatabaseStrategyInlineProps));
    		
    		// 分布式序列算法配置
    		Properties snowflakeProperties = new Properties();
    		snowflakeProperties.setProperty("worker-id", "123");
    		shardingRuleConfiguration.getKeyGenerators().put("snowflake", new ShardingSphereAlgorithmConfiguration("SNOWFLAKE", snowflakeProperties));
    		
    		// 数据加密规则配置 
//        	Properties encryptProperties = new Properties();
//        	encryptProperties.setProperty("aes-key-value", "123456");
//        	EncryptColumnRuleConfiguration columnConfigAes = new EncryptColumnRuleConfiguration("user_name", "user_name", "", "user_name_plain", "name_encryptor");
//        	EncryptColumnRuleConfiguration columnConfigTest = new EncryptColumnRuleConfiguration("pwd", "pwd", "assisted_query_pwd", "", "pwd_encryptor");
//        	EncryptTableRuleConfiguration encryptTableRuleConfig = new EncryptTableRuleConfiguration("t_user", Arrays.asList(columnConfigAes, columnConfigTest));
    		
    		
//        	Map<String, ShardingSphereAlgorithmConfiguration> encryptAlgorithmConfigs = new LinkedHashMap<>(2, 1);
//        	encryptAlgorithmConfigs.put("name_encryptor", new ShardingSphereAlgorithmConfiguration("AES", encryptProperties));
//        	encryptAlgorithmConfigs.put("pwd_encryptor", new ShardingSphereAlgorithmConfiguration("assistedTest", encryptProperties));
//        	EncryptRuleConfiguration encryptRuleConfiguration = new EncryptRuleConfiguration(Collections.singleton(encryptTableRuleConfig), encryptAlgorithmConfigs);
    		
    		/* 读写分离规则配置 */
//        	ReadwriteSplittingDataSourceRuleConfiguration dataSourceConfiguration1 = new ReadwriteSplittingDataSourceRuleConfiguration("ds_0", "write_ds0", Arrays.asList("write_ds0_read0", "write_ds0_read1"), "roundRobin");
//        	ReadwriteSplittingDataSourceRuleConfiguration dataSourceConfiguration2 = new ReadwriteSplittingDataSourceRuleConfiguration("ds_1", "write_ds0", Arrays.asList("write_ds1_read0", "write_ds1_read0"), "roundRobin");
    		
    		//负载均衡算法
//        	Map<String, ShardingSphereAlgorithmConfiguration> loadBalanceMaps = new HashMap<>(1);
//        	loadBalanceMaps.put("roundRobin", new ShardingSphereAlgorithmConfiguration("ROUND_ROBIN", new Properties()));
    		
//        	ReadwriteSplittingRuleConfiguration readWriteSplittingyRuleConfiguration = new ReadwriteSplittingRuleConfiguration(Arrays.asList(dataSourceConfiguration1, dataSourceConfiguration2), loadBalanceMaps);
    		
    		/* 其他配置 */
    		Properties otherProperties = new Properties();
    		otherProperties.setProperty("sql-show", "true");
    		
    		/* shardingDataSource 就是最终被ORM框架或其他jdbc框架引用的数据源名称 */
//        	DataSource shardingDataSource = ShardingSphereDataSourceFactory.createDataSource(datasourceMaps, 
//        			Arrays.asList(shardingRuleConfiguration, readWriteSplittingyRuleConfiguration, encryptRuleConfiguration), otherProperties);
//        	DataSource shardingDataSource = ShardingSphereDataSourceFactory.createDataSource(datasourceMaps, 
//        			Arrays.asList(shardingRuleConfiguration), otherProperties);
    		DataSource shardingDataSource = ShardingSphereDataSourceFactory.createDataSource(datasourceMaps, 
    				Collections.singleton(shardingRuleConfiguration), otherProperties);
    		return shardingDataSource;
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
    }
        
    private static Properties getProperties() {
        Properties result = new Properties();
        result.setProperty("worker.id", "123");
        return result;
    }
}
