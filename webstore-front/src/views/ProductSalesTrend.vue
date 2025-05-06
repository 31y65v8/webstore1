<template>
    <div>
      <h2>商品销售趋势与异常监控</h2>
      <button @click="fetchTrends" :disabled="loading">刷新</button>
      <div v-if="loading" class="loading">加载中...</div>
      <table v-else class="trend-table">
        <thead>
          <tr>
            <th>商品名称</th>
            <th>近12月销量</th>
            <th>预测下月</th>
            <th>趋势</th>
            <th>本月销量</th>
            <th>异常</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in productTrends" :key="item.productId">
            <td>{{ item.productName }}</td>
            <td>{{ item.monthlySales.join(',') }}</td>
            <td>{{ item.nextMonthPrediction.toFixed(2) }}</td>
            <td>{{ item.trend }}</td>
            <td>{{ item.currentMonthSales }}</td>
            <td>
              <span v-if="item.isAnomaly" style="color:red;">异常</span>
              <span v-else>正常</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import axios from 'axios'
  
  const productTrends = ref([])
  const loading = ref(false)
  
  const fetchTrends = async () => {
    loading.value = true
    try {
      const token = localStorage.getItem('token')
      const res = await axios.get('/api/admin/product-sales-trend', {
        headers: { Authorization: `Bearer ${token}` }
      })
      productTrends.value = res.data.data || res.data
    } finally {
      loading.value = false
    }
  }
  
  // 页面加载时自动拉取
  fetchTrends()
  </script>
  
  <style scoped>
  .trend-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    background: white;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    border-radius: 4px;
    overflow: hidden;
  }
  .trend-table th, .trend-table td {
    padding: 12px 16px;
    text-align: left;
    border-bottom: 1px solid #f0f0f0;
  }
  .trend-table th {
    background: #fafafa;
    font-weight: 500;
  }
  .loading {
    padding: 30px;
    text-align: center;
    color: #999;
  }
  </style>
  