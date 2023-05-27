package com.xclj.common.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.net.util.Base64;

/**
 * RSA密钥对工具类
 * @author fangg
 * 2022年2月3日 下午5:10:05
 */
public class RSAUtil {

	private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
	
	static {
		// 公钥
		keyMap.put(0, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhutV4Iu8LuD8hKDIh6oqMG0mL9JXaTi29+vU0MDo5wNRRqeyfqfwDjjNcoi+fsCyXSbAaXI16yoXGtEdAmD8Oj7cN19Yk0blxoCz39Tp87Zw3bG3eB5ScK2PhQjpfanMZM6jHOZSiybXPMEMZmf21+EFaUGYo5qFEGhX/+1oraT23xCaC4E+hf4RuibwcgzeEQC37bBkzUlXfvoZ0npx7ZJar4cDtido0MOs3sHw3w95dqIAmH/LzkBY+mAc28Ihy+o8ZA7o+GOvBSICc9yL5n+yV6gFROYyop/MhXywqtVV+OCl3IinDESJYL/FUsfFihWgVGoGDxpNY86Lq07VNwIDAQAB");
		// 私钥
		keyMap.put(1, "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCG61Xgi7wu4PyEoMiHqiowbSYv0ldpOLb369TQwOjnA1FGp7J+p/AOOM1yiL5+wLJdJsBpcjXrKhca0R0CYPw6Ptw3X1iTRuXGgLPf1OnztnDdsbd4HlJwrY+FCOl9qcxkzqMc5lKLJtc8wQxmZ/bX4QVpQZijmoUQaFf/7WitpPbfEJoLgT6F/hG6JvByDN4RALftsGTNSVd++hnSenHtklqvhwO2J2jQw6zewfDfD3l2ogCYf8vOQFj6YBzbwiHL6jxkDuj4Y68FIgJz3Ivmf7JXqAVE5jKin8yFfLCq1VX44KXciKcMRIlgv8VSx8WKFaBUagYPGk1jzourTtU3AgMBAAECggEAJC3bO3l9Hv0J+e67l1WUNqQuR0iaVAb3WYgN62Svj7MeClpRzRDvmIC25u2wV4EI013e3ufXeZYx1+kXZFnpiRzuJJMWx3eNiitdELqhec8EeQ586mMHLFv+TmhTZHUx8B5RMyC4JcRml04VKyjZ0o3uy4jhi42V/Q5d+q0JEvbzrDkcBI+Weq6/nantbXZZib3d0roMo4jabvDLNp+/9aniot9hQTKpGO/SOJHYTRZYaiCNdxREY30zqCtfQNecaQj3fxX6sFDxobFrkEDDAY4efixKjNIdJWhy82PigSF3rGcit02KDwMbLd+5cmpyPEFRswhd8fXl3uYHysyv8QKBgQDHBXAv2RzYnhln6AzfErYdHvPe+dNcy84626Er98fpLoSOSLcFIHL/q23TvC464GB0DVz/rhq9u3UCukz9SKl0l2Wr+FOdsr30Yiy3J/pTFuBP0HvmTzDy2gKirUTl4vdABE9qGOpUVedo1NixNc/KEyjceReOwXQgwfStWcoaiwKBgQCti8Tx/rbccqthtUuFeucggOcRLCbsmMfoz9EppUflOdsSZkI6EWErtU1LidOlsgXY+Z06I9nNbcGl0wLndaEfKvEcB6oFWKXnlLm53VDLyYRn+MiH02hdemUprBFdsD2EUQ5KCIuvyYvMA78d3vkjXirhGiJLGjNrDf9peq2BhQKBgDQ6wiFcegyDGSnCpTYZo/9qcxeuOoDc34dkwhxWz3jrg1AP1zAaPvPH2m+5r+w1nDp4k1JqL4xhWKjZmYDTA92cKcuCxPbaxK0Q4qlCbZhGpaFslkdvuunQuuZUUIOAF+EOAIVYeo6daLiDO0buV1SxyIRrpvoI3OAH5p1jtf1XAoGAFhhtgsIDVh3jZBVJAD2dn63JvY35hrYM/b83yi2Y8b5j/fu3At2jmYUDylJJ+BaqV+2v7u8Rk+4dcZdkD0QSt30oJYF0TCn1NX7iBp/+4XNOwE4UefaYmw4yQxP9QghmTKplP1fHMSJgAiGX+52f6rjajL5fD2DOmxq1IiDUZmECgYEAvsFrhNJpciOix3+ZF6ThizD2lZBh7F8jggOzi9koqHtObAFQZEO2u4md8bZf5YXPnI35TTO7UIeNe7yJDDCHCEi+Pon5rRM7qiOW78YDPAbpVXa4+ATV4upumt4vOwy76wAt7SNYiXvSwffYlsYEdlAnwHNyEhPkKDUb27NFIJU=");
	}
	
	//	来自CSDN 作者：HFUT_qianyang 网址--https://qianyang-hfut.blog.csdn.net/article/details/83105736
	/*public static void main(String[] args) throws Exception {
		//生成公钥和私钥
//		genKeyPair();
		//加密字符串
		String message = "这是一段加密信息";
//		System.out.println("随机生成的公钥为:" + keyMap.get(0));
//		System.out.println("随机生成的私钥为:" + keyMap.get(1));

//		JSONObject ideJson = new JSONObject();
//		ideJson.put("ide", "cHx6bw/WHElFGtbEGIx2fYhxtJJQu");
//		ideJson.put("ip", "192.168.0.1");
//		ideJson.put("loginType", 0);
		
//		String messageEn = encrypt(ideJson.toJSONString(), keyMap.get(0));
//		System.out.println(message + "加密后的字符串为:" + messageEn);
		
//		keyMap.put(0, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvHudu15nLwfj6cTHPZlTZkcVEy4f9NZOVRucoXb8O2xZMoRqS5nYA0PPvpf7FsYnDU9s81jnN3R6m2+rVYwhcIDxwkineG9dsdOO3Ieg62Jup1OHzsDAJFn266rZARQnzDXAtAE3CwSe7/mR4Bf6IEHpOI4WdlA+2yaY8FgNeMQIDAQAB");
//		keyMap.put(1, "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDH8J6L8PnoW4Kb69eFcKddoxHYK8z8KGIEnpZE5X7hvBoNmoq8+7BO7+1uBdCrbzECOkhgj4dhucfsCR0zCSa+Lru2Pek00M3hZ8E4Gi4QlX2vu6HPy8hKrWMVJEM7ZIOWR1iyQuwoXMknb4tg8e3GFvQ4+Q88z1qYgn78LINsPBZWF6hwLx2YR0hWrnHG79OVaQxw8qnz/KwwVah8mAzj/d0X0iy0ymPX+jClEERRkFpKV91vaGlhlHYNykdvXVsTXXx6NicUeIdpRl1VYjke93KpTqLkEuTjltjJ+EeMuW+y5uQk10ZAYp42GJJEMrYQqHc8LTv6li53F2whrxwvAgMBAAECggEAPJz5s7sOSB7V7acF43JPiteQ5VQCBL/JV1UsgR6B5LU9wLoFLV2wCX47IIuqww4mcWJx5rQcesfzj98DdyFjGWzZg1ophNV4VMk7jmKBbI2lWM0tNO22fs0pqJMhvrOuILTocsJPDb1zTf1G59DwIIl86Hi44dqKwIlvLcvg28KiWe0QsfsCuSRNpFjjfBm/HSMNGBRWh6wRNtDwa7h2dRbrEn8UeHoh4/rhE6PLkTvxxy3jKUMXFXtBByIOumHpL6vz8nQGzO22aJTlELR1yOOGidpZhAtQEPWZIuls2HAISitVNDKPIpwiwPKeWoWzVrJ4pSAxZB9pYXpHpu/bAQKBgQDoSjXvrAbicW49kwt7LzY77kEKffl5yBKBQRcYNQKgSexti9UaP6rw3BtWBajwC6pimflnWvRkY3g/dQIQROTe/YjO5+ffvgbhsrW7o/+zv0ubvyV0JomgQ8RhFcd7XI85P40JYsYftrhRWTVQb3i1yFln0aadxr98jyJwzBS7jwKBgQDcWRiua0EQeXqwC43Vfbuc0kR7BzXRPANpNZ7Zt0KFAwmmrTRI8PC5J4PfOpRv4qNj70CW3tKKKkAAWIX2X851kiGHgP0WNSRy3W9rJI612wqhFsiAWWILaUVtPBGrpG2eLC3LtuLF4oelzqUb1McETCapRMmjqWQ0ciP4yxHFYQKBgQCB7IAefemb3jX62rQhQlbjTm+ynqNbKjyvbuir8Xp2jOJgo0WeWN4TugjQJHpJST0TyWtmE8l+HOS1GYz97IrW7pgGok9fJbpTotFjEmeC8gwoINGiFawEp+K/Kp9YsHyrWJBaH5URR/HwDiVzx+WMR3M6TlkTBocz9BsfXAZ8oQKBgDJfU9sbvcYMX1W7bnMtySPylaEiZtUsSQNWNLhEwroxlZU650b9fap2NBlmBOUHBJ8l5LgYvDzFp9fsZMOL52DNRUj63eCjyW9Q29W8h7tfFbD5ymA9DsLZD/wGzWtVrZGJO/bkGjdur9/hu7D/FXgQCI6NoSL+6F9s8HhBxBBBAoGBAODwxziCR0lULsdm+5N+P9NRIkd9+0JIUwy9elVLhuq4j1+PVwmUKqPyvEDQzOkh3AqgeBI3MhwkZkLrDsrEkEPHB+1vXcG6afIZAOkJQg0QFIJBBEafVTgn7CdullmvsLh1h+30drZe4mEATt5E9cJD4g53Q5m44awtN1lgZnr9");
		
		String encodeStr = "YuyO2+n/1ZOJ5iRfqJoSnROYCl/SKqYB5Ezqp45GVYApf4UwpQdYl5fXIgv42WDsoi/UttN15XZeXpd/2tJafSPPIoR614e/otcfAM+3PlFWUANbjMGFx6Hx9vH2gL6a06tor1durVAbbiv8DqMiH0Vwz0pNUXYIJe3r9ueEM20fTQL1o4AbXjhcuo6qRrCTR2SXGg06a5xrQFHNTr0cSOHnzGrmdNVeoOu9LVURcl2tZvyW4me8zGe99R8scta8m0hDEn/nw1yFInR+86bHz1TCv9ACawRwMs5M2nvFCVFmKPxFmRMPGdSFJPPAImbB4OSCUVgXY/KYbJ0zmbddDA==";
//		String encryptedBytes = "UTHKqzhvvg8F+cM3FWDcipDIPJdPzQpXBMJc+7KnXDDaelFY3MIdLjMA9x4YWNKWyfmoIaA68q5bUDgRSKsudDwSGM81FY+tDUIA110VTJs4BOPEvU6lYA7wdzHZ0XPcLg92FPfzBzwOAQuKP15MeVeuFjPkodpICwVPjwM/Yug=";
		String messageDe = decrypt(encodeStr, keyMap.get(1));
		System.out.println("还原后的字符串为:" + messageDe);
	}*/

	/** 
	 * 随机生成密钥对 
	 * @throws NoSuchAlgorithmException 
	 */  
	public static Map<Integer, String> genKeyPair() throws NoSuchAlgorithmException {  
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象  
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");  
		// 初始化密钥对生成器，密钥大小为96-1024位  
		keyPairGen.initialize(2048, new SecureRandom());  
		// 生成一个密钥对，保存在keyPair中  
		KeyPair keyPair = keyPairGen.generateKeyPair();  
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥  
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥  
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));  
		// 得到私钥字符串  
		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));  
		// 将公钥和私钥保存到Map
		keyMap.put(0,publicKeyString);  	//0表示公钥
		keyMap.put(1,privateKeyString);  	//1表示私钥
		Map<Integer, String> resultMap = new HashMap<Integer, String>();
		resultMap.put(0, publicKeyString);  	//0表示公钥
		resultMap.put(1, privateKeyString);  	//1表示私钥
		return resultMap;
	}  

	/**
	 * 默认public_key要与application.properties中的rsa_public_key保持一致(否则此方法不可使用)
	 */
	public static String encryptByBefault( String str) throws Exception{
		return encrypt(str, keyMap.get(0));
	}

	/**
	 * 默认private_key要与application.properties中的aes_private_key保持一致(否则此方法不可使用)
	 */
	public static String decryptByDefault(String str) throws Exception{
		return decrypt(str, keyMap.get(1));
	}
	
	/** 
	 * RSA公钥加密 
	 *  
	 * @param str 
	 *            加密字符串
	 * @param publicKey 
	 *            公钥 
	 * @return 密文 
	 * @throws Exception 
	 *             加密过程中的异常信息 
	 */  
	public static String encrypt( String str, String publicKey ) throws Exception{
		//base64编码的公钥
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		//RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
		return outStr;
	}

	/** 
	 * RSA私钥解密
	 *  
	 * @param str 
	 *            加密字符串
	 * @param privateKey 
	 *            私钥 
	 * @return 铭文
	 * @throws Exception 
	 *             解密过程中的异常信息 
	 */  
	public static String decrypt(String str, String privateKey) throws Exception{
		//64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
		//base64编码的私钥
		byte[] decoded = Base64.decodeBase64(privateKey);  
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));  
		//RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		String outStr = new String(cipher.doFinal(inputByte));
		return outStr;
	}
	
	
	
	
	
	
	
	
	
	
	//********************************************************** 公钥加密，私钥解密，私钥加签，公钥验签 ***************************************************************/
	/**
	 * 来自CSDN博主「ZKNA_」的原创文章
		原文链接：https://blog.csdn.net/Site_Dave/article/details/101372338
	 */
	
	public static String data = "12345";
