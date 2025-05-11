<template>
  <div class="product-card">
    <div class="product-image" @click="handleProductClick">
      <img :src="product.imgurl" :alt="product.name">
    </div>
    <div class="product-info">
      <h3 class="product-name" @click="handleProductClick">{{ product.name }}</h3>
      <!--<p class="product-description" @click="$emit('click')">{{ product.description }}</p>-->
      <div class="product-footer">
        <p class="price">¥{{ formatPrice(product.price) }}</p>
        <span class="sales">销量: {{ product.sales || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useAuth } from '@/composables/useAuth'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

const { isLoggedIn, userRole } = useAuth()

const emit = defineEmits(['click'])

const props = defineProps({
  product: {
    type: Object,
    required: true,
    validator: (value) => {
      return String(value.id) && value.name && value.price
    }
  }
})

const showQuantityModal = ref(false)
const quantity = ref(1)

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
    // 从localStorage获取userId
    const token = localStorage.getItem('token')
    if (!token) {
      alert('登录信息已过期，请重新登录')
      showQuantityModal.value = false
      return
    }
    
    // 解析JWT令牌获取userId（简化版，实际应考虑使用专门的JWT解析库）
    //const payload = JSON.parse(atob(token.split('.')[1]))
    //const userId = payload.userId
    
    // 调用添加到购物车API
    await axios.post('/cart/add', null, {
      params: {
        //userId: userId,
        productId: String(props.product.id),
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

// 处理商品点击事件
const handleProductClick = () => {
  // 只触发click事件，导航到详情页，具体的浏览记录会在详情页进行
  emit('click')
}
</script>

<style scoped>
.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative; /* 添加相对定位 */
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 1rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.product-description {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 1rem;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-footer {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.price {
  color: #f5222d;
  font-weight: bold;
  font-size: 1.2rem;
  margin: 0;
}

.sales {
  color: #999;
  font-size: 0.9rem;
}

/* 加入购物车按钮样式 */
.add-to-cart-btn {
  width: 100%;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0.5rem;
  cursor: pointer;
  transition: background-color 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
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

@media screen and (max-width: 768px) {
  .product-image {
    height: 180px;
  }

  .product-name {
    font-size: 1rem;
  }

  .product-description {
    font-size: 0.8rem;
  }

  .price {
    font-size: 1.1rem;
  }
  
  .quantity-modal-content {
    padding: 1rem;
  }
}
</style>