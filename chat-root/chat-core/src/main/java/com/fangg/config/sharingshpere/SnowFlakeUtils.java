package com.fangg.config.sharingshpere;

import java.util.ArrayList;
import java.util.List;

import org.apache.shardingsphere.sharding.algorithm.keygen.SnowflakeKeyGenerateAlgorithm;

import com.alibaba.fastjson.JSONObject;
import com.fangg.constant.RedisConstant;
import com.google.common.collect.Lists;
import com.xclj.common.redis.RedisClientTemplate;

/**
 * 雪花算法ID获取
 * @author fangg
 * 2022年1月5日 上午11:27:34
 */
public class SnowFlakeUtils {
	
	private static int BIT_NUM = 4;
	static SnowflakeKeyGenerateAlgorithm sn;


	// 新表数范围（新增操作时表数量只能属于其中之一，大于9按9算，小于9但不匹配的按小一个算，如指定8个表，则根据tableNames取最后的7个表）
	private static List<Integer> splitList = Lists.newArrayList(new Integer[]{2, 3, 5, 6, 7, 9});
	
	static {
    	//工作进程位10位 取值1-1024 默认0
//    	SimpleShardingKeyGenerator.setWorkerId(1020);
//        //时钟回拨，最大允许容忍差异毫秒数，超过这个时间将返回异常，默认10ms
//    	SimpleShardingKeyGenerator.setMaxTolerateTimeDifferenceMilliseconds(10);
    	
//	    Properties props = new Properties();
//	    props.setProperty("worker-id", String.valueOf(workerId));
//	    sn.setProps(props);
    	sn = new SnowflakeKeyGenerateAlgorithm();
	}
	
    public static Comparable<?>  getId() {
        return getId(new Long(1020));
    }
    
    public static Comparable<?>  getId(Long workerId) {
    	
//    	//工作进程位10位 取值1-1024 默认0
//    	SimpleShardingKeyGenerator.setWorkerId(workerId);
//        //时钟回拨，最大允许容忍差异毫秒数，超过这个时间将返回异常，默认10ms
//    	SimpleShardingKeyGenerator.setMaxTolerateTimeDifferenceMilliseconds(10);
//    	SimpleShardingKeyGenerator sn = new SimpleShardingKeyGenerator();
	    return sn.generateKey();
    }
    
    /**
     * endSuTbffix-startSuffix的大小只能为2, 3, 5, 6, 7, 9其中之一，
     * 大于9按9算，小于9但不匹配的按小一个算，如指定8个表，则取7个表）
     * <pre>默认取尾数四位取整判断表名</pre>
     */
    public static String getTableId(int startTbSuffix, int endSuTbffix) {
		return getTableId(startTbSuffix, endSuTbffix, BIT_NUM);
    }
    
    public static String getTableId(int startTbSuffix, int endSuTbffix, int num) {
    	String [] aTables = "0,1,2,3,4,5,6,7,8".split(",");
    	if ((endSuTbffix > startTbSuffix) && (endSuTbffix - startTbSuffix) <= 9) {
    		aTables = new String[endSuTbffix - startTbSuffix + 1];
    		for (int i = 0,j=startTbSuffix; j <= endSuTbffix; i++,j++) {
    			aTables[i] = j+"";
    		}
    	}
    	
    	List<String> tableNames = new ArrayList<>();
    	for (String aSuffix : aTables) {
    		tableNames.add("tb_"+aSuffix);
    	}
    	
    	return getTableId(tableNames, aTables, num);
    }
    
    /**
     * redis的key为“split_tb_config_key”，保存为map类型("tableSuffix"为tableName对象属性)
     */
    public static String getTableIdByRedis(String tableName, RedisClientTemplate redisClientTemplate) {
		String resultStr = redisClientTemplate.getMapValue(RedisConstant.SPLIT_TB_CONFIG_KEY, tableName);
    	if (resultStr != null) {
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
//			//logger.info("分表配置信息：{}", resultStr);
//			
			String [] aTables = jsonObject.getString("tableSuffix").split(",");
    		List<String> tableNames = new ArrayList<>();
    		for (String aSuffix : aTables) {
    			tableNames.add(tableName+"_"+aSuffix);
    		}
    		
    		return getTableId(tableNames, aTables, BIT_NUM);
    	}
    	
    	return null;
    }

    /**
     * redis的key为“split_tb_config_key”，保存为map类型("tableSuffix"为tableName对象属性)
     */
    public static String getTableIdByRedis(List<String> tableNames, String tableName, RedisClientTemplate redisClientTemplate) {
		String resultStr = redisClientTemplate.getMapValue(RedisConstant.SPLIT_TB_CONFIG_KEY, tableName);
    	if (resultStr != null) {
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
//			//logger.info("分表配置信息：{}", resultStr);
//			
			String [] aTables = jsonObject.getString("tableSuffix").split(",");
    		List<String> newTableNames = new ArrayList<>();
			for (int i = 0,size=tableNames.size(); i < size; i++) {
				newTableNames.add(tableNames.get(i)+"_"+i);
			}
    		
    		return getTableId(newTableNames, aTables, BIT_NUM);
    	}
    	
    	return null;
    }
    
