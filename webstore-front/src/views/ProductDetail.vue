<template>
  <div class="product-detail">
    <h1>{{ product.name }}</h1>
    <img :src="product.image" alt="Product Image">
    <p>{{ product.description }}</p>
    <p>Price: {{ product.price }}</p>
    <!-- Add more product details as needed -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const product = ref({})

const fetchProductDetails = async () => {
  try {
    const response = await axios.get(`/api/products/${route.params.id}`)
    product.value = response.data
  } catch (error) {
    console.error('Failed to fetch product details:', error)
  }
}

onMounted(() => {
  fetchProductDetails()
})
</script>

<style scoped>
.product-detail {
  padding: 2rem;
}
</style>
