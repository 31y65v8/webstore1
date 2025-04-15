<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <h2>编辑商品</h2>
      
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
                <span>点击更换图片</span>
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="$emit('close')">取消</button>
          <button type="submit" class="submit-btn" :disabled="isSubmitting">
            {{ isSubmitting ? '保存中...' : '保存修改' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({
  productId: {
    type: Number,
    required: true
  }
})

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

// 加载商品信息
const loadProduct = async () => {
  try {
    const response = await axios.get(`/api/product/${props.productId}`)
    const product = response.data.data
    Object.assign(formData, product)
  } catch (error) {
    console.error('获取商品信息失败:', error)
    alert('获取商品信息失败')
    emit('close')
  }
}

const handleImageSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await axios.post('/api/product/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    formData.imgurl = response.data.data
  } catch (error) {
    console.error('上传图片失败:', error)
    alert('上传图片失败，请重试')
  }
}

const handleSubmit = async () => {
  if (isSubmitting.value) return;

  // 构造只包含非空字段的新对象
  const updateData = {};

  // 只包含填写了的字段（允许 name 留空）
  if (formData.name !== '') updateData.name = formData.name;
  if (formData.price !== '' && !isNaN(formData.price)) updateData.price = parseFloat(formData.price);
  if (formData.storage !== '' && !isNaN(formData.storage)) updateData.storage = parseInt(formData.storage);
  if (formData.category !== '') updateData.category = formData.category;
  if (formData.description !== '') updateData.description = formData.description;
  if (formData.imgurl !== '') updateData.imgurl = formData.imgurl;

  // 校验价格和库存必须 ≥ 0
  if (updateData.price !== undefined && updateData.price < 0) {
    alert('商品价格不能小于 0');
    return;
  }

  if (updateData.storage !== undefined && updateData.storage < 0) {
    alert('库存数量不能小于 0');
    return;
  }

  try {
    isSubmitting.value = true;
    const response = await axios.put(`/api/product/${props.productId}`, updateData);
    emit('success', response.data.data);
  } catch (error) {
    console.error('更新商品失败:', error);
    alert('更新商品失败，请重试');
  } finally {
    isSubmitting.value = false;
  }
};


onMounted(() => {
  loadProduct()
})
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