# 

## 框架信息
1. 基础框架  
    * Spring Cloud Hoxton.SR3
    * Spring Cloud Alibaba 2.2.1.RELEASE
    * Spring Boot 2.2.5.RELEASE
    * springcloud gateway(服务网关)
    * Auth 2.0（服务鉴权）
    * sentinel（流控、熔断、降级）
    * Nacos（服务发现、配置管理）
2. 关系型数据库
    * mysql 5.7（8.0）
    * Oracle（11g\12c）
    * Mycat插件（分库分表使用）
3. 非关系型数据库
    * redis 6.0
    * MongoDB 4.4.0
4. 分布式事务框架：Seata（AT模式）
5. 分布式日志框架：ELK（EFK）
6. 分布式任务调度：XXL-Job 7.30 版本 v2.2.0
7. ORM框架：mybatis 3.5.4（插件建议选择通用mapper）
8. 运维部署
    * 容器化部署 docker
    * 服务编排部署 K8S
    * 持续集成部署 Jenkins
9. 其他   
    * ES搜索框架：Elasticsearch 7.7
    * 流程引擎：Activity
## 项目目录结构介绍


## 常用配置参考


## 代码规范
### 注释规范
1. 【强制】类、类属性、类方法的注释必须使用Javadoc规范，使用/**内容*/格式，不得使用 // xxx 方式。
> 说明：在 IDE 编辑窗口中，Javadoc 方式会提示相关注释，生成 Javadoc 可以正确输出相应注释;在 IDE 中，工程调用方法时，不进入方法即可悬浮提示方法、参数、返回值的意义，提高阅读效率。
2. 【强制】所有的抽象方法(包括接口中的方法)必须要用Javadoc注释、除了返回值、参数、 异常说明外，还必须指出该方法做什么事情，实现什么功能。
> 说明：对子类的实现要求，或者调用注意事项，请一并说明。
3. 【强制】所有的类都必须添加创建者和创建日期。
> 说明：在设置模板时，注意 IDEA 的@author为`${USER}`，而 eclipse 的 @author 为`${user}`,大小写有区别，而日期的统一设置为yyyy/MM/dd的格式，示例：
4. 【强制】方法内部单行注释，在被注释语句上方另起一行，使用//注释。方法内部多行注释使 用/* */注释，注意与代码对齐。
5. 【强制】所有的枚举类型字段必须要有注释，说明每个数据项的用途。
6. 【推荐】代码修改的同时，注释也要进行相应的修改，尤其是参数、返回值、异常、核心逻辑 等的修改。
> 说明:代码与注释更新不同步，就像路网与导航软件更新不同步一样，如果导航软件严重滞后，就失去了 导航的意义。
### 日志规范
1. 【强制】日志框架使用 SLF4J+Logback，记录日志使用SLF4J，建议配合 lombok 注解**@Slf4j**使用。
2. 【强制】在日志输出时，字符串变量之间的拼接使用占位符的方式。
> 因为 String 字符串的拼接会使用 StringBuilder 的 append()方式，有一定的性能损耗。使用占位符仅 是替换动作，可以有效提升性能。 
> 正例:`logger.debug("Processing trade with id: {} and symbol: {}", id, symbol)`;
3. 【强制】生产环境禁止直接使用 System.out 或 System.err 输出日志或使用 e.printStackTrace()打印异常堆栈。
> 说明:标准日志输出与标准错误输出文件每次 Jboss 重启时才滚动，如果大量输出送往这两个文件，容易 造成文件大小超过操作系统大小限制。
4. 【强制】所有日志文件至少保存15天，因为有些异常具备以“周”为频次发生的特点。对于 当天日志，以“应用名.log”来保存，保存在/home/admin/应用名/logs/</font>目录下， 过往日志格式为: {logname}.log.{保存日期}，日期格式:yyyy-MM-dd
> 说明:以 mppserver 应用为例，日志保存在/home/admin/mppserver/logs/mppserver.log，历史日志 名称为 mppserver.log.2016-08-01
5. 【强制】应用中的扩展日志(如打点、临时监控、访问日志等)命名方式: appName_logType_logName.log。logType:日志类型，如 stats/monitor/access 等;logName:日志描述。这种命名的好处:通过文件名就可知道日志文件属于什么应用，什么类型，什么目的，也有利于归类查找。
6. 【强制】日志打印时禁止直接用JSON工具将对象转换成String。
7. 【推荐】谨慎地记录日志。生产环境禁止输出 debug 日志;有选择地输出 info 日志;如果使用 warn 来记录刚上线时的业务行为信息，一定要注意日志输出量的问题，避免把服务器磁盘撑 爆，并记得及时删除这些观察日志。
8. 【强制】异常信息应该包括两类信息:案发现场信息和异常堆栈信息。如果不处理，那么通过 关键字 throws 往上抛出。
> 正例:`logger.error(各类参数或者对象 toString() + "_" + e.getMessage(), e)`;