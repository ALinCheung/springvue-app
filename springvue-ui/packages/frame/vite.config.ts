import { ConfigEnv, defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import legacy from "@vitejs/plugin-legacy";
import eslintPlugin from "vite-plugin-eslint";
import * as path from "path";

// https://vitejs.dev/config/
export default defineConfig(({ command }: ConfigEnv) => {
  // 公共配置
  const config = {
    base: "",
    server: {
      port: 8001,
      // 为开发服务器配置自定义代理规则
      proxy: {
        "/api": {
          target: "http://localhost:8080/server",
          rewrite: (path) => {
            return path.replace(/^\/api/, "");
          },
        },
      },
    },
    // 打包
    build: {
      outDir: "dist", // 指定打包输出路径
      assetsDir: "static", // 指定静态资源存放路径
      minify: "terser", // 混淆器,terser构建后文件体积更小
      // target: "modules", // 指定es版本,浏览器的兼容性, 被plugin-legacy覆盖
      sourcemap: false, // 是否构建source map 文件
      cssCodeSplit: true, // css代码拆分,禁用则所有样式保存在一个css里面
      chunkSizeWarningLimit: 1500, // 文件大小限制
      terserOptions: {
        compress: {
          drop_console: true, // 生产环境移除console
          drop_debugger: true, // 生产环境移除debugger
        },
      },
      rollupOptions: {
        output: {
          manualChunks(id) {
            // 解决【Vue3】vite打包报错：块的大小超过限制，Some chunks are larger than 500kb after minification
            if (id.includes("node_modules")) {
              return id
                .toString()
                .split("node_modules/")[1]
                .split("/")[0]
                .toString();
            }
          },
          chunkFileNames: (chunkInfo) => {
            const facadeModuleId = chunkInfo.facadeModuleId
              ? chunkInfo.facadeModuleId.split("/")
              : [];
            const fileName =
              facadeModuleId[facadeModuleId.length - 2] || "[name]";
            return `modules/${fileName}/[name].[hash].js`;
          },
        },
      },
    },
    resolve: {
      alias: {
        "@": path.resolve(__dirname, "src"),
      },
    },
    plugins: [
      vue(),
      // vite浏览器兼容
      legacy({
        targets: ["chrome 52"],
        additionalLegacyPolyfills: ["regenerator-runtime/runtime"],
        renderLegacyChunks: true,
        polyfills: [
          "es.symbol",
          "es.array.filter",
          "es.promise",
          "es.promise.finally",
          "es/map",
          "es/set",
          "es.array.for-each",
          "es.object.define-properties",
          "es.object.define-property",
          "es.object.get-own-property-descriptor",
          "es.object.get-own-property-descriptors",
          "es.object.keys",
          "es.object.to-string",
          "web.dom-collections.for-each",
          "esnext.global-this",
          "esnext.string.match-all",
        ],
      }),
      // eslint代码规范
      eslintPlugin({
        include: [
          "src/**/*.js",
          "src/**/*.vue",
          "src/**/*.ts",
          "src/*.js",
          "src/*.vue",
          "src/*.ts",
        ],
      }),
    ],
  };
  // 环境配置
  if (command === "serve") {
    return {
      // dev 独有配置
      ...config,
    };
  } else {
    // command === 'build'
    return {
      // build 独有配置
      ...config,
    };
  }
});
