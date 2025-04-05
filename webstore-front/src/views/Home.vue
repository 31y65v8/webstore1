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
      v-model:visible="showLoginModal"
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
import { ref } from 'vue'
import Navbar from '@/components/layout/Navbar.vue'
import ProductGrid from '@/components/product/ProductGrid.vue'
import LoginModal from '@/components/auth/LoginModal.vue'
import RegisterModal from '@/components/auth/RegisterModal.vue'

const currentCategory = ref('ALL')
const isLoading = ref(false)
const showLoginModal = ref(false)
const showRegisterModal = ref(false)

const handleCategoryChange = (category) => {
  currentCategory.value = category
}

const handleProductClick = (product) => {
  // 处理商品点击，比如跳转到商品详情页
  console.log('Product clicked:', product)
}

const handleLoginSuccess = () => {
  showLoginModal.value = false
}

const handleRegisterSuccess = () => {
  showRegisterModal.value = false
  showLoginModal.value = true
}
</script>

<style scoped>
.home {
  min-height: 100vh;
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
</style> 