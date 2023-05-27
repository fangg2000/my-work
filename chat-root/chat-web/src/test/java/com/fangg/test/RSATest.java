package com.fangg.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.xclj.common.redis.RedisClientTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RSATest {

	@Autowired
	RedisClientTemplate redisClientTemplate;
	
	

	@Test
	public void test() {
		// 加解密测试
		System.out.println("加密开始--");
		
		/*
		String encoded = redisClientTemplate.getString("public_key");
		if (encoded == null) {
			RsaSecretEncryptor encryptor = new RsaSecretEncryptor();
			System.out.println(JSONObject.toJSONString(encryptor));
			encoded = encryptor.getPublicKey();
			redisClientTemplate.setString("public_key", encoded);
		}
		
		RsaSecretEncryptor encryptor = new RsaSecretEncryptor(encoded, RsaAlgorithm.OAEP);
		assertFalse("Encryptor schould not be able to decrypt", encryptor.canDecrypt());
		assertEquals("encryptor", encryptor.decrypt(encryptor.encrypt("encryptor")));
		
//		System.out.println("新信息：" + JSONObject.toJSONString(keyPair));
//		System.out.println("新公钥信息：" + JSONObject.toJSONString(keyPair.getPublic()));
//		System.out.println("新私钥信息：" + JSONObject.toJSONString(keyPair.getPrivate()));
		
//		RsaSecretEncryptor other = new RsaSecretEncryptor(keyPair);
		String message = encryptor.encrypt("这是加密信息");
		System.out.println("加密信息：" + message);
		System.out.println("解密信息：" + encryptor.decrypt(message));
		*/
		
		
		
		System.out.println("加密结束--");
	}

}
