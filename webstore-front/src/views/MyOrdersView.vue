<template>
  <div class="orders-view">
    <div class="header">
      <button class="back-button" @click="goBack">
        <span>&larr;</span> 
      </button>
      <h1>我的订单</h1>
    </div>
    
    <div class="order-tabs">
      <button 
        v-for="tab in orderTabs" 
        :key="tab.status" 
        :class="['tab-btn', { active: currentOrderTab === tab.status }]"
        @click="changeOrderTab(tab.status)"
      >
        {{ tab.label }}
      </button>
    </div>
    
    <div v-if="ordersLoading" class="loading">加载订单中...</div>
    <div v-else-if="orders.length === 0" class="empty-orders">
      暂无{{ getOrderTabLabel(currentOrderTab) }}订单
    </div>
    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-id">订单号：{{ order.id }}</span>
          <span class="order-status">{{ getOrderStatusText(order.status) }}</span>
        </div>
        
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <div class="item-image">
              <img :src="item.productImage" :alt="item.productName">
            </div>
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-price">¥{{ formatPrice(item.productPrice) }} × {{ item.quantity }}</div>
            </div>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-time">下单时间：{{ formatTime(order.createTime) }}</div>
          <div class="order-total">合计：¥{{ formatPrice(getOrderTotal(order)) }}</div>
          
          <div class="order-actions">
            <button v-if="order.status === 'PENDING'" class="cancel-order-btn" @click="cancelOrder(order.id)">
              取消订单
            </button>
            <button v-if="order.status === 'PENDING'" class="pay-order-btn" @click="payOrder(order.id)">
              去支付
            </button>
            <button v-if="order.status === 'SHIPPED'" class="confirm-receipt-btn" @click="confirmReceipt(order.id)">
              确认收货
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'

const router = useRouter()
const { isLoggedIn } = useAuth()
const orders = ref([])
const ordersLoading = ref(false)
const currentOrderTab = ref('ALL')

// 订单状态选项卡
const orderTabs = [
  { status: 'ALL', label: '全部' },
  { status: 'PENDING', label: '待支付' },
  { status: 'PAID', label: '已支付' },
  { status: 'SHIPPED', label: '已发货' },
  { status: 'DELIVERED', label: '已送达' },
  { status: 'CANCELLED', label: '已取消' }
]

// 获取当前选项卡的标签
const getOrderTabLabel = (status) => {
  const tab = orderTabs.find(tab => tab.status === status)
  return tab ? tab.label : ''
}

// 切换订单选项卡
const changeOrderTab = (status) => {
  currentOrderTab.value = status
  fetchOrders()
}

// 格式化价格
const formatPrice = (price) => {
  return Number(price).toFixed(2)
}

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 计算订单总价
const getOrderTotal = (order) => {
  if (!order.items || !order.items.length) return 0
  return order.items.reduce((total, item) => {
    return total + (item.productPrice * item.quantity)
  }, 0)
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  switch (status) {
    case 'PENDING': return '待支付'
    case 'PAID': return '已支付'
    case 'SHIPPED': return '已发货'
    case 'DELIVERED': return '已送达'
    case 'CANCELLED': return '已取消'
    default: return status
  }
}

// 导航返回
const goBack = () => {
  router.push('/customer')
}

