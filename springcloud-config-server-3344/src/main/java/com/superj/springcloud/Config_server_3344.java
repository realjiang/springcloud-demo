package com.superj.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer // 开启spring cloud config server服务
@SpringBootApplication
public class Config_server_3344 {
    public static void main(String[] args) {
        SpringApplication.run(Config_server_3344.class,args);
    }
}
