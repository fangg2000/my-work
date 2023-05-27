package com.xclj.common.util;

import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.net.util.Base64;

/**
 * AES加密解密
 * 
 * @author fangg 2022年2月3日 下午7:58:12
 */
public class AESUtil {

	// 前后端保持一致
	private static String PRIVATE_KEY = "JDkCaf76rm6C3NK9";
    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";
    
    /*public static void main(String[] args) throws Exception {
    	JSONObject jsonObject = new JSONObject();
		jsonObject.put("c", "U123456789");
		jsonObject.put("t", DateUtil.getNextTimeWithSeconds(new Date(), 1800).getTime());
		
		String encryptStr = encryptByDefault(jsonObject.toJSONString());
		System.out.println(encryptStr);
	}*/
    
    /** 
     * 解密 
     * 默认private_key要与application.properties中的aes_private_key保持一致(否则此方法不可使用)
     * @throws Exception 
     */  
    public static String decryptByDefault(String encrypt) throws Exception {  
    	return decrypt(encrypt, PRIVATE_KEY);
    }  
      

	/**
	 * 加密
	 * 默认private_key要与application.properties中的aes_private_key保持一致(否则此方法不可使用)
	 * @throws Exception 
	 */  
    public static String encryptByDefault(String content) throws Exception {  
    	return encrypt(content, PRIVATE_KEY); 
    }  
  
    /** 
     * 将byte[]转为各种进制的字符串 
     * @param bytes byte[] 
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制 
     * @return 转换后的字符串 
     */  
    public static String binary(byte[] bytes, int radix){  
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数  
    }  
  
    /** 
     * base 64 encode 
     * @param bytes 待编码的byte[] 
     * @return 编码后的base 64 code 
     */  
    public static String base64Encode(byte[] bytes){  
        return Base64.encodeBase64String(bytes);  
    }  
  
    /** 
     * base 64 decode 
     * @param base64Code 待解码的base 64 code 
     * @return 解码后的byte[] 
     * @throws Exception 
     */  
    public static byte[] base64Decode(String base64Code) throws Exception{  
        return StringUtil.isEmpty(base64Code) ? null : Base64.decodeBase64(base64Code.getBytes("UTF-8"));  
    }  
  
      
    /** 
     * AES加密 
     * @param content 待加密的内容 
     * @param encryptKey 加密密钥 
     * @return 加密后的byte[] 
     * @throws Exception 
     */  
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128);  
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);  
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));  
  
        return cipher.doFinal(content.getBytes("utf-8"));  
    }  
  
  
    /** 
     * AES加密
     * @param content 待加密的内容 
     * @param encryptKey 加密密钥 
     * @return 加密后的base 64 code 
     * @throws Exception 
     */  
    public static String encrypt(String content, String encryptKey) throws Exception {  
        return base64Encode(aesEncryptToBytes(content, encryptKey));  
    }  
  
    /** 
     * AES解密 
     * @param encryptBytes 待解密的byte[] 
     * @param decryptKey 解密密钥 
     * @return 解密后的String 
     * @throws Exception 
     */  
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128);  
  
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);  
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));  
        byte[] decryptBytes = cipher.doFinal(encryptBytes);  
        return new String(decryptBytes);  
    }  
  
  
    /** 
     * AES解密 
     * @param encryptStr 待解密的base 64 code 
     * @param decryptKey 解密密钥 
     * @return 解密后的string 
     * @throws Exception 
     */  
    public static String decrypt(String encryptStr, String decryptKey) throws Exception {  
        return StringUtil.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);  
    }  
    
    /**
     * 测试
     */
    /*public static void main(String[] args) throws Exception {  
//        String content = "这是一段加密信息";  
//        System.out.println("加密前：" + content);  
//        System.out.println("加密密钥和解密密钥：" + PRIVATE_KEY);  
//        String encrypt = encrypt(content, PRIVATE_KEY);  
//        System.out.println("加密后：" + encrypt);  
        String decrypt = decrypt("W3E1fK5m6zjjUgDYiv3uUPzMe6xcQl6TJ3pFr5KlaxZnMRuqc2jE/w4JIwmgsmg4hzSqlgSLCs8NWXfgEKrEWRx51vD3D+BEVbl/Vrj0KEFNPiDVwIGyw6QC275Heyb4ssAOo2EnQXGGt05pR+BiblypN2B9qiVBmZKd0YKvbmclIzTyAZIHoM4ATQIItBEzDgfSc6MzjMbkU78TloxzwHrdUZeLS39Iz68vbXliaIbV3Tj5u/9QWgRVBuG95BuJI2Dr1R8s00E7DaeVrSz6zBNMTZVgtAvTutfU5/bTC2c=", 
        		"yzhCgxgnhh0pyoo7");  
        System.out.println("解密后：" + decrypt);  
        
//        JSONObject ideJson = new JSONObject();
//		ideJson.put("t", "cHx6bw/WHElFGtb");
//		ideJson.put("i", "192.168.0.1");
//		ideJson.put("l", 0);
		
//		String enMsg = encrypt(ideJson.toJSONString(), PRIVATE_KEY);
//		System.out.println(enMsg);
//		System.out.println("解密："+decrypt(enMsg, PRIVATE_KEY));
    }*/

}
