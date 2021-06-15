package com.superj.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

// 开启Dashboard
@EnableHystrixDashboard
@SpringBootApplication
public class DeptConsumer_hystrix_dashboard {
    public static void main(String[] args) {
        SpringApplication.run(DeptConsumer_hystrix_dashboard.class, args);
    }
}
