<template>
  <div class="product-detail">
    <div class="back-button-container">
      <button class="back-button" @click="goBack">返回上一页</button>
    </div>
    <h1>{{ product.productName }}</h1>
    <img :src="product.productImage" alt="Product Image">
    <p>{{ product.productDescription }}</p>
    <p>单价: {{ product.productPrice }}</p>
    <p>库存: {{ product.productStorage }}</p>
    <button class="add-to-cart-btn" @click.stop="showQuantityModal = true">
        <font-awesome-icon icon="shopping-cart" /> 加入购物车
      </button>
  

  <div v-if="showQuantityModal" class="quantity-modal">
      <div class="quantity-modal-content" @click.stop>
        <h3>选择数量</h3>
        <div class="product-preview">
          <img :src="product.productImage" :alt="product.productName">
          <div class="product-preview-info">
            <h4>{{ product.productName }}</h4>
            <p class="preview-price">¥{{ formatPrice(product.productPrice) }}</p>
          </div>
        </div>
        
        <div class="quantity-selector">
          <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
          <input type="number" v-model.number="quantity" min="1" max="99">
          <button @click="increaseQuantity" :disabled="quantity >= 99">+</button>
        </div>
        
        <div class="modal-actions">
          <button class="cancel-btn" @click="showQuantityModal = false">取消</button>
          <button class="confirm-btn" @click="addToCart">确定</button>
        </div>
      </div>
      <div class="modal-overlay" @click="showQuantityModal = false"></div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useAuth } from '@/composables/useAuth'

const route = useRoute()
const router = useRouter()
const product = ref({})
const { isLoggedIn, userRole } = useAuth()

const showQuantityModal = ref(false)
const quantity = ref(1)

// 添加计时相关变量
const entryTime = ref(0)
const isRecorded = ref(false)

const formatPrice = (price) => {
  return Number(price).toFixed(2)
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const increaseQuantity = () => {
  if (quantity.value < 99) {
    quantity.value++
  }
}

const addToCart = async () => {
  // 检查用户是否已登录
  if (!isLoggedIn.value) {
    alert('请先登录后再添加商品到购物车')
    showQuantityModal.value = false
    return
  }
  
  // 检查是否是普通用户
  if (userRole.value !== 'CUSTOMER') {
    alert('只有普通用户才能添加商品到购物车')
    showQuantityModal.value = false
    return
  }
  
  try {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (!token) {
      alert('登录信息已过期，请重新登录')
      showQuantityModal.value = false
      return
    }
    
    // 调用添加到购物车API
    await axios.post('/api/cart/add', null, {
      params: {
        productId: product.value.productId,
        quantity: quantity.value
      },
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    alert('商品已成功加入购物车！')
    showQuantityModal.value = false
    quantity.value = 1  // 重置数量
  } catch (error) {
    console.error('添加到购物车失败:', error)
    
    if (error.response && error.response.status === 401) {
      alert('登录已过期，请重新登录')
    } else {
      alert('添加到购物车失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

const fetchProductDetails = async () => {
  try {
    const response = await axios.get(`/api/product/${route.params.id}`)
    product.value = response.data.data
    
    // 商品加载完成后开始计时
    startTimer()
  } catch (error) {
    console.error('Failed to fetch product details:', error)
  }
}

// 开始计时
const startTimer = () => {
  // 记录页面进入时间
  entryTime.value = Date.now()
  
  // 如果是CUSTOMER角色，记录浏览开始
  if (isLoggedIn.value && userRole.value === 'CUSTOMER') {
    // 这里可以选择在进入时就记录浏览，或者仅在离开时记录完整的停留时间
    recordBrowse(0) // 传入0表示刚进入，还没有停留时间
  }
}

// 记录浏览信息
const recordBrowse = async (duration) => {
  try {
    // 确保只记录一次完整的浏览（防止多次调用）
    if (duration > 0 && isRecorded.value) return
    
    const token = localStorage.getItem('token')
    if (!token) return
    
    await axios.post('/api/browse/record', {
      productId: product.value.productId,
      stayDuration: duration
    }, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (duration > 0) {
      isRecorded.value = true
      console.log(`浏览记录已保存，停留时间: ${duration}秒`)
    }
  } catch (error) {
    console.error('记录浏览历史失败:', error)
  }
}

// 计算停留时间并发送给后端
const recordStayDuration = () => {
  if (entryTime.value === 0 || !isLoggedIn.value || userRole.value !== 'CUSTOMER') return
  
  const exitTime = Date.now()
  const duration = Math.floor((exitTime - entryTime.value) / 1000) // 转换为秒
  
  // 只有停留时间超过一定阈值（如3秒）才记录，避免记录过短的浏览
  if (duration >= 1) {
    recordBrowse(duration)
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchProductDetails()
})

// 在组件即将销毁前记录停留时间
onBeforeUnmount(() => {
  recordStayDuration()
})

// 还需要处理用户通过浏览器的前进/后退按钮离开的情况
window.addEventListener('beforeunload', recordStayDuration)
onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', recordStayDuration)
})
</script>

<style scoped>
.product-detail {
  padding: 2rem;
}

.back-button-container {
  margin-bottom: 1rem;
}

.back-button {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.back-button:hover {
  background-color: #40a9ff;
}

/* 加入购物车按钮样式 */
.add-to-cart-btn {
  width: 100%;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0.8rem;
  cursor: pointer;
  transition: background-color 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  font-size: 1rem;
  margin-top: 2rem;
}

.add-to-cart-btn:hover {
  background-color: #40a9ff;
}

/* 数量选择弹窗样式 */
.quantity-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1001;
}

.quantity-modal-content {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  width: 90%;
  max-width: 400px;
  z-index: 1002;
  position: relative;
}

.quantity-modal-content h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  text-align: center;
}

.product-preview {
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
}

.product-preview img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 1rem;
}

.product-preview-info h4 {
  margin-top: 0;
  margin-bottom: 0.5rem;
  font-size: 1rem;
}

.preview-price {
  color: #f5222d;
  font-weight: bold;
  margin: 0;
}

.quantity-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
}

.quantity-selector button {
  width: 32px;
  height: 32px;
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  cursor: pointer;
}

.quantity-selector button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-selector input {
  width: 60px;
  height: 32px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin: 0 0.5rem;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
}

.modal-actions button {
  flex: 1;
  padding: 0.5rem 0;
  border-radius: 4px;
  border: none;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
  margin-right: 0.5rem;
}

.confirm-btn {
  background-color: #1890ff;
  color: white;
  margin-left: 0.5rem;
}

.confirm-btn:hover {
  background-color: #40a9ff;
}
</style>
