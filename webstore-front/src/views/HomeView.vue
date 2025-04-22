<template>
  <div class="home">
    <Navbar 
      @category-change="handleCategoryChange"
      @show-login="showLoginModal = true"
      @show-register="showRegisterModal = true"
    />

    

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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/components/layout/Navbar.vue'
import ProductGrid from '@/components/product/ProductGrid.vue'
import LoginModal from '@/components/auth/LoginModal.vue'
import RegisterModal from '@/components/auth/RegisterModal.vue'
import axios from 'axios'
import { useAuth } from '@/composables/useAuth'

const currentCategory = ref('ALL')
const isLoading = ref(false)
const showLoginModal = ref(false)
const showRegisterModal = ref(false)
const router = useRouter()

// 使用统一的useAuth
const { isLoggedIn, username, userRole, login, logout } = useAuth()

const handleCategoryChange = (category) => {
  currentCategory.value = category
}

const handleProductClick = (product) => {
  router.push({ name: 'ProductDetail', params: { id: product.id } })
}

const handleLoginSuccess = (userData) => {
  showLoginModal.value = false;
  
  // 如果有直接传递的角色信息，使用它
  if (userData && userData.role) {
    if (userData.role === 'SELLER') {
      router.push('/seller');
    } else if (userData.role === 'ADMIN') {
      router.push('/admin');
    }
    // CUSTOMER角色在LoginModal中已处理刷新
  }
  // 没有角色信息时，默认刷新
  else {
    window.location.reload();
  }
}

const handleRegisterSuccess = () => {
  showRegisterModal.value = false
  showLoginModal.value = true
}

const handleLogout = () => {
  logout() // 调用统一的logout方法
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