import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import { setupAxiosInterceptors } from '@/composables/axios'
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faShoppingCart, faUser, faHome } from '@fortawesome/free-solid-svg-icons'

// 配置axios默认值
axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.headers.common['Content-Type'] = 'application/json'

// 添加请求拦截器
/*axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})*/

// 在创建 Vue app 之前设置拦截器
setupAxiosInterceptors()

// 添加你需要的图标
library.add(faShoppingCart, faUser, faHome)

// 创建 Vue app
const app = createApp(App)
app.use(router)
app.component('font-awesome-icon', FontAwesomeIcon)
app.mount('#app')
