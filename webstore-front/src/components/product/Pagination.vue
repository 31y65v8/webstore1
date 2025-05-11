<template>
  <div class="pagination">
    <button 
      :disabled="currentPage === 1"
      @click="handlePageChange(currentPage - 1)">
      上一页
    </button>
    <span>{{ currentPage }} / {{ totalPages }}</span>
    <button 
      :disabled="currentPage === totalPages"
      @click="handlePageChange(currentPage + 1)">
      下一页
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update:currentPage'])

const handlePageChange = (page) => {
  if (page >= 1 && page <= props.totalPages) {
    emit('update:currentPage', page)
  }
}
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.pagination button {
  padding: 0.5rem 1rem;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background-color: white;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination button:hover:not(:disabled) {
  color: #1890ff;
  border-color: #1890ff;
}

.pagination button:disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

.pagination span {
  color: #666;
}
</style>