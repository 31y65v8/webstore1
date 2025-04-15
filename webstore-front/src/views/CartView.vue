<template>
  <div class="cart">
    <h1>我的购物车</h1>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <div v-if="cartItems.length === 0" class="empty-cart">购物车为空</div>
      <div v-else class="cart-items">
        <div v-for="item in cartItems" :key="item.product.id" class="cart-item">
          <input type="checkbox" v-model="item.selected" @change="selectProduct(item)">
          <span>{{ item.product.name }}</span>
          <input type="number" v-model.number="item.quantity" @change="updateQuantity(item)">
          <button @click="removeFromCart(item)">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const cartItems = ref([])
const loading = ref(false)

const fetchCartItems = async () => {
  loading.value = true
  try {
    const response = await axios.get('/cart/items', { params: { userId: 1 } }) // 假设用户ID为1
    cartItems.value = response.data
  } catch (error) {
    console.error('获取购物车商品失败:', error)
  } finally {
    loading.value = false
  }
}

const selectProduct = async (item) => {
  try {
    await axios.put('/cart/select', {
      userId: 1, // 假设用户ID为1
      productId: item.product.id,
      selected: item.selected
    })
  } catch (error) {
    console.error('选择商品失败:', error)
  }
}

const updateQuantity = async (item) => {
  try {
    await axios.put('/cart/update', {
      userId: 1, // 假设用户ID为1
      productId: item.product.id,
      quantity: item.quantity
    })
  } catch (error) {
    console.error('更新商品数量失败:', error)
  }
}

const removeFromCart = async (item) => {
  try {
    await axios.delete('/cart/remove', {
      params: {
        userId: 1, // 假设用户ID为1
        productId: item.product.id
      }
    })
    fetchCartItems() // 重新获取购物车商品
  } catch (error) {
    console.error('删除商品失败:', error)
  }
}

onMounted(() => {
  fetchCartItems()
})
</script>

<style scoped>
.cart {
  padding: 2rem;
}

.loading {
  text-align: center;
}

.empty-cart {
  text-align: center;
  color: #666;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.cart-item input[type="number"] {
  width: 60px;
}
</style>
