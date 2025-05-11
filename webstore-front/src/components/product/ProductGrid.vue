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
import { ref, watch ,computed} from 'vue'
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

const emit = defineEmits(['loading-change', 'product-click', 'data-loaded'])

const products = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const localLoading = ref(false)
const requestCount = ref(0) // 用于跟踪当前请求

// 计算属性判断是否有数据
const hasData = computed(() => products.value.length > 0)


const fetchProducts = async () => {
  const currentRequest = ++requestCount.value
  localLoading.value = true
  emit('loading-change', true)
  try {
    let url = '/api/product/products'
    const params = {
      pageNum: currentPage.value,
      pageSize: props.pageSize
    }
    
    if (props.searchQuery) {
      url = '/api/product/products/search'
      params.q = props.searchQuery
    } else if (props.category !== 'ALL') {
      url = '/api/product/products/category'
      params.category = props.category
    }

    const response = await axios.get(url, { params })
    console.log('API response:', response) // 添加调试日志
    
    if (currentRequest !== requestCount.value) return
    // 将后端返回的字段名映射为前端组件需要的字段名
    products.value = response.data.records.map(product => ({
      id: product.productId,
      name: product.productName,
      price: product.productPrice,
      imgurl: product.productImage,
      description: product.productDescription || '',
      sales: product.productSales || 0
    }))
    
    totalPages.value = Number(response.data.pages) || 1
    emit('data-loaded', products.value)
  } catch (error) {
    console.error('获取商品列表失败:', error)
    products.value = []
    totalPages.value = 1
    emit('data-loaded', [])
    throw error // 重新抛出错误
  } finally {
    /*emit('loading-change', false)*/
    if (currentRequest === requestCount.value) {
      localLoading.value = false
      emit('loading-change', false)
    }
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
// 确保只在必要时触发请求
watch(() => props.searchQuery, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    fetchProducts()
  }
}, { immediate: true })

// 初始加载
fetchProducts()
</script>

<style scoped>
.products-container {
  width: 100%; /* 明确宽度 */
  padding: 2rem;
  box-sizing: border-box; /* 包含 padding 在宽度内 */
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); /* 减小最小宽度 */
  gap: 1.5rem; /* 减小间隙 
  margin-bottom: 2rem;*/
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