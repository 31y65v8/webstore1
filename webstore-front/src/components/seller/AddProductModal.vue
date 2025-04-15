<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <h2>添加新商品</h2>
      
      <form @submit.prevent="handleSubmit" class="product-form">
        <div class="form-group">
          <label for="name">商品名称</label>
          <input 
            id="name"
            v-model="formData.name"
            type="text"
            required
            placeholder="请输入商品名称"
          >
        </div>

        <div class="form-group">
          <label for="price">商品价格</label>
          <input 
            id="price"
            v-model="formData.price"
            type="number"
            step="0.01"
            required
            placeholder="请输入商品价格"
          >
        </div>

        <div class="form-group">
          <label for="storage">库存数量</label>
          <input 
            id="storage"
            v-model="formData.storage"
            type="number"
            required
            placeholder="请输入库存数量"
          >
        </div>

        <div class="form-group">
          <label for="category">商品分类</label>
          <select 
            id="category"
            v-model="formData.category"
            required
          >
            <option value="">请选择分类</option>
            <option v-for="category in categories" :key="category.value" :value="category.value">
              {{ category.label }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="description">商品描述</label>
          <textarea 
            id="description"
            v-model="formData.description"
            required
            placeholder="请输入商品描述"
          ></textarea>
        </div>

        <div class="form-group">
          <label>商品图片</label>
          <div class="image-upload">
            <input 
              type="file"
              @change="handleImageSelect"
              accept="image/*"
              ref="fileInput"
              style="display: none"
            >
            <div 
              class="upload-area"
              @click="$refs.fileInput.click()"
              :class="{ 'has-image': formData.imgurl }"
            >
              <img v-if="formData.imgurl" :src="formData.imgurl" alt="商品图片预览">
              <div v-else class="upload-placeholder">
                <i class="fas fa-cloud-upload-alt"></i>
                <span>点击上传图片</span>
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="$emit('close')">取消</button>
          <button type="submit" class="submit-btn" :disabled="isSubmitting">
            {{ isSubmitting ? '提交中...' : '添加商品' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import axios from 'axios'

const emit = defineEmits(['close', 'success'])

const categories = [
  { value: 'FASHION', label: '服饰' },
  { value: 'HOME', label: '家居' },
  { value: 'FOOD', label: '食品' },
  { value: 'BEAUTY', label: '美妆' },
  { value: 'BABY', label: '母婴' },
  { value: 'SPORTS', label: '运动' },
  { value: 'BOOKS', label: '书籍' },
  { value: 'DIGITAL', label: '数码' }
]

const formData = reactive({
  name: '',
  price: '',
  storage: '',
  category: '',
  description: '',
  imgurl: ''
})

const isSubmitting = ref(false)
const fileInput = ref(null)

const handleImageSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }

  // 验证文件大小（限制为5MB）
  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过5MB')
    return
  }

  const imageFormData = new FormData()
  imageFormData.append('file', file)

  // 获取token
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    return
  }

  try {
    const response = await axios.post('/api/product/upload/image', imageFormData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data.code === 200) {
      formData.imgurl = response.data.data
    } else {
      throw new Error(response.data.message || '上传失败')
    }
  } catch (error) {
    console.error('上传图片失败:', error)
    alert(error.response?.data?.message || '上传图片失败，请重试')
  }
}

const handleSubmit = async () => {
  if (isSubmitting.value) return

  try {
    isSubmitting.value = true
    const response = await axios.post('/api/product/add', formData)
    emit('success', response.data.data)
  } catch (error) {
    console.error('添加商品失败:', error)
    alert('添加商品失败，请重试')
  } finally {
    isSubmitting.value = false
  }
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
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

h2 {
  margin-bottom: 1.5rem;
  color: #333;
}

.product-form {
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

input, select, textarea {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

textarea {
  min-height: 100px;
  resize: vertical;
}

.image-upload .upload-area {
  border: 2px dashed #ddd;
  border-radius: 4px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.image-upload .upload-area:hover {
  border-color: #1890ff;
}

.upload-area.has-image {
  padding: 0;
}

.upload-area img {
  max-width: 100%;
  max-height: 200px;
  object-fit: contain;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  color: #666;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
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