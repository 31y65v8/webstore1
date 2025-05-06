<template>
    <div>
      <h2>全部销售人员</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <table v-else class="seller-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>账号</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="seller in sellers" :key="seller.id">
            <td>{{ seller.id }}</td>
            <td>{{ seller.username }}</td>
            <td>{{ seller.registerPhone || seller.registerEmail || '-' }}</td>
            <td>
              <button @click="fetchSalesReport(seller.id)">查看销售报表</button>
            </td>
          </tr>
        </tbody>
      </table>
  
     <!-- <button @click="fetchSalesReport">查看我的品类销售报表</button>-->
  
      <!-- 销售报表弹窗或区域 -->
      <div v-if="showSalesReport" class="modal">
        <div class="modal-content">
          <h3>我的品类销售报表</h3>
          <div v-if="loadingSalesReport" class="loading">加载中...</div>
          <table v-else class="seller-table">
            <thead>
              <tr>
                <th>商品类别</th>
                <th>销量</th>
                <th>销售额</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in salesReport" :key="item.category">
                <td>{{ item.category }}</td>
                <td>{{ item.totalQuantity }}</td>
                <td>{{ item.totalSalesAmount }}</td>
              </tr>
            </tbody>
          </table>
          <div class="modal-actions">
            <button class="cancel-btn" @click="showSalesReport = false">关闭</button>
          </div>
        </div>
        <div class="modal-overlay" @click="showSalesReport = false"></div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  
  const props = defineProps({
    // 可选：支持父组件传入销售人员数据
    sellers: {
      type: Array,
      default: () => []
    }
  });
  const emit = defineEmits(['view-report']);
  
  const loading = ref(false);
  const sellers = ref(props.sellers);
  const showSalesReport = ref(false)
  const salesReport = ref([])
  const loadingSalesReport = ref(false)
  
  const fetchSellers = async () => {
    loading.value = true;
    try {
      const token = localStorage.getItem('token');
      const res = await axios.get('/api/user/allsellers', {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (res.data.code === 200) {
        sellers.value = res.data.data || [];
      } else {
        alert('获取销售人员失败: ' + res.data.msg);
      }
    } catch (e) {
      alert('获取销售人员失败: ' + (e.response?.data?.msg || e.message));
    } finally {
      loading.value = false;
    }
  };
  
  const fetchSalesReport = async (sellerId) => {
    if (!sellerId) {
    alert("缺少销售人员ID");
    return;
  }
    loadingSalesReport.value = true
    try {
      const token = localStorage.getItem('token')
      const res = await axios.get('/api/admin/seller-category-report', {
        params: { sellerId }, // 如果需要传sellerId
        headers: { Authorization: `Bearer ${token}` }
      })
      if (res.data.code === 200 || Array.isArray(res.data)) {
        salesReport.value = res.data.data || res.data
        showSalesReport.value = true
      } else {
        alert('获取销售报表失败: ' + (res.data.msg || '未知错误'))
      }
    } catch (e) {
      alert('获取销售报表失败: ' + (e.response?.data?.msg || e.message))
    } finally {
      loadingSalesReport.value = false
    }
  }
  
  onMounted(() => {
    if (!props.sellers.length) {
      fetchSellers();
    }
  });
  
  
  </script>
  
  <style scoped>
  .seller-table {
    width: 100%;
    border-collapse: collapse;
    background: white;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border-radius: 4px;
    overflow: hidden;
  }
  .seller-table th, .seller-table td {
    padding: 12px 16px;
    text-align: left;
    border-bottom: 1px solid #f0f0f0;
  }
  .seller-table th {
    background: #fafafa;
    font-weight: 500;
  }
  .loading {
    padding: 30px;
    text-align: center;
    color: #999;
  }
  
  .modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .modal-content {
    background-color: white;
    padding: 20px;
    border-radius: 4px;
    width: 80%;
    max-width: 600px;
  }
  
  .modal-actions {
    margin-top: 20px;
    text-align: right;
  }
  
  .cancel-btn {
    padding: 8px 16px;
    background-color: #f0f0f0;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }
  </style>
  