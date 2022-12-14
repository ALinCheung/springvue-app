# SpringVue-App

> 一个快速搭建SpringBoot + Vue基座项目

## 技术栈

### 父端

主要技术：

- Java 11 后端环境
- Maven 3.6.x 工程项目构建工具
- Nodejs 16 前端环境
- Npm 8/Yarn 1.2 包管理工具
- Lerna 项目管理工具
- com.github.eirslett.frontend-maven-plugin 前端构建工具
- org.codehaus.mojo.appassembler-maven-plugin jar打包启动脚本构建工具
- maven-assembly-plugin 压缩打包工具

### 前端

主要技术：

- Vite 3 构建工具
- Vue 3 前端框架
- Vue-router 路由组件
- Vuex 状态管理组件
- Element-plus 前端组件
- Axios 网络请求库
- Bable 语法兼容
- Eslint 代码规范控制
- Prettier 美化代码
- Postcss Css浏览器兼容
- TypeScript 3 类型控制
- Terser 代码压缩

### 后端

主要技术：

- SpringBoot 5 构建工具
- Spring Gateway 网关组件
- Knife4j Swagger2 Restful Api文档工具
- Nacos 配置中心/服务中心
- H2 Datasource 本地数据库
- Mybatis-plus 数据访问框架
- Lombok Java代码注解工具
- Hutool 公共工具类库
- Fastjson Json工具
- EasyExcel Excel工具
- Javacsv Csv工具
- commons-pool2/common-net/jsch Ftp工具
- Kaptcha 验证码工具

### 1. 命令

打包, 根目录执行

```
mvn clean install -Dmaven.test.skip=true
```