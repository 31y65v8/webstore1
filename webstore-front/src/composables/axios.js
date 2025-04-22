import axios from 'axios'
import { useAuth } from '@/composables/useAuth'
import router from '@/router'

// 创建一个函数来设置拦截器
export function setupAxiosInterceptors() {
  const { logout } = useAuth()
  
  // 添加请求拦截器
  axios.interceptors.request.use(
    config => {
      // 排除登录和注册请求
      const isAuthEndpoint = config.url.includes('/api/user/login') || 
                            config.url.includes('/api/user/register');
      
      if (!isAuthEndpoint) {
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      }
      
      // 调试信息 - 只在开发环境
      if (process.env.NODE_ENV === 'development') {
        console.log(`${config.method.toUpperCase()} ${config.url}`, {
          headers: config.headers,
          data: config.data
        });
      }
      
      return config;
    },
    error => Promise.reject(error)
  );
  
  axios.interceptors.response.use(
    response => {
      // 检查响应中是否包含大整数ID
      if (response.data && response.data.data) {
        const data = response.data.data;
        if (Array.isArray(data)) {
          // 处理数组情况
          data.forEach(item => {
            if (item.id && typeof item.id === 'number') {
              item.id = String(item.id);
            }
          });
        } else if (data.id && typeof data.id === 'number') {
          // 处理单个对象情况
          data.id = String(data.id);
        }
      }
      return response;
    },
    error => {
      if (error.response && (error.response.status === 401 || error.response.status === 403)) {
        // 清除登录状态
        logout()
        
        // 只有在不是登录页面时才提示和跳转
        if (router.currentRoute.value.path !== '/login') {
          alert('登录已过期，请重新登录')
          router.push('/')
        }
      }
      return Promise.reject(error)
    }
  )
}
