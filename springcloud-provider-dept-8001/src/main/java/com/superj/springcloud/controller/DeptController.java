package com.superj.springcloud.controller;

import com.superj.springcloud.pojo.Dept;
import com.superj.springcloud.service.DeptService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController {
    @Resource
    private DeptService deptService;

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
}
