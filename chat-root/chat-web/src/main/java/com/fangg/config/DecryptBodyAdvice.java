package com.fangg.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.alibaba.fastjson.JSONObject;
import com.fangg.annotation.Decrypt;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.constant.TypeConstant;
import com.fangg.exception.ChatException;
import com.fangg.service.WebBaseService;
import com.xclj.common.util.AESUtil;
import com.xclj.common.util.RSAUtil;
import com.xclj.replay.ResultParam;

/**
 * Controller方法执行前做解密处理
 * <pre>AES 参数加解密统一使用前端登录时生成的密钥</pre>
 * @author fangg
 * 2022年2月3日 下午6:47:23
 */
// 此处设置需要当前Advice执行的域 , 省略默认全局生效
@RestControllerAdvice(basePackages = "com.fangg.controller")
public class DecryptBodyAdvice implements RequestBodyAdvice {
    private static final Logger logger = LoggerFactory.getLogger(DecryptBodyAdvice.class);
    
	@Value("${rsa.private.key}")
	private String rsaPrivateKey;
	@Value("${aes.private.key}")
	private String aesPrivateKey;
	@Value("${aes.ticket.key}")
	private String aesTicketKey;
	@Autowired
	WebBaseService webBaseService;
	
	@Override
	public Object afterBodyRead(Object arg0, HttpInputMessage arg1, MethodParameter arg2, Type arg3,
			Class<? extends HttpMessageConverter<?>> arg4) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type arg2,
			Class<? extends HttpMessageConverter<?>> arg3) throws IOException {
		if (methodParameter.getMethod().isAnnotationPresent(Decrypt.class)) {
			Decrypt secretAnnotation = methodParameter.getMethod().getAnnotation(Decrypt.class);
            if (TypeConstant.AES.equals(secretAnnotation.value())) {
                return new HttpInputMessage() {
                    @Override
                    public InputStream getBody() throws IOException {
                        try {
                        	String aesKey = null;
                            List<String> aesList = httpInputMessage.getHeaders().getValuesAsList("Other");
                            if (aesList.isEmpty()){
                                List<String> ticketList = httpInputMessage.getHeaders().getValuesAsList("ticket");
                                if (ticketList.isEmpty()){
                                	throw new RuntimeException("ticket不存在");
                                }
                            	// ticket解密
                            	String ticketStr = AESUtil.decrypt(ticketList.get(0), aesTicketKey);
                                JSONObject ticketJson = JSONObject.parseObject(ticketStr);
                                UserConfigTO userConfig = webBaseService.getUserInfoByCache(ticketJson.getString("t"));
                                if (userConfig == null) {
                                	throw new ChatException(ResultParam.FAIlED, "根据ticket获取用户缓存信息失败");
    							}
                                aesKey = userConfig.getAesKey();
                            } else {
								aesKey = aesList.get(0);
							}
                            
                            // @Decrypt注解上defaultKey=true时
                            if (secretAnnotation.defaultKey()) {
								aesKey = aesPrivateKey;
							}
                            
							String bodyStr = IOUtils.toString(httpInputMessage.getBody(), "UTF-8");
							
							// 先通过前端生成的密钥解密
							bodyStr = AESUtil.decrypt(bodyStr, aesKey);
							JSONObject resultJson = JSONObject.parseObject(bodyStr);
							if (resultJson == null && aesKey != aesPrivateKey) {
								// 如果使用动态AES密钥解密失败，再次通过默认密钥解密
								bodyStr = AESUtil.decrypt(bodyStr, aesPrivateKey);
							}
							return  IOUtils.toInputStream(bodyStr, "UTF-8");
						} catch (ChatException e) {
							logger.error("{}方法aes解密异常：\n{}\n", methodParameter.getMethod().getName(), httpInputMessage.getBody(), e);
							throw new RuntimeException(e.getMessage());
						} catch (Exception e) {
							logger.error("{}方法aes解密异常：\n{}\n", methodParameter.getMethod().getName(), httpInputMessage.getBody(), e);
						}
						return httpInputMessage.getBody();
                    }

                    @Override
                    public HttpHeaders getHeaders() {
                        return httpInputMessage.getHeaders();
                    }
                };
            } else if (TypeConstant.RSA.equals(secretAnnotation.value())) {
                return new HttpInputMessage() {
                    @Override
                    public InputStream getBody() throws IOException {
                        /*List<String> appIdList = httpInputMessage.getHeaders().get("appId");
                        if (appIdList.isEmpty()){
                            throw new RuntimeException("请求头缺少appID");
                        }*/
                        try {
							String bodyStr = IOUtils.toString(httpInputMessage.getBody(), "UTF-8");
							// 参数解密
							bodyStr = RSAUtil.decrypt(bodyStr, rsaPrivateKey);
							return  IOUtils.toInputStream(bodyStr, "UTF-8");
						} catch (Exception e) {
							logger.error("{}方法rsa解密异常：", methodParameter.getMethod().getName(), e);
						}
						return httpInputMessage.getBody();
                    }

                    @Override
                    public HttpHeaders getHeaders() {
                        return httpInputMessage.getHeaders();
                    }
                };
            }
        }
        return httpInputMessage;
	}

	@Override
	public Object handleEmptyBody(Object arg0, HttpInputMessage arg1, MethodParameter arg2, Type arg3,
			Class<? extends HttpMessageConverter<?>> arg4) {
		return arg0;
	}

	@Override
	public boolean supports(MethodParameter methodParameter, Type arg1, Class<? extends HttpMessageConverter<?>> arg2) {
		// 仅支持@RequestBody传参处理
		return methodParameter.hasParameterAnnotation(RequestBody.class);
	}

}
