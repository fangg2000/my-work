package com.fangg.config.redis;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * redis多数据库链接配置
 * 来自CSDN博主「木木在线撸码」的原创文章
 * @author fangg
 * 2022年2月21日 下午1:38:13
 */
@Configuration
public class RedisConfig {
    //redis地址
    @Value("${redis.host}")
    private String host;

    //redis端口号
    @Value("${redis.port}")
    private int port;

    //redis密码
    @Value("${redis.pass}")
    private String password;
    
    // pool配置
    @Value("${redis.minIdle}")
    private int minIdle;
    @Value("${redis.maxIdle}")
    private int maxIdle;
    @Value("${redis.maxTotal}")
    private int maxTotal;
    @Value("${redis.maxWaitMillis}")
    private int maxWaitMillis;
    @Value("${redis.timeout}")
    private int timeout;
    @Value("${redis.shutdown-timeout}")
    private int shutdownTimeOut;

    //默认数据库
    private int defaultDB;

    //多个数据库集合
    @Value("${redis.dbs}")
    private List<Integer> dbList;

    //RedisTemplate实例
    private static Map<Integer, RedisTemplate<String, Object>> redisTemplateMap = new HashMap<>();

    /**
     * 初始化连接池
     */
    @PostConstruct
    public void initRedisTemplate() {
        defaultDB = dbList.get(0);//设置默认数据库
        for (Integer db : dbList) {
            //存储多个RedisTemplate实例
            redisTemplateMap.put(db, redisTemplate(db));
        }
    }

    public LettuceConnectionFactory redisConnection(int db) {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host); 	// 指定地址
        redisConfiguration.setDatabase(db); 	// 指定数据库
        redisConfiguration.setPort(port); 		//指定端口
        redisConfiguration.setPassword(password); //指定密码
        /*LettuceConnectionFactory factory = new LettuceConnectionFactory(server);
        factory.afterPropertiesSet(); //刷新配置
        //factory.setTimeout(timeout);
        //factory.getValidateConnection();
        
        return factory;*/
    	
    	
    	//redis配置
//        RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration(host, port);
//        ((RedisStandaloneConfiguration) redisConfiguration).setDatabase(db);
//        ((RedisStandaloneConfiguration) redisConfiguration).setPassword(password);

        //连接池配置
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxTotal(maxTotal);
        genericObjectPoolConfig.setMaxWaitMillis(maxWaitMillis);
        // 是否提前进行validate操作；如果为true，则得到的连接是可用的
        genericObjectPoolConfig.setTestOnBorrow(true);

        //redis客户端配置
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder 
    	  		  builder = LettucePoolingClientConfiguration.builder().commandTimeout(Duration.ofMillis(timeout));
                
        builder.shutdownTimeout(Duration.ofMillis(shutdownTimeOut));
        builder.poolConfig(genericObjectPoolConfig);
        LettuceClientConfiguration lettuceClientConfiguration = builder.build();

        //根据配置和客户端配置创建连接
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration,lettuceClientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();

        return lettuceConnectionFactory;
    }

    //RedisTemplate模板
    public RedisTemplate<String, Object> redisTemplate(int db) {
        //为了开发方便，一般直接使用<String,Object>
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnection(db)); //设置连接
        //Json序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //String的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //hash的key采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        //value序列化采用String的序列化方式
        template.setValueSerializer(stringRedisSerializer);
        //hash序列化方式采用jackson
        //template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 指定数据库进行切换
     * @param db  数据库索引
     * @return
     */
    public RedisTemplate<String, Object> getRedisTemplateByDb(int db) {
        return redisTemplateMap.get(db);
    }

    /**
     * 使用默认数据库
     *
     * @return
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplateMap.get(defaultDB);
    }
    
}