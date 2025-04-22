<template>
  <div class="cart">
    <button class="back-button" @click="goBack">
        <span>&larr;</span> 
    </button>
    <h1>我的购物车</h1>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <div v-if="cartItems.length === 0" class="empty-cart">购物车为空</div>
      <div v-else class="cart-container">
        <div class="cart-header">
          <div class="select-all">
            <input type="checkbox" v-model="allSelected" @change="selectAllItems">
            <span>全选</span>
          </div>
          <button class="checkout-btn" @click="createOrder" :disabled="!hasSelectedItems">
            立即下单 ({{ selectedCount }})
          </button>
        </div>
        
        <div class="cart-items">
          <div v-for="item in cartItems" :key="item.cartId" class="cart-item">
            <input type="checkbox" v-model="item.selected" @change="selectProduct(item)">
            <div class="cart-item-image">
              <img :src="item.productImage" :alt="item.productName">
            </div>
            <div class="cart-item-details">
              <h3 class="product-name">{{ item.productName }}</h3>
              <p class="product-price">¥{{ formatPrice(item.productPrice) }}</p>
            </div>
            <div class="cart-item-actions">
              <div class="quantity-control">
                <button @click="decreaseQuantity(item)">-</button>
                <input type="number" v-model.number="item.quantity" @change="updateQuantity(item)">
                <button @click="increaseQuantity(item)">+</button>
              </div>
              <button class="remove-btn" @click="removeFromCart(item)">删除</button>
            </div>
          </div>
        </div>
        
        <div class="cart-footer">
          <div class="total">
            <span>合计:</span>
            <span class="total-price">¥{{ formatPrice(totalPrice) }}</span>
          </div>
          
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const router = useRouter()
const { isLoggedIn } = useAuth()
const cartItems = ref([])
const loading = ref(false)
const allSelected = ref(false)

// 计算选中的商品数量
const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).length
})

// 判断是否有选中商品
const hasSelectedItems = computed(() => {
  return selectedCount.value > 0
})

// 计算总价
const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((total, item) => total + (Number(item.productPrice) * item.quantity), 0)
})

// 全选/取消全选
const selectAllItems = () => {
  const isAllSelected = allSelected.value
  cartItems.value.forEach(item => {
    item.selected = isAllSelected
  })
  // 向后端同步全选状态
  updateSelectedStatus()
}

