<template>
  <div class="home">
    <Navbar 
      @category-change="handleCategoryChange"
      @show-login="showLoginModal = true"
      @show-register="showRegisterModal = true"
    />

    <div class="user-actions">
      <template v-if="isLoggedIn">
        <!-- 对于顾客角色显示个人中心、购物车和退出登录按钮 -->
        <template v-if="userRole === 'CUSTOMER'">
          <button class="user-btn" @click="goToUserInfo">
            <i class="fas fa-user"></i> 个人中心
          </button>
          <button class="cart-btn" @click="goToCart">
            <i class="fas fa-shopping-cart"></i> 购物车
          </button>
          <button class="logout-btn" @click="handleLogout">
            <i class="fas fa-sign-out-alt"></i> 退出登录
          </button>
        </template>
        
        <!-- 默认情况下至少显示退出登录按钮 -->
        <template v-else>
          <button class="logout-btn" @click="handleLogout">
            <i class="fas fa-sign-out-alt"></i> 退出登录
          </button>
        </template>
      </template>
      <template v-else>
        <button class="login-btn" @click="showLoginModal = true">
          <i class="fas fa-sign-in-alt"></i> 登录
        </button>
        <button class="register-btn" @click="showRegisterModal = true">
          <i class="fas fa-user-plus"></i> 注册
        </button>
      </template>
    </div>

    <ProductGrid
      :category="currentCategory"
      @loading-change="isLoading = $event"
      @product-click="handleProductClick"
    />
    
    <LoginModal 
      v-if="showLoginModal"
      @close="showLoginModal = false"
      @success="handleLoginSuccess"
    />


    
    <RegisterModal 
      v-model:visible="showRegisterModal"
      @success="handleRegisterSuccess"
    />

    <!-- 可以添加加载状态显示 -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-spinner"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/components/layout/Navbar.vue'
import ProductGrid from '@/components/product/ProductGrid.vue'
import LoginModal from '@/components/auth/LoginModal.vue'
import RegisterModal from '@/components/auth/RegisterModal.vue'
import axios from 'axios'

const currentCategory = ref('ALL')
const isLoading = ref(false)
const showLoginModal = ref(false)
const showRegisterModal = ref(false)
const isLoggedIn = ref(false)
const userRole = ref('')
const router = useRouter()

// 检查用户登录状态和角色
/*const checkLoginStatus = async () => {
  const token = localStorage.getItem('token')
  
  if (token) {
    try {
      // 配置请求头带上token
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
      
      // 获取用户信息包括角色
      const response = await axios.get('/api/user/info')
      console.log('API响应:', response.data) // 调试用
      
      // 正确的数据路径: response.data.data.role
      if (response.data && response.data.code === 200 && response.data.data) {
        isLoggedIn.value = true
        userRole.value = response.data.data.role
        console.log('获取到的角色:', userRole.value)
      } else {
        throw new Error('响应格式不正确')
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      localStorage.removeItem('token')
      isLoggedIn.value = false
      userRole.value = ''
    }
  } else {
    isLoggedIn.value = false
    userRole.value = ''
  }
  
  console.log('最终状态 - 登录状态:', isLoggedIn.value, '用户角色:', userRole.value)
}*/

const handleCategoryChange = (category) => {
  currentCategory.value = category
}

const handleProductClick = (product) => {
  router.push({ name: 'ProductDetail', params: { id: product.id } })
}

/*const handleLoginSuccess = async () => {
  showLoginModal.value = false
  showLoginModal.value = false
  isLoggedIn.value = true
  userRole.value = userInfo.role  // login后返回的用户信息
}*/
const handleLoginSuccess = (userData) => {
  const { token, role } = userData
  isLoggedIn.value = true
  userRole.value = role
  localStorage.setItem('token', token)
  localStorage.setItem('userRole', role)
  showLoginModal.value = false
  
  // 延时检查状态
  setTimeout(() => {
    console.log('延时检查 - 登录状态:', isLoggedIn.value, '角色:', userRole.value)
  }, 100)
}


const handleRegisterSuccess = () => {
  showRegisterModal.value = false
  showLoginModal.value = true
}

const handleLogout = () => {
  // 清除本地存储中的 token
  localStorage.removeItem('token')
  isLoggedIn.value = false
  // 重定向到主页
  router.push('/')
}

const goToCart = () => {
  router.push('/cart')
}

const goToUserInfo = () => {
  router.push('/customer')
}

const goToSellerCenter = () => {
  router.push('/seller')
}

const goToAdminCenter = () => {
  router.push('/admin')
}

/*onMounted(() => {
  checkLoginStatus()
})*/
onMounted(() => {
  // 读取本地存储的用户信息
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('userRole')
  
  if (token && role) {
    isLoggedIn.value = true
    userRole.value = role
    console.log('从localStorage恢复登录状态:', isLoggedIn.value, '角色:', userRole.value)
  }
})
</script>

<style scoped>
.home {
  min-height: 100vh;
}

.user-actions {
  margin: 1rem 0;
  text-align: right;
}

.cart-btn, .logout-btn, .user-btn, .login-btn, .register-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  transition: background-color 0.3s;
  margin-left: 1rem;
}

.user-btn {
  background-color: #52c41a;
}

.user-btn:hover {
  background-color: #73d13d;
}

.cart-btn:hover {
  background-color: #40a9ff;
}

.logout-btn {
  background-color: #f5222d;
}

.logout-btn:hover {
  background-color: #ff7875;
}

.login-btn, .register-btn {
  background-color: #1890ff;
}

.login-btn:hover, .register-btn:hover {
  background-color: #40a9ff;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.seller-btn {
  background-color: #fa8c16;
}

.seller-btn:hover {
  background-color: #ffa940;
}

.admin-btn {
  background-color: #722ed1;
}

.admin-btn:hover {
  background-color: #9254de;
}
</style> 