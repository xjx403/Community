# Community
基于SpringBoot的社区讨论系统
附相关技术分析：

# 一、MyBatisGenerator 详解：

主要有Generator.java , CommentGenerator.java 和 generatorConfig.xml三部分组成

- `generatorConfig.xml`

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!DOCTYPE generatorConfiguration
          PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
          "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
  
  <generatorConfiguration>
      <properties resource="generator.properties"/>
      <context id="MySqlContext" targetRuntime="MyBatis3" defaultModelType="flat">
          <property name="beginningDelimiter" value="`"/>
          <property name="endingDelimiter" value="`"/>
          <property name="javaFileEncoding" value="UTF-8"/>
          <!-- 为模型生成序列化方法-->
          <plugin type="org.mybatis.generator.plugins.SerializablePlugin"/>
          <!-- 为生成的Java模型创建一个toString方法 -->
          <plugin type="org.mybatis.generator.plugins.ToStringPlugin"/>
          <!--生成mapper.xml时覆盖原文件-->
          <plugin type="org.mybatis.generator.plugins.UnmergeableXmlMappersPlugin" />
          <!--可以自定义生成model的代码注释-->
          <commentGenerator type="com.groups.mymall.mbg.CommentGenerator">
              <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
              <property name="suppressAllComments" value="true"/>
              <property name="suppressDate" value="true"/>
              <property name="addRemarkComments" value="true"/>
          </commentGenerator>
          <!--配置数据库连接-->
          <jdbcConnection driverClass="${jdbc.driverClass}"
                          connectionURL="${jdbc.connectionURL}"
                          userId="${jdbc.userId}"
                          password="${jdbc.password}">
              <!--解决mysql驱动升级到8.0后不生成指定数据库代码的问题-->
              <property name="nullCatalogMeansCurrent" value="true" />
          </jdbcConnection>
          <!--指定生成model的路径-->
          <javaModelGenerator targetPackage="com.groups.mymall.mbg.model" targetProject="mymall\src\main\java"/>
          <!--指定生成mapper.xml的路径-->
          <sqlMapGenerator targetPackage="com.groups.mymall.mbg.mapper" targetProject="mymall\src\main\resources"/>
          <!--指定生成mapper接口的的路径-->
          <javaClientGenerator type="XMLMAPPER" targetPackage="com.groups.mymall.mbg.mapper"
                               targetProject="mymall\src\main\java"/>
          <!--生成全部表tableName设为%-->
          <table tableName="pms_brand">
              <generatedKey column="id" sqlStatement="MySql" identity="true"/>
          </table>
          <table tableName="ums_admin">
              <generatedKey column="id" sqlStatement="MySql" identity="true"/>
          </table>
      </context>
  </generatorConfiguration>
  ```

- Generator.java

  ```java
  package com.groups.mymall.mbg;
  
  import org.mybatis.generator.api.MyBatisGenerator;
  import org.mybatis.generator.config.Configuration;
  import org.mybatis.generator.config.xml.ConfigurationParser;
  import org.mybatis.generator.internal.DefaultShellCallback;
  
  import java.io.InputStream;
  import java.util.ArrayList;
  import java.util.List;
  /**
   * 用于生产MBG的代码
   * Created by macro on 2018/4/26.
   */
  public class Generator {
      public static void main(String[] args) throws Exception {
          //MBG 执行过程中的警告信息
          List<String> warnings = new ArrayList<String>();
          //当生成的代码重复时，覆盖原代码
          boolean overwrite = true;
          //读取我们的 MBG 配置文件
          InputStream is = Generator.class.getResourceAsStream("/generatorConfig.xml");
          ConfigurationParser cp = new ConfigurationParser(warnings);
          Configuration config = cp.parseConfiguration(is);
          is.close();
  
          DefaultShellCallback callback = new DefaultShellCallback(overwrite);
          //创建 MBG
          MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
          //执行生成代码
          myBatisGenerator.generate(null);
          //输出警告信息
          for (String warning : warnings) {
              System.out.println(warning);
          }
      }
  }
  ```

- `CommentGenerator.java`

  ```java
  package com.groups.mymall.mbg;
  
  import org.mybatis.generator.api.IntrospectedColumn;
  import org.mybatis.generator.api.IntrospectedTable;
  import org.mybatis.generator.api.dom.java.CompilationUnit;
  import org.mybatis.generator.api.dom.java.Field;
  import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
  import org.mybatis.generator.internal.DefaultCommentGenerator;
  import org.mybatis.generator.internal.util.StringUtility;
  
  import java.util.Properties;
  
  /**
   * 自定义注释生成器
   * Created by macro on 2018/4/26.
   */
  public class CommentGenerator extends DefaultCommentGenerator {
      private boolean addRemarkComments = false;
      private static final String EXAMPLE_SUFFIX="Example";
      private static final String MAPPER_SUFFIX="Mapper";
      private static final String API_MODEL_PROPERTY_FULL_CLASS_NAME="io.swagger.annotations.ApiModelProperty";
  
      /**
       * 设置用户配置的参数
       */
      @Override
      public void addConfigurationProperties(Properties properties) {
          super.addConfigurationProperties(properties);
          this.addRemarkComments = StringUtility.isTrue(properties.getProperty("addRemarkComments"));
      }
  
      /**
       * 给字段添加注释
       */
      @Override
      public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                  IntrospectedColumn introspectedColumn) {
          String remarks = introspectedColumn.getRemarks();
          //根据参数和备注信息判断是否添加swagger注解信息
          if(addRemarkComments&&StringUtility.stringHasValue(remarks)){
  //            addFieldJavaDoc(field, remarks);
              //数据库中特殊字符需要转义
              if(remarks.contains("\"")){
                  remarks = remarks.replace("\"","'");
              }
              //给model的字段添加swagger注解
              field.addJavaDocLine("@ApiModelProperty(value = \""+remarks+"\")");
          }
      }
  
      /**
       * 给model的字段添加注释
       */
      private void addFieldJavaDoc(Field field, String remarks) {
          //文档注释开始
          field.addJavaDocLine("/**");
          //获取数据库字段的备注信息
          String[] remarkLines = remarks.split(System.getProperty("line.separator"));
          for(String remarkLine:remarkLines){
              field.addJavaDocLine(" * "+remarkLine);
          }
          addJavadocTag(field, false);
          field.addJavaDocLine(" */");
      }
  
      @Override
      public void addJavaFileComment(CompilationUnit compilationUnit) {
          super.addJavaFileComment(compilationUnit);
          //只在model中添加swagger注解类的导入
          if(!compilationUnit.getType().getFullyQualifiedName().contains(MAPPER_SUFFIX)&&!compilationUnit.getType().getFullyQualifiedName().contains(EXAMPLE_SUFFIX)){
              compilationUnit.addImportedType(new FullyQualifiedJavaType(API_MODEL_PROPERTY_FULL_CLASS_NAME));
          }
      }
  }
  
  ```

## 踩坑集

- xml篇

  ```xml
  <!--生成全部表tableName设为%-->
  <table tableName="pms_brand">
      <generatedKey column="id" sqlStatement="MySql" identity="true"/>
  </table>
  <table tableName="ums_admin">
      <generatedKey column="id" sqlStatement="MySql" identity="true"/>
  </table>
  ```

  上段代码为指定为哪些表生成对于的model、mapper、mapper.xml。**在多次运行时，会覆盖Java的文件，**但是不会覆盖.xml文件**，所以会存在，xml有多份内容，导致和mapper对应不起来。

  > 解决办法：
  >
  > 升级MyBatis生成器：
  >
  > ```xml
  > <!-- MyBatis 生成器 -->
  > <dependency>
  >  <groupId>org.mybatis.generator</groupId>
  >  <artifactId>mybatis-generator-core</artifactId>
  >  <version>1.3.7</version>
  > </dependency>
  > ```
  >
  > 在generatorconfig-xml文件中添加覆盖mapper-xml的插在generatorConfig.xml文件中添加覆盖mapper.xml的插件:
  >
  > ```xml
  > <!--生成mapper.xml时覆盖原文件-->
  > <plugin type="org.mybatis.generator.plugins.UnmergeableXmlMappersPlugin" />
  > ```

- mapper接口和Mapper.xml文件的对应

  在运行过程中会出现，调用mapper接口时，为空指针（NullPointException），则说明有可能mapper 接口和xml文件的对应出现问题。

  1. **配置类中class路径配置问题！！！**

     ```yml
     mybatis:
       mapper-locations:
         - classpath:mapper/*.xml
         - classpath:com/**/mapper/*.xml
     ```

     路径含义已学，可自查，故略。

  2. 记得加@Autowired

- Generator生成的接口类，默认**没有加@Mapper注释**，测试时使用**@Autowired idea标红**，但是在实际运行后仍然能够进行查询。

  是因为使用了MybatisConfig配置类配置了Mapper扫描路径。

# 二、Swagger-UI配置过程：

# SpringBoot配置Swagger流程及踩坑记录

## 一、依赖

```xml
 <!--swagger-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
        </dependency>     
```

## 二、配置类

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    
}
```

#### 常用注解

- @Api：用于修饰Controller类，生成Controller相关文档信息
- @ApiOperation：用于修饰Controller类中的方法，生成接口方法相关文档信息
- @ApiParam：用于修饰接口中的参数，生成接口参数相关文档信息
- @ApiModelProperty：用于修饰实体类的属性，当实体类是请求参数或返回结果时，直接生成相关文档信息





# 附：踩坑记录

## 1. 报错如下:

```java
Error starting ApplicationContext. To display the conditions report re-run your application with 'debug' enabled.
2023-03-07 10:59:12.849 ERROR 22312 --- [           main] o.s.boot.SpringApplication               : Application run failed

org.springframework.context.ApplicationContextException: Failed to start bean 'documentationPluginsBootstrapper'; nested exception is java.lang.NullPointerException
	at org.springframework.context.support.DefaultLifecycleProcessor.doStart(DefaultLifecycleProcessor.java:181) ~[spring-context-5.3.25.jar:5.3.25]
	at org.springframework.context.support.DefaultLifecycleProcessor.access$200(DefaultLifecycleProcessor.java:54) ~[spring-context-5.3.25.jar:5.3.25]
	at org.springframework.context.support.DefaultLifecycleProcessor$LifecycleGroup.start(DefaultLifecycleProcessor.java:356) ~[spring-context-5.3.25.jar:5.3.25]
	at java.lang.Iterable.forEach(Iterable.java:75) ~[na:1.8.0_281]
	at org.springframework.context.support.DefaultLifecycleProcessor.startBeans(DefaultLifecycleProcessor.java:155) ~[spring-context-5.3.25.jar:5.3.25]
	at org.springframework.context.support.DefaultLifecycleProcessor.onRefresh(DefaultLifecycleProcessor.java:123) ~[spring-context-5.3.25.jar:5.3.25]
	at org.springframework.context.support.AbstractApplicationContext.finishRefresh(AbstractApplicationContext.java:935) ~[spring-context-5.3.25.jar:5.3.25]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:586) ~[spring-context-5.3.25.jar:5.3.25]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:147) ~[spring-boot-2.7.9.jar:2.7.9]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:731) [spring-boot-2.7.9.jar:2.7.9]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:408) [spring-boot-2.7.9.jar:2.7.9]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:307) [spring-boot-2.7.9.jar:2.7.9]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1303) [spring-boot-2.7.9.jar:2.7.9]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1292) [spring-boot-2.7.9.jar:2.7.9]
	at com.example.snapsalesimulation.SnapSaleSimulationApplication.main(SnapSaleSimulationApplication.java:10) [classes/:na]
Caused by: java.lang.NullPointerException: null
	at springfox.documentation.spring.web.WebMvcPatternsRequestConditionWrapper.getPatterns(WebMvcPatternsRequestConditionWrapper.java:56) ~[springfox-spring-webmvc-3.0.0.jar:3.0.0]
	at springfox.documentation.RequestHandler.sortedPaths(RequestHandler.java:113) ~[springfox-core-3.0.0.jar:3.0.0]
	at springfox.documentation.spi.service.contexts.Orderings.lambda$byPatternsCondition$3(Orderings.java:89) ~[springfox-spi-3.0.0.jar:3.0.0]
	at java.util.Comparator.lambda$comparing$77a9974f$1(Comparator.java:469) ~[na:1.8.0_281]
	at java.util.TimSort.countRunAndMakeAscending(TimSort.java:355) ~[na:1.8.0_281]
	at java.util.TimSort.sort(TimSort.java:220) ~[na:1.8.0_281]
	at java.util.Arrays.sort(Arrays.java:1512) ~[na:1.8.0_281]
	at java.util.ArrayList.sort(ArrayList.java:1464) ~[na:1.8.0_281]
	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:387) ~[na:1.8.0_281]
	at java.util.stream.Sink$ChainedReference.end(Sink.java:258) ~[na:1.8.0_281]
	at java.util.stream.Sink$ChainedReference.end(Sink.java:258) ~[na:1.8.0_281]
	at java.util.stream.Sink$ChainedReference.end(Sink.java:258) ~[na:1.8.0_281]
	at java.util.stream.Sink$ChainedReference.end(Sink.java:258) ~[na:1.8.0_281]
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483) ~[na:1.8.0_281]
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472) ~[na:1.8.0_281]
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708) ~[na:1.8.0_281]
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[na:1.8.0_281]
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499) ~[na:1.8.0_281]
	at springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider.requestHandlers(WebMvcRequestHandlerProvider.java:81) ~[springfox-spring-webmvc-3.0.0.jar:3.0.0]
	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193) ~[na:1.8.0_281]
	at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1384) ~[na:1.8.0_281]
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482) ~[na:1.8.0_281]
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472) ~[na:1.8.0_281]
	at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708) ~[na:1.8.0_281]
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[na:1.8.0_281]
	at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499) ~[na:1.8.0_281]
	at springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper.withDefaults(AbstractDocumentationPluginsBootstrapper.java:107) ~[springfox-spring-web-3.0.0.jar:3.0.0]
	at springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper.buildContext(AbstractDocumentationPluginsBootstrapper.java:91) ~[springfox-spring-web-3.0.0.jar:3.0.0]
	at springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper.bootstrapDocumentationPlugins(AbstractDocumentationPluginsBootstrapper.java:82) ~[springfox-spring-web-3.0.0.jar:3.0.0]
	at springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper.start(DocumentationPluginsBootstrapper.java:100) ~[springfox-spring-web-3.0.0.jar:3.0.0]
	at org.springframework.context.support.DefaultLifecycleProcessor.doStart(DefaultLifecycleProcessor.java:178) ~[spring-context-5.3.25.jar:5.3.25]
	... 14 common frames omitted


Process finished with exit code 1

```

原因：

>https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes
>
>Swagger-ui 2020年之后就没有更新了。
>
>在SpringBoot2.6之后，Spring MVC 处理程序映射**匹配请求路径的默认策略**已从 **AntPathMatcher** 更改为**PathPatternParser**。如果需要切换为AntPathMatcher，官方给出的方法是配置spring.mvc.pathmatch.matching-strategy=ant_path_matcher
>
>但是actuator endpoints在2.6之后也使用基于 PathPattern 的 URL 匹配，而且actuator endpoints的路径匹配策略无法通过配置属性进行配置，如果同时使用Actuator和Springfox，会导致程序启动失败，所以只是进行上面的设置是不行的。

解决方案：修改依赖：

代替原来的依赖

```xml
<!--swagger-->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

以下代码用于将**PathPatternParser**更改为**AntPathMatcher**。

```java
package com.example.snapsalesimulation.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
                                                                         ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
                                                                         EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
                                                                         WebEndpointProperties webEndpointProperties, Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping =
                webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
                        || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
                corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
                shouldRegisterLinksMapping, null);
    }
}
```

# 三、SpringBoot

### @SpringBootConfiguration 注解，继承@Configuration注解，主要用于加载配置文件

### @ComponentScan 注解，主要用于组件扫描和自动装配

我们可以通过basePackages等属性指定@ComponentScan自动扫描的范围，如果不指定，则默认Spring框架实现从声明@ComponentScan所在类的package进行扫描，默认情况下是不指定的，所以SpringBoot的启动类最好放在root package下。

1. bean的注入和装配

   SpringBoot使用Java注解的方式进行bean的注入：

   @Controller:控制器 用于处理Http请求：

   ```java
   @Controller
   public class HomeController {
       @Autowired
       private DicussPostService dicussPostService;
       @Autowired
       private UserService userService;
   
       @RequestMapping(path = "/index",method = RequestMethod.GET)
       public String getIndexPage(Model model, Page page){
           //方法调用前，SpringMVC会自动实例化model和page,并将page注入到model里面
           //所以，在thymeleaf里可以直接访问Page对象中的数据
           page.setRows(dicussPostService.findDiscussPostRows(0));
   
           return "/index";
       }
   }
   ```

    - @RestController ==ResponseBody+Controller：将方法返回的对象之间在浏览器上展示成json格式。

    - @Autowired: 将Controller类方法中需要用到的Spring容器中的bean自动注入。

      注意：如果new 了一个和注入的类相同的类，将会自动注入null ！！；

    - @RequestMapping :设置浏览器请求路径（path) 以及具体响应那种方法(method == )

   @Service:是@Component的一个特例：作用在类上；用于标注服务层组件。@Service(value=”serviceBeanId”)使用时传参数，使用value作为Bean名字，无参时，使用类名作为bean的名字，首字母小写；

   @Repository:DAO层注解：

   @Component: 把普通的类实例化到JSpring容器中

# 四、Mybatis

基于Spring Boot使用Mybatis读取指定MySQL数据库

1. 导入MyBatis和MySQL依赖：

```xml
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.3.16</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.30</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.2</version>
        </dependency>
```

2. 配置MySQL数据库连接信息和Mybatis相关配置：

   ```properties
   #连接mysql的相关信息：
   spring.datasource.url=jdbc:mysql://localhost:3306/(your database name)
   spring.datasource.username=(your username)
   spring.datasource.password=(your password)
   #mysql驱动
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ```

   ```properties
   ###MyBatis###
   #mybatis maper映射文件路径 classpath(可以理解为resources文件),mapper为resources文件下创建的用于存放多个xml的文件夹。*代表扫描所有xml文件
   mybatis.mapper-locations=classpath:mapper/*.xml
   
   # 用于存储读取结果的实体类的文件相对路径
   mybatis.type-aliases-package=com.coder.community.entity
   
   #Mybatis 配置文件 useGeneratedKeys 参数只针对 insert 语句生效，默认为 false。当设置为 true 时，表示如果插入的表以自增列为主键，则允许 JDBC 支持自动生成主键，并可将自动生成的主键返回。
   mybatis.configuration.use-generated-keys=true
   
   #该配置项就是指将带有下划线的表字段映射为驼峰格式的实体类属性。
   mybatis.configuration.map-underscore-to-camel-case=true
   ```

3. 编写实体类存储SQL查询结果

   以MySQL中user表为例

   ```sql
    create table user{
    `id` int(11) NOT NULL AUTO_INCREMENT,
     `username` varchar(50) DEFAULT NULL,
     `password` varchar(50) DEFAULT NULL,
     `salt` varchar(50) DEFAULT NULL,
     `email` varchar(100) DEFAULT NULL,
     `type` int(11) DEFAULT NULL COMMENT '0-普通用户; 1-超级管理员; 2-版主;',
     `status` int(11) DEFAULT NULL COMMENT '0-未激活; 1-已激活;',
     `activation_code` varchar(100) DEFAULT NULL,
     `header_url` varchar(200) DEFAULT NULL,
     `create_time` timestamp NULL DEFAULT NULL,
     }
   ```



   ```java
   package com.coder.community.entity;
   
   import java.util.Date;
   
   /*
   *包含对应的数据，和get set toString方法。
   * */
   public class User {
       private int id;
       private String username;
       private String password;
       private String salt;
       private String email;
       //0-普通用户; 1-超级管理员; 2-版主
       private int type;
       //'0-未激活; 1-已激活;'
       private int status;
       private String activationCode;
       private String headerUrl;
       private Date createTime;
   
       public int getId() {
           return id;
       }
   
       public void setId(int id) {
           this.id = id;
       }
   
       public String getUsername() {
           return username;
       }
   
       public void setUsername(String username) {
           this.username = username;
       }
   
       public String getPassword() {
           return password;
       }
   
       public void setPassword(String password) {
           this.password = password;
       }
   
       public String getSalt() {
           return salt;
       }
   
       public void setSalt(String salt) {
           this.salt = salt;
       }
   
       public String getEmail() {
           return email;
       }
   
       public void setEmail(String email) {
           this.email = email;
       }
   
       public int getType() {
           return type;
       }
   
       public void setType(int type) {
           this.type = type;
       }
   
       public int getStatus() {
           return status;
       }
   
       public void setStatus(int status) {
           this.status = status;
       }
   
       public String getActivationCode() {
           return activationCode;
       }
   
       public void setActivationCode(String activationCode) {
           this.activationCode = activationCode;
       }
   
       public String getHeaderUrl() {
           return headerUrl;
       }
   
       public void setHeaderUrl(String headerUrl) {
           this.headerUrl = headerUrl;
       }
   
       public Date getCreateTime() {
           return createTime;
       }
   
       public void setCreateTime(Date createTime) {
           this.createTime = createTime;
       }
   
       @Override
       public String toString() {
           return "User{" +
                   "id=" + id +
                   ", username='" + username + '\'' +
                   ", password='" + password + '\'' +
                   ", salt='" + salt + '\'' +
                   ", email='" + email + '\'' +
                   ", type=" + type +
                   ", status=" + status +
                   ", activationCode='" + activationCode + '\'' +
                   ", headerUrl='" + headerUrl + '\'' +
                   ", createTime=" + createTime +
                   '}';
       }
   }
   
   ```

4. **在DAO层编写查询接口**

   > 注意：需要在接口上标记@Mapper注解用于和后序的xml文件对应

   ```java
   package com.coder.community.dao;
   
   import com.coder.community.entity.User;
   import org.apache.ibatis.annotations.Mapper;
   
   import java.util.List;
   
   @Mapper
   public interface UserMapper {
       User selectById(int id);
       User selectByEmail(String email);
       User selectByName(String name);
   
       void updateStatus(int id,int status);
       void insertUser(User user);
       void updateHeader(int id,String headerUrl);
       void updatePassword(int id,String password);
   }
   
   ```

5. 在Resource/mapper路径下编写和接口对应的.xml映射文件：

   模板文件如下：

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.coder.community.dao.UserMapper">
   <!--content-->
   </mapper>
   ```

   > 示例 ：user-mapper.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.coder.community.dao.UserMapper">
   
       <!--*   `id` int(11) NOT NULL AUTO_INCREMENT,
     `username` varchar(50) DEFAULT NULL,
     `password` varchar(50) DEFAULT NULL,
     `salt` varchar(50) DEFAULT NULL,
     `email` varchar(100) DEFAULT NULL,
     `type` int(11) DEFAULT NULL COMMENT '0-普通用户; 1-超级管理员; 2-版主;',
     `status` int(11) DEFAULT NULL COMMENT '0-未激活; 1-已激活;',
     `activation_code` varchar(100) DEFAULT NULL,
     `header_url` varchar(200) DEFAULT NULL,
     `create_time` timestamp NULL DEFAULT NULL,-->
   <sql id="selectUser">
       id,username,password,salt,email,type,status,activation_code,header_url,create_time
   </sql>
   
       <select id="selectById" resultType="User">
           select <include refid="selectUser"></include> from user
           where id=#{id};
       </select>
   
       <select id="selectByEmail" resultType="User">
           select <include refid="selectUser"></include> from user
           where email=#{email};
       </select>
   
       <select id="selectByName" resultType="User">
           select <include refid="selectUser"></include> from user
           where username=#{name};
       </select>
       <!--为了节省时间：部分方法的实现省略-->
   
   </mapper>
   ```

   注意：

   <mapper namespace="com.coder.community.dao.UserMapper"\> 编写Sql查询语句前，需要在namespace里指定和xml映射的接口类。用于一一对应。

   <sql \></sql\>用于简化sql语句编写，当需要多次查询，多列数据时，可将列名放在<sql\></sql\>中间，用于代码复用

​		<select  id="`接口类中的对应方法`" resultType="`方法的返回值，如果为实体类 或者非基本类型需要显示声明`"    \>`sql语句`</select\>用于装sql查询语句

6. 测试

   ```java
   public class MapperTest {
       @Autowired
       UserMapper userMapper;
   
       @Test
       public void test02  (){
           User user=userMapper.selectById(1);
           System.out.println(user);
       }
   }
   ```

   执行如上测试方法：在控制台成功得到返回值：

   >2022-11-18 13:48:43.487  INFO 38056 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
   >2022-11-18 13:48:44.412  INFO 38056 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
   >User{id=1, username='SYSTEM', password='SYSTEM', salt='SYSTEM', email='nowcoder1@sina.com', type=0, status=1, activationCode='null', headerUrl='http://static.nowcoder.com/images/head/notify.png', createTime=Sat Apr 13 10:11:03 CST 2019}

# 五、Thymeleaf

基于Spring Boot使用Thymeleaf前端模板读取实现后端和前端的变量交换。

1. 导入依赖：

   ```xml
           <!-- 引入Thymeleaf模板引擎 -->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-thymeleaf</artifactId>
           </dependency>
   
   ```

2. properties配置：

   ```properties
   ##Thymeleaf模板配置 ##
   spring.thymeleaf.mode=HTML5
   spring.thymeleaf.encoding=UTF-8
   spring.thymeleaf.content-type= text/html
   
   #为便于测试，在开发时需要关闭缓存
   spring.thymeleaf.cache=false
   
   #在freemarker中的空值的处理，默认情况以${xxx}的方式取值会报错，我们一般都采用${xxx?if_exists} 的方式去处理，烦死人了。
   spring.freemarker.settings.classic_compatible=true
   
   ```

3. 在Controller里进行URL到文件相对路径的映射

   ```java
   
   @Controller
   public class HelloController {
   
       @RequestMapping(path = "/hello",method = RequestMethod.GET)
       public String hello(){
           return "/site/error/500";
       }
   }
   ```

   注意：`path = "/hello"`的含义是当浏览器访问路径 http://localhost:8080/hello进行调用此方法，`method = RequestMethod.GET`指定HTTP请求方法为GET是响应。return指定返回什么路径下的html文件，如：

   **此时返回的文件应为：src/main/resources/templates/site/error/路径下的500.html文件**。

   500.html:

   ```html
   <!doctype html>
   <html lang="en" xmlns:th="http://www.thymeleaf">
       <!--content-->
   </html>
   ```

   `xmlns:th="http://www.thymeleaf"`用于声明此处为thymleaf模板标记的html文件，告诉编译器，先替换html中`th: `标记的变量为后端的动态数据，再将替换后的html文件传输显示。

   > 在导入thymeleaf依赖后，thymeleaf会默认html存放路径（默认寻址路径）：src/main/resources/templates。静态文件存放路径（默认匹配路径）：src/main/resources/static。*（如js文件引用时：/js/jquery-3.0.0.min.js ==> @{/js/jquery-3.0.0.min.js}*

4. 后端动态数据的显示，以及前端内容的接收。

   格式：th:text(根据需求可变) ="${put 在model里的变量名 如content}"。

   ​			`${}`表示此处为需要替换的变量，`@{}`表示此处为需要替换的路径

   例如：

   ```java
   @Controller
   public class HelloController {
   
       @RequestMapping(path = "/hello",method = RequestMethod.GET)
       public String hello(Model model){
           String str="你好";
           model.addAttribute("content",str);
           return "/site/error/demo";
       }
   }
   ```

   ```html
   <!DOCTYPE html>
   <html lang="en" xmlns:th="http://thymeleaf.org">
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
   </head>
   <body>
     <p1 th:text="${content}">hello</p1>
   </body>
   </html>
   ```

   在编辑运行项目后，在浏览器输入http://localhost:8080/hello，返回的HTML文件会显示**你好**，（即p1中**hello**，已经被替换为后端Controller中指定的 “**你好**”）。

```
|form-control ${passwordMsg!=null?'is-invaild':''}|
```

>注意使用thymeleaf修改html文件时一定要非常注意空格！！不然有可能因为转换失败导致浏览器请求html文件时，无法返回html文件。

5.

#  附：Java类实现的操作

## 一、JavaMailSender实现程序发送邮件

1. 发送方设置

   邮箱一定要开启smtp服务

   properties设置如下。

   ```properties
   # MailProperties
   spring.mail.host=smtp.sina.com
   spring.mail.port=465
   spring.mail.username=邮箱账号
   spring.mail.password=授权码
   spring.mail.protocol=smtps
   spring.mail.properties.mail.smtp.ssl.enable=true
   spring.mail.properties.mail.smtl.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   spring.mail.properties.mail.smtp.starttls.required=trues.mail.smtp.starttls.required=true
   ```

   ```java
   @Component
   public class MailClient {
       private static final Logger logger= LoggerFactory.getLogger(MailClient.class);
   
       @Resource
       private JavaMailSender sender;
   
       @Value("${spring.mail.username}")
       private String from;
   
       public void sendMail(String to,String subject,String content){
           MimeMessage message=sender.createMimeMessage();
           MimeMessageHelper helper=new MimeMessageHelper(message);
   
           try {
               helper.setFrom(from);
               helper.setTo(to);
               helper.setSubject(subject);
               helper.setText(content,true);
               sender.send(helper.getMimeMessage());
           } catch (MessagingException e) {
               logger.error("发送邮件失败"+e.getMessage());
           }
       }
   
   }
   ```

2. 使用md5加密算法，存储用户密码：

   >md5算法是一种单向加密算法，只内置了由密码到密文的转换方法，无法从密文转换到密码。
   >
   >同时在密码加密前，结合密码+盐 ->密文的方式，提高密码的破解难度。

   ```Java
   public class CommunityUtil {
   
       // 生成随机字符串
       public static String generateUUD(){
           return UUID.randomUUID().toString().replaceAll("-","");
       }
   
       //MD5加密
       // hello  -> 113rsadsadd1
       // hello + 3e4a8 ->aabcdscasad
       public static String md5(String key){
           if(StringUtils.isBlank(key)) return null;
   
           return DigestUtils.md5DigestAsHex(key.getBytes());
       }
   }
   
   ```
# 未完待续。。。。。
