# springcloud-demo
springcloud-netfix 练习demo

## 模块列表
- api - 实体类模块demo
    - 只存放实体类
- provider - 服务提供者demo

    **依赖api模块**
    
    - dao层
    - service层
    - controller层(提供restful服务)
    - 主启动类
- consumer - 服务消费者demo

    **依赖api模块**
    
    - springboot的web启动器
    - 主启动类
    
    测试:启动provider,再启动consumer,访问consumer的接口,如果能成功拿到数据表示成功!
- Eureka - 服务注册与发现
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
    3. eureka集群环境配置
       搭建一个三个注册中心的集群,防止一个注册中心挂了就全完了
       - 修改注册中心yml
       
       7001 绑定 7002/7003        
       7002 绑定 7001/7003        
       7003 绑定 7002/7001
       - provider配置yml 注册中心地址为集群所有地址
       
       一个节点挂了,其他的注册中心还可以继续使用
    4. 对比zookeeper
        
        RDBMS (MySQL\Oracle\sqlServer) ===> ACID
        NoSQL (Redis\MongoDB) ===> CAP
        
        - ACID
           - A (Atomicity) 原子性
           - C (Consistency) 一致性
           - I (Isolation) 隔离性
           - D (Durability) 持久性
        
        - CAP原则
           - C (Consistency) 强一致性
           - A (Availability) 可用性
           - P (Partition tolerance) 分区容错性
           一个分布式系统中只能三选二:CA、AP、CP
        - CAP理论核心
        
           著名的CAP理论指出，一个分布式系统不可能同时满足C (一致性) 、A (可用性) 、P (容错性)，
           由于分区容错性P再分布式系统中是必须要保证的，因此我们只能再A和C之间进行权衡。
           
           - Zookeeper 保证的是 CP —> 满足一致性，分区容错的系统，通常性能不是特别高
             > zookeeper会出现这样一种情况，当master节点因为网络故障与其他节点失去联系时，
               剩余节点会重新进行leader选举。问题在于，选举leader的时间太长，30-120s，且
               选举期间整个zookeeper集群是不可用的，这就导致在选举期间注册服务瘫痪。
               在云部署的环境下，因为网络问题使得zookeeper集群失去master节点是较大概率发生的事件，
               虽然服务最终能够恢复，但是，漫长的选举时间导致注册长期不可用，是不可容忍的。
                
           - Eureka 保证的是 AP —> 满足可用性，分区容错的系统，通常可能对一致性要求低一些
             > Eureka各个节点都是平等的，几个节点挂掉不会影响正常节点的工作，剩余的节点依然可以提供
               注册和查询服务。而Eureka的客户端在向某个Eureka注册时，如果发现连接失败，则会自动切换
               至其他节点，只要有一台Eureka还在，就能保住注册服务的可用性，只不过查到的信息可能不是最
               新的，除此之外，Eureka还有之中自我保护机制，如果在15分钟内超过85%的节点都没有正常的心跳，
               那么Eureka就认为客户端与注册中心出现了网络故障，此时会出现以下几种情况：
               
               >     - Eureka不在从注册列表中移除因为长时间没收到心跳而应该过期的服务
               >     - Eureka仍然能够接受新服务的注册和查询请求，但是不会被同步到其他节点上 (即保证当前节点依然可用)
               >     - 当网络稳定时，当前实例新的注册信息会被同步到其他节点中
             因此，Eureka可以很好的应对因网络故障导致部分节点失去联系的情况，而不会像zookeeper那样使整个注册服务瘫痪
             
            
- Ribbon 负载均衡

    > 负载均衡: 将用户的请求平摊的分配到多个服务器上,从而达到系统的HA(高可用)
    
    - 常见的负载均衡软件
        - nginx
        - lvs - linus虚拟服务器
        - apache+tomcat
        - ...
        
    - 负载均衡简单分类
        - 集中式LB - 如nginx
            
            消费方和服务方之间使用独立的LB设施,由该设施通过某种策略转发请求
            
        - 进程式LB - 如Ribbon
        
            消费方集成LB逻辑,消费方从注册中心获取哪些地址可用,自己从中选择合适的地址
            
    - 集成Ribbon
    
        1. 消费方添加ribbon和eureka客户端依赖
        2. 消费方开启@EnableEurekaClient
        3. 消费方yml配置eureka
        4. 消费方restTemplate配置
        
        `@Bean
             @LoadBalanced //配置赋值均衡实现RestTemplate
             public RestTemplate getRestTemplate() {
                 return new RestTemplate();
             }`
        
        5. 消费方修改提供方url
        
        `//通过ribbon负载均衡的时候,这里的url应该是一个变量,通过服务名来访问
         //    private static final String REST_URL_PREFIX = "http://localhost:8001";
             private static final String REST_URL_PREFIX = "http://SPRINGCLOUD-PROVIDER-DEPT";`
         
         6. 测试
         
         准备:三个数据库(数据一致),三个提供者(服务名一致,只是连接的库不同)
         
    - ribbon工作流程
    
    ![ribbon](https://gitee.com/superjishere/images/raw/master/img/20210614234030.png)
    
    - 自定义ribbon负载均衡算法
    
      默认是轮询         
      
      - IRule 接口
        - RoundRobinRule 轮询 (默认实现)
        - RandomRule 随机
        - AvailabilityFilterRule 会先过滤掉 跳闸/访问故障 的服务,对剩下的服务进行轮询
        - RetryRule 先按照轮询获取服务,如果服务获取失败,则会在指定时间内重试
        - ...   
      - 自定义算法
        1. 启动类增加注解,指定rule配置类
        
        `//在微服务启动时自动加载自定义的SuperjRule配置类,自定义的会覆盖原来的
         @RibbonClient(name = "SPRINGCLOUD-PROVIDER-DEPT",configuration = SuperjRule.class)`  
        
        2. 编写自己的rule类,以及rule配置类
        
        `//注意:不能和启动类放同级目录,会被扫描到,被所有ribbon客户端共享,不能
         @Configuration
         public class SuperjRule {
             @Bean
             public IRule myrule() {
                 return new MyRandomRule();//自定义rule
             }
         }`

- Feign 负载均衡

    优雅简单地实现服务调用
    
    - 和ribbon区别
        - ribbon - 通过微服务名字
        - feign - 通过接口+注解,集成了ribbon,可读性高了,但因为加了一层,性能变低了      
        
    - 使用步骤
    
        1. 消费方和api添加feign依赖
        
        `<dependency>
                     <groupId>org.springframework.cloud</groupId>
                     <artifactId>spring-cloud-starter-feign</artifactId>
                     <version>1.4.6.RELEASE</version>
                 </dependency>`
        2. api中编写接口
        
        `com.superj.springcloud.service.DeptClientService`
        
        3. 消费方启动类增加注解
        
        `@EnableFeignClients(basePackages = {"com.superj.springcloud"})`
        
        