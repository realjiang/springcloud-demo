package com.superj.myrule;

import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//注意:不能和启动类放同级目录,会被扫描到,被所有ribbon客户端共享,不能
@Configuration
public class SuperjRule {
    @Bean
    public IRule myrule() {
        return new MyRandomRule();//自定义rule
    }
}