// 获取订单列表
const fetchOrders = async () => {
  if (!isLoggedIn.value) {
    alert('请先登录')
    router.push('/')
    return
  }
  
  ordersLoading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      alert('登录已过期，请重新登录')
      router.push('/')
      return
    }

    let url = '/api/orders/user'
    if (currentOrderTab.value !== 'ALL') {
      url += `?status=${currentOrderTab.value}`
    }

    const response = await axios.get(url, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    console.log('订单列表响应:', response.data)
    
    // 确保每个订单包含items数组
    const ordersData = response.data.data || []
    
    // 获取订单详情（包括订单项）
    const ordersWithItems = await Promise.all(
      ordersData.map(async order => {
        try {
          const itemsResponse = await axios.get(`/api/orders/${order.id}/items`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          })
          return {
            ...order,
            items: itemsResponse.data.data || []
          }
        } catch (error) {
          console.error(`获取订单${order.id}的详情失败:`, error)
          return {
            ...order,
            items: []
          }
        }
      })
    )
    
    orders.value = ordersWithItems
  } catch (error) {
    console.error('获取订单列表失败:', error)
    if (error.response?.status === 401) {
      alert('登录已过期，请重新登录')
      router.push('/')
    }
  } finally {
    ordersLoading.value = false
  }
}

// 取消订单
const cancelOrder = async (orderId) => {
  if (!confirm('确定要取消该订单吗？')) return
  
  try {
    await axios.post(`/api/orders/cancel/${orderId}`)
    alert('订单已取消')
    fetchOrders()
  } catch (error) {
    console.error('取消订单失败:', error)
    alert(`取消订单失败: ${error.response?.data?.message || error.message}`)
  }
}

// 支付订单（模拟）
const payOrder = async (orderId) => {
  alert('跳转到支付页面...')
  // 这里可以实现跳转到支付页面的逻辑
}

// 确认收货
const confirmReceipt = async (orderId) => {
  if (!confirm('确认已收到商品吗？')) return
  
  try {
    await axios.put(`/api/orders/status/${orderId}`, null, {
      params: {
        newStatus: 'DELIVERED'
      }
    })
    alert('已确认收货')
    fetchOrders()
  } catch (error) {
    console.error('确认收货失败:', error)
    alert(`确认收货失败: ${error.response?.data?.message || error.message}`)
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-view {
  padding: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
}

.back-button {
  background-color: #f5f5f5;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  margin-right: 1rem;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.back-button:hover {
  background-color: #e0e0e0;
}

.back-button span {
  margin-right: 0.3rem;
}

h1 {
  margin: 0;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.order-tabs {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 1.5rem;
  overflow-x: auto;
}

.tab-btn {
  padding: 0.8rem 1.2rem;
  border: none;
  background: none;
  cursor: pointer;
  color: #666;
  font-size: 1rem;
  transition: all 0.3s;
  position: relative;
  white-space: nowrap;
}

.tab-btn.active {
  color: #1890ff;
  font-weight: bold;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #1890ff;
}

.empty-orders {
  text-align: center;
  padding: 3rem;
  color: #999;
  background: #f9f9f9;
  border-radius: 4px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.order-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: #f9f9f9;
}

.order-id {
  color: #666;
  font-size: 0.9rem;
}

.order-status {
  color: #1890ff;
  font-weight: bold;
}

.order-items {
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
}

.order-item {
  display: flex;
  margin-bottom: 1rem;
}

.order-item:last-child {
  margin-bottom: 0;
}

.item-image {
  width: 60px;
  height: 60px;
  overflow: hidden;
  border-radius: 4px;
  margin-right: 1rem;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-name {
  margin-bottom: 0.5rem;
}

.item-price {
  color: #999;
  font-size: 0.9rem;
}

.order-footer {
  padding: 1rem;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
}

.order-time {
  color: #999;
  font-size: 0.9rem;
}

.order-total {
  font-weight: bold;
  color: #f5222d;
}

.order-actions {
  display: flex;
  gap: 0.8rem;
  margin-top: 1rem;
  width: 100%;
  justify-content: flex-end;
}

.order-actions button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-order-btn {
  background-color: #f0f0f0;
  color: #666;
}

.pay-order-btn {
  background-color: #f5222d;
  color: white;
}

.confirm-receipt-btn {
  background-color: #52c41a;
  color: white;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .orders-view {
    padding: 1rem;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.8rem;
  }
  
  .order-actions {
    justify-content: flex-start;
  }
}
</style>
