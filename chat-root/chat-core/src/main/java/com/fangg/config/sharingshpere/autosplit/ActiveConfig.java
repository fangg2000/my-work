//package com.fangg.config.sharingshpere.autosplit;
//
//import java.io.FileNotFoundException;
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import javax.servlet.ServletContext;
//
//import org.apache.commons.configuration.ConfigurationException;
//import org.apache.shardingsphere.infra.config.exception.ShardingSphereConfigurationException;
//import org.apache.shardingsphere.infra.datanode.DataNode;
//import org.apache.shardingsphere.sharding.rule.ShardingRule;
//import org.apache.shardingsphere.sharding.rule.TableRule;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//import org.springframework.web.context.ConfigurableWebApplicationContext;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//
///**
// * 另一种 基于java.util.properties
// * 这种方式不会造成写入的键值对顺序紊乱（配置文件里面参数多的情况下极力推荐），不过需要引入commons-configuration包
// */
//@Repository
//public class ActiveConfig {
//
//    Logger log = LoggerFactory.getLogger(getClass());
//    /**
//     * message_id_mapping 逻辑表名
//     */
//    public static final String MESSAGE_ID_MAPPING = "wms_message";
//
//    /**
//     * message_id_mapping 分表前缀
//     */
//    public static final String MESSAGE_ID_MAPPING_PREFIX = "message_id_mapping_";
//
//    /**
//     * message_id_mapping 单分片表数量
//     */
//    public static final Long MESSAGE_ID_MAPPING_SINGLE_TABLE_CAPACITY = 20000000L;
//
//    @Autowired
//    private ConfigurableWebApplicationContext configurableWebApplicationContext;
//    @Autowired
//    private ServletContext servletContext;
//    @Autowired
//    private ShardingRule shardingRule;
//    @Autowired
//    private DynamicTablesProperties dynamicTables;
////    @Resource(name = "shardingDataSource")
////    private DataSource dataSource;
//
//    private String[] names={"wms_message"};
//
//    public void configInit() throws ConfigurationException, FileNotFoundException, Exception {
//        //获取配置文件路径
//        /*
//        String path = "/config/spring-shardingjdbc.xml";
//        PropertiesConfiguration config  = new PropertiesConfiguration(path);
//        System.out.println(path);
//        config.setAutoSave(true);
//        config.setProperty("current.term", "35");
//
//        //重新加载配置文件使属性生效
//        //重新加载元数据
////        XmlWebApplicationContext context = (XmlWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(servletContext);
////        context.refresh();
//        //重新load所有bean
//        configurableWebApplicationContext.refresh();
//        */
//
//
//        /****************************方案二************************************/
////        String path = "/config/applicationContext.xml";
////        XMLConfiguration config = new XMLConfiguration(path);
////        String go=config.getString("sharding:data-source.sharding:sharding-rule.sharding:table-rules.sharding:table-rule(0)[@actual-data-nodes]");
////        System.out.println(go);
//
//        //修改配置文件
////        String toSave = "ds0.wms_message_20200$->{1..9},ds0.wms_message_20201$->{0..2}";
////        config.setAutoSave(true);
////        config.setProperty("sharding:data-source.sharding:sharding-rule.sharding:table-rules.sharding:table-rule(0)[@actual-data-nodes]",toSave);
////        String go2=config.getString("sharding:data-source.sharding:sharding-rule.sharding:table-rules.sharding:table-rule(0)[@actual-data-nodes]");
////        System.out.println(go2);
//        //从新加载配置文件
////        config.setReloadingStrategy(new FileChangedReloadingStrategy());
//
//        refreshActualDataNodes();
//    }
//
//    /**
//     * 动态刷新配置文件
//     * @throws NoSuchFieldException
//     * @throws IllegalAccessException
//     */
//    public void refreshActualDataNodes() throws NoSuchFieldException, IllegalAccessException {
//
//        log.info("Job 动态刷新 actualDataNodes START");
//        if (dynamicTables.getNames() == null || dynamicTables.getNames().length == 0) {
//            log.error("【dynamic.table.names】配置为空!");
//            return;
//        }
//        for (int i = 0; i < dynamicTables.getNames().length; i++) {
//            String dynamicTableName = dynamicTables.getNames()[i];
//            TableRule tableRule = null;
//            try {
//            	//tableRule = shardingSphereRule.getTableRule("");
//                tableRule = shardingRule.getTableRule(dynamicTableName);
//            } catch (ShardingSphereConfigurationException e) {
//                log.error(String.format("逻辑表：%s 动态分表配置错误！", dynamicTableName));
//            }
//            String dataSourceName = tableRule.getActualDataNodes().get(0).getDataSourceName();
//            String logicTableName = tableRule.getLogicTable();
//            assert tableRule != null;
//            List<DataNode> newDataNodes = getDataNodes(dynamicTableName, dataSourceName, logicTableName);
//            if (newDataNodes.isEmpty()) {
//                throw new UnsupportedOperationException();
//            }
//            dynamicRefreshDatasource(dataSourceName, tableRule, newDataNodes);
//        }
//        log.info("Job 动态刷新 actualDataNodes END");
//    }
//
//    /**
//     * 获取数据节点
//     */
//    private List<DataNode> getDataNodes(String tableName, String dataSourceName, String logicTableName) {
//        Set<DataNode> newDataNodes = Sets.newHashSet();
////        StringBuilder stringBuilder = new StringBuilder().append(dataSourceName).append(".").append(logicTableName);
//        //创建的逻辑表明
//        StringBuilder stringBuilder = new StringBuilder().append(dataSourceName).append(".").append(dynamicTables.getActualTableName());
//        final int length = stringBuilder.length();
//        // 根据自增id范围分表的场景
////        if (tableName.equals(MESSAGE_ID_MAPPING)) {
////                stringBuilder.setLength(length);
////                stringBuilder.append("_").append("202010");  //创建新的节点
////                DataNode dataNode = new DataNode(stringBuilder.toString());
////                newDataNodes.add(dataNode);
////        }
//        //传入的表明
//        stringBuilder.setLength(length);
//        DataNode dataNode = new DataNode(stringBuilder.toString());
//        newDataNodes.add(dataNode);
//        // 扩展点
//        return Lists.newArrayList(newDataNodes);
//    }
//
//    /**
//     * 动态刷新数据源
//     */
//    private void dynamicRefreshDatasource(String dataSourceName, TableRule tableRule, List<DataNode> newDataNodes)
//            throws NoSuchFieldException, IllegalAccessException {
//        Set<String> actualTables = Sets.newHashSet();
//        Map<DataNode, Integer> dataNodeIndexMap = Maps.newHashMap();
//        AtomicInteger index = new AtomicInteger(0);
//        //把现有的加进去
//        List<DataNode> actualDataNodes = tableRule.getActualDataNodes();
//        for (DataNode actualDataNode : actualDataNodes) {
//            dataNodeIndexMap.put(actualDataNode,index.intValue());
//            actualTables.add(actualDataNode.getTableName());
//            index.set(index.intValue()+1);
//        }
//
//        dataNodeIndexMap.put(newDataNodes.get(0), index.intValue());
//        actualTables.add(newDataNodes.get(0).getTableName());
//        //加入现有的表
//        newDataNodes.addAll(actualDataNodes);
//
//        // 动态刷新：actualDataNodesField
//        Field actualDataNodesField = TableRule.class.getDeclaredField("actualDataNodes");
//        Field modifiersField = Field.class.getDeclaredField("modifiers");
//        modifiersField.setAccessible(true);
//        modifiersField.setInt(actualDataNodesField, actualDataNodesField.getModifiers() & ~Modifier.FINAL);
//        actualDataNodesField.setAccessible(true);
//        actualDataNodesField.set(tableRule, newDataNodes);
//
//        // 动态刷新：actualTablesField
//        Field actualTablesField = TableRule.class.getDeclaredField("actualTables");
//        actualTablesField.setAccessible(true);
//        actualTablesField.set(tableRule, actualTables);
//        // 动态刷新：dataNodeIndexMapField
//        Field dataNodeIndexMapField = TableRule.class.getDeclaredField("dataNodeIndexMap");
//        dataNodeIndexMapField.setAccessible(true);
//        dataNodeIndexMapField.set(tableRule, dataNodeIndexMap);
//        // 动态刷新：datasourceToTablesMapField
////        Map<String, Collection<String>> datasourceToTablesMap = Maps.newHashMap();
////        datasourceToTablesMap.put(dataSourceName, actualTables);
////        Field datasourceToTablesMapField = TableRule.class.getDeclaredField("datasourceToTablesMap");
////        datasourceToTablesMapField.setAccessible(true);
////        datasourceToTablesMapField.set(tableRule, datasourceToTablesMap);
//    }
//
//}
