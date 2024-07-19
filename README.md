# huaer-resource-admin

## 初始化项目

### 初始化目录

- src/main/java

- src/resources/static

  - com.huaer.resource.admin
    - entity
    - service
    - web
    - Application.java
    - DatabaseInitializer.java

- src/resources/templates

- src/application.yml

- src/logback-spring.xml

### maven 依赖

- org.springframework.boot:spring-boot-starter-parent:3.0.0

- org.springframework.boot:spring-boot-starter-web

- org.springframework.boot:spring-boot-starter-jdbc

- org.springframework.boot:spring-boot-devtools

### application.yml 基本配置

```yml
# application.yml
server:
  port: ${APP_PORT:8080}

spring:
  application:
    name: ${APP_NAME:unnamed}
  datasource:
    url:
    username:
    password:
    driver-class-name:
```

### logback-spring.xml

logback 配置控制台输出与日志输出

### Application.java

`main()` 方法启动整个 Spring Boot 应用程序

```java
@SpringBootApplication
// 暂时排除数据库自动配置
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
```

此时运行项目，成功可访问 80 端口服务

## 编写 User 实体类

entity/User.java

## 参数校验

手动引入 `spring-boot-starter-validation`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

常用验证注解包括：

- @NotNull：值不能为 null；
- @NotEmpty：字符串、集合或数组的值不能为空，即长度大于 0；
- @NotBlank：字符串的值不能为空白，即不能只包含空格；
- @Size：字符串、集合或数组的大小是否在指定范围内；
- @Min：数值的最小值；
- @Max：数值的最大值；
- @DecimalMin：数值的最小值，可以包含小数；
- @DecimalMax：数值的最大值，可以包含小数；
- @Digits：数值是否符合指定的整数和小数位数；
- @Pattern：字符串是否匹配指定的正则表达式；
- @Email：字符串是否为有效的电子邮件地址；
- @AssertTrue：布尔值是否为 true；
- @AssertFalse：布尔值是否为 false；
- @Future：日期是否为将来的日期；
- @Past：日期是否为过去的日期；

**编写 User 实体类**

```java
// entity/User.java
public class User {
    private Long id;
    // ......
    @Override
    public String toString(){
      return String.format("User[username=%s, id=%s, createAt=%s]", getUsername(), getId(), getCreatedAt());
    }
}
```

### 单个参数

```java

@Controller
@RestController
@Validated
public class UserController {
  @PostMapping("/signin")
  public Map<String, Object> doSignin(
          @RequestParam @NotBlank String username,
          @RequestParam @NotBlank(message = "密码不能为空") @Length(min = 6, message = "密码长度不能少于6位") String password
  ) {
  //  ......
  }
}
```

单参数校验时我们需要，在方法的类上加上@Validated 注解，否则校验不生效。

### 整个参数

**@RequestBody + Entity**

```java
@PostMapping("/signin")
public Map<String, String> doSignin(@Validated @RequestBody User user) {
  String token = String.valueOf(System.currentTimeMillis());
  return Map.of("token", token);
}
```

**@RequestBody + VO/DTO**

```java
@PostMapping("create")
public ResultResponse<Void> createUser(@Validated @RequestBody UserCreateRequestVO requestVO) {
    return ResultResponse.success(null);
}
```

如果请求参数与对象字段不一致，可借助 `@JsonProperty` 实现映射

```java
public class User {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @JsonProperty("username") // 对应json中username
    private String name;

    // ...
}
```

**@ModelAttribute**

```java
@Controller
@RestController
public class UserController {
  @PostMapping("/signin")
  public Map<String, Object> doSignin(@Valid @ModelAttribute User user) {
    System.out.println(user.getName());
    String token = String.valueOf(System.currentTimeMillis());
    return Map.of("token", token);
  }
}
```

当绑定中有不符合项将返回，否则将绑定部分有效值的字段，不匹配项将未绑定（使用默认值）

这并不影响我们通过额外的 `@RequestParam` 获取指定 http 请求参数，进行其它操作

```java
@Controller
@RestController
public class UserController {
    @PostMapping("/signin")
    public Map<String, Object> doSignin(
            @Valid @ModelAttribute User user,BindingResult bindingResult,
            @RequestParam("username") String username
    ) {
        System.out.println(username); // 单独获取用户名参数
        String token = String.valueOf(System.currentTimeMillis());
        return Map.of("token", token);
    }
}
```

