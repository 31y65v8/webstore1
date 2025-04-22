<template>
  <div class="products-container">
    <div class="products-grid">
      <ProductCard 
        v-for="product in products" 
        :key="product.id" 
        :product="product"
        @click="$emit('product-click', product)"
      />
    </div>
    
    <Pagination
      v-model:currentPage="currentPage"
      :total-pages="totalPages"
      @update:currentPage="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import ProductCard from './ProductCard.vue'
import Pagination from './Pagination.vue'
import axios from 'axios'



const props = defineProps({
  category: {
    type: String,
    default: 'ALL'
  },
  pageSize: {
    type: Number,
    default: 12
  },
  searchQuery: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['loading-change'])

const products = ref([])
const currentPage = ref(1)
const totalPages = ref(1)

const fetchProducts = async () => {
  emit('loading-change', true)
  try {
    let url = '/api/product/products'
    const params = {
      pageNum: currentPage.value,
      pageSize: props.pageSize
    }
    
    if (props.searchQuery) {
      url = '/api/product/products/search'
      params.name = props.searchQuery
    } else if (props.category !== 'ALL') {
      url = '/api/product/products/category'
      params.category = props.category
    }

    const response = await axios.get(url, { params })
    
    // 将后端返回的字段名映射为前端组件需要的字段名
    products.value = response.data.records.map(product => ({
      id: product.productId,
      name: product.productName,
      price: product.productPrice,
      imgurl: product.productImage,
      description: product.productDescription || '',
      sales: product.productSales || 0
    }))
    
    totalPages.value = response.data.pages
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    emit('loading-change', false)
  }
  console.log('商品数据：', products.value)
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchProducts()
}

// 监听分类变化
watch(() => props.category, () => {
  currentPage.value = 1 // 重置页码
  fetchProducts()
})

// 监听搜索查询变化
watch(() => props.searchQuery, () => {
  currentPage.value = 1 // 重置页码
  fetchProducts()
})

// 初始加载
fetchProducts()
</script>

<style scoped>
.products-container {
  padding: 2rem;
  margin-top: 4rem;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

@media screen and (max-width: 768px) {
  .products-container {
    padding: 1rem;
  }
  
  .products-grid {
    gap: 1rem;
  }
}
</style>
