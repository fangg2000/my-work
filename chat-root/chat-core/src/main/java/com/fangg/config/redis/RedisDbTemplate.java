package com.fangg.config.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * redis可切换DB工具类
 * (不指定db时默认使用db0)
 * @author fangg
 * 2022年2月21日 下午4:40:37
 */
@Repository("redisDbTemplate")
public class RedisDbTemplate {

	private static final Logger log = LoggerFactory.getLogger(RedisDbTemplate.class);

	@Autowired
	RedisConfig redisConfig;
	

	/**
	 * 判断连接是否有效；
	 */
	public boolean isValidConnect(Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			//log.info("redis连接...");
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			return redisTemplate.getRequiredConnectionFactory().getConnection().isClosed()?false:true;
			//log.info("redis连接ok");
		} catch (Exception e) {
			log.error("redis连接异常：", e);
		}
		return false;
	}
	

	/**
	 * 设置单个值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setString(String key, String value, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			redisTemplate.opsForValue().set(key, value);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * 获取单个值(String 类型)
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return null;
			}
			return String.valueOf(redisTemplate.opsForValue().get(key));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	// 判断键值是否存在
	public boolean exists(String key, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 向缓存中设置对象
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key, Object value, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 删除缓存中的对象，根据key
	 * 
	 * @param key
	 * @return
	 */
	public boolean del(String key, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			return redisTemplate.delete(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 根据key 获取内容
	 * 
	 * @param key
	 * @return
	 */
	public Object getObject(String key, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return null;
			}
			
			return redisTemplate.opsForValue().get(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 根据key 获取对象
	 * 
	 * @param key
	 * @return
	 */
	public <T> T get(String key, Class<T> clazz, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return null;
			}
			
			return JSON.parseObject(getString(key, db), clazz);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 只有第一次设置才返回true,场景：防止重复提交
	 * 
	 * @param key
	 *            如：companyKey + memberCard + couponId
	 * @param value
	 * @param seconds
	 * @return
	 */
	public boolean setNxFirstStringByTimeout(String key, String value, int seconds, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			return redisTemplate.opsForValue().setIfPresent(key, value, seconds, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} 
		return false;
	}

	/**
	 * 设置单个值和过期时间
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setStringByTimeout(String key, String value, int seconds, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
			getString("");
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 保存列表并设置超时(0或负则不超时)
	 * @param key
	 * @param myList
	 * @param seconds
	 * @return
	 */
	public <T> boolean setListByTimeout(String key, List<T> listValue, int seconds, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			if (key != null && key != "") {
				if (listValue != null && listValue.size() > 0) {
					redisTemplate.opsForList().rightPushAll(key, listValue);
					//redisTemplate.opsForList().rightPush(key, listValue);
					if (seconds > 0) {
						redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
					}
					return true;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 在列表头部保存值并设置超时
	 * @param key
	 * @param value(放前面的排后面--先进后出)
	 * @param seconds
	 * @return
	 */
	public boolean setListValueFrom0ByTimeout(String key, String [] value, int seconds, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			if (key != null && key != "") {
				redisTemplate.opsForList().leftPush(key, value);
				if (seconds > 0) {
					redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
				}
				return true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * 在列表最后保存值并设置超时
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public boolean setListValueByTimeout(String key, Object value, int seconds, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			if (key != null && key != "") {
				redisTemplate.opsForList().rightPush(key, value);
				if (seconds > 0) {
					redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
				}
				return true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 更新list中的某个值
	 */
	public boolean setListValueByIndexAndTimeout(String key, Object value, long index, int seconds, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			if (key != null && key != "") {
				redisTemplate.opsForList().set(key, index, value);
				redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 根据对象获取列表
	 * @param key
	 * @param beanClass
	 * @return
	 */
	public <T> List<T> getArraylist(String key, Class<T> beanClass, Integer ... db) {
		List<T> result = null;
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return result;
			}
			
			if (key != null && key != "") {
				return JSON.parseArray(JSON.toJSONString(redisTemplate.opsForList().range(key, 0, -1)), beanClass);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	/**
	 * 根据index获取列表中一个值
	 * @param key
	 * @return
	 */
	public String getListValueByIndex(String key, int index, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return null;
			}
			
			if (key != null && key != "") {
				return String.valueOf(redisTemplate.opsForList().index(key, index));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 获取列表长度
	 * @param key
	 * @return
	 */
	public long getListSize(String key, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return 0;
			}
			
			if (key != null && key != "") {
				return redisTemplate.opsForList().size(key);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return 0;
	}
	
	/**
	 * 新增Map值(覆盖旧值返回0)
	 * @param key
	 * @return
	 */
	public boolean setMapValue(String key, String field, Object value, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			if (key != null && key != "") {
				redisTemplate.opsForHash().put(key, field, value);
				return true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return false;
	}
	
	/**
	 * 新增Map(覆盖旧值)
	 * @param key
	 * @return
	 */
	public boolean setMap(String key, Map<String, Object> map, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			if (key != null && key != "") {
				redisTemplate.opsForHash().putAll(key, map);
				return true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return false;
	}
	
	/**
	 * 获取Map的key值(field为Map的key-value中的key)
	 */
	public Object getMapValue(String key, String field, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return null;
			}
			
			if (key != null && key != "") {
				return redisTemplate.opsForHash().get(key, field);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * 获取Map的key值(field为Map的key-value中的key)
	 */
	public <T> T getMapValue(String key, String field, Class<T> beanClass, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return null;
			}
			
			if (key != null && key != "") {
				Object object = redisTemplate.opsForHash().get(key, field);
				return JSONObject.parseObject(String.valueOf(object), beanClass);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * 获取hmget多个field值(field为Map的key-value中的key)
	 */
	public List<Object> getMapValue(String key, Collection<Object> fields, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return new ArrayList<>();
			}
			
			if (key != null && key != "") {
				return redisTemplate.opsForHash().multiGet(key, fields);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return new ArrayList<>();
	}
	
	/**
	 * 获取hmget多个field值(field为Map的key-value中的key)
	 */
	public <T> List<T> getMapValue(String key, Collection<Object> fields, Class<T> beanClass, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		List<T> resultList = null;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return resultList;
			}
			
			if (key != null && key != "") {
				List<Object> list = redisTemplate.opsForHash().multiGet(key, fields);
				if (list != null) {
					resultList = new ArrayList<>();
					for (Object object : list) {
						resultList.add(JSONObject.parseObject(String.valueOf(object), beanClass));
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return resultList;
	}
	
	/**
	 * 删除Map的单个key值(field为Map的key-value中的key)
	 */
	public boolean delMapValue(String key, String field, Integer ... db) {
		Object [] fields = new Object[1];
		fields[0] = field;
		return delMapValue(key, fields, db);
	}
	
	/**
	 * 删除Map的多个key值(field为Map的key-value中的key)
	 */
	public boolean delMapValue(String key, Object [] fields, Integer ... db) {
		RedisTemplate<String, Object> redisTemplate;
		try {
			if (db.length == 0) {
				redisTemplate = redisConfig.getRedisTemplate();
			} else {
				redisTemplate = redisConfig.getRedisTemplateByDb(db[0]);
			}
			if (redisTemplate == null) {
				return false;
			}
			
			if (key != null && key != "") {
				redisTemplate.opsForHash().delete(key, fields);
				return true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return false;
	}
	
	

}
