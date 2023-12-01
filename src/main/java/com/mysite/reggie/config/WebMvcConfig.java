package com.mysite.reggie.config;

import com.mysite.reggie.common.JacksonObjectMapper;
import com.mysite.reggie.interceptor.LoginInterceptor;
import com.sun.media.sound.UlawCodec;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: WebMvcConfig
 * Package: com.mysite.reggie.config
 * Description
 *  SpringMVC的配置
 *      配置拦截器
 *      配置静态资源的放行路径
 *      配置消息转换器 Long =>
 * @Author zhl
 * @Create 2023/11/21 20:24
 * version 1.0
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 扩展mvc框架的消息转换器
     * @param converters the list of configured converters to be extended
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginInterceptor loginInterceptor = new LoginInterceptor();
        //放行的url
        List<String> urls = new ArrayList<>();
        urls.add("/employee/login");
        urls.add("/employee/logout");
        urls.add("/backend/**");
        urls.add("/front/**");
        //对移动端用户放行
        urls.add("/user/login");
        urls.add("/user/sendMsg");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(urls);
    }

    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }
}
