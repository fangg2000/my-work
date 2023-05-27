package com.fangg.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yeauty.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		logger.info("底层基于netty的webscoket service启动");
		return new ServerEndpointExporter();
	}
	
}
