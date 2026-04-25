# AGENTS.md - SpringVue-App

### OpenCode Agents 配置 – Spring Boot 项目

本文档用于指导 AI 助手在该 Spring Boot 项目中的所有代码生成、编辑和协作行为。请严格遵守以下规则。

## 1. 代码格式化规范

本项目要求所有 Java/Kotlin 代码遵循统一的格式，以提高可读性和可维护性。

### 1.1 具体规则

- **缩进：** 2 个空格，不使用 Tab
- **行宽：** 最多 100 字符
- **大括号：** 左括号不换行，右括号单独一行（K&R 风格）
- **导入语句：** 按 `java` → `javax` → `org` → `com` → 静态导入排序，不使用 `*` 导入
- **空行：** 方法之间、类定义之后、逻辑块之间保留一个空行
- **注解：** 每个注解独占一行（除非是参数或字段上的简短注解）
- **Lambda：** 尽量使用方法引用，参数类型可省略
- **Lombok：** 推荐使用，但避免过度使用 `@Data`，改用 `@Getter/@Setter`

## 2. Git 操作规则（严格遵守）

AI 助手绝对禁止执行任何会导致代码提交或推送到远程仓库的操作。这是不可协商的安全底线。

### 2.1 禁止的命令

- `git commit`
- `git push`
- `git merge`、`git pull --rebase` 等会修改历史或远程分支的命令
- `git rebase`、`git cherry-pick` 等结合提交的操作

### 2.2 允许的 Git 命令

- `git status`、`git diff`、`git log`（只读操作）
- `git add`（仅限暂存区，但需要提示用户后续手动 commit）
- `git stash` / `git stash pop`（在用户明确要求且明确风险的情况下）

### 2.3 行为准则

- 永远不要假设用户希望自动提交。任何 Git 变更（包括暂存）都必须经过人工确认。
- 如果生成了新的文件或修改了现有代码，应输出提示：「以下文件已修改，请人工审核后手动执行 git add、git commit 和 git push。」
- 禁止在回答中自动嵌入 `&& git commit -m "..."` 之类的链式命令。

## 3. 其他规范与最佳实践

为保证项目的健壮性和团队协作效率，请同时遵守以下约定。

### 3.1 代码结构与分层

标准包结构如下：

```
com.example.project
├── controller      # REST 控制器
├── service         # 业务逻辑接口与实现
├── repository      # 数据访问层（JPA / MyBatis）
├── model/entity    # 实体类
├── model/dto       # 数据传输对象
├── config          # Spring 配置类
├── util            # 工具类
└── exception       # 自定义异常与全局异常处理器
```

- Controller 中只处理请求/响应转换，不包含业务逻辑。
- Service 层使用接口 + 实现类，便于测试和替换。

### 3.2 依赖管理

- Spring Boot 版本需要对应的 JDK 版本，请确保生成的代码兼容当前使用的 JDK 版本（如 `var`、文本块、记录类）。
- 使用 Spring Boot Starter 管理依赖，避免手动添加不兼容版本。
- 禁止使用已废弃的 API（如 `finalize()`、`Thread.stop()`）。

### 3.3 测试要求

- **单元测试：** 使用 JUnit 5 + Mockito，覆盖率目标 > 80%（核心模块）。
- **集成测试：** 使用 `@SpringBootTest` 和 Testcontainers（数据库、Redis 等）。
- **测试命名：** `should_expectedBehavior_when_condition` 或 `testXxx`。
- AI 生成的代码必须包含对应的单元测试（除非用户明确跳过）。

### 3.4 日志规范

- 使用 SLF4J + Logback，注解 `@Slf4j`（Lombok）。
- **日志级别：** 跟踪输入参数使用 DEBUG，重要状态变化使用 INFO，异常使用 WARN/ERROR。
- 禁止在日志中输出敏感信息（密码、Token、身份证号等）。

### 3.5 API 文档

- 使用 SpringDoc OpenAPI 3（生成 OpenAPI 3.0 规范文档，`springdoc-openapi-starter-webmvc-ui`）。
- 所有 Controller 端点必须添加 `@Operation` 和 `@ApiResponses` 注解。
- DTO 字段使用 `@Schema(description = "...")`。

### 3.6 配置管理

- 使用 `application.yml`，按环境拆分 `application-{profile}.yml`。
- 敏感配置（数据库密码、API密钥）使用环境变量或配置中心，禁止硬编码。

### 3.7 异常处理

- 统一使用 `@RestControllerAdvice` 处理全局异常，返回 RFC 7807 格式的错误响应。
- 业务异常继承 `RuntimeException`，避免 checked 异常。
- 不要吞掉异常，必要时要记录日志并抛出。

### 3.8 性能与安全

- 避免 N+1 查询，使用 `@EntityGraph` 或 JOIN FETCH。
- 所有用户输入必须验证（使用 Jakarta Validation `@Valid`）。
- 防范注入攻击：JPA 参数化查询，MyBatis 使用 `#{}` 而非 `${}`。
- 启用 Spring Security 时，默认所有端点需要认证，并使用 BCrypt 加密密码。

## 4. 不可变原则与代码生成要求

- **优先使用不可变对象：** `record`、`@Value`（Lombok）、`Collections.unmodifiableList`。
- **避免返回 null：** 使用 `Optional`、空集合或异常代替。
- **自包含代码：** 生成的代码必须自包含，不依赖未导入的类，不引用不存在的配置文件。
- **明确性：** 如果现有代码中存在不明确的业务规则，必须向用户提问，而非随意假设。

## 5. 提交前检查清单（供人工使用）

在人工执行 `git commit` 之前，请确保：

- 所有测试通过（`mvn test`）
- 没有在日志或代码中留下调试用的 `System.out` 或 `printStackTrace`
- 已从 IDE 清理未使用的导入和变量
- 敏感配置没有被提交（检查 `.gitignore` 是否忽略了 `application-secret.yml`）