import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import legacy from '@vitejs/plugin-legacy'
import eslintPlugin from 'vite-plugin-eslint'

// https://vitejs.dev/config/
export default defineConfig(({ command, mode, ssrBuild }) => {
  // 公共配置
  const config = {
    base: '',
    server: {
      port: 8000,
      // 为开发服务器配置自定义代理规则
      proxy: {

      }
    },
    build: {
      target: 'es2015'
    },
    plugins: [
      vue(),
      // vite浏览器兼容
      legacy({
        targets: ['chrome 52'],
        additionalLegacyPolyfills: ['regenerator-runtime/runtime'],
        renderLegacyChunks: true,
        polyfills: [
          'es.symbol',
          'es.array.filter',
          'es.promise',
          'es.promise.finally',
          'es/map',
          'es/set',
          'es.array.for-each',
          'es.object.define-properties',
          'es.object.define-property',
          'es.object.get-own-property-descriptor',
          'es.object.get-own-property-descriptors',
          'es.object.keys',
          'es.object.to-string',
          'web.dom-collections.for-each',
          'esnext.global-this',
          'esnext.string.match-all'
        ]
      }),
      // eslint代码规范
      eslintPlugin({
        include: ['src/**/*.js', 'src/**/*.vue', 'src/*.js', 'src/*.vue']
      })
    ]
  }
  // 环境配置
  if (command === 'serve') {
    return {
      // dev 独有配置
      ...config
    }
  } else {
    // command === 'build'
    return {
      // build 独有配置
      ...config
    }
  }
})
