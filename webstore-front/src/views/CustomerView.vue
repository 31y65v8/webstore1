<template>
  <div class="customer-view">
    <div class="header">
      <button class="back-button" @click="goBack">
        <span>&larr;</span> 
      </button>
      <h1>个人中心</h1>
    </div>
    
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <!-- 用户基本信息 -->
      <div class="user-info-section">
        <h2>基本信息</h2>
        <p>用户名: {{ user.username }}</p>
        <p>登录账号: 
          <span v-if="user.registerPhone">{{ user.registerPhone }} </span>
          <span v-else-if="user.registerEmail">{{ user.registerEmail }}</span>
          <span v-else>未设置</span>
        </p>
        <div class="account-actions">
          <button class="view-orders-btn" @click="goToOrders">查看我的订单</button>
          <button class="delete-account-btn" @click="confirmDeleteAccount">注销账号</button>
        </div>
      </div>
      
      <!-- 地址管理部分 -->
      <div class="address-section">
        <div class="section-header">
          <h2>收货地址</h2>
          <button class="add-btn" @click="showAddressForm = true">添加新地址</button>
        </div>
        
        <!-- 地址列表 -->
        <div v-if="addressesLoading" class="loading">加载地址中...</div>
        <div v-else-if="addresses.length === 0" class="empty-address">
          您还没有添加收货地址，点击"添加新地址"按钮添加
        </div>
        <div v-else class="address-list">
          <div v-for="address in addresses" :key="address.id" 
               class="address-card" 
               :class="{ 'default-address': address.isDefault }">
            <div class="address-info">
              <div class="address-header">
                <span class="receiver">{{ address.receiverName }} {{ address.receiverPhone }}</span>
                <span v-if="address.isDefault" class="default-tag">默认</span>
                <span v-if="address.label" class="label-tag">{{ address.label }}</span>
              </div>
              <div class="address-detail">
                {{ address.provinceName }} {{ address.cityName }} {{ address.districtName }} {{ address.detail }}
              </div>
            </div>
            <div class="address-actions">
              <button class="edit-btn" @click="editAddress(address)">编辑</button>
              <button class="delete-btn" @click="deleteAddress(address.id)">删除</button>
              <button v-if="!address.isDefault" class="default-btn" @click="setDefaultAddress(String(address.id))">
                设为默认
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加地址的弹窗 -->
    <div v-if="showAddressForm" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingAddress ? '编辑地址' : '添加新地址' }}</h3>
          <button class="close-btn" @click="closeAddressForm">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveAddress">
            <div class="form-group">
              <label for="receiverName">收货人姓名</label>
              <input id="receiverName" v-model="addressForm.receiverName" required />
            </div>
            <div class="form-group">
              <label for="receiverPhone">联系电话</label>
              <input id="receiverPhone" v-model="addressForm.receiverPhone" 
                     pattern="^1[3-9]\d{9}$" required />
            </div>
            <div class="form-group">
              <label for="detailAddress">详细地址</label>
              <textarea id="detailAddress" v-model="addressForm.fullAddress" 
                        placeholder="请输入完整地址，如：浙江省杭州市西湖区xxx街道xxx号" 
                        @input="parseAddress" rows="3" required></textarea>
            </div>
            <div class="address-parts">
              <div class="form-group">
                <label for="provinceName">省份</label>
                <input id="provinceName" v-model="addressForm.provinceName" required />
              </div>
              <div class="form-group">
                <label for="cityName">城市</label>
                <input id="cityName" v-model="addressForm.cityName" required />
              </div>
              <div class="form-group">
                <label for="districtName">区/县</label>
                <input id="districtName" v-model="addressForm.districtName" required />
              </div>
            </div>
            <div class="form-group">
              <label for="detail">详细街道门牌</label>
              <input id="detail" v-model="addressForm.detail" required />
            </div>
            <div class="form-group">
              <label for="label">地址标签</label>
              <select id="label" v-model="addressForm.label">
                <option value="">无标签</option>
                <option value="家">家</option>
                <option value="公司">公司</option>
                <option value="学校">学校</option>
              </select>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeAddressForm">取消</button>
              <button type="submit" class="save-btn">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import axios from 'axios'