**@Validated 与 @Valid**

@Validated：
可以用在类或方法上。
通常用于类级别，以启用方法级别的验证。
支持分组验证（例如：@Validated(Group.class)）。

@Valid：
用在参数、属性或方法上。
常用于方法参数或属性级别的验证，不支持分组验证。

### SpringBoot 统一结果返回，统一异常处理

**统一结果返回**

1. 定义通用的响应对象
   编写 dto/ResultResponse.java，它是一个 Serializable 实现类

2. 定义接口响应状态码
   编写 enums/StatusEnum.java，枚举应用程序特定状态码和信息（message）

3. 定义统一的成功/失败的处理方法
   为 dto/ResultResponse.java 补充 ResultResponse.success/error 方法用于构造并返回 response

4. web 层统一响应结果：

```java
@PostMapping("create")
public ResultResponse<Void> createUser(@Validated @RequestBody UserCreateRequestVO requestVO) {
    return ResultResponse.success(null);
}

@GetMapping("info")
public ResultResponse<UserInfoResponseVO> getUser(@NotBlank(message = "请选择用户") String userId) {
  final UserInfoResponseVO responseVO = userService.getUserInfoById(userId);
  return ResultResponse.success(responseVO);
}
```

**统一异常处理**

统一异常处理的必要性体现在保持代码的一致性、提供更清晰的错误信息、以及更容易排查问题。
通过定义统一的异常处理方式，确保在整个应用中对异常的处理保持一致，减少了重复编写相似异常处理逻辑的工作，
同时提供友好的错误信息帮助开发者和维护人员更快地定位和解决问题，最终提高了应用的可维护性和可读性。

1. 定义统一的异常类
   编写 exception/ServiceException.java，继承自 RuntimeException 异常；成员核心属性 status message；
   供 Controller 抛出

**使用**

```java
// UserService.java
@Override
public void createUser(UserCreateRequestVO requestVO) {
    final UserDO userDO = userManager.selectUserByName(requestVO.getUserName());
    if (userDO != null) {
        throw new ServiceException(StatusEnum.PARAM_INVALID, "用户名已存在");
    }
}
```

2. 全局异常处理器
   用于声明一个全局控制器建言（Advice），相当于把@ExceptionHandler、@InitBinder 和@ModelAttribute 注解的方法集中到一个地方。
   常放在一个特定的类上，这个类被认为是全局异常处理器，可以跨足多个控制器。
   配合 @ExceptionHandler 实现全局的异常处理，当控制器抛出异常时，SpringBoot 会自动调用对应方法处理并返回定义响应

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    // ServiceException
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResultResponse<Void> handleServiceException(ServiceException serviceException, HttpServletRequest request) {
      // todo:日志记录
      return ResultResponse.error(serviceException.getStatus(), serviceException.getMessage());
    }

    // Exception
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultResponse<Void> handleException(Exception ex, HttpServletRequest request) {
      // todo:日志记录
      return ResultResponse.error(StatusEnum.SERVICE_ERROR);
    }
}
```

3. 补充需要的异常类型处理

- @ExceptionHandler(ServiceException.class)
- @ExceptionHandler(Exception.class)
- @ExceptionHandler(ConstraintViolationException.class)
- @ExceptionHandler(MethodArgumentNotValidException.class)
- @ExceptionHandler(MissingServletRequestParameterException.class)
- @ExceptionHandler({UnexpectedTypeException.class,MethodArgumentTypeMismatchException.class})
- @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeException.class})
- @ExceptionHandler(HttpMessageNotReadableException.class)

## MySQL

本地安装 MySQL 并新建数据库

`CREATE DATABASE IF NOT EXISTS resource;`

编写 DatabaseInitializer；

```java
@Component
public class DatabaseInitializer {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init(){
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS t_user ("
        + "id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY, "
        + "username VARCHAR(100) NOT NULL, "
        + "password VARCHAR(100) NOT NULL, "
        + "createAt BIGINT NOT NULL, "
        + "UNIQUE(username)"
        + ");"
        );
    }
}
```

application.yml 配置数据库

```yml
datasource:
  url: jdbc:mysql://localhost:3306/resource?useSSL=false&serverTimezone=UTC
  username: root
  password:
  driver-class-name: com.mysql.cj.jdbc.Driver
  hikari:
    maximum-pool-size: 20
    minimum-idle: 5
    idle-timeout: 60000
    max-lifetime: 1800000
    connection-timeout: 30000
