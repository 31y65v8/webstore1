import axios from 'axios'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

export function useAuth() {
  const isLoggedIn = ref(false)
  const username = ref('')
  const userRole = ref('')
  const router = useRouter()

  const init = () => {
    const token = localStorage.getItem('token')
    const role = localStorage.getItem('userRole')
    
    if (token) {
      isLoggedIn.value = true
      userRole.value = role || ''
      
      try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        username.value = payload.sub || '用户'
      } catch (e) {
        username.value = '用户'
      }
    }
  }

  init()

  const register = async (formData) => {
    try {
      await axios.post('/api/user/register', {
        account: formData.account,
        username: formData.username,
        password: formData.password,
        role: formData.role
      })
    } catch (error) {
      if (error.response) {
        throw new Error(error.response.data.message || '注册失败')
      }
      throw error
    }
  }

  const login = async (credentials) => {
    try {
      // 清除可能存在的token
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
      
      // 临时删除可能存在的 Authorization 头
      const oldAuth = axios.defaults.headers.common['Authorization']
      delete axios.defaults.headers.common['Authorization']
      
      console.log('正在发送登录请求:', credentials)
      const response = await axios.post('/api/user/login', credentials)
      console.log('登录响应:', response.data)
      
      // 正确提取token（在response.data.data中）
      const token = response.data.data

      // 确保令牌没有空白字符
    //if (token) {
     // token = token.trim()
    //}
      
      localStorage.setItem('token', token)
      localStorage.setItem('userRole', credentials.role)
      
      // 设置全局请求头
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
      
      // 更新状态
      isLoggedIn.value = true
      username.value = credentials.account
      userRole.value = credentials.role
      
      console.log('登录成功，状态已更新')
      return true
    } catch (error) {
      console.error('登录失败:', error)
      console.error('错误详情:', error.response?.data)
      return false
    }
  }

  const logout = () => {
    isLoggedIn.value = false
    username.value = ''
    userRole.value = null
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('userRole')
  }

  const checkTokenValidity = () => {
    const token = localStorage.getItem('token')
    
    if (!token) {
      logout()
      return
    }
    
    // 简单检查 token 是否过期
    try {
      // 解析 JWT (不验证签名)
      const base64Url = token.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
      }).join(''))
      
      const { exp } = JSON.parse(jsonPayload)
      if (exp * 1000 < Date.now()) {
        logout()
        if (router.currentRoute.value.path !== '/') {
          alert('登录已过期，请重新登录')
          router.push('/')
        }
      }
    } catch (e) {
      console.error('Token 解析失败', e)
      logout()
    }
  }

  return {
    isLoggedIn,
    username,
    userRole,
    register,
    login,
    logout,
    init,
    checkTokenValidity
  }
}
