<template>
  <div class="receiver-info-view">
    <h1>收货信息管理</h1>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <div v-if="receiverInfos.length === 0" class="empty-info">暂无收货信息</div>
      <div v-else class="info-list">
        <div v-for="info in receiverInfos" :key="info.id" class="info-item">
          <p>收货人: {{ info.receiverName }}</p>
          <p>电话: {{ info.receiverPhone }}</p>
          <p>地址: {{ info.provinceName }} {{ info.cityName }} {{ info.districtName }} {{ info.detail }}</p>
          <button @click="editReceiverInfo(info)">编辑</button>
          <button @click="deleteReceiverInfo(info.id)">删除</button>
        </div>
      </div>
      <button @click="showAddModal = true">添加收货信息</button>
    </div>

    <AddReceiverInfoModal v-if="showAddModal" @close="showAddModal = false" @success="fetchReceiverInfos" />
    <EditReceiverInfoModal v-if="showEditModal" :info="currentEditInfo" @close="showEditModal = false" @success="fetchReceiverInfos" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import AddReceiverInfoModal from '@/components/receiverInfo/AddReceiverInfoModal.vue'
import EditReceiverInfoModal from '@/components/receiverInfo/EditReceiverInfoModal.vue'

const receiverInfos = ref([])
const loading = ref(false)
const showAddModal = ref(false)
const showEditModal = ref(false)
const currentEditInfo = ref(null)

const fetchReceiverInfos = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/receiverInfo/list', { params: { userId: 1 } }) // 假设用户ID为1
    receiverInfos.value = response.data
  } catch (error) {
    console.error('获取收货信息失败:', error)
  } finally {
    loading.value = false
  }
}

const deleteReceiverInfo = async (id) => {
  try {
    await axios.delete(`/api/receiverInfo/delete/${id}`)
    fetchReceiverInfos() // 重新获取收货信息
  } catch (error) {
    console.error('删除收货信息失败:', error)
  }
}

const editReceiverInfo = (info) => {
  currentEditInfo.value = info
  showEditModal.value = true
}

onMounted(() => {
  fetchReceiverInfos()
})
</script>

<style scoped>
.receiver-info-view {
  padding: 2rem;
}

.loading {
  text-align: center;
}

.empty-info {
  text-align: center;
  color: #666;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.info-item {
  border: 1px solid #ddd;
  padding: 1rem;
  border-radius: 4px;
}

button {
  margin-top: 1rem;
}
</style>
