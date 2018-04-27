package com.mmall.controller.common;

import com.mmall.controller.common.interceptor.TimeInterceptor;
import com.mmall.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description :
 * <p>Date : 2018/1/20 11:00
 * <p>@author : wjq
 */
//@Configuration//声明一个配置文件
public class WebConfig extends WebMvcConfigurerAdapter {
    //注入一个过滤器到mvc容器
    @Bean
    public FilterRegistrationBean characterEncodingFilterRegister(){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CharacterEncodingFilter filter = new CharacterEncodingFilter("UTF8");
        filter.setForceEncoding(true);
        registrationBean.setFilter(filter);
        List<String> list = new ArrayList<>();
        list.add("/*");
        registrationBean.setUrlPatterns(list);
        return  registrationBean;
    }
    @Autowired
    private TimeInterceptor timeInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //将拦截器添加到mvc容器
        registry.addInterceptor(timeInterceptor);
    }

    @Bean
    public RedisHttpSessionConfiguration initConfig(){
        RedisHttpSessionConfiguration config = new RedisHttpSessionConfiguration();
        config.setMaxInactiveIntervalInSeconds(60*30);//30分钟
        return config;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20")));
        config.setMaxTotal(Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "10")));
        config.setMinIdle(Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "20")));
        return config;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory factory = new JedisConnectionFactory();
        return factory;
    }

}
