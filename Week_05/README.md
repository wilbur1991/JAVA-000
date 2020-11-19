[TOC]


完成如下必做题目：

### 2（必做）写代码实现Spring Bean的装配，方式越多越好（XML、Annotation都可以）,提交到Github。

a XML装配 student123，class1，school

b @Bean装配  studentUsingBean

c 自动装配 ComponentDemo

详见项目spring01  

### 4（必做）给前面课程提供的Student/Klass/School 实现自动配置和Starter。

a 自动配置详见springbootDemo项目的wilbur.demo.springboot.beans

b 实现Starter，详见demo-starter项目，通过该作业熟悉了创建spring-boot-starter的流程及规范。
  demo-spring-boot-autoconfigue模块包括StudentProperties(设置student相关属性)，StudentAutoConfiguration（自动配置，使用@ConditionOnClass和@ConditionalOnMissingBean）；
  demo-spring-boot-starter模块依赖autoconfigure模块，无实质代码或配置；
  demo-spring-boot-samples模块为starter使用示例，直接引用demo-spring-boot-starter模块。


### 6（必做）研究一下JDBC 接口和数据库连接池，掌握它们的设计和用法：

1）使用JDBC 原生接口，实现数据库的增删改查操作。

见wilbur.demo.springboot.database.JDBCDemo.java

2）使用事务，PrepareStatement 方式，批处理方式，改进上述操作。	

见wilbur.demo.springboot.database.JDBCTransactionDemo.java

3）配置Hikari 连接池，改进上述操作。提交代码到Github。

见wilbur.demo.springboot.database.HikariCPDemo.java
