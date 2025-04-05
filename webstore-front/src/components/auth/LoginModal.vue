<template>
  <div v-if="visible" class="modal">
    <div class="modal-content">
      <h2>登录</h2>
      <form @submit.prevent="handleSubmit">
        <input 
          v-model="form.account" 
          type="text" 
          placeholder="账号" 
          required>
        <input 
          v-model="form.password" 
          type="password" 
          placeholder="密码" 
          required>
        <select v-model="form.role">
          <option value="CUSTOMER">顾客</option>
          <option value="SELLER">销售人员</option>
          <option value="ADMIN">管理员</option>
        </select>
        <button type="submit">登录</button>
        <button type="button" @click="handleClose">取消</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useAuth } from '@/composables/useAuth'

const props = defineProps({
  visible: Boolean
})

const emit = defineEmits(['update:visible', 'success'])

const { login } = useAuth()

const form = reactive({
  account: '',
  password: '',
  role: 'CUSTOMER'
})

const handleSubmit = async () => {
  try {
    await login(form)
    emit('success')
    handleClose()
  } catch (error) {
    alert(error.message)
  }
}

const handleClose = () => {
  emit('update:visible', false)
  // 重置表单
  Object.assign(form, {
    account: '',
    password: '',
    role: 'CUSTOMER'
  })
}
</script>

<style scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  width: 400px;
}

.modal-content input,
.modal-content select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 100%;
  margin-bottom: 0.5rem;
}

.modal-content form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.modal-content button {
  padding: 0.5rem;
  border: none;
  border-radius: 4px;
  background-color: #1890ff;
  color: white;
  cursor: pointer;
  width: 100%;
}

.modal-content button[type="button"] {
  background-color: #f5f5f5;
  color: #333;
}
</style>