```

移除禁用数据库自动配置代码

```java
// 注释这行
// @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
```

PRIMARY UNIQUE MULTIPLE
PRI：Primary Key（主键），每行数据唯一标识的列，不允许重复且不能为空。
UNI：Unique Key（唯一键），不允许重复值，可以有多个，但每个键的值必须唯一。
MUL：Multiple Key（多重索引），允许重复值，通常用于外键或其他非唯一索引列。

```sql
CREATE TABLE example (
    id INT AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    age INT,
    PRIMARY KEY (id),
    UNIQUE KEY unique_username (username),
    UNIQUE KEY unique_email (email),
    KEY idx_phone (phone),
    KEY idx_age (age)
);
```

### MyBatis-Plus

**引入 MyBatis-Plus**

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.2</version>
</dependency>
```

**引入 Lombok**

引入 Lombok 依赖

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.24</version>
    <scope>provided</scope>
</dependency>
```

IDE 安装 Lombok 插件：

IntelliJ IDEA
打开 IntelliJ IDEA，进入 File -> Settings -> Plugins。
搜索 Lombok 插件并安装。
确保在 File -> Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors 中，勾选 Enable annotation processing。

更多 Lombok 学习：https://www.quanxiaoha.com/lombok/lombok-tutorial.html

**重写 User 实体类**

```java
@Data
@TableName("t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message="用户名不能为空")
    private String username;

    @NotBlank(message="密码不能为空")
    private String password;

    private long createdAt;
}
```

**编写mapper**

项目根目录下新建 config 包，并创建 MybatisPlusConfig 配置类：

```java
@Configuration
@MapperScan("com.huaer.resource.admin")
public class MyBatisPlusConfig {
}
```

项目根目录下新建 mapper 包，添加 Mapper 类

```java
public interface UserMapper extends BaseMapper<User> {
}
```

Mapper使用

```java
// service/UserService.java
@Autowired
private UserMapper userMapper;

@Override
public User register(String username, String password){
  User user = new User();
  user.setUsername(username);
  user.setPassword(password);
  user.setCreatedAt(System.currentTimeMillis());
  userMapper.insert(user);
  Long id = user.getId();
  System.out.println("id:" + id);
  return user;
}
```

**Service层**

改写 `UserService.java` 为接口，使用 Mybatis Plus 封装的通用 Service 层 `IService`；

```java
public interface UserService extends IService<User> {
    User register(String username, String password);
}
```

新增 `UserServiceImpl.java` 继承于 ServiceImpl，实现 UserService

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.serCreatedAt(System.currentTimeMills());
        this.save(user);
        return user;
    }
}
```

## 注册

完善 UserServiceImpl.register,包含 用户已存在，密码md5存储 等业务逻辑；

**用户已存在**

`t_user` 表查找username用户，判断存在性

```java
@Override
public User register(String username, String password) {
// 如果用户名已存在,抛出ServiceError
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.select("id", "username");
    queryWrapper.eq("username", username);
    User user = this.getOne(queryWrapper);
    if (user != null) {
        throw new ServiceException(StatusEnum.USER_ALREADY_EXIST);
    }
//    ......
}
```

**密码md5存储**

Utils包下新增 `MD5Util` 工具类，提供encrypt方法

`MessageDigest` 消息摘要算法（实例）

`md.digest` 哈希计算

## 登录

UserServiceImpl.signin,包含 用户不存在，账号密码错误

```java
@Override
public String signin(String username, String password){
    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    User user = this.getOne(queryWrapper);
    // 如果用户不存在，抛出 Login_ERROR
    if (user == null) {
        throw new ServiceException(StatusEnum.Login_ERROR);
    }
    // 如果账号密码错误，抛出 Login_ERROR
    if(!Objects.equals(user.getPassword(), MD5Util.encrypt(password))){
        throw new ServiceException(StatusEnum.Login_ERROR);
    }
    // .....
}
```

**JWT**

todo: 根据用户信息 java jwt生成

