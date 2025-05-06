<template>
  <div class="admin-container">
    <h1>管理控制台</h1>
    
    <div class="admin-menu">
      
      
      <div class="menu-card" @click="goToSellerManagement">
        <i class="icon user-icon"></i>
        <h3>销售人员管理</h3>
        <p>管理销售人员、修改销售人员口令</p>
      </div>

      <div class="menu-card" @click="goToSellerSalesReport">
        <i class="icon chart-icon"></i>
        <h3>销售人员销售报表</h3>
        <p>查看销售人员负责的各类别商品的销量等统计</p>
      </div>
      <SellerList v-if="showSellerList" @view-report="fetchSalesReport" />


      <div class="menu-card" @click="goToCategoriesSalesReport">
        <i class="icon chart-icon"></i>
        <h3>商品类别销售报表</h3>
        <p>查看各类别商品的销量、库存等统计</p>
      </div>

      <div class="menu-card" @click="goToProductSalesTrend">
        <i class="icon chart-icon"></i>
        <h3>商品销售趋势与异常</h3>
        <p>查看商品销售趋势预测与异常监控</p>
      </div>
      <div class="menu-card" @click="goToUserProfile">
        <i class="icon chart-icon"></i>
        <h3>用户画像</h3>
        <p>查看用户画像</p>
      </div>
    </div>

    <!-- 销售人员管理 -->
    <div v-if="showSellerManagement" class="seller-management">
      <h2>销售人员管理</h2>
      
      <div class="actions">
        <button class="add-btn" @click="showAddSellerModal = true">新增销售人员</button>
        <input v-model="searchQuery" placeholder="搜索销售人员..." class="search-input" />
      </div>
      
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="sellers.length === 0" class="empty-state">
        暂无销售人员
      </div>
      
      <table v-else class="seller-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>联系邮箱</th>
            <th>联系电话</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="seller in filteredSellers" :key="seller.id">
            <td>{{ seller.id }}</td>
            <td>{{ seller.username }}</td>
            <td>{{ seller.registerEmail || '-' }}</td>
            <td>{{ seller.registerPhone || '-' }}</td>
            <td>{{ formatDate(seller.registerTime) }}</td>
            <td class="actions-cell">
              <button class="action-btn reset-btn" @click="showPasswordModal(seller)">
                重置密码
              </button>
              <button class="action-btn delete-btn" @click="showDeleteModal(seller)">
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增销售人员模态框 -->
    <div v-if="showAddSellerModal" class="modal">
      <div class="modal-content">
        <h3>新增销售人员</h3>
        <div class="form-group">
          <label>用户名</label>
          <input v-model="newSeller.username" placeholder="请输入用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input type="password" v-model="newSeller.password" placeholder="请输入密码" />
        </div>
        <div class="form-group">
          <label>联系邮箱</label>
          <input v-model="newSeller.registerEmail" placeholder="请输入邮箱" />
        </div>
        <div class="form-group">
          <label>联系电话</label>
          <input v-model="newSeller.registerPhone" placeholder="请输入手机号" />
        </div>
        <div class="modal-actions">
          <button class="cancel-btn" @click="showAddSellerModal = false">取消</button>
          <button class="confirm-btn" @click="addSeller">确认添加</button>
        </div>
      </div>
      <div class="modal-overlay" @click="showAddSellerModal = false"></div>
    </div>

    <!-- 重置密码模态框 -->
    <div v-if="showPasswordResetModal" class="modal">
      <div class="modal-content">
        <h3>重置密码</h3>
        <p>为销售人员 "{{ selectedSeller?.username }}" 重置密码</p>
        <div class="form-group">
          <label>新密码</label>
          <input type="password" v-model="newPassword" placeholder="请输入新密码" />
        </div>
        <div class="modal-actions">
          <button class="cancel-btn" @click="showPasswordResetModal = false">取消</button>
          <button class="confirm-btn" @click="resetPassword">确认重置</button>
        </div>
      </div>
      <div class="modal-overlay" @click="showPasswordResetModal = false"></div>
    </div>

    <!-- 删除确认模态框 -->
    <div v-if="showDeleteSellerModal" class="modal">
      <div class="modal-content">
        <h3>删除销售人员</h3>
        <p>确定要删除销售人员 "{{ selectedSeller?.username }}" 吗？此操作不可撤销。</p>
        <div class="modal-actions">
          <button class="cancel-btn" @click="showDeleteSellerModal = false">取消</button>
          <button class="confirm-btn delete-btn" @click="deleteSeller">确认删除</button>
        </div>
      </div>
      <div class="modal-overlay" @click="showDeleteSellerModal = false"></div>
    </div>

    <CategoriesSalesReport v-if="showCategoriesSalesReport" @close="showCategoriesSalesReport = false" />
    <div v-if="showCategoriesSalesReport" class="modal-overlay" @click="showCategoriesSalesReport = false"></div>

    <ProductSalesTrend v-if="showProductSalesTrend" @close="showProductSalesTrend = false" />
    <div v-if="showProductSalesTrend" class="modal-overlay" @click="showProductSalesTrend = false"></div>
  </div>

