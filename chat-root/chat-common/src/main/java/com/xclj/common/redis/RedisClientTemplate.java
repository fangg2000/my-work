package com.xclj.common.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.ShardedJedis;

@Repository("redisClientTemplate")
public class RedisClientTemplate {

	private static final Logger log = LoggerFactory.getLogger(RedisClientTemplate.class);

	@Autowired
	private RedisDataSource redisDataSource;

	/**
	 * 判断连接是否有效；
	 */
	public boolean isValidConnect() {
		ShardedJedis shardedJedis = null;
		try {
			log.info("redis连接...");
			shardedJedis = redisDataSource.getRedisClient();
			log.info("redis连接ok");
		} catch (Exception e) {
			log.error("redis连接异常：", e);
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return shardedJedis == null ? false : true;
	}

	/**
	 * 设置单个值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String setString(String key, String value) {
		String result = null;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			result = shardedJedis.set(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 获取单个值(String 类型)
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		String result = null;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			result = shardedJedis.get(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	// 判断键值是否存在
	public Boolean exists(String key) {
		Boolean result = false;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			result = shardedJedis.exists(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 向缓存中设置对象
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key, Object value) {
		ShardedJedis jedis = null;
		try {
			jedis = redisDataSource.getRedisClient();
			String objectJson = JSON.toJSONString(value);
			jedis.set(key, objectJson);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				redisDataSource.returnResource(jedis);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
	}

	/**
	 * 删除缓存中的对象，根据key
	 * 
	 * @param key
	 * @return
	 */
	public boolean del(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = redisDataSource.getRedisClient();
			jedis.del(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				redisDataSource.returnResource(jedis);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
	}

	/**
	 * 根据key 获取内容
	 * 
	 * @param key
	 * @return
	 */
	public Object getObject(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = redisDataSource.getRedisClient();
			Object value = jedis.get(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				redisDataSource.returnResource(jedis);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
	}

	/**
	 * 根据key 获取对象
	 * 
	 * @param key
	 * @return
	 */
	public <T> T get(String key, Class<T> clazz) {
		ShardedJedis jedis = null;
		try {
			jedis = redisDataSource.getRedisClient();
			String value = jedis.get(key);
			return JSON.parseObject(value, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				redisDataSource.returnResource(jedis);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
	}

	/**
	 * 只有第一次设置才返回OK,场景：防止重复提交
	 * 
	 * @param key
	 *            如：companyKey + memberCard + couponId
	 * @param value
	 * @param seconds
	 * @return
	 */
	public String setNxFirstStringByTimeout(String key, String value, int seconds) {
		String result = null;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			result = shardedJedis.set(key, value, "NX", "EX", seconds);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 设置单个值和过期时间
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String setStringByTimeout(String key, String value, int seconds) {
		String result = null;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			boolean flag = exists(key);
			if (flag) {
				// NX -- Only set the key if it does not already exist. XX --
				// Only set the key if it already exist.
				// EX = seconds; PX = milliseconds
				result = shardedJedis.set(key, value, "XX", "EX", seconds);
			} else {
				result = shardedJedis.set(key, value, "NX", "EX", seconds);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 保存列表并设置超时
	 * @param key
	 * @param myList
	 * @param seconds
	 * @return
	 */
	public <T> String setArrayListByTimeout(String key, List<T> myList, int seconds) {
		String result = null;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				boolean flag = exists(key);
				if (flag) {
					result = shardedJedis.set(key, JSONArray.toJSONString(myList), "XX", "EX", seconds);
				} else {
					result = shardedJedis.set(key, JSONArray.toJSONString(myList), "NX", "EX", seconds);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 保存列表并设置超时(0或负值则没有超时)
	 * @param key
	 * @param myList
	 * @param seconds
	 * @return
	 */
	public <T> boolean setListByTimeout(String key, List<T> listValue, int seconds) {
		boolean result = false;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				if (listValue != null && listValue.size() > 0) {
					boolean flag = exists(key);
					for (T t : listValue) {
						if (flag) {
							shardedJedis.rpush(key, JSONArray.toJSONString(t));
						} else {
							shardedJedis.lpush(key, JSONArray.toJSONString(t));
							flag = true;
						}
					}
					if (seconds > 0) {
						shardedJedis.expire(key, seconds);
					}
					result = true;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
			result = false;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 在列表头部保存值并设置超时
	 * @param key
	 * @param value(放前面的排后面--先进后出)
	 * @param seconds
	 * @return
	 */
	public boolean setListValueFrom0ByTimeout(String key, String [] value, int seconds) {
		boolean result = false;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				shardedJedis.lpush(key, value);
				shardedJedis.expire(key, seconds);
				result = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
			result = false;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}
	
	/**
	 * 在列表最后保存值并设置超时(0无超时)
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public boolean setListValueByTimeout(String key, Object value, int seconds) {
		boolean result = false;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				boolean flag = exists(key);
				if (flag) {
					shardedJedis.rpush(key, JSONArray.toJSONString(value));
				} else {
					shardedJedis.lpush(key, JSONArray.toJSONString(value));
				}
				
				if (seconds > 0) {
					shardedJedis.expire(key, seconds);
				}
				result = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
			result = false;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 更新list中的某个值
	 */
	public boolean setListValueByIndexAndTimeout(String key, Object value, long index, int seconds) {
		boolean result = false;
		boolean broken = false;
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				shardedJedis.lset(key, index, JSONArray.toJSONString(value));
				shardedJedis.expire(key, seconds);
				result = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
			result = false;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 根据对象获取列表
	 * @param key
	 * @param beanClass
	 * @return
	 */
	public <T> List<T> getArraylist(String key, Class<T> beanClass) {
		List<T> result = new ArrayList<T>();
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				return JSON.parseArray(shardedJedis.get(key), beanClass);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 根据对象获取列表
	 * @param key
	 * @param beanClass
	 * @return
	 */
	public <T> List<T> getList(String key, Class<T> beanClass) {
		List<T> result = new ArrayList<T>();
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				List<String> listR = shardedJedis.lrange(key, 0, (shardedJedis.llen(key) - 1));
				if (listR != null && listR.size() > 0) {
					for (String str : listR) {
						result.add(JSONObject.parseObject(str, beanClass));
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}

	/**
	 * 获取列表中最后一个值
	 * @param key
	 * @return
	 */
	public String getLastListObject(String key) {
		/*String result = null;
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				result = shardedJedis.lindex(key, -1);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}*/
		return getListValueByIndex(key, -1);
	}
	
	/**
	 * 根据index获取列表中一个值
	 * @param key
	 * @return
	 */
	public String getListValueByIndex(String key, int index) {
		String result = null;
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				result = shardedJedis.lindex(key, index);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		return result;
	}
	
	/**
	 * 获取列表长度
	 * @param key
	 * @return
	 */
	public long getListSize(String key) {
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return 0;
			}
			if (key != null && key != "") {
				return shardedJedis.llen(key);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		
		return 0;
	}
	
	/**
	 * 新增Map值(覆盖旧值返回0)
	 * @param key
	 * @return
	 */
	public long setMapValue(String key, String field, String value) {
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return 0;
			}
			if (key != null && key != "") {
				return shardedJedis.hset(key, field, value);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		
		return 0;
	}
	
	/**
	 * 新增Map(覆盖旧值)
	 * @param key
	 * @return
	 */
	public String setMap(String key, Map<String, String> map) {
		String result = null;
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				return shardedJedis.hmset(key, map);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		
		return result;
	}
	
	/**
	 * 获取Map的key值(field为Map的key-value中的key)
	 */
	public String getMapValue(String key, String field) {
		String result = null;
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				return shardedJedis.hget(key, field);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		
		return result;
	}
	
	/**
	 * 获取Map的全部key值(field为Map的key-value中的key)
	 */
	public Map<String, String> getMapValueAll(String key) {
		Map<String, String> result = null;
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				return shardedJedis.hgetAll(key);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		
		return result;
	}
	
	/**
	 * 获取Map的key值(field为Map的key-value中的key)
	 */
	public <T> T getMapValue(String key, String field, Class<T> beanClass) {
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return null;
			}
			if (key != null && key != "") {
				return JSONObject.parseObject(shardedJedis.hget(key, field), beanClass);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		
		return null;
	}
	
	/**
	 * 获取hmget多个field值(field为Map的key-value中的key)
	 */
	public List<String> getMapValue(String key, String ... fields) {
		List<String> result = null;
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				return shardedJedis.hmget(key, fields);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		
		return result;
	}
	
	/**
	 * 获取hmget多个field值(field为Map的key-value中的key)
	 */
	public <T> List<T> getMapValue(String key, Class<T> beanClass, String ... fields) {
		List<T> result = null;
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return result;
			}
			if (key != null && key != "") {
				return JSONArray.parseArray(JSONArray.toJSONString(shardedJedis.hmget(key, fields)), beanClass);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		
		return result;
	}
	
	/**
	 * 删除Map的key值(field为Map的key-value中的key)
	 */
	public long delMapValue(String key, String ... fields) {
		boolean broken = false;
		// Class<T> clazz
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = redisDataSource.getRedisClient();
			if (shardedJedis == null) {
				return 0;
			}
			if (key != null && key != "") {
				return shardedJedis.hdel(key, fields);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			broken = true;
		} finally {
			try {
				redisDataSource.returnResource(shardedJedis, broken);
			} catch (Exception e) {
				log.error("redis连接异常：", e);
			}
		}
		
		return 0;
	}
	
	

}