//	public static BASE64Encoder base64Encoder = new BASE64Encoder();
//	public static BASE64Decoder base64Decoder = new BASE64Decoder();
//	// 公钥
//	private static String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/OpVr+aQu6B3stSUgsLcZWpaxatset8zTqat1FF543hoECcTnRqDXKwfX09J+RLCc/1fbITt0s4wUUwJNU7lKJSTGZp5/xHcEiFJjTa+XY6pQHQKvvZjAQMkyzC3H5tmaNTapKYJOAWw7u1dxcRNFdD3k5E+EiqSnlo30u7SLCwIDAQAB";
//	// 私钥
//	private static String privateKeyStr = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL86lWv5pC7oHey1JSCwtxlalrFq2x63zNOpq3UUXnjeGgQJxOdGoNcrB9fT0n5EsJz/V9shO3SzjBRTAk1TuUolJMZmnn/EdwSIUmNNr5djqlAdAq+9mMBAyTLMLcfm2Zo1Nqkpgk4BbDu7V3FxE0V0PeTkT4SKpKeWjfS7tIsLAgMBAAECgYBicjt4geV3TIITWVJK2Q76G3vWzIcP8lmdYgzl0l2sZdMI3yqiUeb9vqZkAyWrYZt2x7GoGxyrwL9Nu0pFGuQZFaZIrHRj6LoNq/dgGUpN5zviXUDq2RrhhP7dW4Zc2UbbZqtTzn4jgv8/dviT+LACBmbavojjbb6YZHO/YDml2QJBAPWWu7SkyqfHSDOBBYWyI0GON2ApqTOIsENpQ572IvjNzT8TcXsNRr1hy4o5JfJN4KutBSsJkxAv3+nCc7pvRo0CQQDHVefkgjyuCyQjTtm8WPeIP7Ny8Rul44SmoyaSOANiPufsjIAPvxtNwyvkyUKtI7AMx6XrAWltRMWWiByVH533AkBp87fTfWz46V7a6YTqYyoWtDZrxE19MDFrQ9SqleIMmS09UzQYNGgaeECJx5H5cWPGbQTXxm+uAhmGDiBDhJJZAkEAu84SR1b1OL1CdQmrVyszPGlX9ul3NRphNmbsxkKD3aKK/HF7jlptrRw/VLTSXzIKgl/v0LRp0gtDZgojc9RwDQJBAJ2d0E9huqG9yP0bA9q0lIFwqJogLnoRvQCkNW6hATUrA5b7lrZYniPbwRfSALW2jgweTeTaeouPBHPWbVz/ws8=";
//	private static RSAUtil ourInstance = new RSAUtil();
//
//	public static RSAUtil getInstance() {
//		return ourInstance;
//	}

