package com.fangg.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//声明这是一个配置类
@Configuration
// 引入ch2.taskexecutor下面的@service,@component,@repository,@controller注册为bean
@ComponentScan("com.fangg.config")
// 开启注解：开启异步支持
@EnableAsync
public class ExecutorConfig implements AsyncConfigurer {
	
	@Value("${thread.pool.corepoolsize}")
	private int corepoolsize;
	@Value("${thread.pool.maxpoolsize}")
	private int maxpoolsize;
	@Value("${thread.pool.queuecapacity}")
	private int queuecapacity;
	@Value("${thread.pool.aliveseconds}")
	private int aliveseconds;

	// 配置类实现AsyncConfigurer接口并重写AsyncConfigurer方法，并返回一个ThreadPoolTaskExecutor
	// 这样我们就得到了一个基于线程池的TaskExecutor
	@Bean(name = "taskExecutor")
	@Override
	public ThreadPoolTaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		// 如果池中的实际线程数小于corePoolSize,无论是否其中有空闲的线程，都会给新的任务产生新的线程
		taskExecutor.setCorePoolSize(corepoolsize);
		// 连接池中保留的最大连接数。Default: 15 maxPoolSize
		taskExecutor.setMaxPoolSize(maxpoolsize);
		// queueCapacity 线程池所使用的缓冲队列
		taskExecutor.setQueueCapacity(queuecapacity);
		// 允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
		taskExecutor.setKeepAliveSeconds(aliveseconds);
		// 设置拒绝策略（默认AbortPolicy，抛异常），会重试任务
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		taskExecutor.initialize();
		return taskExecutor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}
}
