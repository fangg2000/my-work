package com.fangg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableAutoConfiguration
@MapperScan(basePackages = "com.fangg.dao")
@ImportResource(value = "classpath:spring-bean.xml")
@ComponentScan({"com.xclj"})
public class ChatCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatCoreApplication.class, args);
	}
    
}
