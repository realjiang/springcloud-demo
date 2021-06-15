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
        
- Hystrix 断路器 弃车保帅~
    
    - 官网
    
    https://github.com/Netflix/Hystrix/wiki
    
    - 服务雪崩
        
        ​ 多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其他的微服务，
         这就是所谓的“扇出”，如果扇出的链路上某个微服务的调用响应时间过长，或者不可用，对微服务A的调用就
         会占用越来越多的系统资源，进而引起系统崩溃，所谓的“雪崩效应”。

    - 什么是Hystrix?
    
        用于处理分布式系统的延迟和容错,保证一个依赖出问题不会导致整体服务失败,避免级联故障,以提高分布式系统的弹性.
        
        "断路器"本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控 (类似熔断保险丝) ，向调用方返回一个服务预期的，
        可处理的备选响应 (FallBack) ，而不是长时间的等待或者抛出调用方法无法处理的异常，这样就可以保证了服务调用方的线程不会被长时间
        不必要的占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。
        
    - Hystrix能做什么?
    
        - 服务降级
        - 服务熔断
        - 服务限流
        - 接近实时的监控
        - …        
    
    - 服务熔断(服务端)
    
        - 导入依赖
        
        `<dependency>
             <groupId>org.springframework.cloud</groupId>
             <artifactId>spring-cloud-starter-hystrix</artifactId>
             <version>1.4.6.RELEASE</version>
         </dependency>`
        
        - 配置yml
          
        - 编写服务接口及熔断机制
          
          `@HystrixCommand(fallbackMethod = "hystrixGet")`
           如果根据id查询出现异常,则走hystrixGet这段备选代码
            
        - 启动类增加注解启动hystrix
        
          `@EnableCircuitBreaker // 添加对熔断的支持注解`
                
    - 服务降级(客户端)
        
        - 什么是服务降级?
        
          ​ 服务降级是指 当服务器压力剧增的情况下，根据实际业务情况及流量，对一些服务和页面有策略的不处理，
          或换种简单的方式处理，从而释放服务器资源以保证核心业务正常运作或高效运作。说白了，就是尽可能的
          把系统资源让给优先级高的服务。
        
        - 服务降级需要考虑的问题
        
          1）那些服务是核心服务，哪些服务是非核心服务
          
          2）那些服务可以支持降级，那些服务不能支持降级，降级策略是什么
          
          3）除服务降级之外是否存在更复杂的业务放通场景，策略是什么？
        
        - 自动降级分类
          1）超时降级：主要配置好超时时间和超时重试次数和机制，并使用异步机制探测回复情况
          
          2）失败次数降级：主要是一些不稳定的api，当失败调用次数达到一定阀值自动降级，同样要
          使用异步机制探测回复情况
          
          3）故障降级：比如要调用的远程服务挂掉了（网络故障、DNS故障、http服务返回错误的状态码、
          rpc服务抛出异常），则可以直接降级。降级后的处理方案有：默认值（比如库存服务挂了，返回
          默认现货）、兜底数据（比如广告挂了，返回提前准备好的一些静态页面）、缓存（之前暂存的一些缓存数据）
          
          4）限流降级：秒杀或者抢购一些限购商品时，此时可能会因为访问量太大而导致系统崩溃，此时会
          使用限流来进行限制访问量，当达到限流阀值，后续请求会被降级；降级后的处理方案可以是：排队页面（
          将用户导流到排队页面等一会重试）、无货（直接告知用户没货了）、错误页（如活动太火爆了，稍后重试）。
          
        - 基本使用案例
        
            - 客户端编写降级配置类
            
                在springcloud-api模块下的service包中新建降级配置类DeptClientServiceFallBackFactory.java
            
            - 在DeptClientService中指定降级配置类DeptClientServiceFallBackFactory
            
                在注解中指定回调方法,一个用fallback指定,多个用fallbackFactory指定
                
                `@FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT",fallbackFactory = DeptClientServiceFallBackFactory.class)//fallbackFactory指定降级配置类`
                
            - 在springcloud-consumer-dept-feign模块yml开启服务降级
                
                 `# 开启降级feign.hystrix
                  feign:
                    hystrix:
                      enabled: true`
            
        - 熔断和降级的区别
            
            > 服务熔断—>服务端：某个服务超时或异常，引起熔断，类似于保险丝(自我熔断)
              服务降级—>客户端：从整体网站请求负载考虑，当某个服务熔断或者关闭之后，服务将不再被调用，此时在客户端，
                       我们可以准备一个 FallBackFactory ，返回一个默认的值(缺省值)。会导致整体的服务下降，但是
                       好歹能用，比直接挂掉强。
              触发原因不太一样:
                       服务熔断一般是某个服务（下游服务）故障引起，而服务降级一般是从整体负荷考虑；
                       管理目标的层次不太一样，熔断其实是一个框架级的处理，每个微服务都需要（无层级之分），
                       而降级一般需要对业务有层级之分（比如降级一般是从最外围服务开始）
              实现方式不太一样，服务降级具有代码侵入性(由控制器完成/或自动降级)，熔断一般称为自我熔断。
        - 熔断，降级，限流
          
          限流：限制并发的请求访问量，超过阈值则拒绝；
          
          降级：服务分优先级，牺牲非核心服务（不可用），保证核心服务稳定；从整体负荷考虑；
          
          熔断：依赖的下游服务故障触发熔断，避免引发本系统崩溃；系统自动执行和恢复      
            
        - Dashboard 流监控
          - 新建模块,编写监控服务
              - 添加依赖
              - 配置yml
              - 编写启动类,注解开启Dashboard
          - 提供者模块配置监控
              - 需要actuator监控依赖 + Hystrix依赖
              - 启动类注入Servlet流
          - 监控页面访问地址: http://localhost:9001/hystrix  
            ![dashboard页面](https://gitee.com/superjishere/images/raw/master/img/20210615154652.png)

            ![监控信息](https://gitee.com/superjishere/images/raw/master/img/20210615154924.png)

- Zuul 路由网关
    - 什么是Zuul
        > Zull包含了对请求的路由(用来跳转的)和过滤两个最主要功能：
          其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础，
          而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验，服务聚合等功能的基础。
          Zuul和Eureka进行整合，将Zuul自身注册为Eureka服务治理下的应用，同时从Eureka中
          获得其他服务的消息，也即以后的访问微服务都是通过Zuul跳转后获得。
        ![Zuul](https://gitee.com/superjishere/images/raw/master/img/20210615164453.png)
        > 注意：Zuul 服务最终还是会注册进 Eureka.提供：代理 + 路由 + 过滤 三大功能！                    
          ​ 
    - 基本使用案例
        - 添加相关依赖 Zuul + eureka
        - 编写yml
        - 启动类添加启动Zuul注解
    - 参考网址
        https://www.springcloud.cc/spring-cloud-greenwich.html#_router_and_filter_zuul
        
- Spring Cloud Config 分布式配置
    - 什么是Spring Cloud Config?
    >  spring cloud config 为微服务架构中的微服务提供集中化的外部支持，配置服务器为各个不同微服务应用的所有环节提供了一个中心化的外部配置。
       spring cloud config 分为服务端和客户端两部分。
       服务端也称为 分布式配置中心，它是一个独立的微服务应用，用来连接配置服务器并为客户端提供获取配置信息，加密，解密信息等访问接口。
       客户端则是通过指定的配置中心来管理应用资源，以及与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息。配置服务器默认
       采用git来存储配置信息，这样就有助于对环境配置进行版本管理。并且可用通过git客户端工具来方便的管理和访问配置内容
      
    - spring cloud config 分布式配置中心能干嘛？
      
    >  集中式管理配置文件
       不同环境，不同配置，动态化的配置更新，分环境部署，比如 /dev /test /prod /beta /release
       运行期间动态调整配置，不再需要在每个服务部署的机器上编写配置文件，服务会向配置中心统一拉取配置自己的信息
       当配置发生变动时，服务不需要重启，即可感知到配置的变化，并应用新的配置
       将配置信息以REST接口的形式暴露
      
    - spring cloud config 分布式配置中心与GitHub整合
      
    >  ​ 由于spring cloud config 默认使用git来存储配置文件 (也有其他方式，比如自持SVN 和本地文件)，
        但是最推荐的还是git ，而且使用的是 http / https 访问的形式。
    ![spring_cloud_config](https://gitee.com/superjishere/images/raw/master/img/20210615175609.png)  
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
        - 基础使用案例
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
        需要创建服务端(连接config的git远程仓库)和客户端.
        
        1. 服务端 springcloud-config-server-3344
        
        定位资源的默认策略是克隆一个git仓库（在spring.cloud.config.server.git.uri），
        并使用它来初始化一个迷你SpringApplication。小应用程序的Environment用于枚举属性源并通过JSON端点发布。
        
        HTTP服务具有以下格式的资源：
        
        `/{application}/{profile}[/{label}]
         /{application}-{profile}.yml
         /{label}/{application}-{profile}.yml
         /{application}-{profile}.properties
         /{label}/{application}-{profile}.properties`
        
        其中“应用程序”作为SpringApplication中的spring.config.name注入（即常规的Spring Boot应用程序中通常是“应用程序”），
        “配置文件”是活动配置文件（或逗号分隔列表的属性），“label”是可选的git分支（默认为“master”）。
        
        测试访问 http://localhost:3344/application-dev.yml
        
        测试访问 http://localhost:3344/application/test/master
        
        测试访问不存在的配置则不显示 如：http://localhost:3344/master/application-aaa.yml
        
        2. 客户端 springcloud-config-client-3355
          
          启动服务端Config_server_3344 再启动客户端ConfigClient
          
          访问：http://localhost:8201/config/             
        
        3. 案例 springcloud-config-eureka-7001 、springcloud-config-dept-8001
        
        
       
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          