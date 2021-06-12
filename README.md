# springcloud-demo
springcloud-netfix 练习demo

## 模块列表
- api 实体类模块demo
    - 只存放实体类
- provider 服务提供者demo
    **依赖api模块**
    - dao层
    - service层
    - controller层(提供restful服务)
    - 主启动类
- consumer 服务消费者demo
    **依赖api模块**
    - springboot的web启动器
    - 主启动类
    测试:启动provider,再启动consumer,访问consumer的接口,如果能成功拿到数据表示成功!
- Eureka 服务注册与发现
    1. eureka服务端
    - pom导入依赖
    `<dependency>
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-starter-eureka-server</artifactId>
         <version>1.4.6.RELEASE</version>
     </dependency>`
    - 配置文件(yml)中配置
    - 启动类开启功能 @EnableEurekaServer
    访问控台地址:http://localhost:7001/
    2. provider配置eureka
    实现将服务提供者provider注册到注册中心
    - pom添加eureka依赖
    `<dependency>
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-starter-eureka</artifactId>
         <version>1.4.6.RELEASE</version>
     </dependency>`
    - yml中配置eureka
    - 启动类开启功能 @EnableEurekaClient
    - 警告:eureka自我保护机制 - 应对网络异常 
      ![eureka警告](https://gitee.com/superjishere/images/raw/master/img/20210612180459.png)
      有时网络不好,eureka没有即使收到某服务的心跳,会认为那个服务挂了(实际上没有挂,此时不应将该服务关闭),
      但是该服务的注册信息还保存着,等那个服务重新注册到eureka后,eureka节点会自动退出自我保护模式.
      '好死不如赖活着',宁可保留所有微服务也不盲目注销任何健康的微服务.
      `eureka.server.enable-self-preservation = false` 禁用自我保护模式.**不推荐!!!**
    - 补充:
        - 完善监控信息 - actuator
          - pom添加actuator依赖
          `<dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-actuator</artifactId>
           </dependency>`
           - yml中配置
           `# info配置,控台status的链接,展示的内容,如果不配置,点击链接报404
            info:
              app.name: superj-springcloud
              company.name: www.superj.com`
           - 点击控台status的链接
           ![配置info](https://gitee.com/superjishere/images/raw/master/img/20210612183449.png)
        - 通过DiscoveryCilent 从注册中心获取一些信息
           注意包:org.springframework.cloud.client.discovery.DiscoveryClient
           接口例子:见discovery
           还需要启动类增加注解:@EnableDiscoveryClient
           测试接口:http://localhost:8001/dept/discovery
           页面:
           ![discovery页面响应](https://gitee.com/superjishere/images/raw/master/img/20210612190121.png)
           控台:windows主机是显示localhost
           ![discovery控台打印内容](https://gitee.com/superjishere/images/raw/master/img/20210612190220.png)
           