# -SpringBoot-
基于SpringBoot的社区讨论系统
附相关技术分析：
# 一、SpringBoot

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

# 二、Mybatis

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

# 三、Thymeleaf

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