<template>
  <div class="product-list">
    <div v-if="loading" class="loading">
      加载中...
    </div>
    
    <div v-else-if="products.length === 0" class="empty-state">
      暂无商品，点击"添加新商品"开始创建
    </div>
    
    <div v-else class="products-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        <div class="product-image">
          <img :src="product.imgurl" :alt="product.name">
        </div>
        <div class="product-info">
          <h3>{{ product.name }}</h3>
          <div class="product-meta">
            <span class="price">¥{{ product.price }}</span>
            <span class="stock">库存: {{ product.storage }}</span>
          </div>
          <div class="product-category">
            分类: {{ getCategoryLabel(product.category) }}
          </div>
          <div class="product-actions">
            <button @click="handleEdit(product)" class="edit-btn">编辑</button>
            <button @click="handleDelete(product)" class="delete-btn">删除</button>
          </div>
        </div>
      </div>
    </div>

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

    <EditProductModal
      v-if="showEditModal"
      :product-id="editingProductId"
      @close="showEditModal = false"
      @success="handleEditSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import EditProductModal from './EditProductModal.vue'

const props = defineProps({
  refresh: {
    type: Number,
    default: 0
  }
})

const loading = ref(false)
const products = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const pageSize = 12
const showEditModal = ref(false)
const editingProductId = ref(null)

const categories = {
  'FASHION': '服饰',
  'HOME': '家居',
  'FOOD': '食品',
  'BEAUTY': '美妆',
  'BABY': '母婴',
  'SPORTS': '运动',
  'BOOKS': '书籍',
  'DIGITAL': '数码'
}

const getCategoryLabel = (category) => {
  return categories[category] || category
}

const fetchProducts = async (page = 1) => {
  loading.value = true
  try {
    const response = await axios.get('/api/product/seller/products', {
      params: {
        pageNum: page,
        pageSize
      }
    })
    const data = response.data.data
    products.value = data.records
    currentPage.value = data.current
    totalPages.value = data.pages
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  if (page < 1 || page > totalPages.value) return
  fetchProducts(page)
}

const handleEdit = (product) => {
  editingProductId.value = product.id
  showEditModal.value = true
}

const handleEditSuccess = () => {
  showEditModal.value = false
  fetchProducts()
}

const handleDelete = async (product) => {
  if (!confirm(`确定要删除商品"${product.name}"吗？`)) {
    return;
  }

  try {
    // 获取存储的JWT令牌
    const token = localStorage.getItem('token');
    if (!token) {
      alert('登录已过期，请重新登录');
      return;
    }

    // 发送带有认证头的删除请求
    await axios.delete(`/api/product/${product.id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    alert('商品删除成功');
    fetchProducts(currentPage.value); // 刷新当前页
  } catch (error) {
    console.error('删除商品失败:', error);
    
    // 提供更详细的错误信息给用户
    let errorMessage = '删除商品失败，请重试';
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403) {
        errorMessage = '您没有权限执行此操作';
      } else if (error.response.data && error.response.data.message) {
        errorMessage = error.response.data.message;
      }
    }
    
    alert(errorMessage);
  }
};

// 监听刷新信号
watch(() => props.refresh, () => {
  fetchProducts(1)
})

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.product-list {
  margin-top: 1rem;
}

.loading, .empty-state {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 1rem;
}

.product-info h3 {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  color: #333;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.price {
  color: #f5222d;
  font-weight: bold;
}

.stock {
  color: #666;
}

.product-category {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.product-actions {
  display: flex;
  gap: 0.5rem;
}

.edit-btn, .delete-btn {
  flex: 1;
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.edit-btn {
  background: #1890ff;
  color: white;
}

.edit-btn:hover {
  background: #40a9ff;
}

.delete-btn {
  background: #ff4d4f;
  color: white;
}

.delete-btn:hover {
  background: #ff7875;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.pagination button {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.pagination button:not(:disabled):hover {
  background: #f5f5f5;
}

.pagination button:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.5;
}

.pagination span {
  color: #666;
}

.actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

.edit-btn, .delete-btn {
  flex: 1;
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.edit-btn {
  background: #1890ff;
  color: white;
}

.edit-btn:hover {
  background: #40a9ff;
}

.delete-btn {
  background: #ff4d4f;
  color: white;
}

.delete-btn:hover {
  background: #ff7875;
}
</style> 