import { useAuth } from '@/composables/useAuth'
import { useRouter } from 'vue-router'

const router = useRouter()
const { isLoggedIn, logout } = useAuth()
const user = ref({})
const loading = ref(false)
const addresses = ref([])
const addressesLoading = ref(false)
const showAddressForm = ref(false)
const editingAddress = ref(null)

// 地址表单
const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  fullAddress: '', // 用于解析省市区
  provinceName: '',
  provinceCode: '',
  cityName: '',
  cityCode: '',
  districtName: '',
  districtCode: '',
  detail: '',
  label: ''
})

// 省市区映射数据
const regionData = reactive({
  provinces: {},  // 省份名称到编码的映射
  cities: {},     // 城市名称到编码的映射
  districts: {}   // 区县名称到编码的映射
})

// 导航返回
const goBack = () => {
  router.push('/')
}

// 跳转到我的订单页面
const goToOrders = () => {
  router.push('/orders')
}

// 获取用户信息
const fetchUserInfo = async () => {
  if (!isLoggedIn.value) {
    console.log('未登录，不获取用户信息')
    return
  }

  loading.value = true
  try {
    const response = await axios.get('/api/user/info')
    console.log('用户信息响应:', response.data)
    user.value = response.data.data || {}
  } catch (error) {
    console.error('获取用户信息失败:', error)
    
    if (error.response) {
      if (error.response.status === 401 || 
          error.response.status === 403 || 
          (error.response.status === 500 && error.response.data && 
           (error.response.data.message === 'Token已过期' || 
            error.response.data.error?.includes('Token')))) {
        
        logout()
        alert('登录已过期，请重新登录')
        router.push('/')
      }
    }
  } finally {
    loading.value = false
  }
}

// 获取地址列表
const fetchAddresses = async () => {
  addressesLoading.value = true;
  try {
    const response = await axios.get('/api/receiver-info/list');
    console.log('地址列表响应:', response.data);
    addresses.value = response.data.data || [];
  } catch (error) {
    console.error('获取地址列表失败:', error);
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误信息:', error.response.data);
    }
  } finally {
    addressesLoading.value = false;
  }
}

// 解析地址
const parseAddress = () => {
  const fullAddress = addressForm.fullAddress
  if (!fullAddress) return
  
  // 简单的正则匹配，匹配省市区
  const provinceReg = /(.*?(省|自治区|北京市|上海市|天津市|重庆市|香港特别行政区|澳门特别行政区))/
  const cityReg = /(.*?(市|自治州|地区|区划|县))/
  const districtReg = /(.*?(区|县|市|旗))/
  
  let province = ''
  let city = ''
  let district = ''
  let detail = fullAddress
  
  // 提取省份
  const provinceMatch = fullAddress.match(provinceReg)
  if (provinceMatch && provinceMatch[1]) {
    province = provinceMatch[1]
    detail = detail.replace(province, '')
  }
  
  // 提取城市
  const cityMatch = detail.match(cityReg)
  if (cityMatch && cityMatch[1]) {
    city = cityMatch[1]
    detail = detail.replace(city, '')
  }
  
  // 提取区/县
  const districtMatch = detail.match(districtReg)
  if (districtMatch && districtMatch[1]) {
    district = districtMatch[1]
    detail = detail.replace(district, '')
  }
  
  // 更新表单数据
  addressForm.provinceName = province
  addressForm.cityName = city
  addressForm.districtName = district
  addressForm.detail = detail.trim()
  
  // 尝试匹配编码（如果已加载区域数据）
  matchRegionCodes()
}