    /**
     * tableSplit为“,”分隔的表后缀
     */
    public static String getTableId(String tableSplit) {
    	if (tableSplit != null && "".equals(tableSplit.trim()) == false) {
//			JSONObject jsonObject = JSONObject.parseObject(resultStr);
//			//logger.info("分表配置信息：{}", resultStr);
//			
//			String [] aTables = jsonObject.getString("tableSuffix").split(",");
    		String [] aTables = tableSplit.split(",");
    		List<String> newTableNames = new ArrayList<>();
        	for (String aSuffix : aTables) {
        		newTableNames.add("tb_"+aSuffix);
        	}
    		
    		return getTableId(newTableNames, aTables, BIT_NUM);
    	}
    	
    	return null;
    }

	private static String getTableId(List<String> tableNames, String[] aTables, int num) {
    	long sn_id = getLongValue(sn.generateKey());
		aTables = defaultTableCheck(tableNames, aTables);
		//logger.info(JSONArray.toJSONString(aTables));
		
		String table = checkTable(tableNames, aTables, sn_id);
		if (table != null) {
			//logger.info("更新表{}--{}", table, JSONArray.toJSONString(shardingValue));
			String [] temp = table.split("_");
			return sn_id + suffixCode(temp[temp.length-1], num);
		}
		
		return null;
	}

	private static String suffixCode(String suffixNum, int num) {
		if (num > suffixNum.length()) {
			int codeNum = Integer.parseInt(suffixNum);
			int num0 = num - suffixNum.length(); 
			StringBuffer sBuffer = new StringBuffer(num);
			for (int i = 0; i < num0; i++) {
				sBuffer.append("0");
			}
			return sBuffer.append(codeNum).toString();
		}
		return suffixNum;
	}

	/**
	 * 返回表
	 */
    private static String checkTable(List<String> listTables, String[] aTables, Long val) {    	
    	for (String tableN : listTables) {
    		for (String activeT : aTables) {
				if (tableN.endsWith(activeT)) {
					if (tableN.endsWith(checkEndStr(listTables, val, aTables))) {
		                return tableN;
		            }
				}
			}
        }
		return null;
	}

	private static String checkEndStr(List<String> listTables, Long val, String [] activeTables) {
		// 活动表个数
		int count = activeTables.length;
		int oCount = Integer.parseInt(activeTables[count-1]) - count + 1;
		String result = null;
		
		switch (count) {
			case 2:
				result = String.valueOf(getLongValue(val) % 2 + oCount);
				break;
			case 3:
				result = String.valueOf(getLongValue(val) % 3 + oCount);
				break;
			case 5:
				result = String.valueOf(getLongValue(val) % 5 + oCount);
				break;
			case 6:
				result = String.valueOf(getLongValue(val) % 6 + oCount);
				break;
			case 7:
				result = String.valueOf(getLongValue(val) % 7 + oCount);
				break;
			case 9:
				result = String.valueOf(getLongValue(val) % 9 + oCount);
				break;
			default:
				
				break;
		}
		
		return result;
	}
	
    private static long getLongValue(final Comparable<?> value) {
        return Long.parseLong(value.toString());
    }

	private static String [] defaultTableCheck(List<String> listTables, String[] aTables) {
		if (splitList.contains(aTables.length) == false) {
			int size = listTables.size();
			String [] activeTables = null;
			int lastNum = splitList.get(0);
			
			for (int splitNum : splitList) {
				if (splitNum >= aTables.length && size >= lastNum) {
					activeTables = new String[lastNum];
					String [] temp = null;
					int num = lastNum-1;
					for (int i = size-1; i >= (size-lastNum); i--) {
						temp = listTables.get(i).split("_");
						activeTables[num] = temp[temp.length-1];
						num--;
					}
					return activeTables;
				} else {
					lastNum = splitNum;
				}
			}
			
			// 多于9个的按9算
			lastNum = 9;
			if (size >= lastNum) {
				activeTables = new String[lastNum];
				String [] temp = null;
				int num = lastNum-1;
				for (int i = size-1; i >= (size-lastNum); i--) {
					temp = listTables.get(i).split("_");
					activeTables[num] = temp[temp.length-1];
					num--;
				}
				return activeTables;
			}
		}
		
		return aTables;
	}


	/**
	 * 取后缀
	 */
	public static String checkTbSuffix(final Comparable<?> value) {
		String valStr = value+"";
		valStr = valStr.substring(valStr.length()-4);
		String tbSuffix = Integer.parseInt(valStr) + "";
		return tbSuffix;
	}
	
	/**
	 * 返回连续的后缀列表
	 */
	public static String [] backTableSuffix(final Comparable<?> lowerEndpoint, final Comparable<?> upperEndpoint) {
		int startTbSuffix = Integer.parseInt(checkTbSuffix(lowerEndpoint));
		int endSuTbffix = Integer.parseInt(checkTbSuffix(upperEndpoint));
		String [] aTables = new String[endSuTbffix - startTbSuffix + 1];
		for (int i = 0,j=startTbSuffix; j <= endSuTbffix; i++,j++) {
			aTables[i] = j+"";
		}
		return aTables;
	}
    
}

