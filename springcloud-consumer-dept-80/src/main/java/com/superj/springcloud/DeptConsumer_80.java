package com.superj.springcloud;

import com.superj.myrule.SuperjRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

//在微服务启动时自动加载自定义的SuperjRule配置类,自定义的会覆盖原来的
@RibbonClient(name = "SPRINGCLOUD-PROVIDER-DEPT",configuration = SuperjRule.class)
//ribbon和eureka整合后,客户端可以直接调用,不用关心具体的ip和端口号
@EnableEurekaClient
@SpringBootApplication
public class DeptConsumer_80 {
    public static void main(String[] args) {
        SpringApplication.run(DeptConsumer_80.class, args);
    }
}
