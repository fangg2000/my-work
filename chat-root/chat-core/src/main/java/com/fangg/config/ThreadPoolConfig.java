package com.fangg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

//异步方法和调用方法一定要写在不同的类中
@Component
//@Configuration
//@EnableAsync // 启用异步任务
public class ThreadPoolConfig {
	
	@Value("${thread.pool.corepoolsize}")
	private int corepoolsize;
	@Value("${thread.pool.maxpoolsize}")
	private int maxpoolsize;
	@Value("${thread.pool.queuecapacity}")
	private int queuecapacity;
	@Value("${thread.pool.aliveseconds}")
	private int aliveseconds;
    
    // 声明一个线程池(并指定线程池的名字)
    //在用到的方法前加 @Async("taskExecutor") 启动异步线程
    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数5：线程池创建时候初始化的线程数
        executor.setCorePoolSize(corepoolsize);
        // 最大线程数5：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(maxpoolsize);
        // 缓冲队列500：用来缓冲执行任务的队列
        executor.setQueueCapacity(queuecapacity);
        // 允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(aliveseconds);
        // 线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix("ThreadPool-core-");
		// 设置拒绝策略（默认AbortPolicy，抛异常），会重试任务
        //executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
