<!-- src/components/auth/RegisterModal.vue -->
<template>
    <div v-if="visible" class="modal">
      <div class="modal-content">
        <h2>注册</h2>
        <form @submit.prevent="handleSubmit">
          <input 
            v-model="form.account" 
            type="text" 
            placeholder="手机号或邮箱" 
            required>
          <input 
            v-model="form.username" 
            type="text" 
            placeholder="用户名" 
            required>
          <input 
            v-model="form.password" 
            type="password" 
            placeholder="密码" 
            required>
          <input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="确认密码" 
            required>
          <select v-model="form.role">
            <option value="CUSTOMER">顾客</option>
            <option value="SELLER">销售人员</option>
          </select>
          <button type="submit">注册</button>
          <button type="button" @click="handleClose">取消</button>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useAuth } from '@/composables/useAuth'
  
  const props = defineProps({
    visible: Boolean
  })
  
  const emit = defineEmits(['update:visible', 'success'])
  
  const { register } = useAuth()
  
  const form = reactive({
    account: '',
    username: '',
    password: '',
    confirmPassword: '',
    role: 'CUSTOMER'
  })
  
  const validateForm = () => {
    if (!form.account) {
      throw new Error('请输入手机号或邮箱')
    }
    if (!form.username) {
      throw new Error('请输入用户名')
    }
    if (form.username.length < 4 || form.username.length > 20) {
      throw new Error('用户名长度必须在4-20位之间')
    }
    if (form.password !== form.confirmPassword) {
      throw new Error('两次输入的密码不一致')
    }
    if (form.password.length < 6 || form.password.length > 20) {
      throw new Error('密码长度必须在6-20位之间')
    }
  
    const phoneRegex = /^1[3-9]\d{9}$/
    const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/
    if (!phoneRegex.test(form.account) && !emailRegex.test(form.account)) {
      throw new Error('请输入有效的手机号或邮箱')
    }
  }
  
  const handleSubmit = async () => {
    try {
      validateForm()
      await register(form)
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
      username: '',
      password: '',
      confirmPassword: '',
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