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
                                                 >
             因此，Eureka可以很好的应对因网络故障导致部分节点失去联系的情况，而不会像zookeeper那样使整个注册服务瘫痪    
