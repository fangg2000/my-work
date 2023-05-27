package com.xclj.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

public class StringUtil extends StringUtils{
	
	public static boolean isEmpty(Object object) {
		if (object == null || "".equals(object.toString())) {
			return true;
		}
		return false;
	}
	
	public static boolean isNotEmpty(Object object) {
		return isEmpty(object)?false:true;
	}
	
	public static String getLocalIp(){ 
		try{
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while(en.hasMoreElements()){
				NetworkInterface ni = en.nextElement();
				Enumeration<InetAddress> addresses = ni.getInetAddresses();
				while(addresses.hasMoreElements()){
					InetAddress address = addresses.nextElement();
					if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()
							&& address.isSiteLocalAddress()) {
						return address.getHostAddress().toString();
					}
				}
			}
		}catch (Exception e) {
			System.out.println("获取内网地址异常");
		}
		return "127.0.0.1";
	}

	/**
	 * 随机生成XY坐标(150~300)
	 */
	public static String getXY() {
		Random random = new Random();
		return (150+random.nextInt(150))+","+(150+random.nextInt(150));
	}
	
}
