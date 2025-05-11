<template>
  <div class="recommand-products">
    <h2>为你推荐</h2>
    
    <div v-if="loading">加载中...</div>
    <div v-else-if="error">{{ error }}</div>
    <div v-else class="product-list">
      <div v-for="product in products" :key="product.productId" class="product-card">
        <img :src="product.productImage" alt="商品图片" class="product-image" />
        <div class="product-info">
          <h3>{{ product.productName }}</h3>
          <p>价格：¥{{ product.productPrice }}</p>
          <p>销量：{{ product.productSales }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const products = ref([])
const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      error.value = '未登录，请先登录获取推荐'
      loading.value = false
      return
    }

    const response = await axios.get('/api/recommand/products', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
        topK: 2,
        topN: 10
      }
    })

    products.value = response.data.data
  } catch (err) {
    error.value = err.response?.data?.message || '获取推荐失败'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.recommand-products {
  padding: 20px;
}

.product-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.product-card {
  width: 200px;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  padding: 10px;
  background-color: #fff;
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.product-info {
  padding: 8px 0;
}
</style>
