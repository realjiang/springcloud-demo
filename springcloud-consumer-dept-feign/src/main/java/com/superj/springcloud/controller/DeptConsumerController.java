package com.superj.springcloud.controller;

import com.superj.springcloud.pojo.Dept;
import com.superj.springcloud.service.DeptClientService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/consumer")
public class DeptConsumerController {
    //理解:消费者,不应该有service层
    //RestTemplate 供我们直接调用远程方法.提供了多种便捷访问远程http服务的方法,是一个简单的restful服务模板
    @Resource
    private RestTemplate restTemplate;

    @Resource
    private DeptClientService service = null;

    @RequestMapping("/dept/add")
    public boolean add(@RequestBody Dept dept) {
        return this.service.addDept(dept);
    }


    // 回顾:rpc 要通过@Reference 引用远程服务的service来调用, 这里直接通过url就可以调用其他服务的方法了
    @RequestMapping("/dept/get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return this.service.queryById(id);
    }

    @RequestMapping("/dept/list")
    public List<Dept> list() {
        return this.service.queryAll();
    }

}
