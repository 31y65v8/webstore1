<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <h2>用户登录</h2>
      
      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group">
          <label for="account">账号</label>
          <input 
            id="account"
            v-model="formData.account"
            type="text"
            required
            placeholder="请输入手机号或邮箱"
          >
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input 
            id="password"
            v-model="formData.password"
            type="password"
            required
            placeholder="请输入密码"
          >
        </div>

        <div class="form-group">
          <label for="role">角色</label>
          <select 
            id="role"
            v-model="formData.role"
            required
          >
            <option value="">请选择角色</option>
            <option value="CUSTOMER">普通用户</option>
            <option value="SELLER">销售人员</option>
            <option value="ADMIN">管理员</option>
          </select>
        </div>

        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="handleCancel">
            取消
          </button>
          <button type="submit" class="submit-btn" :disabled="isSubmitting">
            {{ isSubmitting ? '登录中...' : '登录' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useAuth } from '@/composables/useAuth'

const emit = defineEmits(['close', 'success', 'show-register'])
const router = useRouter()
const { login } = useAuth()

const formData = reactive({
  account: '',
  password: '',
  role: ''
})

const isSubmitting = ref(false)

const handleSubmit = async () => {
  if (isSubmitting.value) return

  try {
    isSubmitting.value = true
    
    // 使用 useAuth 的 login 方法
    const success = await login({
      account: formData.account,
      password: formData.password,
      role: formData.role
    })
    
    if (success) {
      // 告知父组件登录成功，但不传递用户数据，因为useAuth已经设置了全局状态
      emit('success')
      emit('close') // 关闭登录模态框
       // 对于CUSTOMER角色，强制刷新页面以显示导航栏的变化
       if (formData.role === 'CUSTOMER') {
        window.location.reload()
      }
    } else {
      alert('登录失败，请检查账号密码是否正确')
    }
  } catch (error) {
    console.error('登录失败:', error)
    alert('登录失败，请检查账号密码是否正确')
  } finally {
    isSubmitting.value = false
  }
}

const handleCancel = () => {
  emit('close')  // 先关闭模态框
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
}

h2 {
  margin-bottom: 1.5rem;
  color: #333;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  font-weight: 500;
  color: #333;
}

input, select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
}

.cancel-btn, .submit-btn {
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
}

.cancel-btn {
  background: white;
  border: 1px solid #ddd;
  color: #666;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
  margin-left: 1rem;
}

.cancel-btn:hover {
  background: #f5f5f5;
}

.submit-btn {
  background: #1890ff;
  border: none;
  color: white;
}

.submit-btn:hover {
  background: #40a9ff;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
