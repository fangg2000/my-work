package com.fangg.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fangg.interceptor.ChatWebInterceptor;

/**
 * 拦截器配置
 * @author fangg
 * 2021年12月22日 上午7:26:28
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Value("${http-headers}")
	String httpHeaders;
	
	/**
	 * 为了ChatWebInterceptor能使用spring注入
	 */
	@Bean
    public ChatWebInterceptor initInterceptor() {
        return new ChatWebInterceptor();
    }

	/**
	 * 过滤设置
	 */
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(initInterceptor());
        registration.addPathPatterns("/**");                      	// 所有路径都被拦截
        // 添加不拦截路径
        registration.excludePathPatterns(                         	
                                         "/user/login",           	// 登录
                                         "/user/regist",           	// 注册
                                         "/config/unbindCheck",     // 解绑验证
                                         "/user/getProCityList",    // 省份城市列表
                                         "/**/*.js",              	// js静态资源
                                         "/**/*.css",             	// css静态资源
                                         "/**/*.jpg",
                                         "/**/*.png",
                                         "/**/*.gif",
                                         "/**/*.html"
                                         );    
    }

	/**
	 * 跨域设置
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// 对于有多次调用的（如vue用axios请求时会发出两次请求），这里的配置只能成功一次，要在拦截器那里加上此配置
		registry.addMapping("/**")    	// 添加映射路径，“/**”表示对所有的路径实行全局跨域访问权限的设置
	        .allowedOrigins("*")    	// 开放哪些ip、端口、域名的访问权限(需要凭证时不能设置为*)
	        .allowCredentials(true)  	// 是否允许发送Cookie信息 
	        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")     	// 开放哪些Http方法，允许跨域访问
//	        .allowedHeaders("*");     	// 允许HTTP请求中的携带哪些Header信息
//			.allowedHeaders("Access-Control-Allow-Headers", "X-Requested-With", "Content-Type", "ticket");     	// 允许HTTP请求中的携带哪些Header信息
			.allowedHeaders(httpHeaders.split(","));     	// 允许HTTP请求中的携带哪些Header信息(不能使用一个字符串然后逗号分隔这种形式)
	        //.exposedHeaders("*");   	// 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
	}

	// 必须在pom.xml引入fastjson的jar包，并且版必须大于1.2.10
	/*@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		// 1、定义一个convert转换消息的对象
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
		// 2、添加fastjson的配置信息
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		SerializerFeature[] serializerFeatures = new SerializerFeature[] {
				// 输出key是包含双引号
				// SerializerFeature.QuoteFieldNames,
				// 是否输出为null的字段,若为null 则显示该字段//
				// SerializerFeature.WriteMapNullValue,
				// 数值字段如果为null，则输出为0
				SerializerFeature.WriteNullNumberAsZero,
				// List字段如果为null,输出为[],而非null
				SerializerFeature.WriteNullListAsEmpty,
				// 字符类型字段如果为null,输出为"",而非null
				SerializerFeature.WriteNullStringAsEmpty,
				// Boolean字段如果为null,输出为false,而非null
				SerializerFeature.WriteNullBooleanAsFalse,
				// Date的日期转换器
				SerializerFeature.WriteDateUseDateFormat,
				// 循环引用
				SerializerFeature.DisableCircularReferenceDetect };
		fastJsonConfig.setSerializerFeatures(serializerFeatures);
		fastJsonConfig.setCharset(Charset.forName("UTF-8"));
		// 3、在convert中添加配置信息
		fastConverter.setFastJsonConfig(fastJsonConfig);
		//
		//List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
		//supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		//fastConverter.setSupportedMediaTypes(supportedMediaTypes);
		// 4、将convert添加到 converters中
		HttpMessageConverter<?> converter = fastConverter;
		return new HttpMessageConverters(converter);
	}*/
	
	
	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
	   // 1、需要先定义一个 convert 转换消息的对象;
	   FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

	   //2、添加fastJson 的配置信息，比如：是否要格式化返回的json数据;
	   FastJsonConfig fastJsonConfig = new FastJsonConfig();
	   fastJsonConfig.setSerializerFeatures(
			   	// 数值为NULL时过滤掉
			   	SerializerFeature.PrettyFormat
			   	// 数值字段如果为null，则输出为0
				//SerializerFeature.WriteNullNumberAsZero,
				// List字段如果为null,输出为[],而非null
//				SerializerFeature.WriteNullListAsEmpty,
				// 字符类型字段如果为null,输出为"",而非null
//				SerializerFeature.WriteNullStringAsEmpty,
				// Boolean字段如果为null,输出为false,而非null
//				SerializerFeature.WriteNullBooleanAsFalse,
				// Date的日期转换器
				//SerializerFeature.WriteDateUseDateFormat,
				// 保留map空的字段
//	            SerializerFeature.WriteMapNullValue,
				// 关闭循环引用检测(在循环引用时可能会导致StackOverflowError异常。不关闭时如果出现循环引用则用$ref代替)
				//SerializerFeature.DisableCircularReferenceDetect
			   	);

	   //3、在convert中添加配置信息.
	   fastConverter.setFastJsonConfig(fastJsonConfig);
	   // 编码设置
	   // 中文乱码解决方案
       List<MediaType> mediaTypes = new ArrayList<>();
       mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);		//设定json格式且编码为UTF-8
       //mediaTypes.add(MediaType.valueOf("text/html;charset=UTF-8"));	//"text/html;charset=UTF-8"
       fastConverter.setSupportedMediaTypes(mediaTypes);
	   
	   HttpMessageConverter<?> converter = fastConverter;
	   return new HttpMessageConverters(converter);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFormatters(FormatterRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addViewControllers(ViewControllerRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Validator getValidator() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
