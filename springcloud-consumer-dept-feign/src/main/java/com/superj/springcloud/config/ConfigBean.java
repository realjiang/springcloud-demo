package com.superj.springcloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigBean { // @Configuration 相当于 spring的 applicationContext.xml

    @Bean
    @LoadBalanced //配置赋值均衡实现RestTemplate
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
