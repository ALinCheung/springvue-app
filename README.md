# SpringVue-App

一个快速搭建 Spring Boot + Vue 基座项目。

## 特性

- Spring Boot 2.3.x 后端 + Vue 3 前端
- 微服务架构支持（Spring Gateway + Nacos）
- Lerna 多包管理
- Maven 前端构建集成
- H2 本地数据库
- RESTful API 文档（Knife4j）

## 技术栈

### 后端

| 技术 | 说明 |
| ---- | ---- |
| Spring Boot 2.3.x | 框架 |
| Spring Gateway | 网关 |
| Nacos | 配置中心/服务发现 |
| MyBatis-Plus | ORM |
| H2 | 嵌入式数据库 |
| Lombok | 注解 |
| Hutool | 工具集 |
| EasyExcel | Excel 处理 |
| Knife4j | API 文档 |

### 前端

| 技术 | 说明 |
| ---- | ---- |
| Vue 3 | 框架 |
| Vite 3 | 构建工具 |
| Element Plus | 组件库 |
| Vue Router | 路由 |
| Vuex | 状态管理 |
| Axios | HTTP 客户端 |
| TypeScript | 类型 |

### 构建工具

| 技术 | 说明 |
| ---- | ---- |
| Maven 3.6.x | Java 构建 |
| Lerna | 多包管理 |
| frontend-maven-plugin | 前端集成 |
| appassembler-maven-plugin | 打包 |

## 快速开始

### 环境要求

- Java 11+
- Maven 3.6.x
- Node.js 16+
- npm 8+ / Yarn 1.2+

### 构建

```bash
mvn clean install -Dmaven.test.skip=true
```

### 运行

启动后端：

```bash
cd springvue-admin && mvn spring-boot:run
```

启动前端：

```bash
cd springvue-ui && npm run dev
```

## 项目结构

```
springvue-app
├── springvue-admin        # 后端服务
├── springvue-common    # 公共模块
├── springvue-gateway   # 网关服务
└── springvue-ui       # 前端项目
    └── packages
        ├── frame     # 框架包
        └── portal   # 门户包
```

## 许可证

MIT