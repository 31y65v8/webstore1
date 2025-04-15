<template>
  <div class="seller-dashboard">
    <h1>销售人员管理面板</h1>
    
    <div class="dashboard-actions">
      <button class="add-product-btn" @click="showAddProductModal = true">
        <i class="fas fa-plus"></i> 添加新商品
      </button>
      <button class="logout-btn" @click="handleLogout">
        <i class="fas fa-sign-out-alt"></i> 退出登录
      </button>
    </div>

    <div class="products-list">
      <h2>我的商品列表</h2>
      <ProductList :refresh="refreshTrigger" />
    </div>

    <!-- 添加商品对话框 -->
    <AddProductModal 
      v-if="showAddProductModal"
      @close="showAddProductModal = false"
      @success="handleProductAdded"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AddProductModal from '@/components/seller/AddProductModal.vue'
import ProductList from '@/components/seller/ProductList.vue'

const showAddProductModal = ref(false)
const refreshTrigger = ref(0)
const router = useRouter()

const handleProductAdded = () => {
  showAddProductModal.value = false
  // 触发商品列表刷新
  refreshTrigger.value++
}

const handleLogout = () => {
  // 清除本地存储中的 token
  localStorage.removeItem('token')
  // 重定向到主页
  router.push('/')
}
</script>

<style scoped>
.seller-dashboard {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  color: #333;
  margin-bottom: 2rem;
}

.dashboard-actions {
  margin-bottom: 2rem;
}

.add-product-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: background-color 0.3s;
}

.add-product-btn:hover {
  background-color: #40a9ff;
}

.logout-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: background-color 0.3s;
  margin-left: 1rem;
}

.logout-btn:hover {
  background-color: #ff7875;
}

.products-list {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #333;
  margin-bottom: 1.5rem;
}
</style> 