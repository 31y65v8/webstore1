<template>
  <div class="browse-history">
    <h2>商品浏览记录</h2>
    
    <div class="filter-container">
      <div class="date-range">
        <label>日期范围:</label>
        <input type="date" v-model="startDate">
        <span>至</span>
        <input type="date" v-model="endDate">
      </div>
      
      <div class="product-filter">
        <label>商品:</label>
        <select v-model="selectedProduct">
          <option value="">全部商品</option>
          <option v-for="product in products" :key="product.id" :value="product.id">
            {{ product.name }}
          </option>
        </select>
      </div>
      
      <button class="search-btn" @click="fetchBrowseHistory">查询</button>
    </div>
    
    <div v-if="loading" class="loading">
      加载中...
    </div>
    
    <div v-else-if="browseHistory.length === 0" class="empty-state">
      暂无浏览记录
    </div>
    
    <div v-else class="history-table">
      <table>
        <thead>
          <tr>
            <th>商品名称</th>
            <th>用户ID</th>
            <th>浏览时间</th>
            <th>详细信息</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(record, index) in browseHistory" :key="index">
            <td>{{ record.productName }}</td>
            <td>{{ record.userId }}</td>
            <td>{{ formatDate(record.browseTime) }}</td>
            <td>
              <button class="detail-btn" @click="showUserDetails(record.userId)">
                查看用户详情
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div class="pagination">
        <button 
          :disabled="currentPage === 1"
          @click="handlePageChange(currentPage - 1)"
        >
          上一页
        </button>
        <span>{{ currentPage }} / {{ totalPages }}</span>
        <button 
          :disabled="currentPage === totalPages"
          @click="handlePageChange(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
    
    <!-- 用户详情弹窗 -->
    <div v-if="showDetailModal" class="detail-modal">
      <div class="modal-content">
        <h3>用户浏览详情</h3>
        <div v-if="userDetail">
          <p>用户ID: {{ userDetail.userId }}</p>
          <p>浏览商品数: {{ userDetail.browseCount }}</p>
          <p>首次浏览时间: {{ formatDate(userDetail.firstBrowseTime) }}</p>
          <p>最近浏览时间: {{ formatDate(userDetail.lastBrowseTime) }}</p>
          <h4>商品浏览频率</h4>
          <ul>
            <li v-for="(item, index) in userDetail.products" :key="index">
              {{ item.productName }}: {{ item.count }}次
            </li>
          </ul>
        </div>
        <div class="modal-actions">
          <button @click="showDetailModal = false">关闭</button>
        </div>
      </div>
      <div class="modal-overlay" @click="showDetailModal = false"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const loading = ref(false)
const browseHistory = ref([])
const products = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 10

// 过滤条件
const startDate = ref('')
const endDate = ref('')
const selectedProduct = ref('')

// 用户详情
const showDetailModal = ref(false)
const userDetail = ref(null)

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 获取所有商品
const fetchProducts = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return
    
    const response = await axios.get('/api/product/seller/products', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data && response.data.data) {
      products.value = response.data.data.records || []
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
  }
}

// 获取浏览历史
const fetchBrowseHistory = async (page = 1) => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) return
    
    const params = {
      page: page,
      size: pageSize
    }
    
    if (startDate.value) params.startDate = startDate.value
    if (endDate.value) params.endDate = endDate.value
    if (selectedProduct.value) params.productId = selectedProduct.value
    
    const response = await axios.get('/api/browse/history', {
      params,
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data && response.data.data) {
      browseHistory.value = response.data.data.records || []
      currentPage.value = Number(response.data.data.current) || 1
      totalPages.value = Number(response.data.data.pages) || 1
    }
  } catch (error) {
    console.error('获取浏览历史失败:', error)
  } finally {
    loading.value = false
  }
}

// 页面切换
const handlePageChange = (page) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  fetchBrowseHistory(page)
}

// 查看用户详情
const showUserDetails = async (userId) => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return
    
    const response = await axios.get(`/api/browse/user/${userId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data && response.data.data) {
      userDetail.value = response.data.data
      showDetailModal.value = true
    }
  } catch (error) {
    console.error('获取用户详情失败:', error)
    alert('获取用户详情失败')
  }
}

onMounted(() => {
  fetchProducts()
  fetchBrowseHistory()
})
</script>

<style scoped>
.browse-history {
  padding: 2rem;
}

h2 {
  margin-bottom: 1.5rem;
  color: #333;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
  padding: 1rem;
  background-color: #f8f8f8;
  border-radius: 8px;
}

.date-range, .product-filter {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.search-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.search-btn:hover {
  background-color: #40a9ff;
}

.loading, .empty-state {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.history-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f5f5f5;
}

.detail-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.detail-btn:hover {
  background-color: #40a9ff;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

/* 弹窗样式 */
.detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
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

.modal-content {
  position: relative;
  z-index: 1002;
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  width: 90%;
  max-width: 500px;
  margin: 10vh auto;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-actions {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
}

.modal-actions button {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.modal-actions button:hover {
  background-color: #40a9ff;
}

@media screen and (max-width: 768px) {
  .filter-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .date-range, .product-filter {
    flex-wrap: wrap;
  }
}
</style>
