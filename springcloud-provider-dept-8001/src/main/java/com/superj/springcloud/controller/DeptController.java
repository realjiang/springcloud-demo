package com.superj.springcloud.controller;

import com.superj.springcloud.pojo.Dept;
import com.superj.springcloud.service.DeptService;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {
    @Resource
    private DeptService deptService;
    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping("/add")
    public boolean addDept(@RequestBody Dept dept) {
        return deptService.addDept(dept);
    }

    @GetMapping("/queryById/{id}")
    public Dept addDept(@PathVariable("id") Long id) {
        return deptService.queryById(id);
    }

    @GetMapping("/queryAll")
    public List<Dept> addDept() {
        return deptService.queryAll();
    }

    @GetMapping("/discovery")
    public Object discovery() {
        //获取微服务列表的清单
        List<String> services = discoveryClient.getServices();
        System.out.println("discovery=>services"+services);
        //得到一个具体的微服务信息,通过微服务id,applicationName
        List<ServiceInstance> instances = discoveryClient.getInstances("SPRINGCLOUD-PROVIDER-DEPT");
        for (ServiceInstance instance : instances) {
            System.out.println(
                    instance.getHost()+"\t"+
                    instance.getPort()+"\t"+
                    instance.getUri()+"\t"+
                    instance.getServiceId()
            );
        }

        return this.discoveryClient;
    }
}