// 匹配省市区编码
const matchRegionCodes = () => {
  // 如果有省市区名称，尝试查找对应编码
  if (addressForm.provinceName && regionData.provinces[addressForm.provinceName]) {
    addressForm.provinceCode = regionData.provinces[addressForm.provinceName]
  }
  
  if (addressForm.cityName && regionData.cities[addressForm.cityName]) {
    addressForm.cityCode = regionData.cities[addressForm.cityName]
  }
  
  if (addressForm.districtName && regionData.districts[addressForm.districtName]) {
    addressForm.districtCode = regionData.districts[addressForm.districtName]
  }
}

// 加载区域数据（省市区编码）
const loadRegionData = async () => {
  try {
    // 这里可以从后端API获取，也可以使用静态JSON文件
    // 例如: const response = await axios.get('/api/region/data')
    // 这里使用一个示例数据结构
    const response = {
      data: {
        provinces: {
          '浙江省': '330000',
          '北京市': '110000',
          // ...更多省份
        },
        cities: {
          '杭州市': '330100',
          '宁波市': '330200',
          // ...更多城市
        },
        districts: {
          '西湖区': '330106',
          '余杭区': '330110',
          // ...更多区县
        }
      }
    }
    
    // 更新区域数据
    regionData.provinces = response.data.provinces
    regionData.cities = response.data.cities
    regionData.districts = response.data.districts
  } catch (error) {
    console.error('加载区域数据失败:', error)
  }
}

// 重置地址表单
const resetAddressForm = () => {
  addressForm.receiverName = ''
  addressForm.receiverPhone = ''
  addressForm.fullAddress = ''
  addressForm.provinceName = ''
  addressForm.provinceCode = ''
  addressForm.cityName = ''
  addressForm.cityCode = ''
  addressForm.districtName = ''
  addressForm.districtCode = ''
  addressForm.detail = ''
  addressForm.label = ''
  editingAddress.value = null
}

// 关闭地址表单
const closeAddressForm = () => {
  showAddressForm.value = false
  resetAddressForm()
}

// 编辑地址
const editAddress = (address) => {
  editingAddress.value = address
  addressForm.receiverName = address.receiverName
  addressForm.receiverPhone = address.receiverPhone
  addressForm.provinceName = address.provinceName
  addressForm.provinceCode = address.provinceCode
  addressForm.cityName = address.cityName
  addressForm.cityCode = address.cityCode
  addressForm.districtName = address.districtName
  addressForm.districtCode = address.districtCode
  addressForm.detail = address.detail
  addressForm.label = address.label || ''
  
  // 合成完整地址用于显示
  addressForm.fullAddress = `${address.provinceName}${address.cityName}${address.districtName}${address.detail}`
  
  showAddressForm.value = true
}

// 保存地址
const saveAddress = async () => {
  try {
    // 构建保存的数据对象
    const addressData = {
      receiverName: addressForm.receiverName,
      receiverPhone: addressForm.receiverPhone,
      provinceCode: addressForm.provinceCode || '',
      provinceName: addressForm.provinceName,
      cityCode: addressForm.cityCode || '',
      cityName: addressForm.cityName,
      districtCode: addressForm.districtCode || '',
      districtName: addressForm.districtName,
      detail: addressForm.detail,
      label: addressForm.label
    }
    
    if (editingAddress.value) {
      // 更新地址，确保ID是字符串
      const addressId = String(editingAddress.value.id);
      await axios.put(`/api/receiver-info/${addressId}`, addressData);
      alert('地址更新成功');
    } else {
      // 新建地址
      await axios.post('/api/receiver-info/create', addressData);
      alert('地址添加成功');
    }
    
    // 关闭表单并刷新地址列表
    closeAddressForm();
    fetchAddresses();
  } catch (error) {
    console.error('保存地址失败:', error);
    alert(`保存地址失败: ${error.response?.data?.message || error.message}`);
  }
}

// 删除地址
const deleteAddress = async (id) => {
  if (!confirm('确定要删除这个地址吗？')) return
  
  try {
    // 确保ID是字符串
    const addressId = String(id);
    console.log('删除地址，ID:', addressId);
    
    await axios.delete(`/api/receiver-info/${addressId}`);
    alert('地址删除成功');
    fetchAddresses();
  } catch (error) {
    console.error('删除地址失败:', error);
    alert(`删除地址失败: ${error.response?.data?.message || error.message}`);
  }
}

