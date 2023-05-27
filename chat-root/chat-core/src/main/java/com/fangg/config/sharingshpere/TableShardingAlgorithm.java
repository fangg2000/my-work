package com.fangg.config.sharingshpere;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fangg.config.ApplicationContextHolder;
import com.fangg.constant.ShardingConstant;
import com.google.common.collect.Range;
import com.xclj.common.redis.RedisClientTemplate;

/**
 * 自定义水平分片
 * @author fangg
 * 2022年1月4日 上午8:13:03
 */
//@Component
public class TableShardingAlgorithm implements StandardShardingAlgorithm<Long> {
	private static Logger logger = LoggerFactory.getLogger(TableShardingAlgorithm.class);

	RedisClientTemplate redisClientTemplate;

	public String getType() {
		// 分片类型 
		return ShardingConstant.CLASS_BASED;
	}

	public void init() {
		// 
		redisClientTemplate = ApplicationContextHolder.getBean("redisClientTemplate");
	}

	/**
	 * 精准分片算法（自动生成ID插入、=或in根据ID查询）
	 */
	public String doSharding(Collection<String> tableNames, PreciseShardingValue<Long> shardingValue) {
		//logger.info("精准分片算法--{}", JSONArray.toJSONString(tableNames));
		//logger.info("精准分片算法--{}", JSONObject.toJSONString(shardingValue));
		
		/*String resultStr = redisClientTemplate.getMapValue(RedisConstant.SPLIT_TB_CONFIG_KEY, shardingValue.getLogicTableName());
		if (resultStr != null) {
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
			//logger.info("分表配置信息：{}", resultStr);
			
			String [] aTables = jsonObject.getString("tableSuffix").split(",");
	    	aTables = defaultTableCheck(Lists.newArrayList(tableNames), aTables);
	    	//logger.info(JSONArray.toJSONString(aTables));
			
	    	String table = checkTable(Lists.newArrayList(tableNames), aTables, shardingValue.getValue());
	    	if (table != null) {
	    		logger.info("更新表{}--{}", table, JSONArray.toJSONString(shardingValue));
				return table;
			}
		}*/
		
		// 取值后4位并取整作为表名后缀判断
		String tbSuffix = checkTbSuffix(shardingValue.getValue());
		for (String tableN : tableNames) {
			if (tableN.endsWith(tbSuffix)) {
				//logger.info("更新表{}--{}", tableN, JSONObject.toJSONString(shardingValue));
				return tableN;
            }
		}
    	
		// 匹配不到则取最后一个表名
        //return Lists.newArrayList(tableNames).get(tableNames.size()-1);
        return null;
	}

	/**
	 * 取后缀
	 */
	private String checkTbSuffix(final Comparable<?> value) {
		String valStr = value+"";
		valStr = valStr.substring(valStr.length()-4);
		String tbSuffix = Integer.parseInt(valStr) + "";
		return tbSuffix;
	}

	/**
	 * 范围分片算法（between and或> and <）
	 */
	public Collection<String> doSharding(Collection<String> tableNames, RangeShardingValue<Long> shardingValue) {
//		logger.info("范围分片算法--{}", JSONObject.toJSONString(shardingValue));
        Collection<String> collect = new ArrayList<>();
        Range<Long> valueRange = shardingValue.getValueRange();

//        logger.info("valueRange:{}", JSONObject.toJSONString(valueRange));
//        logger.info("最小值lowerEndpoint:{}", valueRange.lowerEndpoint());
//        logger.info("最大值upperEndpoint:{}", valueRange.upperEndpoint());
        String [] tbSuffixs = backTableSuffix(valueRange.lowerEndpoint(), valueRange.upperEndpoint());
        
        for (String tableN : tableNames) {
        	for (String tbSuffix : tbSuffixs) {
                if (tableN.endsWith(tbSuffix)) {
                    collect.add(tableN);
                }
			}
        }

        //logger.info("collect:{}", JSONObject.toJSONString(collect));
        return collect;
	}
	
	private String [] backTableSuffix(final Comparable<?> lowerEndpoint, final Comparable<?> upperEndpoint) {
		int startTbSuffix = Integer.parseInt(checkTbSuffix(lowerEndpoint));
		int endSuTbffix = Integer.parseInt(checkTbSuffix(upperEndpoint));
		String [] aTables = new String[endSuTbffix - startTbSuffix + 1];
		for (int i = 0,j=startTbSuffix; j <= endSuTbffix; i++,j++) {
			aTables[i] = j+"";
		}
		return aTables;
	}

	
}
