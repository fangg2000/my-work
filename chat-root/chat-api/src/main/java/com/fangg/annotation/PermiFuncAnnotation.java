/**
 * 
 */
package com.fangg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能权限--属性标识
 * @author fangg
 * 2022年1月20日 下午12:11:43
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermiFuncAnnotation {

	/** 在属性字段上指定功能的code **/
	String code() default "";
	
	/** 数量限制(默认false，limit=true时属性须为PermissionEntity类型) **/
	boolean limit() default false;
	
}