// 向后端同步全选状态
const updateSelectedStatus = async () => {
  try {
    let token = localStorage.getItem('token')
    if (!token) return
    
    token = token.trim()
    
    await axios.put('/api/cart/selectAll', null, {
      params: {
        selected: allSelected.value ? 1 : 0
      },
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
  } catch (error) {
    console.error('更新全选状态失败:', error)
  }
}

// 创建订单
const createOrder = async () => {
  if (!hasSelectedItems.value) {
    alert('请至少选择一件商品')
    return
  }
  
  try {
    loading.value = true
    let token = localStorage.getItem('token')
    if (!token) {
      alert('登录信息已过期，请重新登录')
      router.push('/')
      return
    }
    
    token = token.trim()
    
    // 根据JWT令牌解析userId
    //const payload = JSON.parse(atob(token.split('.')[1]))
    //const userId = payload.userId
    
    // 调用创建订单接口
    await axios.post('/api/orders/create', null, {
      params: {
        totalPrice: totalPrice.value
      },
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    //alert('订单创建成功！')
    // 刷新购物车数据
    fetchCartItems()
    
  } catch (error) {
    console.error('创建订单失败:', error)
    alert('创建订单失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const fetchCartItems = async () => {
  loading.value = true
  try {
    // 检查是否已登录
    if (!isLoggedIn.value) {
      alert('请先登录后查看购物车')
      router.push('/')
      return
    }

    // 从localStorage获取token
    let token = localStorage.getItem('token')
    if (!token) {
      alert('登录信息已过期，请重新登录')
      router.push('/')
      return
    }
    
    // 检查并修复令牌中的空白字符
    token = token.trim()
    
    // 如果令牌格式有问题，重新登录
    if (token.includes(' ')) {
      console.error('令牌格式错误，含有空白字符，将重置令牌')
      localStorage.removeItem('token')
      alert('登录信息异常，请重新登录')
      router.push('/')
      return
    }
    
    // 调试信息
    console.log('使用的令牌:', token)

    const response = await axios.get('/api/cart/items', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    cartItems.value = response.data.map(item => ({
      ...item,
      selected: item.selected === true || item.selected === 1 || item.selected === "1"
    }))

    // 在获取数据后检查全选状态
    if (cartItems.value.length > 0) {
      allSelected.value = cartItems.value.every(item => item.selected)
    } else {
      allSelected.value = false
    }
  } catch (error) {
    console.error('获取购物车商品失败:', error)
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      alert('登录已过期，请重新登录')
      router.push('/')
    } else {
      alert('获取购物车内容失败: ' + (error.response?.data?.message || error.message))
    }
  } finally {
    loading.value = false
  }
}

const selectProduct = async (item) => {
  try {
    let token = localStorage.getItem('token')
    if (!token) {
      alert('登录信息已过期，请重新登录')
      router.push('/')
      return
    }
    
    // 清理令牌中的空白字符
    token = token.trim()
    
    await axios.put('/api/cart/select', null, {
      params: {
        productId: item.productId,
        selected: item.selected ? 1 : 0
      },
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
  } catch (error) {
    console.error('选择商品失败:', error)
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      alert('登录已过期，请重新登录')
      router.push('/')
    }
  }
}

const updateQuantity = async (item) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return

    await axios.put('/api/cart/update', null, {
      params: {
        productId: item.productId,
        quantity: item.quantity
      },
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
  } catch (error) {
    console.error('更新商品数量失败:', error)
  }
}

const removeFromCart = async (item) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return

    await axios.delete('/api/cart/remove', {
      params: {
        productId: item.productId
      },
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    fetchCartItems() // 重新获取购物车商品
  } catch (error) {
    console.error('删除商品失败:', error)
  }
}

// 导航返回
const goBack = () => {
  router.push('/')  // 直接返回首页
}

// 格式化价格显示
const formatPrice = (price) => {
  return Number(price).toFixed(2)
}

// 增加数量
const increaseQuantity = (item) => {
  item.quantity += 1
  updateQuantity(item)
}

// 减少数量
const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    item.quantity -= 1
    updateQuantity(item)
  }
}

onMounted(() => {
  fetchCartItems()
})
</script>

<style scoped>
.cart {
  padding: 2rem;
}

.loading {
  text-align: center;
}

.empty-cart {
  text-align: center;
  color: #666;
}

.cart-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.cart-header, .cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.total {
  font-size: 1.1rem;
}

.total-price {
  color: #f5222d;
  font-weight: bold;
  margin-left: 0.5rem;
}

.checkout-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.6rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
}

.checkout-btn:hover {
  background-color: #40a9ff;
}

.checkout-btn:disabled {
  background-color: #bfbfbf;
  cursor: not-allowed;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
}

.cart-item-image {
  width: 80px;
  height: 80px;
  overflow: hidden;
  border-radius: 4px;
}

.cart-item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-item-details {
  flex: 1;
}

.product-name {
  margin: 0 0 0.5rem;
  font-size: 1rem;
}

.product-price {
  color: #f5222d;
  font-weight: bold;
  margin: 0;
}

.cart-item-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.quantity-control {
  display: flex;
  align-items: center;
}

.quantity-control button {
  width: 30px;
  height: 30px;
  background: #f5f5f5;
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-control input {
  width: 40px;
  height: 30px;
  text-align: center;
  border: 1px solid #ddd;
}

.remove-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
}

.back-button {
  background: none;
  border: none;
  color: #1890ff;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px 10px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.back-button span {
  margin-right: 5px;
}
</style>

