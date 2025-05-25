# MyBatis-Plus技术与面试指南 (2025版)

## 一、MyBatis-Plus全面解析

### 1.1 MyBatis-Plus基本概念

MyBatis-Plus（简称MP）是一个MyBatis的增强工具，在MyBatis的基础上只做增强不做改变，为简化开发、提高效率而生。它提供了CRUD操作的通用方法，无需手写大量SQL，同时具备强大的条件构造器和代码生成器，极大地提高了开发效率。

**核心特性**：
- 无侵入：只做增强不做改变，引入它不会对现有工程产生影响
- 损耗小：启动即会自动注入基本CRUD，性能基本无损耗
- 强大的CRUD操作：内置通用Mapper和通用Service，可灵活扩展
- 支持Lambda表达式：通过Lambda表达式编写各类查询条件
- 支持主键自动生成：支持多种主键策略
- 支持ActiveRecord模式：实体类只需继承Model类即可实现基本CRUD操作
- 内置分页插件：基于MyBatis物理分页，开发者无需关心具体操作
- 内置性能分析插件：可输出SQL语句及其执行时间
- 内置全局拦截插件：提供全表delete、update操作智能分析阻断
- 内置SQL注入剥离器：有效防止SQL注入攻击

### 1.2 MyBatis-Plus核心组件

#### 1.2.1 通用Mapper
BaseMapper是MP提供的最常用的CRUD接口，定义了常见的数据库操作方法：

```java
public interface BaseMapper<T> {
    // 插入一条记录
    int insert(T entity);
    
    // 根据ID删除
    int deleteById(Serializable id);
    
    // 根据实体(ID)删除
    int deleteById(T entity);
    
    // 根据map条件删除
    int deleteByMap(@Param("cm") Map<String, Object> columnMap);
    
    // 根据条件构造器删除
    int delete(@Param("ew") Wrapper<T> queryWrapper);
    
    // 批量ID删除
    int deleteBatchIds(@Param("coll") Collection<?> idList);
    
    // 根据ID修改
    int updateById(@Param("et") T entity);
    
    // 根据条件构造器修改
    int update(@Param("et") T entity, @Param("ew") Wrapper<T> updateWrapper);
    
    // 根据ID查询
    T selectById(Serializable id);
    
    // 批量ID查询
    List<T> selectBatchIds(@Param("coll") Collection<? extends Serializable> idList);
    
    // 根据map条件查询
    List<T> selectByMap(@Param("cm") Map<String, Object> columnMap);
    
    // 根据条件构造器查询一条记录
    T selectOne(@Param("ew") Wrapper<T> queryWrapper);
    
    // 根据条件构造器查询总记录数
    Long selectCount(@Param("ew") Wrapper<T> queryWrapper);
    
    // 根据条件构造器查询列表
    List<T> selectList(@Param("ew") Wrapper<T> queryWrapper);
    
    // 根据Wrapper条件，查询全部记录
    List<Map<String, Object>> selectMaps(@Param("ew") Wrapper<T> queryWrapper);
    
    // 根据Wrapper条件，查询全部记录，并翻页
    IPage<Map<String, Object>> selectMapsPage(IPage<T> page, @Param("ew") Wrapper<T> queryWrapper);
    
    // 根据Wrapper条件，查询全部记录
    List<Object> selectObjs(@Param("ew") Wrapper<T> queryWrapper);
    
    // 根据条件构造器查询并分页
    IPage<T> selectPage(IPage<T> page, @Param("ew") Wrapper<T> queryWrapper);
}
```

#### 1.2.2 条件构造器
MP提供了强大的条件构造器，用于构建各种复杂的SQL查询条件：

1. **QueryWrapper**：用于构建查询条件
2. **UpdateWrapper**：用于构建更新条件
3. **LambdaQueryWrapper**：使用Lambda表达式构建查询条件，类型安全
4. **LambdaUpdateWrapper**：使用Lambda表达式构建更新条件，类型安全

```java
// 普通构造器示例
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("name", "张三")
       .ge("age", 18)
       .orderByDesc("create_time");
List<User> users = userMapper.selectList(wrapper);

// Lambda构造器示例
LambdaQueryWrapper<User> lambdaWrapper = new LambdaQueryWrapper<>();
lambdaWrapper.eq(User::getName, "张三")
             .ge(User::getAge, 18)
             .orderByDesc(User::getCreateTime);
List<User> lambdaUsers = userMapper.selectList(lambdaWrapper);
```

#### 1.2.3 Service层接口
MP提供了通用Service接口IService和实现类ServiceImpl，封装了更多的CRUD操作：

