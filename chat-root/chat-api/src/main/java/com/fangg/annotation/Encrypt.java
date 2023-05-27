package com.fangg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对有此注解的Controller方法返回值进行加密
 * <pre>默认使用AES加密，用RSA则需要指定</pre>
 * @author fangg
 * 2022年2月3日 下午6:26:00
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {

	String value() default "AES";
	
	// true则使用默认KEY加密
	boolean defaultKey() default false;
	
}
