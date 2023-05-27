package com.fangg.config;

import java.util.Date;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

import com.fangg.bean.chat.vo.SysUserVO;

/**
 * 用户密码加盐
 * @author fangg
 * 2021年12月18日 下午7:51:06
 */
@Component(value="passwordHelper")
public class PasswordHelper {
	private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
	private final String ALGORITHM_NAME = "md5";
	private final int HASH_ITERATIONS = 2;

	/**
	 * 对新密码加盐
	 */
	public void encryptPassword(SysUserVO sysUser) {
		sysUser.setSalt(randomNumberGenerator.nextBytes().toHex());
		String newPassword = new SimpleHash(ALGORITHM_NAME, sysUser.getPassword(),
				ByteSource.Util.bytes(sysUser.getCredentialsSalt()), HASH_ITERATIONS).toHex();
		sysUser.setPassword(newPassword);
		sysUser.setUpdateTime(new Date());
	}
	
	/**
	 * 解密加盐
	 */
	public void decryptPassword(SysUserVO sysUser) {
		String newPassword = new SimpleHash(ALGORITHM_NAME, sysUser.getPassword(),
				ByteSource.Util.bytes(sysUser.getCredentialsSalt()), HASH_ITERATIONS).toHex();
		sysUser.setPassword(newPassword);
	}
	
}
