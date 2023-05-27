package com.xclj.common.util;

import java.util.UUID;

public class UuidUtil {

	public static String getUuid() {
		return UUID.randomUUID().toString();
	}
	
	public static String genUuid(int num) {
		return getUuid().substring(0, num);
	}
	
	/**
	 * 没有"-"符号
	 */
	public static String genUuid_0(int num) {
		return getUuid().replaceAll("-", "").substring(0, num);
	}
	
//	public static void main(String[] args) {
//		System.out.println(getUuid());
//	}
}
