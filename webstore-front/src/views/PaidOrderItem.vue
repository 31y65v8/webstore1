<template>
  <div class="paid-orders">
    <div class="header">
      <button class="back-button" @click="goBack">
        <span>&larr;</span> 返回
      </button>
      <h1>待发货订单</h1>
      <div class="refresh-container">
        <button class="refresh-button" @click="fetchOrders" :disabled="loading">
          <i class="fas fa-sync-alt" :class="{ 'fa-spin': loading }"></i> 刷新
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading">
      加载中...
    </div>
    
    <div v-else-if="orderItems.length === 0" class="empty-state">
      暂无待发货订单
    </div>
    
    <div v-else class="orders-container">
      <div v-for="(orderItemGroup, orderId) in groupedOrderItems" :key="orderId" class="order-card">
        <div class="order-header">
          <h3>订单号: {{ orderId }}</h3>
          <span class="order-date">下单时间: {{ formatDate(orderItemGroup[0].createTime) }}</span>
        </div>
        
        <div class="order-items">
          <div v-for="item in orderItemGroup" :key="item.id" class="order-item">
            <div class="product-info">
              <img :src="item.productImage" :alt="item.productName" class="product-image">
              <div class="product-details">
                <h4>{{ item.productName }}</h4>
                <p class="price">¥{{ formatPrice(item.productPrice) }}</p>
                <p class="quantity">数量: {{ item.quantity }}</p>
              </div>
            </div>
            
            <div class="item-actions">
              <button 
                class="ship-button" 
                @click="handleShipment(item)" 
                :disabled="item.shipping"
              >
                <i v-if="item.shipping" class="fas fa-spinner fa-spin"></i>
                <span v-else>发货</span>
              </button>
            </div>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="customer-info">
            <p><strong>收货人:</strong> {{ orderItemGroup[0].receiverName }}</p>
            <p><strong>联系方式:</strong> {{ orderItemGroup[0].receiverPhone }}</p>
            <p><strong>地址:</strong> {{ formatAddress(orderItemGroup[0]) }}</p>
          </div>
          <div class="order-total">
            <p>共 {{ orderItemGroup.length }} 件商品</p>
            <p class="total-price">总价: ¥{{ formatPrice(orderItemGroup[0].totalPrice) }}</p>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="orderItems.length > 0 && !loading" class="pagination">
      <button 
        :disabled="currentPage === 1" 
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button 
        :disabled="currentPage === totalPages" 
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const loading = ref(false)
const orderItems = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 10

// 为每个订单项添加发货状态跟踪
const setOrderItemShippingStatus = (item, status) => {
  item.shipping = status
}

// 按订单ID分组订单项
const groupedOrderItems = computed(() => {
  const groups = {}
  orderItems.value.forEach(item => {
    if (!groups[item.orderId]) {
      groups[item.orderId] = []
    }
    groups[item.orderId].push(item)
  })
  return groups
})

// 获取待发货订单
const fetchOrders = async (page = 1) => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      router.push('/login')
      return
    }
    
    const response = await axios.get('/api/orderItems/seller/paid', {
      params: {
        page: page,
        size: pageSize
      },
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data && response.data.code === 200) {
      console.log('返回的订单项数据:', response.data.data.records)
      const data = response.data.data || {}
      orderItems.value = (data.records || []).map(item => ({
        ...item,
        shipping: false
      }))
      currentPage.value = Number(data.current) || 1
      totalPages.value = Number(data.pages) || 1
    }
  } catch (error) {
    console.error('获取待发货订单失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理发货
const handleShipment = async (item) => {
  if (item.shipping) return
  
  if (!confirm(`确认发货商品: ${item.productName}?`)) {
    return
  }
  
  setOrderItemShippingStatus(item, true)
  
  try {
    const token = localStorage.getItem('token')
    if (!token) return
    
    const response = await axios.post(`/api/orderItems/seller/${item.id}/ship`, null, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data && response.data.code === 200) {
      alert('发货成功！已通知顾客')
      // 从列表中移除已发货的订单项
      orderItems.value = orderItems.value.filter(i => i.id !== item.id)
    } else {
      alert('发货失败：' + (response.data?.msg || '未知错误'))
      setOrderItemShippingStatus(item, false)
    }
  } catch (error) {
    console.error('发货失败:', error)
    alert('发货失败：' + (error.response?.data?.msg || error.message))
    setOrderItemShippingStatus(item, false)
  }
}

// 格式化价格
const formatPrice = (price) => {
  return Number(price).toFixed(2)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 格式化地址
const formatAddress = (item) => {
  const parts = []
  if (item.provinceName) parts.push(item.provinceName)
  if (item.cityName) parts.push(item.cityName)
  if (item.districtName) parts.push(item.districtName)
  if (item.addressDetail) parts.push(item.addressDetail)
  return parts.join(' ')
}

// 计算订单总价
const calculateOrderTotal = (items) => {
  return formatPrice(
    items.reduce((total, item) => total + (item.price * item.quantity), 0)
  )
}

// 切换页码
const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  fetchOrders(page)
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.paid-orders {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
}

.back-button {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-right: 1rem;
}

.back-button:hover {
  background-color: #40a9ff;
}

h1 {
  color: #333;
  margin: 0;
  flex-grow: 1;
}

.refresh-button {
  background-color: #52c41a;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.refresh-button:hover:not(:disabled) {
  background-color: #73d13d;
}

.refresh-button:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.loading, .empty-state {
  text-align: center;
  padding: 3rem;
  color: #666;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.orders-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.order-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fafafa;
}

.order-header h3 {
  margin: 0;
  color: #333;
}

.order-date {
  color: #888;
  font-size: 0.9rem;
}

.order-items {
  padding: 0 1.5rem;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.order-item:last-child {
  border-bottom: none;
}

.product-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 1rem;
}

.product-details h4 {
  margin: 0 0 0.5rem;
  color: #333;
}

.price {
  color: #f5222d;
  font-weight: bold;
  margin: 0 0 0.5rem;
}

.quantity {
  color: #666;
  margin: 0;
}

.item-actions {
  display: flex;
  align-items: center;
}

.ship-button {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
  width: 80px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ship-button:hover:not(:disabled) {
  background-color: #40a9ff;
}

.ship-button:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}

.order-footer {
  background-color: #fafafa;
  padding: 1.5rem;
  display: flex;
  justify-content: space-between;
  border-top: 1px solid #f0f0f0;
}

.customer-info p {
  margin: 0.3rem 0;
}

.order-total {
  text-align: right;
}

.total-price {
  color: #f5222d;
  font-weight: bold;
  font-size: 1.2rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.pagination button {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #d9d9d9;
  cursor: not-allowed;
}
</style>
