<template>
  <div class="search-view">
    <h2 class="search-title">搜索结果: {{ searchQuery }}</h2>
    <div v-if="loading" class="loading">
      加载中...
    </div>
    <div v-else>
      <ProductGrid 
        ref="productGrid"
        :search-query="searchQuery"
        @loading-change="handleLoadingChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import ProductGrid from '@/components/product/ProductGrid.vue'

const route = useRoute()
const loading = ref(false)
const searchQuery = ref('')

const handleLoadingChange = (isLoading) => {
  loading.value = isLoading
}

watch(() => route.query.q, (newQuery) => {
  if (newQuery) {
    searchQuery.value = newQuery
  }
}, { immediate: true })
</script>

<style scoped>
.search-view {
  padding: 2rem;
  margin-top: 4rem;
}

.search-title {
  margin-bottom: 2rem;
  color: #333;
}

.loading {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: #666;
}
</style> 