</template>

<script>
import axios from 'axios';
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

import CategoriesSalesReport from '../components/admin/CategoriesSalesReport.vue'
import ProductSalesTrend from '../components/admin/ProductSalesTrend.vue'

export default {
  components: {
    
    CategoriesSalesReport,
    ProductSalesTrend
  },
  setup() {
    const router = useRouter();
    const sellers = ref([]);
    const loading = ref(false);
    const showSellerManagement = ref(false);
    const searchQuery = ref('');
    const showAddSellerModal = ref(false);
    const showPasswordResetModal = ref(false);
    const showDeleteSellerModal = ref(false);
    const selectedSeller = ref(null);
    const newPassword = ref('');
    const showSalesReport = ref(false);
    const selectedSellerId = ref(null);
    const salesReport = ref([]);
    const loadingSalesReport = ref(false);
    const showCategoriesSalesReport = ref(false);
    const showProductSalesTrend = ref(false);
    const showSellerList = ref(false);
    
    const newSeller = ref({
      username: '',
      password: '',
      registerEmail: '',
      registerPhone: '',
      role: 'SELLER'
    });

    const filteredSellers = computed(() => {
      if (!searchQuery.value) return sellers.value;
      const query = searchQuery.value.toLowerCase();
      return sellers.value.filter(seller => 
        seller.username.toLowerCase().includes(query) || 
        (seller.registerEmail && seller.registerEmail.toLowerCase().includes(query)) ||
        (seller.registerPhone && seller.registerPhone.includes(query))
      );
    });

    const goToReports = () => {
      router.push('/api/admin/reports');
    };

    const goToSellerManagement = () => {
      showSellerManagement.value = true;
      fetchSellers();
    };
    const goToSellerSalesReport = () => {
      router.push("/admin/report/sellers")
    };

    const goToCategoriesSalesReport = () => {
      router.push("/admin/report/categories")
    };

    const goToProductSalesTrend = () => {
      router.push("/admin/report/products")
    };


    const fetchSellers = async () => {
      loading.value = true;
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('/api/admin/getAllSellers', {
          headers: { Authorization: `Bearer ${token}` }
        });
        if (response.data.code === 200) {
          sellers.value = response.data.data || [];
        } else {
          console.error('获取销售人员失败:', response.data.msg);
        }
      } catch (error) {
        console.error('获取销售人员错误:', error);
      } finally {
        loading.value = false;
      }
    };

    const showPasswordModal = (seller) => {
      selectedSeller.value = seller;
      newPassword.value = '';
      showPasswordResetModal.value = true;
    };

    const showDeleteModal = (seller) => {
      selectedSeller.value = seller;
      showDeleteSellerModal.value = true;
    };

    const addSeller = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.post('/api/admin/addSeller', newSeller.value, {
          headers: { Authorization: `Bearer ${token}` }
        });
        if (response.data.code === 200) {
          showAddSellerModal.value = false;
          newSeller.value = { username: '', password: '', registerEmail: '', registerPhone: '', role: 'SELLER' };
          fetchSellers();
        } else {
          alert('添加销售人员失败: ' + response.data.msg);
        }
      } catch (error) {
        console.error('添加销售人员错误:', error);
        alert('添加销售人员失败: ' + (error.response?.data?.msg || error.message));
      }
    };

    const resetPassword = async () => {
      if (!newPassword.value) {
        alert('请输入新密码');
        return;
      }
      
      try {
        const token = localStorage.getItem('token');
        const response = await axios.post('/api/admin/updateSellerPassword', null, {
          params: {
            userId: selectedSeller.value.id,
            newpassword: newPassword.value
          },
          headers: { Authorization: `Bearer ${token}` }
        });
        
        if (response.data.code === 200) {
          showPasswordResetModal.value = false;
          alert('密码重置成功');
        } else {
          alert('密码重置失败: ' + response.data.msg);
        }
      } catch (error) {
        console.error('密码重置错误:', error);
        alert('密码重置失败: ' + (error.response?.data?.msg || error.message));
      }
    };

    const deleteSeller = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.post('/api/admin/removeSeller', null, {
          params: { userId: selectedSeller.value.id },
          headers: { Authorization: `Bearer ${token}` }
        });
        
        if (response.data.code === 200) {
          showDeleteSellerModal.value = false;
          fetchSellers();
        } else {
          alert('删除销售人员失败: ' + response.data.msg);
        }
      } catch (error) {
        console.error('删除销售人员错误:', error);
        alert('删除销售人员失败: ' + (error.response?.data?.msg || error.message));
      }
    };

    const formatDate = (dateString) => {
      if (!dateString) return '-';
      const date = new Date(dateString);
      return date.toLocaleString();
    };

    const fetchSalesReport = async (sellerId) => {
      showSalesReport.value = true;
      selectedSellerId.value = sellerId;
      loadingSalesReport.value = true;
      try {
        const token = localStorage.getItem('token');
        const res = await axios.get('/api/admin/seller-category-report', {
          params: { sellerId },
          headers: { Authorization: `Bearer ${token}` }
        });
        if (res.data.code === 200 || Array.isArray(res.data)) {
          // 兼容直接返回数组或统一响应格式
          salesReport.value = res.data.data || res.data;
        } else {
          alert('获取销售报表失败: ' + (res.data.msg || '未知错误'));
        }
      } catch (e) {
        alert('获取销售报表失败: ' + (e.response?.data?.msg || e.message));
      } finally {
        loadingSalesReport.value = false;
      }
    };

    onMounted(() => {
      // 根据路由参数决定是否默认显示销售人员管理
      if (router.currentRoute.value.path === '/admin/sellers') {
        showSellerManagement.value = true;
        fetchSellers();
      }
    });

    return {
      sellers,
      loading,
      showSellerManagement,
      searchQuery,
      filteredSellers,
      showAddSellerModal,
      showPasswordResetModal,
      showDeleteSellerModal,
      selectedSeller,
      newPassword,
      newSeller,
      goToReports,
      goToSellerManagement,
      goToSellerSalesReport,
      goToProductSalesTrend,
      showSellerList,
      showPasswordModal,
      showDeleteModal,
      addSeller,
      resetPassword,
      deleteSeller,
      formatDate,
      showSalesReport,
      selectedSellerId,
      salesReport,
      loadingSalesReport,
      fetchSalesReport,
      showCategoriesSalesReport,
      goToCategoriesSalesReport,
      showProductSalesTrend
    };
  }
};
</script>

<style scoped>
.admin-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.admin-menu {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
}

.menu-card {
  flex: 1;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.menu-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.icon {
  font-size: 36px;
  margin-bottom: 15px;
  color: #1890ff;
}

.menu-card h3 {
  font-size: 18px;
  margin-bottom: 10px;
}

.menu-card p {
  color: #666;
  font-size: 14px;
}

/* 销售人员管理样式 */
.seller-management {
  margin-top: 30px;
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.add-btn {
  background: #1890ff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  width: 250px;
}

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

.actions-cell {
  display: flex;
  gap: 8px;
}

.action-btn {
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.reset-btn {
  background: #faad14;
  color: white;
}

.delete-btn {
  background: #ff4d4f;
  color: white;
}

/* 模态框样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  z-index: 1001;
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
}

.form-group input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.cancel-btn {
  background: white;
  border: 1px solid #d9d9d9;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.confirm-btn {
  background: #1890ff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.loading, .empty-state {
  padding: 30px;
  text-align: center;
  color: #999;
}
</style>
