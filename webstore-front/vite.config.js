import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({

  /*server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  },*/
  base: '/', // 关键配置：确保资源路径从根开始
  server: {
    proxy: {
      '/api': {
        target: 'http://193.112.177.126:8080', // 开发环境代理到本地
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: '../../webstore/src/main/resources/static', 
    emptyOutDir: true,
  },
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})

