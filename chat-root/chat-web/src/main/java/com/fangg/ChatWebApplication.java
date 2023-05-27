package com.fangg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableAutoConfiguration
//@MapperScan(basePackages = "com.fangg.dao")
@ComponentScan({"com.xclj", "com.fangg"})
@ImportResource(value = "classpath:spring-bean.xml")
public class ChatWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatWebApplication.class, args);
	}
	
}