// 设置默认地址
const setDefaultAddress = async (id) => {
  console.log('设置默认地址，原始ID:', id, typeof id);
  try {
    // 确保ID是字符串类型，避免JavaScript数字精度问题
    //const addressId = String(id);
    //console.log('处理后ID:', addressId);
    
    const response = await axios.put(`/api/receiver-info/set-default/${id}`);
    console.log('设置默认地址响应:', response.data);
    
    if (response.data.code === 200) {
      alert('默认地址设置成功');
      fetchAddresses();
    } else {
      alert(`设置失败: ${response.data.msg}`);
    }
  } catch (error) {
    console.error('设置默认地址失败:', error);
    alert(`设置默认地址失败: ${error.response?.data?.msg || error.message}`);
  }
}

// 确认注销账号
const confirmDeleteAccount = () => {
  if (confirm('您确定要注销账号吗？此操作将无法恢复，您的所有数据将被删除。')) {
    deleteAccount();
  }
}

// 注销账号
const deleteAccount = async () => {
  try {
    const response = await axios.post('/api/user/deleteuser');
    console.log('注销账号响应:', response.data);
    
    if (response.data.code === 200) {
      alert('账号已成功注销');
      logout(); // 调用logout方法清除本地存储的登录信息
      router.push('/'); // 跳转到首页
    } else {
      alert(`注销失败: ${response.data.msg || '未知错误'}`);
    }
  } catch (error) {
    console.error('注销账号失败:', error);
    
    if (error.response) {
      if (error.response.status === 401) {
        alert('登录已过期，请重新登录');
        logout();
        router.push('/');
      } else {
        alert(`注销失败: ${error.response.data?.msg || error.message}`);
      }
    } else {
      alert(`注销失败: ${error.message}`);
    }
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchAddresses()
  loadRegionData() // 加载省市区编码数据
})
</script>

<style scoped>
.customer-view {
  padding: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
}

.back-button {
  background-color: #f5f5f5;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  margin-right: 1rem;
  font-size: 0.9rem;
  transition: background-color 0.2s;
}

.back-button:hover {
  background-color: #e0e0e0;
}

.back-button span {
  margin-right: 0.3rem;
}

h1, h2, h3 {
  margin: 0;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.user-info-section, .address-section {
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.add-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-btn:hover {
  background-color: #40a9ff;
}

.empty-address {
  text-align: center;
  padding: 2rem;
  color: #999;
  background: #f9f9f9;
  border-radius: 4px;
}

.address-list {
  display: grid;
  gap: 1rem;
}

.address-card {
  display: flex;
  justify-content: space-between;
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 4px;
  transition: all 0.3s;
}

.address-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.default-address {
  border-color: #1890ff;
  background-color: #e6f7ff;
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.receiver {
  font-weight: bold;
  margin-right: 0.5rem;
}

.default-tag, .label-tag {
  font-size: 0.75rem;
  padding: 0.1rem 0.4rem;
  border-radius: 2px;
  margin-right: 0.5rem;
}

.default-tag {
  background-color: #1890ff;
  color: white;
}

.label-tag {
  background-color: #f0f0f0;
  color: #666;
}

.address-detail {
  color: #666;
}

.address-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.5rem;
}

.address-actions button {
  padding: 0.3rem 0.8rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.edit-btn {
  background-color: #f0f0f0;
  color: #333;
}

.delete-btn {
  background-color: #ff4d4f;
  color: white;
}

.default-btn {
  background-color: #52c41a;
  color: white;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.form-group input, .form-group textarea, .form-group select {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.address-parts {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 1rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.5rem;
}

.cancel-btn, .save-btn {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #666;
}

.save-btn {
  background-color: #1890ff;
  color: white;
}

.account-actions {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.view-orders-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.view-orders-btn:hover {
  background-color: #40a9ff;
}

.delete-account-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.delete-account-btn:hover {
  background-color: #ff7875;
}
</style>
