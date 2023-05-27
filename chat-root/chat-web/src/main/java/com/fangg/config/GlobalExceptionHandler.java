package com.fangg.config;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fangg.exception.ChatException;
import com.xclj.replay.ResultEntity;
import com.xclj.replay.ResultParam;

/**
 * 全局异常处理类
 * @author fangg
 * 2021年12月20日 下午7:52:22
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/**
	 * 处理自定义的业务异常
	 * @param req
	 * @param e
	 * @return
	 */
    @ExceptionHandler(value = ChatException.class)  
    @ResponseBody  
	public ResultEntity chatExceptionHandler(HttpServletRequest req, ChatException e){
    	logger.error("业务处理异常：{}", e.getErrorMsg());
		return new ResultEntity(e.getResultParam());
    }

	/**
	 * 处理空指针的异常
	 * @param req
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value =NullPointerException.class)
	@ResponseBody
	public ResultEntity exceptionHandler(HttpServletRequest req, NullPointerException e){
		logger.error("空指针异常：", e);
		return new ResultEntity(ResultParam.FAILED_UNKNOW, "空指针异常");
	}


    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
	@ResponseBody
	public ResultEntity exceptionHandler(HttpServletRequest req, Exception e){
    	logger.error("未知异常：", e);
       	return new ResultEntity(ResultParam.FAILED_UNKNOW);
    }
    
}