//	// 生成密钥对
//	public static Map<Integer, String> generateKeyPair() throws NoSuchAlgorithmException {
//		KeyPairGenerator keyPairGenerator;
//		keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//		keyPairGenerator.initialize(1024);
//		KeyPair keyPair = keyPairGenerator.generateKeyPair();
//		// 获取公钥，并以base64格式打印出来
//		PublicKey publicKey = keyPair.getPublic();
//		//publicKeyStr = new String(base64Encoder.encode(publicKey.getEncoded()));
//		String publicKeyStr = new String(Base64.encodeBase64String(publicKey.getEncoded()));
//		// 获取私钥，并以base64格式打印出来
//		PrivateKey privateKey = keyPair.getPrivate();
//		//privateKeyStr = new String(base64Encoder.encode(privateKey.getEncoded()));
//		String privateKeyStr = new String(Base64.encodeBase64String(privateKey.getEncoded()));
//		
//		Map<Integer, String> resultMap = new HashMap<Integer, String>();
//		resultMap.put(0, publicKeyStr);
//		resultMap.put(1, privateKeyStr);
//		return resultMap;
//	}
//
//	// 将base64编码后的公钥字符串转成PublicKey实例
//	private static PublicKey getPublicKey(String publicKey) throws Exception {
////		byte[] keyBytes = base64Decoder.decodeBuffer(publicKey);
//		byte[] keyBytes = Base64.decodeBase64(publicKey);
//		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//		return keyFactory.generatePublic(keySpec);
//	}
//
//	// 将base64编码后的私钥字符串转成PrivateKey实例
//	private static PrivateKey getPrivateKey(String privateKey) throws Exception {
////		byte[] keyBytes = base64Decoder.decodeBuffer(privateKey);
//		byte[] keyBytes = Base64.decodeBase64(privateKey);
//		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
//		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//		return keyFactory.generatePrivate(keySpec);
//	}
//
//	// 公钥加密
//	public static String encrypt(String content, String publicKeyStr) throws Exception {
//		// 获取公钥
//		PublicKey publicKey = getPublicKey(publicKeyStr);
//		Cipher cipher = Cipher.getInstance("RSA");
//		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//		byte[] cipherText = cipher.doFinal(content.getBytes());
////		String cipherStr = base64Encoder.encode(cipherText);
//		String cipherStr = Base64.encodeBase64String(cipherText);
//		return cipherStr;
//	}
//
//	// 私钥加密
//	public static String encryptByPrivateKey(String content, String privateKeyStr) throws Exception {
//		// 获取私钥
//		PrivateKey privateKey = getPrivateKey(privateKeyStr);
//		Cipher cipher = Cipher.getInstance("RSA");
//		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//		byte[] cipherText = cipher.doFinal(content.getBytes());
////		String cipherStr = base64Encoder.encode(cipherText);
//		String cipherStr = Base64.encodeBase64String(cipherText);
//		return cipherStr;
//	}
//
//	/**
//	 * 默认public_key要与application.properties中的rsa_public_key保持一致(否则此方法不可使用)
//	 */
//	public static String encryptByBefault( String str) throws Exception{
//		return encrypt(str, keyMap.get(0));
//	}
//
//	/**
//	 * 默认private_key要与application.properties中的aes_private_key保持一致(否则此方法不可使用)
//	 */
//	public static String decryptByDefault(String str) throws Exception{
//		return decrypt(str, keyMap.get(1));
//	}
//
//	// 私钥解密
//	public static String decrypt(String content, String privateKeyStr) throws Exception {
//		// 获取私钥
//		PrivateKey privateKey = getPrivateKey(privateKeyStr);
//		Cipher cipher = Cipher.getInstance("RSA");
//		cipher.init(Cipher.DECRYPT_MODE, privateKey);
////		byte[] cipherText = base64Decoder.decodeBuffer(content);
//		byte[] cipherText = Base64.decodeBase64(content);
//		byte[] decryptText = cipher.doFinal(cipherText);
//		return new String(decryptText);
//	}
//
//	// 公钥解密
//	public static String decryptByPublicKey(String content, String publicKeyStr) throws Exception {
//		// 获取公钥
//		PublicKey publicKey = getPublicKey(publicKeyStr);
//		Cipher cipher = Cipher.getInstance("RSA");
//		cipher.init(Cipher.DECRYPT_MODE, publicKey);
////		byte[] cipherText = base64Decoder.decodeBuffer(content);
//		byte[] cipherText = Base64.decodeBase64(content);
//		byte[] decryptText = cipher.doFinal(cipherText);
//		return new String(decryptText);
//	}
//
//
//	public static void main(String[] args) throws Exception {
//		JSONObject ideJson = new JSONObject();
//		ideJson.put("ide", "cHx6bw/WHElFGtbEGIx2fYhxtJJQu");
//		ideJson.put("ip", "192.168.0.1");
//		ideJson.put("loginType", 0);
//		ideJson.put("msg", "这是一段加密信息");
//		
//		data = ideJson.toJSONString();
//		
//		System.out.println("初始数据："+data);
//		// 公钥加密
////		String encryptedBytes = encrypt(data, keyMap.get(0));
//		System.out.println("公钥加密后：" + encryptedBytes);
//		// 私钥解密
//		String decryptedBytes = decrypt(encryptedBytes, keyMap.get(1));
//		System.out.println("私钥解密后：" + decryptedBytes);
//		// 私钥加密
//		String encryptedBytes2 = encryptByPrivateKey(data, keyMap.get(1));
//		System.out.println("私钥加密后：" + encryptedBytes2);
//		// 公钥解密
//		String decryptedBytes2 = decryptByPublicKey(encryptedBytes2, keyMap.get(0));
//		System.out.println("公钥解密后：" + decryptedBytes2);
//	}
	
}
