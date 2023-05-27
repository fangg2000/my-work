package com.fangg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对有此注解的Controller方法参数(目前仅支持@RequestBody的参数)解密
 * <pre>注：解密失败会返回原数据，默认使用AES解密，用RSA则需要指定</pre>
 * @author fangg
 * 2022年2月3日 下午6:25:37
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Decrypt {

	String value() default "AES";

	// true则使用默认KEY解密
	boolean defaultKey() default false;
	
}