```java
public interface IService<T> {
    // 批量插入数据
    boolean saveBatch(Collection<T> entityList);
    
    // 批量插入或更新数据
    boolean saveOrUpdateBatch(Collection<T> entityList);
    
    // 根据ID删除
    boolean removeById(Serializable id);
    
    // 根据实体(ID)删除
    boolean removeById(T entity);
    
    // 根据ID集合批量删除
    boolean removeByIds(Collection<?> idList);
    
    // 根据条件删除
    boolean remove(Wrapper<T> queryWrapper);
    
    // 根据ID更新
    boolean updateById(T entity);
    
    // 根据条件更新
    boolean update(Wrapper<T> updateWrapper);
    
    // 根据ID批量更新
    boolean updateBatchById(Collection<T> entityList);
    
    // 根据ID查询
    T getById(Serializable id);
    
    // 根据条件查询一条记录
    T getOne(Wrapper<T> queryWrapper);
    
    // 查询总记录数
    int count();
    
    // 根据条件查询总记录数
    int count(Wrapper<T> queryWrapper);
    
    // 查询所有记录
    List<T> list();
    
    // 根据条件查询记录
    List<T> list(Wrapper<T> queryWrapper);
    
    // 分页查询
    IPage<T> page(IPage<T> page);
    
    // 条件分页查询
    IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper);
    
    // 链式查询
    QueryChainWrapper<T> query();
    
    // Lambda链式查询
    LambdaQueryChainWrapper<T> lambdaQuery();
    
    // 链式更新
    UpdateChainWrapper<T> update();
    
    // Lambda链式更新
    LambdaUpdateChainWrapper<T> lambdaUpdate();
}
```

#### 1.2.4 分页插件
MP内置了分页插件，只需简单配置即可实现物理分页：

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

使用分页：
```java
// 创建Page对象，指定当前页和每页显示的记录数
Page<User> page = new Page<>(1, 10);
// 查询并返回结果
Page<User> userPage = userMapper.selectPage(page, null);
// 获取总记录数
long total = userPage.getTotal();
// 获取当前页数据
List<User> records = userPage.getRecords();
```

### 1.3 MyBatis-Plus 2025生态现状

#### 1.3.1 版本演进
- **MyBatis-Plus 3.x**：当前稳定版本，提供了更完善的功能和性能优化
- **MyBatis-Plus 4.x**：新版本开发中，增强了对现代Java特性的支持
- **新增特性**：增强了对Kotlin的支持、更强大的动态表名解析、更灵活的条件构造

#### 1.3.2 生态集成
- **Spring Boot Starter**：与Spring Boot无缝集成
- **多数据源支持**：dynamic-datasource插件支持多数据源
- **代码生成器**：新一代代码生成器支持自定义模板
- **多租户方案**：内置多租户解决方案
- **乐观锁插件**：支持乐观锁版本控制
- **SQL注入器**：可自定义全局SQL方法

#### 1.3.3 性能与安全
- **性能优化**：批处理操作性能提升
- **安全增强**：防SQL注入策略更新
- **执行分析插件**：SQL执行效率分析
- **数据权限插件**：精细化数据权限控制

## 二、MyBatis-Plus在项目中的应用

### 2.1 基础配置与集成

#### 2.1.1 与Spring Boot集成
1. **引入依赖**

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3</version>
</dependency>
```

2. **配置数据源**

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/resume_assistant?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: password
```

3. **配置MyBatis-Plus**

```yaml
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  typeAliasesPackage: com.resumeassistant.entity
  global-config:
    db-config:
      id-type: auto
      table-prefix: ra_
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

#### 2.1.2 实体类配置
定义与数据库表对应的实体类，使用MyBatis-Plus注解进行配置：

```java
@Data
@TableName("ra_user")
public class User {
    // 主键ID，自增策略
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 普通字段
    private String username;
    private String password;
    private String email;
    private String phone;
    
    // 逻辑删除字段
    @TableLogic
    private Integer deleted;
    
    // 字段填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 该字段不存在于数据库表中
    @TableField(exist = false)
    private String nonDatabaseField;
}
```

#### 2.1.3 Mapper接口定义
继承BaseMapper接口，无需编写任何方法即可拥有基础的CRUD功能：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 可以定义自定义方法
    List<User> selectActiveUsers();
}
```

#### 2.1.4 Service层实现
定义Service接口和实现类，继承MP提供的通用接口和实现类：

```java
public interface UserService extends IService<User> {
    // 自定义业务方法
    boolean updateUserStatus(Long userId, Integer status);
}

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        return lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getStatus, status)
                .update();
    }
}
```

### 2.2 项目实战案例

#### 2.2.1 智能简历助手平台实现

以下是我建议添加到您的MyBatis-Plus技术指南中的后续内容概要：

项目实战案例部分应包含：
智能简历助手平台中的应用
用户管理模块实现
简历信息管理模块
多表关联查询优化
高级特性应用
动态表名实现多租户
自动填充与乐观锁
性能优化技巧
面试问题部分应包含：
基础概念题
MyBatis-Plus与MyBatis的区别
核心功能和优势
常用注解解析
实战应用题
如何实现复杂查询
性能优化策略
多表关联实现方案
原理剖析题
插件机制工作原理
SQL注入器原理
代码生成器实现
高级应用题
自定义ID生成器
动态表名解析器
分布式场景应用
八股文部分应包含：
核心源码剖析
Mapper接口代理实现
条件构造器设计模式
插件链原理
最佳实践总结
代码规范
性能调优指南
安全防护措施