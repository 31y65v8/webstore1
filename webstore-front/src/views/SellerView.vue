<template>
  <div class="seller-dashboard">
    <button class="back-button" @click="goToHome">
        <span>&larr;</span> 
      </button>
    <h1>管理中心</h1>
    
    <div class="dashboard-actions">
      <button class="add-product-btn" @click="showAddProductModal = true">
        <i class="fas fa-plus"></i> 添加新商品
      </button>
      <button class="order-btn" @click="goToPendingOrders">
        <i class="fas fa-shipping-fast"></i> 待发货订单
        <span v-if="pendingOrdersCount > 0" class="badge">{{ pendingOrdersCount }}</span>
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
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import AddProductModal from '@/components/seller/AddProductModal.vue'
import ProductList from '@/components/seller/ProductList.vue'
import axios from 'axios'

const showAddProductModal = ref(false)
const refreshTrigger = ref(0)
const router = useRouter()
const pendingOrdersCount = ref(0)
let pendingOrdersTimer = null

const handleProductAdded = () => {
  showAddProductModal.value = false
  // 触发商品列表刷新
  refreshTrigger.value++
}

const fetchPendingOrdersCount = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return
    
    const response = await axios.get('/api/orderItems/seller/paid/count', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data && response.data.code === 200) {
      pendingOrdersCount.value = response.data.data || 0
    }
  } catch (error) {
    console.error('获取待发货订单数量失败:', error)
  }
}

const goToPendingOrders = () => {
  router.push('/seller/paid-orderitem')
}

const handleLogout = () => {
  // 清除本地存储中的 token
  localStorage.removeItem('token')
  // 重定向到主页
  router.push('/')
}

const goToHome = () => {
  router.push('/')
}

onMounted(() => {
  fetchPendingOrdersCount()
  // 每分钟刷新一次待发货订单数量
  pendingOrdersTimer = setInterval(fetchPendingOrdersCount, 60000)
})

onBeforeUnmount(() => {
  if (pendingOrdersTimer) {
    clearInterval(pendingOrdersTimer)
  }
})
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
  display: flex;
  margin-bottom: 2rem;
  gap: 1rem;
  flex-wrap: wrap;
}

.add-product-btn, .order-btn {
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

.add-product-btn:hover, .order-btn:hover {
  background-color: #40a9ff;
}

.order-btn {
  background-color: #fa8c16;
  position: relative;
}

.order-btn:hover {
  background-color: #ffa940;
}

.badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #f5222d;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
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