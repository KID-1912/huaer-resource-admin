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

### maven依赖

- org.springframework.boot:spring-boot-starter-parent:3.0.0

- org.springframework.boot:spring-boot-starter-web

- org.springframework.boot:spring-boot-starter-jdbc

- org.springframework.boot:spring-boot-devtools

### application.yml基本配置

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

logback配置控制台输出与日志输出

### Application.java

`main()` 方法启动整个Spring Boot应用程序

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

此时运行项目，成功可访问80端口服务


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

- @NotNull：值不能为null；
- @NotEmpty：字符串、集合或数组的值不能为空，即长度大于0；
- @NotBlank：字符串的值不能为空白，即不能只包含空格；
- @Size：字符串、集合或数组的大小是否在指定范围内；
- @Min：数值的最小值；
- @Max：数值的最大值；
- @DecimalMin：数值的最小值，可以包含小数；
- @DecimalMax：数值的最大值，可以包含小数；
- @Digits：数值是否符合指定的整数和小数位数；
- @Pattern：字符串是否匹配指定的正则表达式；
- @Email：字符串是否为有效的电子邮件地址；
- @AssertTrue：布尔值是否为true；
- @AssertFalse：布尔值是否为false；
- @Future：日期是否为将来的日期；
- @Past：日期是否为过去的日期；

### 单个参数

```java

@Controller
@RestController
@Validated
public class UserController {
  @PostMapping("/signin")
  public Map<String, Object> signin(
          @RequestParam @NotBlank String username,
          @RequestParam @NotBlank(message = "密码不能为空") @Length(min = 6, message = "密码长度不能少于6位") String password
  ) {
  //  ......
  }
}
```

单参数校验时我们需要，在方法的类上加上@Validated注解，否则校验不生效。

### 整个参数

**@RequestBody + Entity**

```java
@PostMapping("/signin")
public Map<String, String> signin(@Validated @RequestBody User user) {
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
  public Map<String, Object> signin(@Valid @ModelAttribute User user) {
    System.out.println(user.getName());
    String token = String.valueOf(System.currentTimeMillis());
    return Map.of("token", token);
  }
}
```

当绑定中有不符合项将返回，否则将绑定部分有效值的字段，不匹配项将未绑定（使用默认值）

这并不影响我们通过额外的 `@RequestParam` 获取指定http请求参数，进行其它操作

```java
@Controller
@RestController
public class UserController {
    @PostMapping("/signin")
    public Map<String, Object> signin(
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

### SpringBoot统一结果返回，统一异常处理

**统一结果返回**

1. 定义通用的响应对象
编写 dto/ResultResponse.java，它是一个Serializable实现类

2. 定义接口响应状态码
编写 enums/StatusEnum.java，枚举应用程序特定状态码和信息（message）

3. 定义统一的成功/失败的处理方法
为 dto/ResultResponse.java 补充 ResultResponse.success/error 方法用于构造并返回 response

4. web层统一响应结果：
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
编写 exception/ServiceException.java，继承自 RuntimeException异常；成员核心属性 status message；
供 Controller 抛出


2. 全局异常处理器
用于声明一个全局控制器建言（Advice），相当于把@ExceptionHandler、@InitBinder和@ModelAttribute注解的方法集中到一个地方。
常放在一个特定的类上，这个类被认为是全局异常处理器，可以跨足多个控制器。
配合 @ExceptionHandler 实现全局的异常处理，当控制器抛出异常时，SpringBoot会自动调用对应方法处理并返回定义响应

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


## Lombok

**引入 Lombok 依赖**
确保你的项目已经包含 Lombok 依赖。以下是 Maven 和 Gradle 的配置示例：

1. Maven
在 pom.xml 文件中添加 Lombok 依赖：

```xml
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <version>1.18.28</version>
  <scope>provided</scope>
</dependency>
```

2. 确保你的 IDE 已正确配置 Lombok 插件：

IntelliJ IDEA
打开 IntelliJ IDEA，进入 File -> Settings -> Plugins。
搜索 Lombok 插件并安装。
确保在 File -> Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors 中，勾选 Enable annotation processing。











