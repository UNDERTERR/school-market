# 测试框架配置说明

## 📋 测试依赖概述

本项目已为所有核心模块添加了完整的测试依赖：

### 基础测试依赖
```xml
<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

### 集成测试依赖
```xml
<!-- H2 内存数据库 -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.15.3</version>
    <scope>test</scope>
</dependency>

<!-- Embedded Redis -->
<dependency>
    <groupId>it.ozimov</groupId>
    <artifactId>embedded-redis</artifactId>
    <version>0.7.3</version>
    <scope>test</scope>
</dependency>
```

## 🏗️ 测试框架结构

```
src/test/java/
├── com/xiaojie/
│   ├── [module]/
│   │   ├── config/           # 测试配置类
│   │   ├── controller/       # 控制器测试
│   │   ├── service/          # 服务层测试
│   │   ├── dao/              # 数据访问层测试
│   │   └── util/             # 测试工具类
│   └── common/               # 通用测试配置
└── resources/
    ├── application-test.yml  # 测试环境配置
    └── testcontainers.properties # Testcontainers配置
```

## 🎯 测试配置注解

### 1. 集成测试配置

```java
@IntegrationTest  // 自定义注解，包含以下配置
```

**等同于：**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
```

### 2. 单元测试配置

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private SomeService someService;
    
    @InjectMocks
    private SomeController someController;
}
```

## 📝 测试示例

### 1. 单元测试示例

```java
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryService categoryService;

    @Test
    void testGetLevel1Categories() {
        // 准备测试数据
        List<CategoryEntity> mockCategories = Arrays.asList(
            createMockCategory(1L, "图书", 0L, 1)
        );
        
        when(categoryService.getLevel1Catagories()).thenReturn(mockCategories);
        
        // 执行测试
        List<CategoryEntity> result = categoryService.getLevel1Catagories();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryService, times(1)).getLevel1Catagories();
    }
}
```

### 2. 集成测试示例

```java
@IntegrationTest
class CategoryControllerIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private CategoryService categoryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testGetLevel1Categories() throws Exception {
        // 测试REST API
        mockMvc.perform(get("/product/category/level1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
```

## 🔧 测试数据管理

### 1. 测试数据生成器

```java
public class TestDataGenerator {
    public static CategoryEntity createCategoryEntity() {
        CategoryEntity category = new CategoryEntity();
        category.setName("测试分类_" + System.currentTimeMillis());
        // ... 设置其他属性
        return category;
    }
}
```

### 2. 测试配置文件

**application-test.yml:**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: create-drop
  redis:
    host: localhost
    port: 6370
  data:
    elasticsearch:
      client:
        reactive:
          endpoints: http://localhost:9201

logging:
  level:
    com.xiaojie: DEBUG
```

## 🚀 运行测试

### 1. 运行所有测试

```bash
# 运行所有模块的测试
mvn test

# 运行特定模块的测试
mvn test -pl market-product

# 运行特定测试类
mvn test -Dtest=CategoryServiceTest
```

### 2. 跳过测试

```bash
# 跳过测试构建
mvn clean install -DskipTests

# 跳过测试编译和运行
mvn clean install -Dmaven.test.skip=true
```

### 3. 生成测试报告

```bash
# 生成Surefire测试报告
mvn test surefire-report:report

# 生成JaCoCo代码覆盖率报告
mvn jacoco:report
```

## 📊 测试覆盖率和质量检查

### 1. JaCoCo代码覆盖率

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
                <goal>report</goal>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 2. 覆盖率要求

- **最低要求**: 60% 行覆盖率
- **推荐目标**: 80% 行覆盖率
- **理想目标**: 90%+ 行覆盖率

### 3. 代码质量检查

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.14.0</version>
</plugin>
```

## 🔍 测试最佳实践

### 1. 命名规范

```java
// 测试类命名
public class CategoryServiceTest { }

// 测试方法命名
@Test
void testGetLevel1Categories_WhenCalled_ShouldReturnCategoryList() { }
```

### 2. 测试结构（AAA模式）

```java
@Test
void testMethod() {
    // Arrange - 准备测试数据
    CategoryEntity category = createMockCategory();
    when(service.getCategory(1L)).thenReturn(category);

    // Act - 执行测试
    CategoryEntity result = service.getCategory(1L);

    // Assert - 验证结果
    assertNotNull(result);
    assertEquals("预期值", result.getName());
}
```

### 3. Mock使用原则

```java
// ✅ 正确使用Mock
@Mock
private ExternalService externalService;

@InjectMocks
private MyService myService;

// ✅ 验证Mock调用
verify(externalService, times(1)).callExternalApi();

// ❌ 不要Mock被测试的类
@Mock  // 错误！
private MyService myService;
```

## 📈 测试报告和分析

### 1. 查看测试报告

测试报告生成位置：
```
target/
├── site/
│   ├── jacoco/           # JaCoCo覆盖率报告
│   └── surefire-report/  # Surefire测试报告
└── surefire-reports/      # 测试结果XML
```

### 2. CI/CD集成

在GitLab CI/CD中运行测试：

```yaml
unit-tests:
  stage: test
  image: maven:3.8.1-openjdk-8
  script:
    - mvn clean test jacoco:report
  artifacts:
    reports:
      junit:
        - target/surefire-reports/TEST-*.xml
      coverage_report:
        coverage_format: jacoco
        path: target/site/jacoco/jacoco.xml
```

## 🚨 常见问题解决

### 1. 测试数据库连接问题

**问题**: H2数据库连接失败
**解决**: 检查`application-test.yml`配置

### 2. Redis连接问题

**问题**: 测试时Redis连接失败
**解决**: 使用Embedded Redis或Testcontainers

### 3. Mock不起作用

**问题**: `@Mock`注解不生效
**解决**: 确保使用`@ExtendWith(MockitoExtension.class)`

### 4. 测试数据隔离

**问题**: 测试之间数据污染
**解决**: 使用`@Transactional`或清理数据

```java
@Transactional
@Rollback
class ServiceTest {
    // 测试会自动回滚
}
```

## 🎯 测试分层策略

1. **单元测试**: 测试单一方法/类，使用Mock
2. **集成测试**: 测试组件间交互，使用嵌入式容器
3. **端到端测试**: 测试完整流程，使用Testcontainers

通过这种分层测试策略，可以确保代码质量和系统稳定性！