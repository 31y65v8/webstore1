<template>
    <div>
      <h2>商品类别销售报表</h2>
      <button @click="fetchReport" :disabled="loading">刷新报表</button>
      <div v-if="loading" class="loading">加载中...</div>
      <table v-else class="report-table">
        <thead>
          <tr>
            <th>类别</th>
            <th>商品数</th>
            <th>总销量</th>
            <th>销售额</th>
            <th>总库存</th>
            <th>售罄商品数</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in report" :key="item.category">
            <td>{{ item.category }}</td>
            <td>{{ item.productCount }}</td>
            <td>{{ item.totalQuantity }}</td>
            <td>{{ item.totalSales }}</td>
            <td>{{ item.totalStock }}</td>
            <td>{{ item.soldOutCount }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import axios from 'axios'
  
  const report = ref([])
  const loading = ref(false)
  
  const fetchReport = async () => {
    loading.value = true
    try {
      const token = localStorage.getItem('token')
      const res = await axios.get('/api/admin/category-sales-report', {
        headers: { Authorization: `Bearer ${token}` }
      })
      if (res.data.code === 200 || Array.isArray(res.data)) {
        report.value = res.data.data || res.data
      } else {
        alert('获取报表失败: ' + (res.data.msg || '未知错误'))
      }
    } catch (e) {
      alert('获取报表失败: ' + (e.response?.data?.msg || e.message))
    } finally {
      loading.value = false
    }
  }
  
  // 可选：页面加载时自动拉取
  fetchReport()
  </script>
  
  <style scoped>
  .report-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    background: white;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    border-radius: 4px;
    overflow: hidden;
  }
  .report-table th, .report-table td {
    padding: 12px 16px;
    text-align: left;
    border-bottom: 1px solid #f0f0f0;
  }
  .report-table th {
    background: #fafafa;
    font-weight: 500;
  }
  .loading {
    padding: 30px;
    text-align: center;
    color: #999;
  }
  </style>
  