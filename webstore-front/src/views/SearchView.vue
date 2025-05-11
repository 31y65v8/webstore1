<template>
    <div class="search-view">
      <div class="search-header">
        <h2>搜索结果: "{{ searchQuery }}"</h2>
        <p v-if="products.length > 0">找到 {{ total }} 个相关商品</p>
        <p v-else>没有找到相关商品</p>
      </div>
  
      <div class="product-list">
        <div v-for="product in products" :key="product.productId" class="product-card" @click="goToProductDetail(product.productId)">
          <img :src="product.productImage" :alt="product.productName" class="product-image">
          <div class="product-info">
            <h3 class="product-name">{{ product.productName }}</h3>
            <p class="product-price">¥{{ product.productPrice.toFixed(2) }}</p>
            <div class="product-meta">
              <span class="product-category">{{ getCategoryName(product.productCategory) }}</span>
              <span class="product-sales">销量: {{ product.productSales }}</span>
            </div>
          </div>
        </div>
      </div>
  
      <div v-if="products.length > 0" class="pagination">
        <button 
          @click="prevPage" 
          :disabled="pageNum === 1"
          class="page-btn"
        >
          上一页
        </button>
        <span class="page-info">第 {{ pageNum }} 页 / 共 {{ totalPages }} 页</span>
        <button 
          @click="nextPage" 
          :disabled="pageNum >= totalPages"
          class="page-btn"
        >
          下一页
        </button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, watch } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import axios from 'axios'
  
  const route = useRoute()
  const router = useRouter()
  
  const searchQuery = ref('')
  const products = ref([])
  const pageNum = ref(1)
  const pageSize = ref(10)
  const total = ref(0)
  const totalPages = ref(1)
  
  const categories = {
    'ALL': '全部',
    'FASHION': '服饰',
    'HOME': '家居',
    'FOOD': '食品',
    'BEAUTY': '美妆',
    'BABY': '母婴',
    'SPORTS': '运动',
    'BOOKS': '书籍',
    'DIGITAL': '数码'
  }
  
  const fetchSearchResults = async () => {
    try {
      const response = await axios.get('/api/product/products/search', {
        params: {
          pageNum: pageNum.value,
          pageSize: pageSize.value,
          q: searchQuery.value
        }
      })
      
      const data = response.data
      products.value = data.records || data.list || []
      total.value = data.total
      totalPages.value = Math.ceil(total.value / pageSize.value)
    } catch (error) {
      console.error('搜索失败:', error)
      products.value = []
      total.value = 0
      totalPages.value = 1
    }
  }
  
  const getCategoryName = (categoryValue) => {
    return categories[categoryValue] || categoryValue
  }
  
  const goToProductDetail = (productId) => {
    router.push(`/product/${productId}`)
  }
  
  const nextPage = () => {
    if (pageNum.value < totalPages.value) {
      pageNum.value++
      fetchSearchResults()
    }
  }
  
  const prevPage = () => {
    if (pageNum.value > 1) {
      pageNum.value--
      fetchSearchResults()
    }
  }
  
  onMounted(() => {
    searchQuery.value = route.query.q || ''
    if (searchQuery.value) {
      fetchSearchResults()
    }
  })
  
  watch(() => route.query.q, (newQuery) => {
    if (newQuery) {
      searchQuery.value = newQuery
      pageNum.value = 1
      fetchSearchResults()
    }
  })
  </script>
  
  <style scoped>
  .search-view {
    max-width: 1200px;
    margin: 80px auto 20px;
    padding: 20px;
  }
  
  .search-header {
    margin-bottom: 30px;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;
  }
  
  .search-header h2 {
    color: #333;
    margin-bottom: 10px;
  }
  
  .search-header p {
    color: #666;
    font-size: 0.9rem;
  }
  
  .product-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
  }
  
  .product-card {
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    cursor: pointer;
  }
  
  .product-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }
  
  .product-image {
    width: 100%;
    height: 200px;
    object-fit: cover;
  }
  
  .product-info {
    padding: 15px;
  }
  
  .product-name {
    font-size: 1rem;
    margin-bottom: 8px;
    color: #333;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .product-price {
    font-size: 1.2rem;
    color: #f56c6c;
    font-weight: bold;
    margin-bottom: 8px;
  }
  
  .product-meta {
    display: flex;
    justify-content: space-between;
    font-size: 0.8rem;
    color: #999;
  }
  
  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 20px;
    margin-top: 30px;
  }
  
  .page-btn {
    padding: 8px 16px;
    background-color: #1890ff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .page-btn:hover:not(:disabled) {
    background-color: #40a9ff;
  }
  
  .page-btn:disabled {
    background-color: #d9d9d9;
    cursor: not-allowed;
  }
  
  .page-info {
    color: #666;
  }
  
  @media (max-width: 768px) {
    .product-list {
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    }
    
    .product-image {
      height: 150px;
    }
  }
  
  @media (max-width: 480px) {
    .search-view {
      margin-top: 70px;
      padding: 10px;
    }
    
    .product-list {
      grid-template-columns: 1fr 1fr;
      gap: 10px;
    }
    
    .pagination {
      gap: 10px;
    }
  }
  </style>