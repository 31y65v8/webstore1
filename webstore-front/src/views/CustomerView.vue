<template>
  <div class="customer-view">
    <h1>用户个人信息</h1>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <p>用户名: {{ user.username }}</p>
      <p>登录账号: {{ user.account }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const user = ref({})
const loading = ref(false)

const fetchUserInfo = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/user/info', { params: { userId: 1 } }) // 假设用户ID为1
    user.value = response.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.customer-view {
  padding: 2rem;
}

.loading {
  text-align: center;
}
</style>
