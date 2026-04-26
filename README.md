# SpringVue-App

一个快速搭建 Spring Boot + Vue 的全栈项目基座。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-green)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 特性

- Spring Boot 3.2.x 后端 + Vue 3 前端
- 微服务架构支持（Spring Gateway + Nacos）
- Maven 多模块管理
- 前端 Monorepo 结构（pnpm workspaces）
- RESTful API 文档（SpringDoc OpenAPI 3.0）

## 技术栈

### 后端

| 技术 | 说明 |
| ---- | ---- |
| Spring Boot 3.2.x | 核心框架 |
| Spring Gateway | API 网关 |
| Nacos | 配置中心/服务发现 |
| MyBatis-Plus | ORM 框架 |
| H2 | 嵌入式数据库 |
| Lombok | 注解处理器 |
| Hutool | 工具集 |
| EasyExcel | Excel 处理 |
| SpringDoc OpenAPI | API 文档 |

### 前端

| 技术 | 说明 |
| ---- | ---- |
| Vue 3 | 框架 |
| Vite 3 | 构建工具 |
| Element Plus | 组件库 |
| Vue Router | 路由 |
| Pinia | 状态管理 |
| Axios | HTTP 客户端 |
| TypeScript | 类型系统 |
| pnpm | 包管理器 |

## 项目结构

```
springvue-app/
├── springvue-ui/                  # 前端 (Vue Monorepo)
│   └── packages/
│       ├── common/               # 公共组件
│       ├── frame/                # 框架/布局
│       └── portal/               # 门户
│
├── springvue-api/                # API 模块
│   ├── springvue-api-core/       # 核心 API
│   ├── springvue-api-demo/       # 示例 API
│   └── springvue-api-gateway/    # API 网关
│
├── springvue-server/             # 业务服务层
├── springvue-repository/         # 数据访问层
├── springvue-dao/               # DAO 层
├── springvue-common/             # 公共工具类
│
├── pom.xml                       # 父 POM
└── mvnw                          # Maven Wrapper
```

## 快速开始

### 环境要求

- Java 17+ （Spring Boot 3.x 最低要求）
- Maven 3.6.x
- Node.js 16+
- pnpm 8+

### 后端构建

```bash
# 构建全部模块
mvn clean install -Dmaven.test.skip=true

# 运行服务
cd springvue-server && mvn spring-boot:run
```

### 前端启动

```bash
cd springvue-ui
pnpm install
pnpm dev
```

### 访问地址

- 前端：http://localhost:3000
- API 文档：http://localhost:8080/doc.html

## 许可证

[MIT](LICENSE)
