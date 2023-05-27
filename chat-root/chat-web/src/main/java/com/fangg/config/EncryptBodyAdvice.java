package com.fangg.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson.JSONObject;
import com.fangg.annotation.Encrypt;
import com.fangg.bean.chat.to.UserConfigTO;
import com.fangg.constant.TypeConstant;
import com.fangg.exception.ChatException;
import com.fangg.service.WebBaseService;
import com.xclj.common.util.AESUtil;
import com.xclj.common.util.RSAUtil;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * Controller方法返回值做加密处理
 * <pre>AES 返回值加解密统一使用前端登录时生成的密钥</pre>
 * @author fangg
 * 2022年2月4日 下午1:03:46
 */
@ControllerAdvice(basePackages = "com.fangg.controller")
public class EncryptBodyAdvice implements ResponseBodyAdvice {
    private static final Logger logger = LoggerFactory.getLogger(EncryptBodyAdvice.class);

	@Value("${rsa.public.key}")
	private String rsaPublicKey;
	@Value("${aes.private.key}")
	private String aesPrivateKey;
	@Value("${aes.ticket.key}")
	private String aesTicketKey;
	
	@Autowired
	WebBaseService webBaseService;

	/**
	 * 返回值加密（其中，如果controller方法中已经取到用户信息，则应把aesKey(先通过AES加密，否则前端会看到真正的key)
	 * 添加到response的header中，否则会解密ticket再去获取用户信息）
	 */
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType arg2, Class arg3, ServerHttpRequest request,
			ServerHttpResponse response) {
        
        try {
        	// 如果不需要加密则直接返回原值
        	if (methodParameter.getMethod().isAnnotationPresent(Encrypt.class)) {
                // 获取注解配置的包含和去除字段
            	Encrypt encryptAnnotation = methodParameter.getMethodAnnotation(Encrypt.class);
            	if (TypeConstant.AES.equals(encryptAnnotation.value())) {
//            		logger.info("对方法method :【" + methodParameter.getMethod().getName() + "】返回数据进行AES加密");
//                    ObjectMapper objectMapper = new ObjectMapper();
            		// 判断是否使用默认KEY进行加密
                	if (encryptAnnotation.defaultKey()) {
                    	// 此处得到的是美化格式的字符串（多行）
                        //String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
                        return AESUtil.encrypt(JSONObject.toJSONString(body), aesPrivateKey);
    				} 
                	
                	List<String> aesList = response.getHeaders().getValuesAsList("Other");
                	if (aesList.isEmpty()){
                    	List<String> ticketList = response.getHeaders().getValuesAsList("ticket");
                        if (ticketList.isEmpty()){
                        	throw new ChatException(ResultParam.FAIlED, "ticket为空");
                        }
                        
                        // ticket统一使用默认key进行解密
                        String ticketStr = AESUtil.decrypt(ticketList.get(0), aesTicketKey);
                        JSONObject ticketJson = JSONObject.parseObject(ticketStr);
                        UserConfigTO userConfig = webBaseService.getUserInfoByCache(ticketJson.getString("t"));
                        if (userConfig == null) {
                        	throw new ChatException(ResultParam.FAIlED, String.format("根据ticket(%s)获取用户缓存信息失败", ticketJson.getString("t")));
        				}
                        
                    	// 此处得到的是美化格式的字符串（多行）
                        //String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
                        return AESUtil.encrypt(JSONObject.toJSONString(body), userConfig.getAesKey());
                	}
                	
                	String aesKey = AESUtil.decrypt(aesList.get(0), aesTicketKey);
                	return AESUtil.encrypt(JSONObject.toJSONString(body), aesKey);
            	} 
            	// 如果需要RSA加密，最好有前端给的公钥(这里暂时使用服务端的公钥)
            	else if (TypeConstant.RSA.equals(encryptAnnotation.value())) {
//            		logger.info("对方法method【" + methodParameter.getMethod().getName() + "】返回数据进行RSA加密");
                    //ObjectMapper objectMapper = new ObjectMapper();

                    //String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
                    return RSAUtil.encrypt(JSONObject.toJSONString(body), rsaPublicKey);
            	}
            	
            	throw new ChatException(ResultParam.FAIlED, "返回值加密失败");
            }
        } catch (ChatException e) {
            logger.error("对方法method【{}】返回值RSA加密异常：", methodParameter.getMethod().getName(), e);
            return new ResultEntity(ResultParam.FAILED_UNKNOW, "登录异常，请联系系统管理员。");
        } catch (Exception e) {
            logger.error("对方法method【{}】返回值RSA加密异常：", methodParameter.getMethod().getName(), e);
            return new ResultEntity(ResultParam.FAILED_UNKNOW, "登录异常，请联系系统管理员。");
        }
        
        return body;
	}

	@Override
	public boolean supports(MethodParameter arg0, Class arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
