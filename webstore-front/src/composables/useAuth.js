import axios from 'axios'
import { ref } from 'vue'

export function useAuth() {
  const isLoggedIn = ref(false)
  const username = ref('')

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

  const login = async (formData) => {
    try {
      const response = await axios.post('/api/user/login', formData)
      const token = response.data.data
      localStorage.setItem('token', token)
      isLoggedIn.value = true
      username.value = formData.account
    } catch (error) {
      if (error.response) {
        throw new Error(error.response.data.message || '登录失败')
      }
      throw error
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    isLoggedIn.value = false
    username.value = ''
  }

  const checkAuth = () => {
    const token = localStorage.getItem('token')
    if (token) {
      isLoggedIn.value = true
      const payload = JSON.parse(atob(token.split('.')[1]))
      username.value = payload.sub
    }
  }

  return {
    isLoggedIn,
    username,
    register,
    login,
    logout,
    checkAuth
  }
}
